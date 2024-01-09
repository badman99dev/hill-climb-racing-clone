package pap.ptmp.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.WheelJoint;
import com.badlogic.gdx.physics.box2d.joints.WheelJointDef;


public class Vehicle extends InputAdapter {
    private final World world;
    private Body body; // Body for the vehicle
    private Body leftWheel; // Left wheel
    private Body rightWheel; // Right wheel
    private final float width;
    private final float height;
    private final float wheelRadius;
    private final float suspensionHeight;
    private final float wheelOffset;
    private WheelJoint leftAxis, rightAxis;
    private final float forwardSpeed;
    private final float backwardSpeed;
    private boolean rotate_left, rotate_right;
    private Body collisionBox;
    private float fuel = 100;
    private final float fuelConsumptionRatio; // 1 represents 1 unit per second
    private final float jointFrictionForce;
    public Vehicle(World world, float x, float y, float width, float height,
                   float wheelRadius, float suspensionHeight, float wheelOffset,
                   float forwardSpeed, float backwardSpeed, float fuelConsumptionRatio,
                   float jointFrictionForce) {
        this.world = world;
        this.width = width;
        this.height = height;
        this.wheelRadius = wheelRadius;
        this.suspensionHeight = suspensionHeight;
        this.wheelOffset = wheelOffset;
        this.forwardSpeed = forwardSpeed;
        this.backwardSpeed = backwardSpeed;
        this.rotate_left = false;
        this.rotate_right = false;
        this.fuelConsumptionRatio = fuelConsumptionRatio;
        this.jointFrictionForce = jointFrictionForce;
        createBody(x, y);
        createWheels();
        VehicleCollisionBox vcb = new VehicleCollisionBox(world, this);
        RoofCollisionDetector rcd = new RoofCollisionDetector(world, this);
    }

    private void createBody(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;

        body.createFixture(fixtureDef);
        body.setAngularDamping(5f);

        shape.dispose();
    }

    private void createWheels() {
        CircleShape wheelShape = new CircleShape();
        wheelShape.setRadius(wheelRadius);

        FixtureDef wheelFixtureDef = new FixtureDef();
        wheelFixtureDef.shape = wheelShape;
        wheelFixtureDef.density = 5.0f;
        wheelFixtureDef.friction = 3f;
        wheelFixtureDef.restitution = 0.3f;


        // Left wheel
        BodyDef leftWheelDef = new BodyDef();
        leftWheelDef.type = BodyDef.BodyType.DynamicBody;
        leftWheelDef.position.set(body.getPosition().x - width / 2 + wheelOffset, body.getPosition().y - height / 2 - suspensionHeight);
        leftWheel = world.createBody(leftWheelDef);
        leftWheel.createFixture(wheelFixtureDef);

        // Right wheel
        BodyDef rightWheelDef = new BodyDef();
        rightWheelDef.type = BodyDef.BodyType.DynamicBody;
        rightWheelDef.position.set(body.getPosition().x + width / 2 - wheelOffset, body.getPosition().y - height / 2 - suspensionHeight);
        rightWheel = world.createBody(rightWheelDef);
        rightWheel.createFixture(wheelFixtureDef);

        // Wheel joints and axis
        WheelJointDef axisDef = new WheelJointDef();
        axisDef.bodyA = body;
        axisDef.bodyB = leftWheel;
        axisDef.localAxisA.set(0, 1);
        axisDef.frequencyHz = 3f;
        axisDef.dampingRatio = 0.9f;
        axisDef.maxMotorTorque = 0.1f;
        axisDef.localAnchorA.set(leftWheel.getPosition().x - body.getPosition().x, leftWheel.getPosition().y - body.getPosition().y);
        leftAxis = (WheelJoint) world.createJoint(axisDef);

        axisDef.bodyB = rightWheel;
        axisDef.localAnchorA.set(rightWheel.getPosition().x - body.getPosition().x, rightWheel.getPosition().y - body.getPosition().y);

        rightAxis = (WheelJoint) world.createJoint(axisDef);

        wheelShape.dispose();
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        float bodyX = body.getPosition().x - width / 2;
        float bodyY = body.getPosition().y - height / 2;

        // Rotate the shape renderer around the body's center
        shapeRenderer.translate(bodyX + width / 2, bodyY + height / 2, 0);
        shapeRenderer.rotate(0, 0, 1, MathUtils.radiansToDegrees * body.getAngle());

        shapeRenderer.rect(-width / 2, -height / 2, width, height);
        shapeRenderer.identity();
        shapeRenderer.end();

        // Render the wheels
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.translate(leftWheel.getPosition().x, leftWheel.getPosition().y, 0);
        shapeRenderer.rotate(0, 0, 1, MathUtils.radiansToDegrees * leftWheel.getAngle());
        shapeRenderer.circle(0, 0, wheelRadius, 10);
        shapeRenderer.identity();

        shapeRenderer.translate(rightWheel.getPosition().x, rightWheel.getPosition().y, 0);
        shapeRenderer.rotate(0, 0, 1, MathUtils.radiansToDegrees * rightWheel.getAngle());
        shapeRenderer.circle(0, 0, wheelRadius, 10);
        shapeRenderer.identity();
        shapeRenderer.end();
    }

    public boolean canMove() {
        return fuel > 0;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch(keycode) {
            case Input.Keys.W:
                if ((!leftWheel.isFixedRotation()) && canMove()) {
                    leftAxis.enableMotor(true);
                    leftAxis.setMotorSpeed(-forwardSpeed);
                }
                rotate_left = true;
                break;
            case Input.Keys.S:
                if ((!leftWheel.isFixedRotation()) && canMove()) {
                    leftAxis.enableMotor(true);
                    leftAxis.setMotorSpeed(backwardSpeed);
                }
                rotate_right = true;
                break;
            case Input.Keys.SPACE:
                leftAxis.enableMotor(false);
                rightAxis.enableMotor(false);
                leftWheel.setFixedRotation(true);
                rightWheel.setFixedRotation(true);
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch(keycode) {
            case Input.Keys.SPACE:
                leftWheel.setFixedRotation(false);
                rightWheel.setFixedRotation(false);
            case Input.Keys.W:
                rotate_left = false;
            case Input.Keys.S:
                leftAxis.enableMotor(false);
                rotate_right = false;
                break;
        }
       return true;
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public void update() {
        // update fuel level
        if (fuel > 0f) {
            fuel -= 1/60f * fuelConsumptionRatio;
        } else {
            fuel = 0f;
        }
        //System.out.println(fuel);
        if (rotate_left && rotate_right || !canMove()){
            // do nothing
        } else if (rotate_right) {
            body.applyAngularImpulse(-0.005f, true);
        } else if (rotate_left) {
            body.applyAngularImpulse(0.005f, true);
            //System.out.println(body.getAngularVelocity());
        }

        // joint friction
        if (leftWheel.getAngularVelocity() < 0) {
            leftWheel.applyTorque(jointFrictionForce, true);
        }
        if (rightWheel.getAngularVelocity() < 0) {
            rightWheel.applyTorque(jointFrictionForce, true);
        }
        if (leftWheel.getAngularVelocity() > 0) {
            leftWheel.applyTorque(-jointFrictionForce, true);
        }
        if (rightWheel.getAngularVelocity() > 0) {
            rightWheel.applyTorque(-jointFrictionForce, true);
        }
    }
    public Body getBody() {
        return body;
    }

    public float getBoxCollisionHeight() {
        return (height + suspensionHeight + wheelRadius) / 2;
    }

    public float getBoxCollisionWidth() {
        return Math.max(width, width - wheelOffset + wheelRadius);
    }


    public Vector2 getCenter() {
        float boxCollisionHeight = getBoxCollisionHeight();
        return body.getWorldCenter().sub(new Vector2(0f , boxCollisionHeight/2));
    }

    public void refill() {
        fuel = 100;
    }

    public float getFuelLevel() {
        return fuel;
    }

    public Vector2 getRoofCollisionDetectorCenter() {
        return body.getWorldCenter().add(0, height/2);
    }

    public float getBodyWidth() {
        return width;
    }

    public float getBodyHeight() {
        return height;
    }
}


