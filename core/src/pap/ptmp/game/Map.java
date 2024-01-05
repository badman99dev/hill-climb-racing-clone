package pap.ptmp.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import fastnoiseLite.*;

public class Map {
    private static final int SEGMENTS = 1000;
    private static final float DRAW_WIDTH = 20000f;
    private static final float AMPLITUDE = 50f;
    private static final float PERIOD = 150f;
    private static final float VERTICAL_OFFSET = 100f;
    private static final float BOOTOM_Y_COORD = -300f;
    private final ShapeRenderer renderer;
    private final World world;
    private final FastNoiseLite noiseGenerator;
    private final String escapeText = "Esc-Menu";
    private SpriteBatch spriteBatch;
    private BitmapFont font;

    public Map(ShapeRenderer Renderer, World world, int map_seed) {
        this.renderer = Renderer;
        this.world = world;
        this.noiseGenerator = new FastNoiseLite(map_seed);
        noiseGenerator.SetFrequency((float) 0.001);
        createGround();
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();

    }

    private void createGround() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        bodyDef.position.set(0, 0);

        Body groundBody = world.createBody(bodyDef);

        ChainShape chainShape = new ChainShape();
        Vector2[] vertices = new Vector2[SEGMENTS];

        for (int i = 0; i < SEGMENTS; i++) {
            float x = DRAW_WIDTH/SEGMENTS * i;
            float y = calculatePerlinHeight(x) + VERTICAL_OFFSET;
            vertices[i] = new Vector2(x, y);
        }

        chainShape.createChain(vertices);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = chainShape;
        fixtureDef.density = 1.0f;

        groundBody.createFixture(fixtureDef);

        chainShape.dispose();
    }

    public void render() {
        renderer.setColor(Color.GREEN);
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        float segmentWidth = DRAW_WIDTH / SEGMENTS;

        for (int i = 0; i < SEGMENTS; i++) {
            float x1 = i * segmentWidth;
            float x2 = (i + 1) * segmentWidth;

            float y1 = calculatePerlinHeight(x1) + VERTICAL_OFFSET;
            float y2 = calculatePerlinHeight(x2) + VERTICAL_OFFSET;

            renderer.triangle(x1, y1, x2, y2, x1, BOOTOM_Y_COORD);
            renderer.triangle(x2, y2, x1, BOOTOM_Y_COORD, x2, BOOTOM_Y_COORD);
        }

        spriteBatch.begin();
        font.setColor(Color.BLUE);
        font.draw(spriteBatch, escapeText, 20, Gdx.graphics.getHeight() - 15);
        spriteBatch.end();

        renderer.end();
    }

    private float calculateSinusoidalNoiseHeight(float x) {
        // Use Perlin noise to generate a wavy ground
        float sinValue = MathUtils.sin(x / PERIOD);
        return AMPLITUDE * sinValue;
    }

    private float calculatePerlinHeight(float x) {
        return noiseGenerator.GetNoise(x, 1) * (50 * (float) Math.log(x * 0.01f));
    }

    public void dispose() {
        spriteBatch.dispose();
        font.dispose();
    }
}
