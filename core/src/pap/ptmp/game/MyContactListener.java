package pap.ptmp.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;

public class MyContactListener implements ContactListener {
    ArrayList<Body> deletionList = new ArrayList<Body>();
    private int currentRoofCollisions = 0; // if >0 roof is colliding with ground
    private PtmPGame game;
    Sound beer = Gdx.audio.newSound(Gdx.files.internal("beer.mp3"));
    Sound coin = Gdx.audio.newSound(Gdx.files.internal("coin.mp3"));

    MyContactListener(PtmPGame game) {
        this.game = game;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if(fa == null || fb == null) return;
        if(fa.getUserData() == null || fb.getUserData() == null) return;

        if (isBeerContact(fa, fb)) {
            if (fa.getUserData() instanceof Beer) {
                deletionList.add(((Beer) fa.getUserData()).body);
                Beer beer = ((Beer) fa.getUserData());
                beer.getMap().removeBeer(beer.getCoords());
                ((VehicleCollisionBox) fb.getUserData()).vehicle.refill();
            } else {
                deletionList.add(((Beer) fb.getUserData()).body);
                Beer beer = ((Beer) fb.getUserData());
                beer.getMap().removeBeer(beer.getCoords());
                ((VehicleCollisionBox) fa.getUserData()).vehicle.refill();
            }
            beer.play(1.0f); // playing sound
        }
        else if (isMoneyContact(fa, fb)) {
            Money money;
            if (fa.getUserData() instanceof Money) {
                money = (Money)fa.getUserData();
            }
            else {
                money = (Money)fb.getUserData();
            }
            deletionList.add(money.body);
            money.getMap().removeCoin(money.getCoords());
            game.addMoney(1);
            coin.play(1.0f); // playing sound
        }
        

        if ((fa.getUserData() instanceof Ground && fb.getUserData() instanceof RoofCollisionDetector) ||
            (fa.getUserData() instanceof RoofCollisionDetector && fb.getUserData() instanceof Ground)) {
            currentRoofCollisions++;
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if ((fa.getUserData() instanceof Ground && fb.getUserData() instanceof RoofCollisionDetector) ||
                (fa.getUserData() instanceof RoofCollisionDetector && fb.getUserData() instanceof Ground)) {
            currentRoofCollisions--;
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

    private boolean isBeerContact(Fixture a, Fixture b) {
        return (a.getUserData() instanceof Beer && b.getUserData() instanceof VehicleCollisionBox) ||
                (a.getUserData() instanceof VehicleCollisionBox && b.getUserData() instanceof Beer);
    }

    private boolean isMoneyContact(Fixture a, Fixture b) {
        return (a.getUserData() instanceof Money && b.getUserData() instanceof VehicleCollisionBox) ||
                (a.getUserData() instanceof VehicleCollisionBox && b.getUserData() instanceof Money);
    }

    public ArrayList<Body> getDeletionList() {
        return  deletionList;
    }

    public int getCurrentRoofCollisions() {
        return currentRoofCollisions;
    }
}
