package pap.ptmp.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;

public class PtmPGame extends ApplicationAdapter {
	private Map map;
	private ShapeRenderer renderer;
	private OrthographicCamera camera;
	private World world;
	private Vehicle vehicle;
	private Box2DDebugRenderer debugRenderer;

	@Override
	public void create () {
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1920, 1080);
		renderer = new ShapeRenderer();
		renderer.setProjectionMatrix(camera.combined);
		world = new World(new Vector2(0, -9.8f), false);
		map = new Map(renderer, world);
		vehicle = new Vehicle(world, 300f, 300f, 100f, 20f, 10f, 20f, 10f);
		debugRenderer = new Box2DDebugRenderer();

	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 1, 1, 1);
		world.step(1/60f, 6, 2);
		camera.update();
		map.render();
		vehicle.render(renderer);
		debugRenderer.render(world, camera.combined);
	}

	@Override
	public void dispose () {
		renderer.dispose();
	}
}
