package pap.ptmp.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import fastnoiseLite.FastNoiseLite;
import java.util.ArrayList;
import java.util.Random;

public class Map {
    private static final int SEGMENTS = 1000;
    private static final float SEGMENT_WIDTH = 0.3f;
    private static final float CHUNK_SIZE = 10f;
    //private static final float DRAW_WIDTH = 20000f;
    private static final float AMPLITUDE = 50f;
    private static final float PERIOD = 150f;
    private static final float VERTICAL_OFFSET = 100f;
    private static final float BOOTOM_Y_COORD = 300f;
    private final ShapeRenderer renderer;
    private final World world;
    private final FastNoiseLite noiseGenerator;
    private Body groundBody;
    private int currentChunk;
    private final SpriteBatch spriteBatch;
    private final Texture groundTexture;
    private final Color skyColor;
    private int fuelX = 1010;
    private int fuelDistance = 50;
    private int fuelDifference = 10; // difference between distances of fuel placement
    private int epsilon = 1;
    private ArrayList<Vector2> beersPos;
    private ArrayList<Vector2> coinsPos;
    private final Texture beerImage;
    private final Texture coinImage;
    Random rand;
    private int coinsCount;
    private final float coinXStart =1015;
    private final int minCoins = 4;
    private final int maxCoins = 8;
    private final float minDistance = 25;
    private final float maxDistance = 40;
    private float currentCoinX = coinXStart;

    public Map(ShapeRenderer Renderer, World world, Texture groundTexture, Texture beerImage, Texture coinImage, Color skyColor, int map_seed) {
        this.renderer = Renderer;
        this.world = world;
        this.noiseGenerator = new FastNoiseLite(map_seed);
        this.spriteBatch = new SpriteBatch();
        spriteBatch.setProjectionMatrix(new Matrix4());
        noiseGenerator.SetFrequency((float) 0.1);

        this.groundTexture = groundTexture;
        this.skyColor = skyColor;
        this.beerImage = beerImage;
        this.coinImage = coinImage;
        this.beersPos = new ArrayList<>();
        this.coinsPos = new ArrayList<>();
        this.rand = new Random(map_seed);
        createGroundAndWall();
    }

    private void createGroundAndWall() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        bodyDef.position.set(0, 0);

        groundBody = world.createBody(bodyDef);
        createGround(0.0f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f;

        //create invisible wall
        BodyDef wallDef = new BodyDef();
        wallDef.type = BodyDef.BodyType.StaticBody; // Static body type
        wallDef.position.set(990, 0); // Set the initial position (x, y)
        Body wallBody = world.createBody(wallDef);
        PolygonShape wallShape = new PolygonShape();
        wallShape.setAsBox(2, 1000);

        fixtureDef.shape = wallShape;
        wallBody.createFixture(fixtureDef);

        wallShape.dispose();
    }

    private void createGround(float start)
    {
        // Add a bit of extra segments across chunk boundaries so it stays smooth.   
        float end = start + CHUNK_SIZE + 50.0f;
        start -= 50.0f;
        start = Math.max(start, 0.0f);

        int segmentStart = (int)Math.floor((double)(start / SEGMENT_WIDTH));
        int segmentEnd = (int)Math.ceil((double)(end / SEGMENT_WIDTH));
        int numSegments = segmentEnd - segmentStart + 1;

        Vector2[] vertices = new Vector2[numSegments];

        for (int i = segmentStart; i <= segmentEnd; i++) {
            float x1 = i * SEGMENT_WIDTH;
            float y1 = calculatePerlinHeight(x1) + VERTICAL_OFFSET;
            vertices[i - segmentStart] = new Vector2(x1, y1);

            // put fuel
            if (Math.abs(x1 - fuelX) < epsilon) {
                Vector2 coordinates = new Vector2(x1, y1 + 0.5f);
                putFuel(coordinates);
                beersPos.add(coordinates);
            }
            if (Math.abs(x1 - currentCoinX) < epsilon) {
                if (Math.abs(currentCoinX - fuelX) < epsilon) {
                    currentCoinX = fuelX + 0.5f;
                }
                if (coinsCount == 0) {
                    coinsCount = rand.nextInt((maxCoins - minCoins) + 1) + minCoins;
                }
                Vector2 coinPos = new Vector2(x1, y1 + 0.5f);
                Money money = new Money(world, this, coinPos);
                coinsPos.add(coinPos);
                coinsCount--;
                currentCoinX += 0.6f;
                if (coinsCount == 0) {
                    currentCoinX += rand.nextFloat(maxDistance - minDistance) + minDistance;
                }
            }
        }

        ChainShape chainShape = new ChainShape();
        chainShape.createChain(vertices);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = chainShape;
        fixtureDef.density = 1.0f;

        groundBody.createFixture(fixtureDef);
        chainShape.dispose();
    }

    public void putFuel(Vector2 coordinates) {
        fuelX += fuelDistance;
        fuelDistance += fuelDifference;
        Beer beer = new Beer(world, this,  coordinates);
    }

    public void removeBeer(Vector2 coordinates) {
        beersPos.remove(coordinates);
    }

    public void removeCoin(Vector2 coordinates) {
        coinsPos.remove(coordinates);
    }

    public void render(OrthographicCamera camera) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(camera.combined);
        for (int i = 0; i < 20000; i+=5)
            for (int j = 70; j <= 130; j+= 5) {
                spriteBatch.draw(groundTexture, i, j, 5, 5);
            }

        spriteBatch.end();

        renderer.setColor(skyColor);
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        // Don't update the ground shape every frame
        // because the performance goes down.
        int newChunk = (int)(camera.position.x / CHUNK_SIZE);
        if (newChunk != currentChunk)
        {
            //System.out.println("CHUNK: " + newChunk);
            createGround((float)newChunk * CHUNK_SIZE);
            currentChunk = newChunk;
        }

        // Render only what is visible.
        //
        float minX = camera.position.x - 0.25f * camera.viewportWidth;
        float maxX = camera.position.x + 0.25f * camera.viewportWidth;

        int segmentStart = (int)Math.floor((double)(minX / SEGMENT_WIDTH));
        int segmentEnd = (int)Math.ceil((double)(maxX / SEGMENT_WIDTH));

        for (int i = segmentStart; i <= segmentEnd; i++) {
            float x1 = i * SEGMENT_WIDTH;
            float x2 = (i + 1) * SEGMENT_WIDTH;

            float y1 = calculatePerlinHeight(x1) + VERTICAL_OFFSET;
            float y2 = calculatePerlinHeight(x2) + VERTICAL_OFFSET;

            renderer.triangle(x1, y1, x2, y2, x1, BOOTOM_Y_COORD);
            renderer.triangle(x2, y2, x1, BOOTOM_Y_COORD, x2, BOOTOM_Y_COORD);
        }

        if (!beersPos.isEmpty() || !coinsPos.isEmpty()) {
            float beerScale = 0.018f;
            spriteBatch.begin();
            for (Vector2 pos : beersPos) {
                float x = pos.x;
                float y = pos.y;
                spriteBatch.draw(beerImage, x - 0.29f, y - 0.29f, beerImage.getWidth() * beerScale, beerImage.getHeight() * beerScale);
            }
            float coinScale = 0.0005f;
            for (Vector2 pos : coinsPos) {
                float x = pos.x;
                float y = pos.y;
                spriteBatch.draw(coinImage, x - 0.12f, y - 0.12f, coinImage.getWidth() * coinScale, coinImage.getHeight() * coinScale);
            }
            spriteBatch.end();
        }

        // DEBUG
        //renderer.line(minX+1, 0, minX+1, 500, Color.RED, Color.RED);
        //renderer.line(maxX-1, 0, maxX-1, 500, Color.RED, Color.RED);

        renderer.end();
    }

    private float calculateSinusoidalNoiseHeight(float x) {
        // Use Perlin noise to generate a wavy ground
        float sinValue = MathUtils.sin(x / PERIOD);
        return AMPLITUDE * sinValue;
    }

    private float calculatePerlinHeight(float x) {
        return noiseGenerator.GetNoise(x, 1) * (0.5f * (float) Math.log(x * 0.01f));
    }

    public void dispose() {
    }
}
