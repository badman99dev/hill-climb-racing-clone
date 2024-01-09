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
    private int fuelX = 1050;
    private int fuelDistance = 50;
    private int fuelDifference = 10; // difference between distances of fuel placement
    private int epsilon = 1;
    private ArrayList<Vector2> beersPos;
    private final Texture beerImage;

    public Map(ShapeRenderer Renderer, World world, Texture groundTexture, Texture beerImage, Color skyColor, int map_seed) {
        this.renderer = Renderer;
        this.world = world;
        this.noiseGenerator = new FastNoiseLite(map_seed);
        this.spriteBatch = new SpriteBatch();
        spriteBatch.setProjectionMatrix(new Matrix4());
        noiseGenerator.SetFrequency((float) 0.1);

        this.groundTexture = groundTexture;
        this.skyColor = skyColor;
        this.beerImage = beerImage;
        this.beersPos = new ArrayList<>();
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
                putFuel(x1, y1 + 0.5f);
                //beersPos.add(new Vector2(x1, y1 + 0.5f));
                beersPos.add(new Vector2(1000f, 100f));
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

    public void putFuel(float x, float y) {
        fuelX += fuelDistance;
        fuelDistance += fuelDifference;
        Beer beer = new Beer(world, x, y);
    }

    public void render(OrthographicCamera camera) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        System.out.println(camera.position.x);
        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(camera.combined);
        for (int i = 0; i < 20000; i+=5)
            for (int j = 70; j <= 130; j+= 5) {
                spriteBatch.draw(groundTexture, i, j, 5, 5);
            }
//        if (!beersPos.isEmpty()) {
//            for (Vector2 pos : beersPos) {
//                float x = pos.x;
//                float y = pos.y;
//                spriteBatch.draw(beerImage, x - 9f, y - 8f);
//            }
//        }
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
