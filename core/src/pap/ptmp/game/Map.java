package pap.ptmp.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public class Map {
    private static final int SEGMENTS = 500;
    private static final float DRAW_WIDTH = 1000f;
    private static final float AMPLITUDE = 50f;
    private static final float PERIOD = 40f;
    private static final float VERTICAL_OFFSET = 100f;

    private final ShapeRenderer renderer;

    public Map(ShapeRenderer Renderer) {
        this.renderer = Renderer;
    }

    public void render() {
        renderer.setColor(Color.BLACK);
        renderer.begin(ShapeRenderer.ShapeType.Line);

        float segmentWidth = DRAW_WIDTH / SEGMENTS;

        for (int i = 0; i < SEGMENTS; i++) {
            float x1 = i * segmentWidth;
            float x2 = (i + 1) * segmentWidth;

            float y1 = calculateSinusoidalNoiseHeight(x1) + VERTICAL_OFFSET;
            float y2 = calculateSinusoidalNoiseHeight(x2) + VERTICAL_OFFSET;

            renderer.line(x1, y1, x2, y2);
        }

        renderer.end();
    }

    private float calculateSinusoidalNoiseHeight(float x) {
        // Use Perlin noise to generate a wavy ground
        float sinValue = MathUtils.sin(x / PERIOD);
        return AMPLITUDE * sinValue;
    }
}
