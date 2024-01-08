package pap.ptmp.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;

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
	private String fpsText, scoreText, highscoreText;
	private float highscore, bestposition = 0;
	private int mapID;

	public PtmPGame(UI game, MapIO.MapData mapData){
		this.game = game;
		camera = new OrthographicCamera();
		spriteBatch = new SpriteBatch();
		camera.setToOrtho(false, 1920, 1080);
		renderer = new ShapeRenderer();
		renderer.setProjectionMatrix(camera.combined);
		world = new World(new Vector2(0, mapData.gravity), false);

		MapIO tempIO = new MapIO();
		Texture groundTexture = tempIO.getTexture(mapData.id);
		Color skyColor = tempIO.getColor(mapData.id);
		map = new Map(renderer, world, groundTexture, skyColor, mapData.seed);
		vehicle = new Vehicle(world, 1000f, 100f, 1f, 0.2f, 0.1f,
				0.2f, 0.1f, 40f, 30f);
		debugRenderer = new Box2DDebugRenderer();
		font = new BitmapFont();
		highscore = mapData.high_score;
		mapID = mapData.id;
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

		camera.position.set(vehicle.getPosition().x, vehicle.getPosition().y, 0);
		camera.zoom = 0.005f;
		camera.update();
		renderer.setProjectionMatrix(camera.combined);

		map.render(camera);
		vehicle.render(renderer);

		bestposition = Math.max(bestposition, vehicle.getPosition().x - 1000f);
		spriteBatch.begin();
		font.setColor(Color.BLUE);
		fpsText = "FPS: " + Gdx.graphics.getFramesPerSecond();
		scoreText = "Score: " + Math.round(bestposition);
		highscoreText = "Highscore: " + Math.round(highscore);
		font.draw(spriteBatch, escapeText, 20, Gdx.graphics.getHeight() - 15);
		font.draw(spriteBatch, fpsText, 20, Gdx.graphics.getHeight() - 30);
		font.draw(spriteBatch, scoreText, 20, Gdx.graphics.getHeight() - 45);
		font.draw(spriteBatch, highscoreText, 20, Gdx.graphics.getHeight() - 60);
		spriteBatch.end();

		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			if(bestposition > highscore){
				MapIO.updateHighScore(mapID, bestposition);
			}
			game.setPauseScreen();
		}
//		debugRenderer.render(world, camera.combined);   //debug renderer works only if map segments <= 1000
	}

	@Override
	public void dispose () {
		spriteBatch.dispose();
		font.dispose();
		renderer.dispose();
	}
}