package pap.ptmp.game;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
public class Ground {
    public Body body;
    private World world;
    public Ground (World world, Body body, FixtureDef fixDef) {
        this.world = world;
        this.body = body;
        createChainBody(fixDef);
    }

    private void createChainBody(FixtureDef fixDef) {
        body.createFixture(fixDef).setUserData(this);
    }
}
