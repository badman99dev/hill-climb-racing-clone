package pap.ptmp.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class UI extends Game{
    private MainMenuScreen mainMenu;
    private PtmPGame gameScreen;
    private Screen pauseScreen;
    private SpriteBatch batch;
    private BitmapFont font;
    private AssetManager assetManager;
    private Cars cars;
    @Override
    public void create(){
        cars = new Cars();
        Assets assets = new Assets();
        assets.loadAll();
        assetManager = assets.getAssetManager();
        assetManager.finishLoading();
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
        this.gameScreen = new PtmPGame(this, mapData, carId, assetManager);
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

    public Cars getCars() {
        return cars;
    }
    @Override
    public void resize(int width, int height){
        getScreen().resize(width, height);
    }
}
