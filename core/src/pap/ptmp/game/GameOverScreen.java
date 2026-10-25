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

public class GameOverScreen extends ScreenAdapter {
    private Stage stage;
    private Viewport viewport;
    private Skin skin;
    private Table mainTable;
    private UI game;
    private AssetManager assetManager;
    private float score, highscore;
    private int coinsEarned;

    public GameOverScreen(AssetManager assetManager, UI game, float score, float highscore, int coinsEarned){
        this.game = game;
        this.assetManager = assetManager;
        this.skin = assetManager.get(Assets.SKIN);
        viewport = new ExtendViewport(1080, 720);
        stage = new Stage(viewport);
        mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);
        this.score = score;
        this.highscore = highscore;
        this.coinsEarned = coinsEarned;
    }

    @Override
    public void show(){
        Texture imageTexture = assetManager.get(Assets.SKULL_IMAGE);
        if (score > highscore){
            mainTable.add(addLabel("NEW RECORD")).row();
        }
        mainTable.add(addImageLabel(imageTexture, 90, 90, "Game Over")).row();
        mainTable.add(addLabel("Distance: " + Integer.toString(Math.round(score)) + " m")).row();
        mainTable.add(addLabel("Best: " + Integer.toString(Math.round(highscore)) + " m")).row();
        mainTable.add(addLabel("Coins earned: +" + coinsEarned)).row();
        mainTable.add(addLabel("Total coins: " + SaveData.get().money)).row();


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

    private Table addLabel(String text){
        Table row = new Table();
        Label label = new Label(text, skin);
        label.setFontScale(2);
        row.add(label).padBottom(20);
        return row;
    }

    @Override
    public void render(float delta){
        Gdx.gl.glClearColor(.30f, .30f, .30f,1);
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
