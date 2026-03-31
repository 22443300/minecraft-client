package dev.phantom.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class RotationUtil {

    private RotationUtil() {}

    /**
     * Returns float[]{yaw, pitch} needed to look at target from the local player's eye position.
     */
    public static float[] getRotations(Vec3d target) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return new float[]{0f, 0f};

        Vec3d eye = mc.player.getEyePos();
        double dx = target.x - eye.x;
        double dy = target.y - eye.y;
        double dz = target.z - eye.z;

        double horizontalDist = Math.sqrt(dx * dx + dz * dz);

        float yaw   = (float)(Math.toDegrees(Math.atan2(dz, dx))) - 90f;
        float pitch = (float)(-Math.toDegrees(Math.atan2(dy, horizontalDist)));

        return new float[]{yaw, pitch};
    }

    /**
     * Smoothly interpolates from current yaw/pitch toward target yaw/pitch by speed (0-1 per tick).
     */
    public static float[] smoothRotations(float currentYaw, float currentPitch,
                                           float targetYaw, float targetPitch, float speed) {
        float yawDiff   = getAngleDifference(currentYaw,   targetYaw);
        float pitchDiff = getAngleDifference(currentPitch, targetPitch);

        float newYaw   = currentYaw   + yawDiff   * speed;
        float newPitch = currentPitch + pitchDiff * speed;

        newPitch = MathHelper.clamp(newPitch, -90f, 90f);

        return new float[]{newYaw, newPitch};
    }

    /**
     * Returns true if the given entity is within the given horizontal FOV from the player's look direction.
     */
    public static boolean isInFov(Entity target, float fov) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return false;

        float[] rotations = getRotations(target.getPos());
        float diffYaw = Math.abs(getAngleDifference(mc.player.getYaw(), rotations[0]));
        return diffYaw <= fov / 2f;
    }

    /**
     * Normalizes an angle to the range [-180, 180].
     */
    public static float normalizeAngle(float angle) {
        angle %= 360f;
        if (angle >= 180f)  angle -= 360f;
        if (angle < -180f)  angle += 360f;
        return angle;
    }

    /**
     * Returns the signed shortest difference between two angles, in [-180, 180].
     */
    public static float getAngleDifference(float a, float b) {
        return normalizeAngle(b - a);
    }

    /**
     * Sets the client-side player rotation silently (no server packet).
     */
    public static void setRotation(float yaw, float pitch) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        mc.player.setYaw(yaw);
        mc.player.setPitch(pitch);
    }
}
