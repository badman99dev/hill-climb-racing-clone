package pap.ptmp.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
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
		world = new World(new Vector2(0, -98f), false);
		MapIO mapData = new MapIO("../core/src/maps.txt");
		mapData.loadMapData(2);
		map = new Map(renderer, world, mapData.seed);
		vehicle = new Vehicle(world, 300f, 300f, 100f, 20f, 10f,
				20f, 10f, 20f, 15f);
		debugRenderer = new Box2DDebugRenderer();

		Gdx.input.setInputProcessor(vehicle);
	}

	@Override
	public void render () {
		ScreenUtils.clear(1, 1, 1, 1);
		world.step(1/60f, 6, 2);

		camera.position.set(vehicle.getPosition().x, vehicle.getPosition().y, 0);
		camera.zoom = 0.5f;
		camera.update();
		renderer.setProjectionMatrix(camera.combined);

		map.render();
		vehicle.render(renderer);
//		debugRenderer.render(world, camera.combined);   //debug renderer works only if map segments <= 1000
	}

	@Override
	public void dispose () {
		renderer.dispose();
	}
}
