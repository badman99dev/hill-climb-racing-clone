package pap.ptmp.game;

import com.badlogic.gdx.physics.box2d.*;

public class Beer {
    public Body body;
    public String id;
    public Beer(World world, String id) {
        this.id = id;
        createBoxBody(world);
    }

    private void createBoxBody(World world) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(1002.5f, 100f);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.2f, 0.2f);

        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = shape;
        fixDef.isSensor = true;


        this.body = world.createBody(bdef);
        body.createFixture(fixDef).setUserData(this);
    }

    public void hit() {
        System.out.println("i've been hit");
    }
}
