package pap.ptmp.game;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

// Lightweight dust puffs kicked up by the wheels.
public class DustParticles {
    private static final int MAX = 140;

    private static class Puff {
        float x, y, vx, vy, life, maxLife, size;
    }

    private final Array<Puff> puffs = new Array<Puff>();

    public void emit(float x, float y, float speedX) {
        if (puffs.size >= MAX) return;
        Puff p = new Puff();
        p.x = x + MathUtils.random(-0.15f, 0.15f);
        p.y = y + MathUtils.random(-0.05f, 0.05f);
        p.vx = -speedX * 0.12f + MathUtils.random(-0.25f, 0.25f);
        p.vy = MathUtils.random(0.1f, 0.5f);
        p.maxLife = p.life = MathUtils.random(0.35f, 0.75f);
        p.size = MathUtils.random(0.05f, 0.12f);
        puffs.add(p);
    }

    public void update(float delta) {
        for (int i = puffs.size - 1; i >= 0; i--) {
            Puff p = puffs.get(i);
            p.life -= delta;
            if (p.life <= 0f) {
                puffs.removeIndex(i);
                continue;
            }
            p.x += p.vx * delta;
            p.y += p.vy * delta;
            p.vy -= 0.6f * delta;
            p.vx *= 0.96f;
        }
    }

    public void render(ShapeRenderer renderer) {
        if (puffs.size == 0) return;
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Puff p : puffs) {
            float t = p.life / p.maxLife;
            renderer.setColor(0.55f, 0.45f, 0.30f, 0.45f * t);
            renderer.circle(p.x, p.y, p.size * (1f + (1f - t) * 1.5f), 8);
        }
        renderer.end();
    }
}
