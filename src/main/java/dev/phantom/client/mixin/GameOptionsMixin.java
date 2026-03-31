package dev.phantom.client.mixin;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(GameOptions.class)
public class GameOptionsMixin {

    @Shadow
    public SimpleOption<Double> gamma;

    @Inject(method = "accept", at = @At("TAIL"))
    private void onAccept(GameOptions.Visitor visitor, CallbackInfo ci) {
        // Used when game options are loaded — FullBright module overrides gamma each tick
        // via its own @EventHandler, so this mixin serves as a hook point if needed
    }
}
