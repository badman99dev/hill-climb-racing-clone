package pap.ptmp.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private final float leftOffset;
    private final float rightOffset;
    private WheelJoint leftAxis, rightAxis;
    private final float forwardSpeed;
    private final float backwardSpeed;
    private boolean rotate_left, rotate_right;
    private Body collisionBox;
    private Texture carTexture;
    private Texture wheelTexture;
    private float fuel = 100;
    private final float fuelConsumptionRatio; // 1 represents 1 unit per second
    private final float jointFrictionForce;
    private int gasPointer = -1;   // active touch pointer id for gas (right side)
    private int brakePointer = -1; // active touch pointer id for brake/reverse (left side)
    private final SpriteBatch spriteBatch;

    public Vehicle(World world, float x, float y, float width, float height,
                   float wheelRadius, float suspensionHeight, float leftOffset, float rightOffset,
                   float forwardSpeed, float backwardSpeed, float fuelConsumptionRatio,
                   float jointFrictionForce, Texture carTexture, Texture wheelTexture) {
        this.world = world;
        this.width = width;
        this.height = height;
        this.wheelRadius = wheelRadius;
        this.suspensionHeight = suspensionHeight;
        this.leftOffset = leftOffset;
        this.rightOffset = rightOffset;
        this.forwardSpeed = forwardSpeed;
        this.backwardSpeed = backwardSpeed;
        this.rotate_left = false;
        this.rotate_right = false;
        this.fuelConsumptionRatio = fuelConsumptionRatio;
        this.jointFrictionForce = jointFrictionForce;
        this.carTexture = carTexture;
        this.wheelTexture = wheelTexture;
        this.spriteBatch = new SpriteBatch();
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
        leftWheelDef.position.set(body.getPosition().x - width / 2 + leftOffset, body.getPosition().y - height / 2 - suspensionHeight);
        leftWheel = world.createBody(leftWheelDef);
        leftWheel.createFixture(wheelFixtureDef);

        // Right wheel
        BodyDef rightWheelDef = new BodyDef();
        rightWheelDef.type = BodyDef.BodyType.DynamicBody;
        rightWheelDef.position.set(body.getPosition().x + width / 2 - rightOffset, body.getPosition().y - height / 2 - suspensionHeight);
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

    public void render(OrthographicCamera camera) {
        float bodyAngle = body.getAngle();
        float bodyX = body.getPosition().x;
        float bodyY = body.getPosition().y;

        float relativeX = -width / 2 - 0.12f;
        float relativeY = -height / 2 - 0.12f;

        float rotatedX = (float) (bodyX + Math.cos(bodyAngle) * relativeX - Math.sin(bodyAngle) * relativeY);
        float rotatedY = (float) (bodyY + Math.sin(bodyAngle) * relativeX + Math.cos(bodyAngle) * relativeY);
        float rotation = MathUtils.radiansToDegrees * body.getAngle();

        SpriteBatch spriteBatch = this.spriteBatch;
        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.draw(carTexture, rotatedX, rotatedY, 0f, 0f, 1.44f, 0.7f, 1f,
                1f, rotation, 0, 0, carTexture.getWidth(), carTexture.getHeight(), true, false);

        float wheelX = leftWheel.getPosition().x;
        float wheelY = leftWheel.getPosition().y;
        rotation = MathUtils.radiansToDegrees * leftWheel.getAngle();
        rotatedX = wheelX - wheelRadius * MathUtils.cos(leftWheel.getAngle());
        rotatedY = wheelY - wheelRadius * MathUtils.sin(leftWheel.getAngle());
        spriteBatch.draw(wheelTexture, rotatedX, rotatedY - wheelRadius, 0, wheelRadius, 2*wheelRadius, 2*wheelRadius, 1f,
                1f, rotation, 0, 0, wheelTexture.getWidth(), wheelTexture.getHeight(), false, false);

        wheelX = rightWheel.getPosition().x;
        wheelY = rightWheel.getPosition().y;
        rotation = MathUtils.radiansToDegrees * rightWheel.getAngle();
        rotatedX = wheelX - wheelRadius * MathUtils.cos(rightWheel.getAngle());
        rotatedY = wheelY - wheelRadius * MathUtils.sin(rightWheel.getAngle());
        spriteBatch.draw(wheelTexture, rotatedX, rotatedY - wheelRadius, 0, wheelRadius, 2*wheelRadius, 2*wheelRadius, 1f,
                1f, rotation, 0, 0, wheelTexture.getWidth(), wheelTexture.getHeight(), false, false);

        spriteBatch.end();

    }

    public boolean canMove() {
        return fuel > 0;
    }

    // ---- Control actions (shared by keyboard and touch) ----

    public void pressForward() {
        if ((!leftWheel.isFixedRotation()) && canMove()) {
            leftAxis.enableMotor(true);
            leftAxis.setMotorSpeed(-forwardSpeed);
        }
        rotate_left = true;
    }

    public void releaseForward() {
        rotate_left = false;
        leftAxis.enableMotor(false);
    }

    public void pressBackward() {
        if ((!leftWheel.isFixedRotation()) && canMove()) {
            leftAxis.enableMotor(true);
            leftAxis.setMotorSpeed(backwardSpeed);
        }
        rotate_right = true;
    }

    public void releaseBackward() {
        rotate_right = false;
        leftAxis.enableMotor(false);
    }

    public void pressBrake() {
        leftAxis.enableMotor(false);
        rightAxis.enableMotor(false);
        leftWheel.setFixedRotation(true);
        rightWheel.setFixedRotation(true);
    }

    public void releaseBrake() {
        leftWheel.setFixedRotation(false);
        rightWheel.setFixedRotation(false);
    }

    public boolean isForwardPressed() {
        return rotate_left;
    }

    public boolean isBackwardPressed() {
        return rotate_right;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch(keycode) {
            case Input.Keys.W:
            case Input.Keys.UP:
                pressForward();
                break;
            case Input.Keys.S:
            case Input.Keys.DOWN:
                pressBackward();
                break;
            case Input.Keys.SPACE:
                pressBrake();
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch(keycode) {
            case Input.Keys.SPACE:
                releaseBrake();
            case Input.Keys.W:
            case Input.Keys.UP:
                releaseForward();
            case Input.Keys.S:
            case Input.Keys.DOWN:
                releaseBackward();
                break;
        }
       return true;
    }

    // ---- On-screen touch controls (Android) ----
    // Right half of the screen = gas, left half = brake/reverse.

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        int half = Gdx.graphics.getWidth() / 2;
        if (screenX >= half) {
            if (gasPointer < 0) {
                gasPointer = pointer;
                pressForward();
            }
        } else {
            if (brakePointer < 0) {
                brakePointer = pointer;
                pressBackward();
            }
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (pointer == gasPointer) {
            gasPointer = -1;
            releaseForward();
        }
        if (pointer == brakePointer) {
            brakePointer = -1;
            releaseBackward();
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        int half = Gdx.graphics.getWidth() / 2;
        boolean right = screenX >= half;
        if (right && pointer == brakePointer) {
            brakePointer = -1;
            releaseBackward();
        } else if (!right && pointer == gasPointer) {
            gasPointer = -1;
            releaseForward();
        } else if (right && gasPointer < 0) {
            gasPointer = pointer;
            pressForward();
        } else if (!right && brakePointer < 0) {
            brakePointer = pointer;
            pressBackward();
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
            leftAxis.enableMotor(false);
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
        return Math.max(width, width - rightOffset + wheelRadius);
    }


    public Vector2 getCenter() {
        float boxCollisionHeight = getBoxCollisionHeight();
        return body.getWorldCenter().sub(new Vector2(0f , boxCollisionHeight/2));
    }

    public void refill() {
        if(fuel + 50  > 100) {
            fuel = 100;
        } else {
            fuel += 50;
        }
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


