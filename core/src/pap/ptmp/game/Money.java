package pap.ptmp.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Money extends Collectible {
    public Money(World world, Map map, Vector2 coordinates) {
        super(world, map, coordinates, 0.13f, 0.13f);
    }
}
