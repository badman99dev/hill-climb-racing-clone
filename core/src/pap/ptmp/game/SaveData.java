package pap.ptmp.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

// Persistent player profile: coins + vehicle upgrade levels.
// Uses Gdx.files.local so it survives app restarts on Android.
public class SaveData {
    public static final int MAX_LEVEL = 10;
    public static final int ENGINE = 0;
    public static final int TYRES = 1;
    public static final int SUSPENSION = 2;

    private static final String FILE = "save.txt";
    private static final int STARTER_COINS = 500;
    private static final int BASE_COST = 100;
    private static final int COST_STEP = 75;

    private static SaveData instance;

    public static SaveData get() {
        if (instance == null) {
            instance = new SaveData();
        }
        return instance;
    }

    public int money;
    public int engineLevel;
    public int tyresLevel;
    public int suspensionLevel;
    public int selectedCar;

    private SaveData() {
        reset();
        load();
    }

    private void reset() {
        money = STARTER_COINS;
        engineLevel = 1;
        tyresLevel = 1;
        suspensionLevel = 1;
        selectedCar = 1;
    }

    public static int upgradeCost(int currentLevel) {
        return BASE_COST + (currentLevel - 1) * COST_STEP;
    }

    public boolean isMax(int which) {
        return getLevel(which) >= MAX_LEVEL;
    }

    public int getLevel(int which) {
        switch (which) {
            case ENGINE: return engineLevel;
            case TYRES: return tyresLevel;
            default: return suspensionLevel;
        }
    }

    private void setLevel(int which, int value) {
        switch (which) {
            case ENGINE: engineLevel = value; break;
            case TYRES: tyresLevel = value; break;
            default: suspensionLevel = value; break;
        }
    }

    // Returns true if the purchase succeeded.
    public boolean tryBuy(int which) {
        int level = getLevel(which);
        if (level >= MAX_LEVEL) return false;
        int cost = upgradeCost(level);
        if (money < cost) return false;
        money -= cost;
        setLevel(which, level + 1);
        save();
        return true;
    }

    public void addMoney(int amount) {
        money += amount;
    }

    public void save() {
        String content = "money=" + money + "\n"
                + "engine=" + engineLevel + "\n"
                + "tyres=" + tyresLevel + "\n"
                + "suspension=" + suspensionLevel + "\n"
                + "car=" + selectedCar + "\n";
        Gdx.files.local(FILE).writeString(content, false);
    }

    private void load() {
        FileHandle local = Gdx.files.local(FILE);
        FileHandle handle = local.exists() ? local : Gdx.files.internal(FILE);
        if (!handle.exists()) return;
        try {
            for (String line : handle.readString().split("\n")) {
                String[] kv = line.split("=");
                if (kv.length != 2) continue;
                String key = kv[0].trim();
                int value = Integer.parseInt(kv[1].trim());
                switch (key) {
                    case "money": money = value; break;
                    case "engine": engineLevel = clamp(value); break;
                    case "tyres": tyresLevel = clamp(value); break;
                    case "suspension": suspensionLevel = clamp(value); break;
                    case "car": selectedCar = value; break;
                }
            }
        } catch (Exception e) {
            Gdx.app.error("SaveData", "Corrupt save, using defaults: " + e.getMessage());
        }
    }

    private int clamp(int level) {
        return Math.max(1, Math.min(MAX_LEVEL, level));
    }
}
