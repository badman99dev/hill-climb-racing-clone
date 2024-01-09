package pap.ptmp.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Beer extends Collectible  {
    public Beer(World world, Map map, Vector2 coordinates) {
        super(world, map, coordinates, 0.2f, 0.2f);
    }
}
