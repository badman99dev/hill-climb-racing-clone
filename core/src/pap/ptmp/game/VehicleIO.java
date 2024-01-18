package pap.ptmp.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;

public class VehicleIO {
    public class CarData {
        int id = -1;
        String name = "";
        public CarData(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
    Assets assets;
    AssetManager assetManager;
    VehicleIO() {
        assets = new Assets();
        assets.loadCars();
        assetManager = assets.getAssetManager();
        assetManager.finishLoading();
        createCars();
    }
    public Texture getTexture(int id) {
        switch(id) {
            case 1:
                return assetManager.get(Assets.BLUE_CAR_IMAGE);
            case 2:
                return assetManager.get(Assets.GREEN_CAR_IMAGE);
            case 3:
                return assetManager.get(Assets.PURPLE_CAR_IMAGE);
            case 4:
                return assetManager.get(Assets.RED_CAR_IMAGE);
            case 5:
                return assetManager.get(Assets.YELLOW_CAR_IMAGE);
            default:
                return assetManager.get(Assets.BLUE_CAR_IMAGE);
        }
    }

    ArrayList<CarData> cars;

    private void createCars() {
        cars = new ArrayList<CarData>();
        cars.add(new CarData(1, "Blue car"));
        cars.add(new CarData(2, "Green car"));
        cars.add(new CarData(3, "Purple car"));
        cars.add(new CarData(4, "Red car"));
        cars.add(new CarData(5, "Yellow car"));
    }

    public ArrayList<CarData> getCars() {
        return cars;
    }
}
