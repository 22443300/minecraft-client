package dev.phantom.client.mixin;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.module.Module;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin targeting {@link SoundSystem}.
 *
 * <p>Intercepts every sound play request so that the NoRender module can
 * optionally mute all game sounds.
 */
@Mixin(SoundSystem.class)
public class SoundSystemMixin {

    /**
     * Injected at HEAD of {@code play(SoundInstance)}, cancellable.
     *
     * <p>Cancels the sound play request when the NoRender module is enabled.
     *
     * @param sound the sound instance about to be played
     * @param ci    mixin callback; cancelled to suppress the sound
     */
    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V",
            at = @At("HEAD"),
            cancellable = true)
    private void onPlay(SoundInstance sound, CallbackInfo ci) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        Module noRender = client.modules.get("norender").orElse(null);
        if (noRender != null && noRender.isEnabled()) {
            ci.cancel();
        }
    }
}
