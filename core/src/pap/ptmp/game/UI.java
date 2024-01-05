package pap.ptmp.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class UI extends Game{
    private MainMenuScreen mainMenu;
    private PtmPGame gameScreen;
    private Screen pauseScreen;
    private SpriteBatch batch;
    private BitmapFont font;

    @Override
    public void create(){
        Assets assets = new Assets();
        assets.loadAll();
        assets.getAssetManager().finishLoading();
        batch = new SpriteBatch();
        font = new BitmapFont();
        //gameScreen = new PtmPGame(this);
        pauseScreen = new PauseScreen(assets.getAssetManager(), this);
        mainMenu = new MainMenuScreen(assets.getAssetManager(), this);
        setScreen(mainMenu);
    }

    public void setMainMenuScreen(){
        setScreen(mainMenu);
    }

    public void setGameScreen(){
        setScreen(gameScreen);
    }

    public void resetGame(MapIO.MapData mapData){
        this.gameScreen = new PtmPGame(this, mapData);
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
}
