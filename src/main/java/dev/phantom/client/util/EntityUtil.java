package dev.phantom.client.util;

import dev.phantom.client.PhantomClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public final class EntityUtil {

    private EntityUtil() {}

    // -------------------------------------------------------------------------

    private static MinecraftClient mc() { return MinecraftClient.getInstance(); }

    // -------------------------------------------------------------------------

    /**
     * Returns all living entities (excluding the local player) within range.
     */
    public static List<LivingEntity> getEntitiesInRange(double range) {
        MinecraftClient mc = mc();
        if (mc.player == null || mc.world == null) return List.of();

        Vec3d playerPos = mc.player.getPos();
        Box box = new Box(
                playerPos.x - range, playerPos.y - range, playerPos.z - range,
                playerPos.x + range, playerPos.y + range, playerPos.z + range
        );

        List<LivingEntity> result = new ArrayList<>();
        for (Entity e : mc.world.getEntitiesByClass(LivingEntity.class, box, entity -> true)) {
            if (e == mc.player) continue;
            if (e.distanceTo(mc.player) <= range) {
                result.add((LivingEntity) e);
            }
        }
        return result;
    }

    /**
     * Returns the nearest living entity within range, excluding the local player.
     */
    public static Optional<LivingEntity> getNearestEntity(double range) {
        MinecraftClient mc = mc();
        if (mc.player == null) return Optional.empty();

        return getEntitiesInRange(range).stream()
                .min(Comparator.comparingDouble(e -> e.distanceTo(mc.player)));
    }

    /**
     * Returns the nearest other player within range.
     */
    public static Optional<PlayerEntity> getNearestPlayer(double range) {
        MinecraftClient mc = mc();
        if (mc.player == null || mc.world == null) return Optional.empty();

        Vec3d playerPos = mc.player.getPos();
        Box box = new Box(
                playerPos.x - range, playerPos.y - range, playerPos.z - range,
                playerPos.x + range, playerPos.y + range, playerPos.z + range
        );

        return mc.world.getEntitiesByClass(PlayerEntity.class, box,
                        p -> p != mc.player && p.distanceTo(mc.player) <= range)
                .stream()
                .min(Comparator.comparingDouble(p -> p.distanceTo(mc.player)));
    }

    /**
     * Simplified visibility check using ray cast from player eye to entity center.
     */
    public static boolean isVisible(Entity entity) {
        MinecraftClient mc = mc();
        if (mc.player == null || mc.world == null) return false;

        Vec3d start = mc.player.getEyePos();
        Vec3d end   = entity.getEyePos();

        HitResult result = mc.world.raycast(new net.minecraft.world.RaycastContext(
                start, end,
                net.minecraft.world.RaycastContext.ShapeType.COLLIDER,
                net.minecraft.world.RaycastContext.FluidHandling.NONE,
                mc.player
        ));

        return result.getType() == HitResult.Type.MISS;
    }

    /**
     * Returns true if the entity is alive (not dead/removed).
     */
    public static boolean isAlive(Entity entity) {
        if (entity instanceof LivingEntity living) {
            return !living.isDead() && living.getHealth() > 0f;
        }
        return !entity.isRemoved();
    }

    /**
     * Returns the distance from the local player to the entity.
     */
    public static double getDistanceTo(Entity entity) {
        MinecraftClient mc = mc();
        if (mc.player == null) return Double.MAX_VALUE;
        return mc.player.distanceTo(entity);
    }

    /**
     * Determines whether an entity should be targeted given the provided flags.
     * Skips the local player and optionally skips friends.
     */
    public static boolean shouldTarget(Entity entity, boolean players, boolean mobs, boolean animals) {
        MinecraftClient mc = mc();
        if (mc.player == null) return false;

        // Skip self
        if (entity == mc.player) return false;

        // Skip dead entities
        if (!isAlive(entity)) return false;

        // Friends check
        if (entity instanceof PlayerEntity player) {
            if (PhantomClient.INSTANCE.friends.isFriend(player.getName().getString())) return false;
        }

        if (entity instanceof PlayerEntity && players)  return true;
        if (entity instanceof AnimalEntity && animals)  return true;
        if (entity instanceof MobEntity    && mobs)     return true;

        return false;
    }

    /**
     * Returns entity health as a fraction 0-1.
     */
    public static float getHealthPercent(LivingEntity entity) {
        float max = entity.getMaxHealth();
        if (max <= 0f) return 0f;
        return entity.getHealth() / max;
    }
}
