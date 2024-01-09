package pap.ptmp.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
public class Collectible {
    public Body body;
    public String id;
    private World world;
    private float x, y;
    private Map map;
    private Vector2 coords;
    public Collectible(World world, Map map, Vector2 coordinates, float hx, float hy) {
        this.world = world;
        this.map = map;
        this.coords = coordinates;
        createBoxBody(coordinates.x, coordinates.y, hx, hy);
    }

    private void createBoxBody(float x, float y, float hx, float hy) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(x, y);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(hx, hy);

        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = shape;
        fixDef.isSensor = true;


        this.body = world.createBody(bdef);
        body.createFixture(fixDef).setUserData(this);
    }

    public Map getMap() {
        return map;
    }

    public Vector2 getCoords() {
        return coords;
    }

}
