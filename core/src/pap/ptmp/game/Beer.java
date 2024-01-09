package pap.ptmp.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
public class Beer {
    public Body body;
    public String id;
    private World world;
    private float x, y;
    private Map map;
    private Vector2 coords;
    public Beer(World world, Map map, Vector2 coordinates) {
        this.world = world;
        this.map = map;
        this.coords = coordinates;
        createBoxBody(coordinates.x, coordinates.y);
    }

    private void createBoxBody(float x, float y) {
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

    public void removeRender() {
        map.removeBeer(coords);
    }
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
