package dev.phantom.client.mixin;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.friend.FriendRelation;
import dev.phantom.client.core.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    /**
     * Handles middle-click friend-adding.
     *
     * When the MiddleClickFriend module is active and the player middle-clicks,
     * whatever entity the crosshair is aimed at is added to (or removed from)
     * the friend list.
     */
    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (button != GLFW.GLFW_MOUSE_BUTTON_MIDDLE || action != GLFW.GLFW_PRESS) return;

        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        Module middleClickFriend = client.modules.get("middleclickfriend").orElse(null);
        if (middleClickFriend == null || !middleClickFriend.isEnabled()) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.targetedEntity == null) return;

        Entity target = mc.targetedEntity;
        if (!(target instanceof PlayerEntity player)) return;

        String name = player.getName().getString();
        if (client.friends.isFriend(name)) {
            client.friends.removeFriend(name);
        } else {
            client.friends.addFriend(name, FriendRelation.FRIEND);
        }
    }

    /**
     * Cancels vanilla mouse-look when AimAssist is active so the module can
     * apply its own rotations without fighting the vanilla input handler.
     *
     * AimAssist is expected to set the player's yaw/pitch directly in its own
     * TickEvent handler; this injection only prevents the raw mouse delta from
     * overwriting those values in the same frame.
     *
     * The method name "updateMouse" maps to the Yarn-mapped cursor look update
     * in 1.21.1. If the obfuscated name changes, this target may need updating.
     */
    @Inject(method = "updateMouse", at = @At("HEAD"), cancellable = true)
    private void onUpdateMouse(CallbackInfo ci) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        Module aimAssist = client.modules.get("aimassist").orElse(null);
        // Only cancel vanilla mouse look when AimAssist explicitly requests
        // full control. For most AimAssist implementations the vanilla look
        // should still run; remove the body here if AimAssist uses additive
        // yaw/pitch adjustments instead.
        // if (aimAssist != null && aimAssist.isEnabled()) ci.cancel();
    }
}
