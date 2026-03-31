package dev.phantom.client.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public final class MathUtil {

    private MathUtil() {}

    // -------------------------------------------------------------------------
    // Clamp
    // -------------------------------------------------------------------------

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    // -------------------------------------------------------------------------
    // Lerp
    // -------------------------------------------------------------------------

    public static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    public static Vec3d lerp(Vec3d a, Vec3d b, double t) {
        return new Vec3d(
                lerp(a.x, b.x, t),
                lerp(a.y, b.y, t),
                lerp(a.z, b.z, t)
        );
    }

    // -------------------------------------------------------------------------
    // Misc
    // -------------------------------------------------------------------------

    public static double square(double x) {
        return x * x;
    }

    /**
     * Returns true if the Euclidean distance between a and b is within range.
     */
    public static boolean isWithinRange(Vec3d a, Vec3d b, double range) {
        return a.squaredDistanceTo(b) <= range * range;
    }

    /**
     * Returns the horizontal (XZ-plane) distance between a and b.
     */
    public static double getHorizontalDistance(Vec3d a, Vec3d b) {
        double dx = a.x - b.x;
        double dz = a.z - b.z;
        return Math.sqrt(dx * dx + dz * dz);
    }

    /**
     * Predicts an entity's position after {@code ticks} ticks using its current velocity.
     * This is an approximation; it ignores gravity, drag, collisions, etc.
     */
    public static Vec3d predictPosition(Entity entity, int ticks) {
        Vec3d pos = entity.getPos();
        Vec3d vel = entity.getVelocity();
        return new Vec3d(
                pos.x + vel.x * ticks,
                pos.y + vel.y * ticks,
                pos.z + vel.z * ticks
        );
    }
}
