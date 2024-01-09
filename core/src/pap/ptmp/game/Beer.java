package pap.ptmp.game;

import com.badlogic.gdx.physics.box2d.*;

public class Beer {
    public Body body;
    public String id;
    public Beer(World world, float x, float y) {
        createBoxBody(world, x, y);
    }

    private void createBoxBody(World world, float x, float y) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(x, y);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.2f, 0.2f);

        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = shape;
        fixDef.isSensor = true;


        this.body = world.createBody(bdef);
        body.createFixture(fixDef).setUserData(this);
    }
}
