package pap.ptmp.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class UI extends Game{
    private MainMenuScreen mainMenu;
    private PtmPGame gameScreen;
    private Screen pauseScreen;
    private SpriteBatch batch;
    private BitmapFont font;
    private AssetManager assetManager;
    private VehicleIO vehicleIO;
    public Music music_background;
    @Override
    public void create(){
        vehicleIO = new VehicleIO();
        Assets assets = new Assets();
        assets.loadAll();
        assetManager = assets.getAssetManager();
        assetManager.finishLoading();
        music_background = Gdx.audio.newMusic(Gdx.files.internal("Nightcall.ogg"));
        batch = new SpriteBatch();
        font = new BitmapFont();
        //gameScreen = new PtmPGame(this);
        pauseScreen = new PauseScreen(assetManager, this);
        mainMenu = new MainMenuScreen(assetManager, this);
        setScreen(mainMenu);
    }

    public void setMainMenuScreen(){
        setScreen(mainMenu);
    }

    public void setGameScreen(){
        setScreen(gameScreen);
    }

    public void resetGame(MapIO.MapData mapData, int carId){
        this.gameScreen = new PtmPGame(this, mapData, carId, assetManager, music_background);
        this.setGameScreen();
    }

    public void setPauseScreen(){
        setScreen(pauseScreen);
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public BitmapFont getFont() {
        return font;
    }

    public VehicleIO getVehicleIO() {
        return vehicleIO;
    }
    @Override
    public void resize(int width, int height){
        getScreen().resize(width, height);
    }
}
