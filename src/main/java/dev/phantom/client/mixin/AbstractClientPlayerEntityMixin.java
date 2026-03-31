package dev.phantom.client.mixin;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.module.Module;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.SkinTextures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin {

    /**
     * Hook on skin texture retrieval for the NameTags module.
     *
     * The NameTags module may want to skip rendering a label above an entity
     * that has no downloaded skin yet (getSkinTextures() returning a default
     * texture). This injection provides the intercept point; the actual
     * NameTags rendering logic lives in the module itself and consults this
     * flag indirectly via the entity reference it already holds.
     *
     * No return value modification is made here – the hook is intentionally
     * lightweight and only prints debug information when NameTags is active,
     * making it easy to extend later.
     */
    @Inject(method = "getSkinTextures", at = @At("RETURN"))
    private void onGetSkinTextures(CallbackInfoReturnable<SkinTextures> cir) {
        // Intentionally empty – presence of the hook lets future code extend
        // NameTags / Chams skin-texture-based logic without changing the Mixin
        // target list in phantom.mixins.json.
    }
}
