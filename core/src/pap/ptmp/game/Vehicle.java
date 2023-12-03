package pap.ptmp.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.WheelJoint;
import com.badlogic.gdx.physics.box2d.joints.WheelJointDef;

public class Vehicle {
    private final World world;
    private Body body; // Body for the vehicle
    private Body leftWheel; // Left wheel
    private Body rightWheel; // Right wheel
    private final float width;
    private final float height;
    private final float wheelRadius;
    private final float suspensionHeight;
    private final float wheelOffset;

    public Vehicle(World world, float x, float y, float width, float height, float wheelRadius, float suspensionHeight, float wheelOffset) {
        this.world = world;
        this.width = width;
        this.height = height;
        this.wheelRadius = wheelRadius;
        this.suspensionHeight = suspensionHeight;
        this.wheelOffset = wheelOffset;

        createBody(x, y);
        createWheels();
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

        shape.dispose();
    }

    private void createWheels() {
        CircleShape wheelShape = new CircleShape();
        wheelShape.setRadius(wheelRadius);

        FixtureDef wheelFixtureDef = new FixtureDef();
        wheelFixtureDef.shape = wheelShape;
        wheelFixtureDef.density = 1.0f;

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

        WheelJointDef LeftSuspensionDef = new WheelJointDef();
        LeftSuspensionDef.dampingRatio = 0.1f;
        LeftSuspensionDef.frequencyHz = 1.5f;
        LeftSuspensionDef.initialize(body, leftWheel, new Vector2(leftWheel.getPosition().x, leftWheel.getPosition().y), new Vector2(1f, 1f));

        WheelJointDef RightSuspensionDef = new WheelJointDef();
        RightSuspensionDef.dampingRatio = 0.1f;
        RightSuspensionDef.frequencyHz = 1.5f;
        RightSuspensionDef.initialize(body, rightWheel, new Vector2(rightWheel.getPosition().x, rightWheel.getPosition().y), new Vector2(1f, 1f));

        WheelJoint LeftSuspension = (WheelJoint) world.createJoint(LeftSuspensionDef);
        WheelJoint RightSuspension = (WheelJoint) world.createJoint(RightSuspensionDef);

        wheelShape.dispose();
    }

    public void render(ShapeRenderer shapeRenderer) {
        // Render the body
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
        shapeRenderer.circle(0, 0, wheelRadius);
        shapeRenderer.identity();

        shapeRenderer.translate(rightWheel.getPosition().x, rightWheel.getPosition().y, 0);
        shapeRenderer.rotate(0, 0, 1, MathUtils.radiansToDegrees * rightWheel.getAngle());
        shapeRenderer.circle(0, 0, wheelRadius);
        shapeRenderer.identity();
        shapeRenderer.end();
    }
}

