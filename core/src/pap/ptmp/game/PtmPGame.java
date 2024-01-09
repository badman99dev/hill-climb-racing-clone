package pap.ptmp.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;

public class PtmPGame extends ScreenAdapter{
	private Map map;
	private ShapeRenderer renderer;
	private OrthographicCamera camera;
	private World world;
	private Vehicle vehicle;
	private Box2DDebugRenderer debugRenderer;
	private UI game;
	private final String escapeText = "Esc-Menu";
	private SpriteBatch spriteBatch;
	private BitmapFont font;
	private BeerContactListener beerContactListener;
	private ArrayList<Body> deletionList;

	public PtmPGame(UI game, MapIO.MapData mapData){
		this.game = game;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1920, 1080);
		renderer = new ShapeRenderer();
		renderer.setProjectionMatrix(camera.combined);
		world = new World(new Vector2(0, mapData.gravity), false);
		vehicle = new Vehicle(world, 1000f, 100f, 1f, 0.2f, 0.1f,
				0.2f, 0.1f, 40f, 40f);
		map = new Map(renderer, world, mapData.seed, vehicle);
		debugRenderer = new Box2DDebugRenderer();
		spriteBatch = new SpriteBatch();
		font = new BitmapFont();

		beerContactListener = new BeerContactListener();
		world.setContactListener(beerContactListener);
		deletionList = beerContactListener.getDeletionList();
        VehicleCollisionBox vcb = new VehicleCollisionBox(world, vehicle);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(vehicle);
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(1, 1, 1, 1);
		vehicle.update();
		world.step(1/60f, 6, 2);

		if(!deletionList.isEmpty()) {
			for (Body body : deletionList) {
				world.destroyBody(body);
			}
			deletionList.clear();
		}

		camera.position.set(vehicle.getPosition().x, vehicle.getPosition().y, 0);
		camera.zoom = 0.005f;
		camera.update();
		renderer.setProjectionMatrix(camera.combined);

		map.render(camera);
		vehicle.render(renderer);

		spriteBatch.begin();
		font.setColor(Color.BLUE);
		String fpsText = "FPS: " + Gdx.graphics.getFramesPerSecond();
		font.draw(spriteBatch, escapeText, 20, Gdx.graphics.getHeight() - 15);
		font.draw(spriteBatch, fpsText, 20, Gdx.graphics.getHeight() - 30);
		spriteBatch.end();

		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			game.setPauseScreen();
		}
		debugRenderer.render(world, camera.combined);   //debug renderer works only if map segments <= 1000
	}

	@Override
	public void dispose () {
		spriteBatch.dispose();
		font.dispose();
		renderer.dispose();
	}
}