package dev.phantom.client.mixin;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.module.Module;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin targeting {@link ParticleManager}.
 *
 * <p>Intercepts every client-side particle spawn request. When the NoRender
 * module has its particle-suppression option enabled, the spawn is cancelled
 * before any work is done, keeping the particle pool empty and eliminating
 * the associated CPU/GPU cost.
 */
@Mixin(ParticleManager.class)
public class ParticleManagerMixin {

    /**
     * Cancels particle spawning when the NoRender module is enabled.
     *
     * <p>The injection targets the primary {@code addParticle} overload that
     * all other convenience helpers eventually delegate to, so a single
     * injection point covers every particle type.
     *
     * @param effect the particle type + parameters to spawn
     * @param x      world X coordinate of the spawn point
     * @param y      world Y coordinate of the spawn point
     * @param z      world Z coordinate of the spawn point
     * @param vx     initial X velocity
     * @param vy     initial Y velocity
     * @param vz     initial Z velocity
     * @param ci     mixin callback; cancelled to suppress the spawn
     */
    @Inject(
            method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onAddParticle(ParticleEffect effect,
                               double x, double y, double z,
                               double vx, double vy, double vz,
                               CallbackInfo ci) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        Module noRender = client.modules.get("norender").orElse(null);
        if (noRender != null && noRender.isEnabled()) {
            ci.cancel();
        }
    }
}
