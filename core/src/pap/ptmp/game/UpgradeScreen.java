package pap.ptmp.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class UpgradeScreen extends ScreenAdapter {
    private final Stage stage;
    private final Viewport viewport;
    private final Skin skin;
    private final Table mainTable;
    private final UI game;
    private final SaveData save;
    private float accum;

    public UpgradeScreen(AssetManager assetManager, UI game) {
        this.game = game;
        this.save = SaveData.get();
        this.skin = assetManager.get(Assets.SKIN);
        viewport = new ExtendViewport(1080, 720);
        stage = new Stage(viewport);
        mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);
        accum = 0f;
    }

    @Override
    public void show() {
        rebuild();
        Gdx.input.setInputProcessor(stage);
    }

    private void rebuild() {
        mainTable.clear();

        Label title = new Label("GARAGE", skin);
        title.setFontScale(3);
        mainTable.add(title).colspan(2).padBottom(15).row();

        Label coins = new Label("Coins: " + save.money, skin);
        coins.setFontScale(2);
        coins.setColor(1f, 0.9f, 0.3f, 1f);
        mainTable.add(coins).colspan(2).padBottom(25).row();

        addUpgradeRow("Engine", SaveData.ENGINE);
        addUpgradeRow("Tyres", SaveData.TYRES);
        addUpgradeRow("Suspension", SaveData.SUSPENSION);

        TextButton back = new TextButton("Back", skin);
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setMainMenuScreen();
            }
        });
        mainTable.add(back).colspan(2).width(500).height(90).padTop(25).row();
    }

    private void addUpgradeRow(String name, final int which) {
        int level = save.getLevel(which);

        Label label = new Label(name + "   Lv " + level + " / " + SaveData.MAX_LEVEL, skin);
        label.setFontScale(2);
        mainTable.add(label).width(420).left().padBottom(12);

        if (save.isMax(which)) {
            Label max = new Label("MAX", skin);
            max.setFontScale(2);
            max.setColor(0.4f, 1f, 0.4f, 1f);
            mainTable.add(max).width(300).padBottom(12).row();
            return;
        }

        int cost = SaveData.upgradeCost(level);
        final TextButton buy = new TextButton("Upgrade  -  " + cost, skin);
        buy.setColor(save.money >= cost ? 1f : 0.6f, 1f, save.money >= cost ? 1f : 0.6f, 1f);
        buy.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (save.tryBuy(which)) {
                    rebuild();
                }
            }
        });
        mainTable.add(buy).width(300).height(76).padBottom(12).row();
    }

    @Override
    public void render(float delta) {
        accum += delta;
        float r = 0.5f * ((float) Math.sin(accum) + 1f);
        Gdx.gl.glClearColor(0.10f * r, 0.13f, 0.18f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void hide() {
        mainTable.clear();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
}
