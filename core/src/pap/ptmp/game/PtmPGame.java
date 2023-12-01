package pap.ptmp.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class PtmPGame extends ApplicationAdapter {
	Map map;
	ShapeRenderer renderer;
	
	@Override
	public void create () {
		renderer = new ShapeRenderer();
		map = new Map(renderer);
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 1, 1, 1);
		map.render();
	}

	@Override
	public void dispose () {
		renderer.dispose();
	}
}
