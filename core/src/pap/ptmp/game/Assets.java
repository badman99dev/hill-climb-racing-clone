package pap.ptmp.game;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Assets {
    private AssetManager assetManager = new AssetManager();
    public static final AssetDescriptor<Skin> SKIN = new AssetDescriptor<Skin>("uiskin.json", Skin.class, new SkinLoader.SkinParameter("uiskin.atlas"));
    public static final String W_IMAGE = "w.png";
    public static final String S_IMAGE = "s.png";
    public static final String SPACE_IMAGE = "space.png";
    public static final String PIWO_IMAGE = "piwo.png";
    public static final String SETTINGS_IMAGE = "settings.png";
    public static final String PAUSE_IMAGE = "pause.png";
    public static final String COIN_IMAGE = "coin.png";



    public void loadAll(){
        assetManager.load(SKIN);
        assetManager.load(W_IMAGE, Texture.class);
        assetManager.load(S_IMAGE, Texture.class);
        assetManager.load(SPACE_IMAGE, Texture.class);
        assetManager.load(PIWO_IMAGE, Texture.class);
        assetManager.load(SETTINGS_IMAGE, Texture.class);
        assetManager.load(PAUSE_IMAGE, Texture.class);
        assetManager.load(COIN_IMAGE, Texture.class);
    }

    public AssetManager getAssetManager(){
        return assetManager;
    }
}
