package pap.ptmp.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
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
	private static final float TIME_STEP = 1f / 60f;
	private static final int MAX_STEPS = 5;
	private static final float BASE_ZOOM = 0.005f;

	private Map map;
	private ShapeRenderer renderer;
	private OrthographicCamera camera;
	private World world;
	private Vehicle vehicle;
	private Box2DDebugRenderer debugRenderer;
	private UI game;
	private final String escapeText = "Pause";
	private SpriteBatch spriteBatch;
	private BitmapFont font;
	private float highscore, bestposition = 0;
	private int mapID, mapWidth, mapHeight;
	private Image coinImg;
	private Image beerImg;
	private Viewport viewport;
	private Music music_background;
	private MyContactListener myContactListener;
	private ArrayList<Body> deletionList;
	private boolean isGameOver;
	private int initialGameOverCount = 180;
	private int gameOverCount = initialGameOverCount;
	private AssetManager assetManager;
	private float fontHeight;

	private ShapeRenderer hudRenderer;
	private OrthographicCamera hudCamera;
	private BitmapFont hudFont;

	private SaveData save;
	private DustParticles dust;
	private Sound engineSound;
	private long engineSoundId = -1L;

	private float accumulator;
	private float camZoom = BASE_ZOOM;
	private final Vector2 camTarget = new Vector2();
	private float shakeTime, shakeMag;

	private float airTime, angleAccum, lastAngle, fallSpeed;
	private boolean wasAirborne;
	private int coinsThisRun;
	private float popupTimer;
	private String popupText = "";

	public PtmPGame(UI game, MapIO.MapData mapData, int carId, AssetManager assetManager, Music music_background){
		this.game = game;
		this.assetManager = assetManager;
		this.save = SaveData.get();
		camera = new OrthographicCamera();
		spriteBatch = new SpriteBatch();
		camera.setToOrtho(false, 1920, 1080);
		renderer = new ShapeRenderer();
		renderer.setProjectionMatrix(camera.combined);
		world = new World(new Vector2(0, mapData.gravity), false);

		MapIO tempIO = new MapIO();
		Texture groundTexture = tempIO.getTexture(mapData.id);
		Color skyColor = tempIO.getColor(mapData.id);
		Texture beerImage = assetManager.get(Assets.BEER_FUEL_IMAGE);
		Texture carTexture = game.getVehicleIO().getTexture(carId);
		Texture wheelTexture = assetManager.get(Assets.WHEEL_TEXTURE);
		Texture coinTexture = assetManager.get(Assets.COIN_IMAGE);
		map = new Map(renderer, world, groundTexture, beerImage, coinTexture, skyColor, mapData.seed);

		VehicleStats stats = VehicleStats.fromLevels(save.engineLevel, save.tyresLevel, save.suspensionLevel);
		vehicle = new Vehicle(world, 1000f, 100f, stats, carTexture, wheelTexture);
		lastAngle = vehicle.getBody().getAngle();

		camera.position.set(vehicle.getPosition().x, vehicle.getPosition().y, 0);
		camera.zoom = BASE_ZOOM;
		camera.update();

		dust = new DustParticles();
		try {
			engineSound = Gdx.audio.newSound(Gdx.files.internal("Drive.mp3"));
			engineSoundId = engineSound.loop(0f);
		} catch (Exception e) {
			Gdx.app.error("PtmPGame", "Engine sound unavailable: " + e.getMessage());
		}

		debugRenderer = new Box2DDebugRenderer();
		font = new BitmapFont();
		font.getData().setScale(3f);
		fontHeight = font.getCapHeight() * 3f;
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
		beerImg = new Image(beerImage);
		beerImg.setWidth(1.5f * fontHeight);
		beerImg.setHeight(1.5f * fontHeight);
		isGameOver = false;
		gameOverCount = initialGameOverCount;

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
		delta = Math.min(delta, 0.25f);
		accumulator += delta;

		int steps = 0;
		while (accumulator >= TIME_STEP && steps < MAX_STEPS) {
			step(TIME_STEP);
			accumulator -= TIME_STEP;
			steps++;
		}
		if (steps >= MAX_STEPS) {
			accumulator = 0f;
		}

		dust.update(delta);
		updateCamera(delta);
		updateEngineSound();

		ScreenUtils.clear(1, 1, 1, 1);
		renderer.setProjectionMatrix(camera.combined);
		map.render(camera);
		vehicle.render(camera);
		dust.render(renderer);

		drawHud(delta);
		drawTouchControls();

		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
			save.save();
			if (bestposition > highscore) {
				MapIO.updateHighScore(mapID, bestposition);
			}
			game.setPauseScreen();
			return;
		}

		if (isGameOver) {
			save.save();
			if (bestposition > highscore) {
				MapIO.updateHighScore(mapID, bestposition);
				highscore = bestposition;
			}
			if (engineSoundId >= 0) engineSound.stop(engineSoundId);
			Gdx.input.vibrate(180);
			game.setScreen(new GameOverScreen(this.assetManager, game, bestposition, highscore, coinsThisRun));
		}
	}

	// Fixed-timestep simulation: physics runs at 60Hz regardless of render framerate.
	private void step(float dt) {
		if (isGameOver) return;
		vehicle.update();
		world.step(dt, 6, 2);

		if (!deletionList.isEmpty()) {
			for (Body body : deletionList) {
				world.destroyBody(body);
			}
			deletionList.clear();
		}

		updateAirAndBonuses(dt);
		emitDust();

		if (updateIsGameOver()) {
			isGameOver = true;
		}
	}

	private void updateAirAndBonuses(float dt) {
		boolean airborne = vehicle.isAirborne();
		float angle = vehicle.getBody().getAngle();

		if (airborne) {
			float d = angle - lastAngle;
			while (d > MathUtils.PI) d -= MathUtils.PI2;
			while (d < -MathUtils.PI) d += MathUtils.PI2;
			angleAccum += d;
			airTime += dt;

			float vy = vehicle.getBody().getLinearVelocity().y;
			if (vy < fallSpeed) fallSpeed = vy;
		} else {
			if (wasAirborne && airTime > 0.35f) {
				int flips = (int) (Math.abs(angleAccum) / MathUtils.PI2);
				int bonus = (int) (airTime * 8f) + flips * 50;
				if (bonus > 0) {
					addMoney(bonus);
					if (flips > 0) {
						showPopup(flips + (flips == 1 ? " FLIP  +" : " FLIPS  +") + bonus);
					} else {
						showPopup("AIR TIME  +" + bonus);
					}
				}
				if (fallSpeed < -9f) {
					addShake(Math.min(0.35f, -fallSpeed * 0.02f));
					Gdx.input.vibrate(60);
				}
			}
			airTime = 0f;
			angleAccum = 0f;
			fallSpeed = 0f;
		}
		lastAngle = angle;
		wasAirborne = airborne;
	}

	private void emitDust() {
		if (vehicle.isAirborne()) return;
		float vx = vehicle.getBody().getLinearVelocity().x;
		if (Math.abs(vx) < 5f) return;
		Vector2 lw = vehicle.getLeftWheelPosition();
		dust.emit(lw.x, lw.y - 0.08f, vx);
		if (MathUtils.randomBoolean(0.6f)) {
			Vector2 rw = vehicle.getRightWheelPosition();
			dust.emit(rw.x, rw.y - 0.08f, vx);
		}
	}

	private void updateCamera(float delta) {
		Vector2 pos = vehicle.getPosition();
		float vx = vehicle.getBody().getLinearVelocity().x;
		float lookAhead = MathUtils.clamp(vx * 0.35f, -3f, 3f);
		camTarget.set(pos.x + lookAhead, pos.y + 1.1f);

		float lerp = 1f - (float) Math.pow(0.0018f, delta);
		camera.position.x += (camTarget.x - camera.position.x) * lerp;
		camera.position.y += (camTarget.y - camera.position.y) * lerp;

		float speedRatio = MathUtils.clamp(Math.abs(vx) / 55f, 0f, 1f);
		float zoomTarget = BASE_ZOOM * (1f + speedRatio * 0.22f);
		camZoom += (zoomTarget - camZoom) * lerp;
		camera.zoom = camZoom;

		if (shakeTime > 0f) {
			shakeTime -= delta;
			float k = Math.max(0f, shakeTime / 0.25f) * shakeMag;
			camera.position.x += MathUtils.random(-k, k);
			camera.position.y += MathUtils.random(-k, k);
			if (shakeTime <= 0f) shakeMag = 0f;
		}

		camera.update();
	}

	private void addShake(float magnitude) {
		if (magnitude > shakeMag) shakeMag = magnitude;
		shakeTime = 0.25f;
	}

	private void updateEngineSound() {
		if (engineSoundId < 0) return;
		float vx = Math.abs(vehicle.getBody().getLinearVelocity().x);
		float ratio = MathUtils.clamp(vx / 45f, 0f, 1f);
		boolean throttle = vehicle.isForwardPressed() || vehicle.isBackwardPressed();
		float pitch = 0.75f + ratio * 0.8f + (throttle ? 0.05f : 0f);
		float volume = (throttle ? 0.32f : 0.10f) * (0.4f + 0.6f * ratio);
		engineSound.setPitch(engineSoundId, pitch);
		engineSound.setVolume(engineSoundId, volume);
	}

	private void showPopup(String text) {
		popupText = text;
		popupTimer = 1.8f;
	}

	private void drawHud(float delta) {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		mapWidth = (int) w;
		mapHeight = (int) h;
		hudCamera.setToOrtho(false, w, h);
		hudCamera.update();

		bestposition = Math.max(bestposition, vehicle.getPosition().x - 1000f);

		float fuelPerc = MathUtils.clamp(vehicle.getFuelLevel() / 100.0f, 0f, 1f);
		Color fuelCol = fuelPerc >= 0.5f
				? new Color(2.0f * (1.0f - fuelPerc), 1.0f, 0.0f, 1.0f)
				: new Color(1.0f, 2.0f * fuelPerc, 0.0f, 1.0f);

		float barW = w * 0.28f;
		float barH = h * 0.035f;
		float barX = w - barW - w * 0.03f;
		float barY = h - barH - h * 0.06f;

		hudRenderer.setProjectionMatrix(hudCamera.combined);
		hudRenderer.begin(ShapeType.Filled);
		hudRenderer.setColor(0f, 0f, 0f, 0.45f);
		hudRenderer.rect(barX - 3, barY - 3, barW + 6, barH + 6);
		hudRenderer.setColor(fuelCol);
		hudRenderer.rect(barX, barY, barW * fuelPerc, barH);
		hudRenderer.end();

		float speedKmh = Math.abs(vehicle.getBody().getLinearVelocity().x) * 3.6f;

		spriteBatch.setProjectionMatrix(hudCamera.combined);
		spriteBatch.begin();
		font.setColor(Color.WHITE);
		font.draw(spriteBatch, "Distance " + Math.round(bestposition) + " m", w * 0.02f, h - fontHeight * 1.3f);
		font.draw(spriteBatch, "Best " + Math.round(highscore) + " m", w * 0.02f, h - fontHeight * 2.65f);
		font.draw(spriteBatch, Math.round(speedKmh) + " km/h", w * 0.02f, h - fontHeight * 4.0f);

		coinImg.setPosition(w - fontHeight * 4.8f, h - fontHeight * 2.4f);
		coinImg.draw(spriteBatch, 1f);
		font.draw(spriteBatch, Integer.toString(save.money), w - fontHeight * 3.1f, h - fontHeight * 1.5f);

		beerImg.setPosition(barX - fontHeight * 1.5f, barY - fontHeight * 0.35f);
		beerImg.draw(spriteBatch, 1f);

		if (popupTimer > 0f) {
			font.setColor(1f, 0.92f, 0.25f, MathUtils.clamp(popupTimer, 0f, 1f));
			GlyphLayout gl = new GlyphLayout(font, popupText);
			font.draw(spriteBatch, gl, (w - gl.width) / 2f, h * 0.8f);
			popupTimer -= delta;
		} else {
			font.setColor(Color.WHITE);
		}

		if (!vehicle.canMove()) {
			GlyphLayout gl = new GlyphLayout(font, "OUT OF FUEL!");
			font.setColor(1f, 0.3f, 0.3f, 1f);
			font.draw(spriteBatch, gl, (w - gl.width) / 2f, h * 0.6f);
		}
		spriteBatch.end();
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

	public void hide () {
		music_background.pause();
		if (engineSoundId >= 0) {
			engineSound.stop(engineSoundId);
		}
	}

	public void addMoney(int amount) {
		save.addMoney(amount);
		coinsThisRun += amount;
	}

	@Override
	public void dispose () {
		spriteBatch.dispose();
		font.dispose();
		renderer.dispose();
		hudRenderer.dispose();
		hudFont.dispose();
		if (engineSound != null) engineSound.dispose();
	}

	@Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

	private boolean updateIsGameOver() {
		float fuelLevel = vehicle.getFuelLevel();
		int currentRoofCollisions = myContactListener.getCurrentRoofCollisions();
		if (fuelLevel == 0 || currentRoofCollisions > 0) {
			if (gameOverCount == 0) {
				return true;
			}
			gameOverCount -= 1;
		} else {
			gameOverCount = initialGameOverCount;
		}
		return false;
	}
}
