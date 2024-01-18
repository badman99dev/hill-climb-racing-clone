package pap.ptmp.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class VehicleIO {
    private final AssetManager assetManager;

    public VehicleIO(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public Texture getTexture(int texture_id) {
        switch (texture_id){
            case 0:
                return assetManager.get(Assets.CAR_BLUE);
            case 1:
                return assetManager.get(Assets.CAR_GREEN);
            case 2:
                return assetManager.get(Assets.CAR_YELLOW);
            case 3:
                return assetManager.get(Assets.CAR_RED);
            case 4:
                return assetManager.get(Assets.CAR_PURPLE);
            default:
                return assetManager.get(Assets.CAR_BLUE);
        }
    }
}
