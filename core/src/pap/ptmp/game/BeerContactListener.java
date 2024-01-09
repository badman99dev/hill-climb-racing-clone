package pap.ptmp.game;

import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;

public class BeerContactListener implements ContactListener {
    ArrayList<Body> deletionList = new ArrayList<Body>();
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
    }

    @Override
    public void endContact(Contact contact) {

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
}
