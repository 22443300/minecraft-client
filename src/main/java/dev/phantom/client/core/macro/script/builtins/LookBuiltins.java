package dev.phantom.client.core.macro.script.builtins;

import dev.phantom.client.core.macro.script.Interpreter;
import dev.phantom.client.util.RotationUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class LookBuiltins {

    private LookBuiltins() {}

    public static void registerAll(Interpreter interp) {
        interp.registerBuiltin("look_at", LookBuiltins::lookAt);
        interp.registerBuiltin("look_at_nearest_player", args -> lookAtNearestPlayer());
    }

    // -------------------------------------------------------------------------
    // look_at(x, y, z)
    // -------------------------------------------------------------------------

    private static Object lookAt(List<Object> args) throws Exception {
        if (args.size() < 3) {
            dev.phantom.client.PhantomClient.LOGGER.warn("[Phantom Macro] look_at requires 3 arguments (x, y, z)");
            return null;
        }

        double x = Interpreter.toDouble(args.get(0));
        double y = Interpreter.toDouble(args.get(1));
        double z = Interpreter.toDouble(args.get(2));
        Vec3d target = new Vec3d(x, y, z);

        MinecraftClient mc = MinecraftClient.getInstance();
        CompletableFuture<Void> future = new CompletableFuture<>();
        mc.execute(() -> {
            try {
                float[] rotations = RotationUtil.getRotations(target);
                RotationUtil.setRotation(rotations[0], rotations[1]);
                future.complete(null);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            dev.phantom.client.PhantomClient.LOGGER.warn("[Phantom Macro] look_at() failed: {}", e.getMessage());
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // look_at_nearest_player()
    // -------------------------------------------------------------------------

    private static Object lookAtNearestPlayer() throws Exception {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return null;

        AtomicReference<Vec3d> targetPos = new AtomicReference<>(null);
        CompletableFuture<Void> future = new CompletableFuture<>();

        mc.execute(() -> {
            try {
                if (mc.player == null || mc.world == null) {
                    future.complete(null);
                    return;
                }

                PlayerEntity nearest = null;
                double nearestDist = Double.MAX_VALUE;

                for (PlayerEntity player : mc.world.getPlayers()) {
                    if (player == mc.player) continue;
                    double dist = mc.player.squaredDistanceTo(player);
                    if (dist < nearestDist) {
                        nearestDist = dist;
                        nearest = player;
                    }
                }

                if (nearest != null) {
                    targetPos.set(nearest.getEyePos());
                    float[] rotations = RotationUtil.getRotations(targetPos.get());
                    RotationUtil.setRotation(rotations[0], rotations[1]);
                }
                future.complete(null);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            dev.phantom.client.PhantomClient.LOGGER.warn("[Phantom Macro] look_at_nearest_player() failed: {}", e.getMessage());
        }
        return null;
    }
}
