package pap.ptmp.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
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
	private String fpsText, scoreText, highscoreText, fuelLevelText;
	private float highscore, bestposition = 0;
	private int mapID, money, mapWidth, mapHeight;
	private Image coinImg;
	private Image beerImg;
	private Viewport viewport;
	private Music music_background;
	private MyContactListener myContactListener;
	private ArrayList<Body> deletionList;
	private boolean isGameOver;
	private int initialGameOverCount = 180; // 60 - 1 second (60fps)
	private int gameOverCount = initialGameOverCount; // if reaches 0 game is over;
	private AssetManager assetManager;
	private float fontHeight;
	private float halfFontHeight;
	private ShapeRenderer hudRenderer;
	private OrthographicCamera hudCamera;
	private BitmapFont hudFont;

	public PtmPGame(UI game, MapIO.MapData mapData, int carId, AssetManager assetManager, Music music_background){
		this.game = game;
		camera = new OrthographicCamera();
		spriteBatch = new SpriteBatch();
		camera.setToOrtho(false, 1920, 1080);
		renderer = new ShapeRenderer();
		renderer.setProjectionMatrix(camera.combined);
		world = new World(new Vector2(0, mapData.gravity), false);
		this.assetManager = assetManager;
		MapIO tempIO = new MapIO();
		Texture groundTexture = tempIO.getTexture(mapData.id);
		Color skyColor = tempIO.getColor(mapData.id);
		Texture beerImage = assetManager.get(Assets.BEER_FUEL_IMAGE);
		Texture carTexture = game.getVehicleIO().getTexture(carId);
		Texture wheelTexture = assetManager.get(Assets.WHEEL_TEXTURE);
		Texture coinTexture = assetManager.get(Assets.COIN_IMAGE);
		map = new Map(renderer, world, groundTexture, beerImage, coinTexture, skyColor, mapData.seed);
		vehicle = new Vehicle(world, 1000f, 100f, 1.24f, 0.2f, 0.1f,
				0.15f, 0.235f, 0.2f, 40f, 30f, 3f, 0.005f, carTexture, wheelTexture);
		debugRenderer = new Box2DDebugRenderer();
		font = new BitmapFont();
		font.getData().setScale(3f);
		fontHeight = font.getCapHeight();
		halfFontHeight = fontHeight * 0.5f;
		hudRenderer = new ShapeRenderer();
		hudCamera = new OrthographicCamera();
		hudFont = new BitmapFont();
		hudFont.getData().setScale(2f);
		highscore = mapData.high_score;
		mapID = mapData.id;
		viewport = new ExtendViewport(1080, 720);
		coinImg = new Image(coinTexture);
		coinImg.setWidth(1.5f * fontHeight);
		coinImg.setHeight(1.5f * fontHeight);
		mapWidth = Gdx.graphics.getWidth();
		mapHeight = Gdx.graphics.getHeight();
		coinImg.setPosition(mapWidth - 155, mapHeight - fontHeight * 5.8f);
		isGameOver = false;
		gameOverCount = initialGameOverCount;

		beerImg = new Image((Texture)assetManager.get(Assets.PIWO_IMAGE));
		beerImg.setWidth(1.5f * 14f/32f * fontHeight);
		beerImg.setHeight(1.5f * fontHeight);
		beerImg.setPosition(mapWidth - 140, mapHeight - fontHeight * 4.3f);

		money = loadMoney((Gdx.files.internal("money.txt")));

		this.music_background = game.music_background;
		music_background.play();
		music_background.setLooping(true);

		myContactListener = new MyContactListener(this);
		world.setContactListener(myContactListener);
		deletionList = myContactListener.getDeletionList();
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(vehicle);
		music_background.play();
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
		vehicle.render(camera);

		Color fuelBarCol;
		float fuelPerc = vehicle.getFuelLevel() / 100.0f;
		if (fuelPerc >= 0.5f)
			fuelBarCol = new Color(2.0f * (1.0f - fuelPerc), 1.0f, 0.0f, 1.0f);
		else
			fuelBarCol = new Color(1.0f, 2.0f * fuelPerc, 0.0f, 1.0f);
		renderer.setColor(fuelBarCol);
		renderer.begin(ShapeType.Filled);
		float fuelBarX = camera.position.x + 0.5f * camera.zoom * camera.viewportWidth - 1.0f;
		float fuelBarY = camera.position.y + 0.55f * camera.zoom * camera.viewportHeight - 0.7f;
		float fuelBarW = 0.9f;
		float fuelBarH = 0.3f;
		renderer.rect(fuelBarX, fuelBarY, fuelPerc * fuelBarW, fuelBarH);
		renderer.setColor(Color.BLACK);
		renderer.rect(fuelBarX, fuelBarY, fuelBarW, 0.01f);
		renderer.rect(fuelBarX, fuelBarY+fuelBarH, fuelBarW, 0.01f);
		renderer.rect(fuelBarX, fuelBarY, 0.01f, fuelBarH);
		renderer.rect(fuelBarX+fuelBarW, fuelBarY, 0.01f, fuelBarH);
		renderer.end();

		bestposition = Math.max(bestposition, vehicle.getPosition().x - 1000f);
		spriteBatch.begin();
		font.setColor(Color.BLUE);
		fpsText = "FPS: " + Gdx.graphics.getFramesPerSecond();
		scoreText = "Score: " + Math.round(bestposition);
		highscoreText = "Highscore: " + Math.round(highscore);
		String moneyText = Integer.toString(money);

		coinImg.draw(spriteBatch, 1);
		beerImg.draw(spriteBatch, 1);
		font.draw(spriteBatch, moneyText, mapWidth - 100f, mapHeight - 4.5f * fontHeight);
		font.draw(spriteBatch, escapeText, 20f, mapHeight - fontHeight * 0.8f);
		font.draw(spriteBatch, fpsText, 20f, mapHeight - fontHeight * 2f);
		font.draw(spriteBatch, scoreText, 20f, mapHeight - fontHeight * 3.2f);
		font.draw(spriteBatch, highscoreText, 20f, mapHeight - fontHeight * 4.4f);
		font.setColor(Color.WHITE);
		font.draw(spriteBatch, Integer.toString((int)vehicle.getFuelLevel()), mapWidth - 100f, mapHeight - 3 * fontHeight);
		System.out.println(fuelBarX);
		spriteBatch.end();

		drawTouchControls();

		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
			if(bestposition > highscore){
				MapIO.updateHighScore(mapID, bestposition);
			}
			game.setPauseScreen();
		}
//		debugRenderer.render(world, camera.combined);   //debug renderer works only if map segments <= 1000

		isGameOver = updateIsGameOver();
		if (isGameOver){
			saveMoney(Gdx.files.local("money.txt"), money);
			if(bestposition > highscore){
				MapIO.updateHighScore(mapID, bestposition);
				highscore = bestposition;
			}
			game.setScreen(new GameOverScreen(this.assetManager, game, bestposition, highscore));
		}
	}

	public void hide () {
		music_background.pause();
	}

	public void addMoney(int money) {
		this.money += money;
	}

	public int loadMoney(FileHandle fileHandle){
		int money = 0;
		if (fileHandle.exists()) {
            String content = fileHandle.readString();
            try {
                money = Integer.parseInt(content);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
		return money;
	}

	public void saveMoney(FileHandle fileHandle, int money){
		String moneyText = Integer.toString(money);
		fileHandle.writeString(moneyText, false);
	}

	@Override
	public void dispose () {
		spriteBatch.dispose();
		font.dispose();
		renderer.dispose();
		hudRenderer.dispose();
		hudFont.dispose();
	}

	private void drawTouchControls() {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		hudCamera.setToOrtho(false, w, h);
		hudCamera.update();

		float r = Math.min(w, h) * 0.10f;
		float marginX = w * 0.07f;
		float marginY = h * 0.06f;
		float lx = marginX + r;
		float rx = w - marginX - r;
		float cy = marginY + r;

		hudRenderer.setProjectionMatrix(hudCamera.combined);
		hudRenderer.begin(ShapeType.Filled);
		hudRenderer.setColor(vehicle.isBackwardPressed() ? 1f : 0.28f, 0.18f, 0.18f, 1f);
		hudRenderer.circle(lx, cy, r, 48);
		hudRenderer.setColor(vehicle.isForwardPressed() ? 1f : 0.28f, 0.85f, 0.18f, 1f);
		hudRenderer.circle(rx, cy, r, 48);
		hudRenderer.end();

		spriteBatch.setProjectionMatrix(hudCamera.combined);
		spriteBatch.begin();
		hudFont.setColor(1f, 1f, 1f, 0.85f);
		GlyphLayout gl = new GlyphLayout(hudFont, "BRAKE");
		hudFont.draw(spriteBatch, gl, lx - gl.width / 2f, cy + gl.height / 2f);
		gl.setText(hudFont, "GAS");
		hudFont.draw(spriteBatch, gl, rx - gl.width / 2f, cy + gl.height / 2f);
		spriteBatch.end();
	}

	@Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

	private boolean updateIsGameOver() {
		float fuelLevel = vehicle.getFuelLevel();
		int currentRoofCollisions = myContactListener.getCurrentRoofCollisions();
		//System.out.println(currentRoofCollisions);
		if(fuelLevel == 0 || currentRoofCollisions > 0) {
			if(gameOverCount == 0) {
				return true;
			}
			gameOverCount -= 1;
		} else {
			gameOverCount = initialGameOverCount;
		}
		return false;
	}
}