package pap.ptmp.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

public class MapSelectScreen extends ScreenAdapter  {
    private Stage stage;
    private Viewport viewport;
    private Skin skin;
    private Table mainTable;
    private UI game;
    private float accum;
    private MapIO mapLoader;

    public MapSelectScreen(AssetManager assetManager, UI game) {
        this.game = game;
        this.skin = assetManager.get(Assets.SKIN);
        viewport = new ExtendViewport(1080, 720);
        stage = new Stage(viewport);
        mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);
        accum = 0.0f;
        mapLoader = new MapIO("maps.txt");
    }

    @Override
    public void show(){
        ArrayList<MapIO.MapData> maps = mapLoader.getMaps();
        
        for (final MapIO.MapData map : maps) {
            addButton(map.name).addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y){
                    game.resetGame(map);
                }
            });
        }

        Gdx.input.setInputProcessor(stage);
    }

    private TextButton addButton(String name){
        TextButton button = new TextButton(name, skin);
        mainTable.add(button).center().width(500).height(90).padBottom(20);
        mainTable.row();
        return button;
    }    

    @Override
    public void render(float delta){
        accum += delta;
        float r = 0.5f * ((float)Math.sin((float)accum) + 1.f);

        Gdx.gl.glClearColor(.2f * r, .0f, .15f,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }
}
