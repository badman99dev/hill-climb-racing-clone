package pap.ptmp.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;

public class VehicleCollisionBox {
    public Body body;
    public String id;
    public Vehicle vehicle;
    public VehicleCollisionBox(World world, Vehicle vehicle) {
        this.vehicle = vehicle;
        createBoxBody(world);
    }

    private void createBoxBody(World world) {
        BodyDef boxDef = new BodyDef();

        Vector2 center = vehicle.getCenter();
        boxDef.position.set(center);
        boxDef.type = BodyDef.BodyType.DynamicBody;
        boxDef.fixedRotation = false;
        body = world.createBody(boxDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(vehicle.getBoxCollisionWidth() / 2, vehicle.getBoxCollisionHeight());

        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = shape;
        fixDef.density = 0.0000001f;
        fixDef.friction = 0.0f;

        body.createFixture(fixDef).setUserData(this);

        WeldJointDef jointDef = new WeldJointDef();

        jointDef.initialize(body, vehicle.getBody(), boxDef.position);
        world.createJoint(jointDef);

        shape.dispose();
    }
}
