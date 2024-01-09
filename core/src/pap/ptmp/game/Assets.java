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
    public static final String GRASS_TEXTURE = "grass.png";
    public static final String MARS_TEXTURE = "mars.jpg";
    public static final String MOON_TEXTURE = "moon.png";
    public static final String COIN_IMAGE = "coin.png";
    public static final String BEER_FUEL_IMAGE = "piwo32.png";
    public static final String CAR_TEXTURE = "car.png";
    public static final String WHEEL_TEXTURE = "wheel.png";
    public static final String SKULL_IMAGE = "skull.png";



    public void loadAll(){
        assetManager.load(SKIN);
        assetManager.load(W_IMAGE, Texture.class);
        assetManager.load(S_IMAGE, Texture.class);
        assetManager.load(SPACE_IMAGE, Texture.class);
        assetManager.load(PIWO_IMAGE, Texture.class);
        assetManager.load(SETTINGS_IMAGE, Texture.class);
        assetManager.load(PAUSE_IMAGE, Texture.class);
        assetManager.load(GRASS_TEXTURE, Texture.class);
        assetManager.load(MARS_TEXTURE, Texture.class);
        assetManager.load(MOON_TEXTURE, Texture.class);
        assetManager.load(COIN_IMAGE, Texture.class);
        assetManager.load(BEER_FUEL_IMAGE, Texture.class);
        assetManager.load(CAR_TEXTURE, Texture.class);
        assetManager.load(WHEEL_TEXTURE, Texture.class);
        assetManager.load(SKULL_IMAGE, Texture.class);
    }

    public AssetManager getAssetManager(){
        return assetManager;
    }
}
