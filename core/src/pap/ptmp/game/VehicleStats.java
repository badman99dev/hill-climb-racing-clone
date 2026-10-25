package pap.ptmp.game;

// Concrete physics tuning for a vehicle, derived from the player's upgrade levels.
public class VehicleStats {
    public float bodyWidth = 1.24f;
    public float bodyHeight = 0.2f;
    public float wheelRadius = 0.1f;
    public float suspensionHeight = 0.15f;
    public float leftOffset = 0.235f;
    public float rightOffset = 0.2f;

    public float forwardSpeed = 40f;
    public float backwardSpeed = 30f;
    public float maxMotorTorque = 0.1f;
    public float wheelFriction = 3f;
    public float suspensionFrequency = 3f;
    public float suspensionDamping = 0.9f;
    public float fuelConsumptionRatio = 3f;
    public float jointFrictionForce = 0.005f;
    public float airRotationImpulse = 0.005f;

    public static VehicleStats fromLevels(int engine, int tyres, int suspension) {
        VehicleStats s = new VehicleStats();

        float engineFactor = 1f + 0.06f * (engine - 1);
        float tyresFactor = 1f + 0.10f * (tyres - 1);
        float suspensionFactor = 1f + 0.06f * (suspension - 1);

        s.forwardSpeed = 40f * engineFactor;
        s.backwardSpeed = 30f * engineFactor;
        s.maxMotorTorque = 0.1f * (1f + 0.08f * (engine - 1));
        s.fuelConsumptionRatio = 3f + 0.12f * (engine - 1);

        s.wheelFriction = 3f * tyresFactor;

        s.suspensionFrequency = 3f * suspensionFactor;
        s.suspensionDamping = Math.min(0.95f, 0.9f + 0.005f * (suspension - 1));

        return s;
    }
}
