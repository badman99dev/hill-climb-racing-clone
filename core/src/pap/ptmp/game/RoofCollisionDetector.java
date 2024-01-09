package pap.ptmp.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;

public class RoofCollisionDetector {
    public Body body;
    public String id;
    public Vehicle vehicle;
    public RoofCollisionDetector(World world, Vehicle vehicle) {
        this.vehicle = vehicle;
        createBoxBody(world);
    }

    private void createBoxBody(World world) {
        BodyDef boxDef = new BodyDef();

        Vector2 center = vehicle.getRoofCollisionDetectorCenter();
        float width = vehicle.getBodyWidth();
        float height = vehicle.getBodyHeight();
        height = 0.05f * height;

        boxDef.position.set(center);
        boxDef.type = BodyDef.BodyType.DynamicBody;
        boxDef.fixedRotation = false;
        body = world.createBody(boxDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height);

        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = shape;
        fixDef.density = 0.0000001f;
        fixDef.friction = 0.0f;
        fixDef.isSensor = true;

        body.createFixture(fixDef).setUserData(this);

        WeldJointDef jointDef = new WeldJointDef();

        jointDef.initialize(body, vehicle.getBody(), boxDef.position);
        world.createJoint(jointDef);

        shape.dispose();
    }
}
