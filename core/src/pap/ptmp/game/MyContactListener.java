package pap.ptmp.game;

import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;

public class MyContactListener implements ContactListener {
    ArrayList<Body> deletionList = new ArrayList<Body>();
    private int currentRoofCollisions = 0; // if >0 roof is colliding with ground
    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if(fa == null || fb == null) return;
        if(fa.getUserData() == null || fb.getUserData() == null) return;

        if (isBeerContact(fa, fb)) {
            if (fa.getUserData() instanceof Beer) {
                deletionList.add(((Beer) fa.getUserData()).body);
                ((Beer) fa.getUserData()).removeRender();
                ((VehicleCollisionBox) fb.getUserData()).vehicle.refill();
            } else {
                deletionList.add(((Beer) fb.getUserData()).body);
                ((Beer) fb.getUserData()).removeRender();
                ((VehicleCollisionBox) fa.getUserData()).vehicle.refill();
            }

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

    public ArrayList<Body> getDeletionList() {
        return  deletionList;
    }

    public int getCurrentRoofCollisions() {
        return currentRoofCollisions;
    }
}
