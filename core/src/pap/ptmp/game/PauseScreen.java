package pap.ptmp.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import javax.swing.event.ChangeEvent;

public class PauseScreen extends ScreenAdapter {
    private Stage stage;
    private Viewport viewport;
    private Skin skin;
    private Table mainTable;
    private UI game;
    private AssetManager assetManager;
    private Music music_background;

    public PauseScreen(AssetManager assetManager, UI game){
        this.game = game;
        this.assetManager = assetManager;
        this.skin = assetManager.get(Assets.SKIN);
        this.music_background = game.music_background;
        viewport = new ExtendViewport(1080, 720);
        stage = new Stage(viewport);
        mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);
    }

    @Override
    public void show(){
        final Slider volumeSlider = new Slider(0, 1, 0.1f, false, skin);
        volumeSlider.setValue(music_background.getVolume()); // Set initial value based on game volume
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Update game volume based on the slider value
                float volume = volumeSlider.getValue();
                music_background.setVolume(volume);
            }
        });

        Texture imageTexture = assetManager.get(Assets.PAUSE_IMAGE);
        mainTable.add(addImageLabel(imageTexture, 90, 90, "Pause")).row();

        mainTable.row();
        mainTable.add(new Label("Music volume", skin)).padBottom(0);
        mainTable.row();
        mainTable.add(volumeSlider).fillX().width(500).height(90).padBottom(20);
        mainTable.row();

        addButton("Return").addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                game.setGameScreen();
            }
        });
        addButton("Controls").addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                game.setScreen(new ControlScreen(assetManager, game, true));;
            }
        });
        addButton("Back to menu").addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                game.setMainMenuScreen();
            }
        });

        Gdx.input.setInputProcessor(stage);
    }

    private TextButton addButton(String name){
        TextButton button = new TextButton(name, skin);
        mainTable.add(button).fill().width(500).height(90).padBottom(20);
        mainTable.row();
        return button;
    }

    private Table addImageLabel(Texture imageTexture, int width, int height, String text){
        Table row = new Table();
        Image image = new Image(imageTexture);
        row.add(image).width(width).height(height).padBottom(20);
        Label label = new Label(text, skin);
        label.setFontScale(3);
        row.add(label).padBottom(20);
        return row;
    }

    @Override
    public void render(float delta){
        Gdx.gl.glClearColor(.1f, .15f, .1f,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void hide(){
        mainTable.clear();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
}
