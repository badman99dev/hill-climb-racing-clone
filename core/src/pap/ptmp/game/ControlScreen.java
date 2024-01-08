package pap.ptmp.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class ControlScreen extends ScreenAdapter {
    private Stage stage;
    private Viewport viewport;
    private Skin skin;
    private Table mainTable;
    private UI game;
    private boolean backToGame;

    public ControlScreen(AssetManager assetManager, final UI game, boolean backToGame){
        this.game = game;
        this.skin = assetManager.get(Assets.SKIN);
        this.backToGame = backToGame;
        viewport = new ExtendViewport(1080, 720);
        stage = new Stage(viewport);
        mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        Texture imageTexture = assetManager.get(Assets.SETTINGS_IMAGE);
        mainTable.add(addImageLabel(imageTexture, 90, 90, "Ustawienia")).row();
        imageTexture = assetManager.get(Assets.W_IMAGE);
        mainTable.add(addImageLabel(imageTexture, 90, 90, " - jazda na przod")).row();
        imageTexture = assetManager.get(Assets.S_IMAGE);
        mainTable.add(addImageLabel(imageTexture, 90, 90, " - jazda wstecz")).row();
        imageTexture = assetManager.get(Assets.SPACE_IMAGE);
        mainTable.add(addImageLabel(imageTexture, 270, 90, " - hamulec")).row();
    }

    @Override
    public void show(){
        addButton("Powrot").addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                if (backToGame) game.setPauseScreen();
                else game.setMainMenuScreen();
            }
        });

        Gdx.input.setInputProcessor(stage);
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

    private TextButton addButton(String name){
        TextButton button = new TextButton(name, skin);
        mainTable.add(button).center().width(500).height(90).padBottom(20);
        mainTable.row();
        return button;
    }

    @Override
    public void render(float delta){
        Gdx.gl.glClearColor(.15f, .1f, .1f,1);
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

