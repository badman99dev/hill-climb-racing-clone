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

public class MainMenuScreen extends ScreenAdapter{
    private Stage stage;
    private Viewport viewport;
    private Skin skin;
    private Table mainTable;
    private UI game;
    private AssetManager assetManager;
    private float accum;

    public MainMenuScreen(AssetManager assetManager, UI game){
        this.game = game;
        this.assetManager = assetManager;
        this.skin = assetManager.get(Assets.SKIN);
        viewport = new ExtendViewport(1080, 720);
        stage = new Stage(viewport);
        mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);
        accum = 0.0f;
    }
    @Override
    public void show(){
        Texture imageTexture = assetManager.get(Assets.MENU_LOGO);
        mainTable.add(addImageLabel(imageTexture, 700, 304, "")).row();

        addButton("Graj").addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                game.setScreen(new MapSelectScreen(assetManager, game));
            }
        });
        addButton("Sterowanie").addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                game.setScreen(new ControlScreen(assetManager, game, false));
            }
        });
        addButton("Wyjscie").addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                Gdx.app.exit();
            }
        });


        Gdx.input.setInputProcessor(stage);
    }

    private TextButton addButton(String name){
        TextButton button = new TextButton(name, skin);
        mainTable.add(button).center().width(500).height(90).padBottom(20);
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
        accum += delta;
        float r = 0.5f * ((float)Math.sin((float)accum) + 1.f);

        Gdx.gl.glClearColor(.2f * r, .1f, .15f,1);
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
