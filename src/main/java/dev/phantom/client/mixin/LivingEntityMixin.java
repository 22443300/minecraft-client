package dev.phantom.client.mixin;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.event.EventBus;
import dev.phantom.client.core.event.events.PlayerDeathEvent;
import dev.phantom.client.core.module.Module;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    /**
     * Modifies incoming knockback strength for the local player.
     *
     * When the Velocity module is active the horizontal knockback strength is
     * halved. Cancelling the CallbackInfo entirely (strength *= 0) is also an
     * option; the current implementation keeps 50 % so motion still feels
     * responsive while reducing the push significantly.
     *
     * The injection is on the local player only: we compare the entity to
     * MinecraftClient.player to avoid affecting other entities.
     */
    @ModifyVariable(method = "takeKnockback", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private double modifyKnockbackStrength(double strength) {
        LivingEntity self = (LivingEntity) (Object) this;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.player == null) return strength;
        if (!self.equals(mc.player)) return strength;

        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return strength;

        Module velocity = client.modules.get("velocity").orElse(null);
        if (velocity != null && velocity.isEnabled()) {
            // 50 % horizontal knockback reduction
            return strength * 0.5;
        }

        return strength;
    }

    /**
     * Detects when the local player dies and fires PlayerDeathEvent.
     *
     * The onDeath method is called by LivingEntity.damage() after health
     * reaches zero. We fire the event here rather than in damage() itself
     * so listeners know the death has actually occurred.
     */
    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(DamageSource source, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.player == null) return;
        if (!self.equals(mc.player)) return;

        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        EventBus.INSTANCE.post(new PlayerDeathEvent(source));
    }
}
