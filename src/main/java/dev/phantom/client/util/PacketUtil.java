package dev.phantom.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

public final class PacketUtil {

    private PacketUtil() {}

    // -------------------------------------------------------------------------

    private static MinecraftClient mc() { return MinecraftClient.getInstance(); }

    // -------------------------------------------------------------------------

    /**
     * Sends any packet to the server through the current network handler.
     */
    public static void send(Packet<?> packet) {
        MinecraftClient mc = mc();
        if (mc.getNetworkHandler() != null) {
            mc.getNetworkHandler().sendPacket(packet);
        }
    }

    /**
     * Sends a rotation-only player move packet to the server.
     * Does not update client-side player rotation.
     */
    public static void sendRotation(float yaw, float pitch, boolean onGround) {
        send(new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, onGround, false));
    }

    /**
     * Sends an attack interaction for the given entity via the interaction manager.
     */
    public static void attackEntity(Entity target) {
        MinecraftClient mc = mc();
        if (mc.player == null || mc.interactionManager == null) return;
        mc.interactionManager.attackEntity(mc.player, target);
    }

    /**
     * Sends a block interaction (right-click) packet via the interaction manager.
     */
    public static void placeBlock(BlockHitResult hitResult) {
        MinecraftClient mc = mc();
        if (mc.player == null || mc.interactionManager == null) return;
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
    }

    /**
     * Makes the player swing the given hand (sends animation packet to server).
     */
    public static void swingHand(Hand hand) {
        MinecraftClient mc = mc();
        if (mc.player == null) return;
        mc.player.swingHand(hand);
    }
}
