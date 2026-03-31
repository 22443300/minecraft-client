package dev.phantom.client.mixin;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.module.Module;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidBlock.class)
public class FluidBlockMixin {

    /**
     * Makes fluid blocks non-opaque when XRay is enabled.
     *
     * When XRay is active all blocks that are not in the ore whitelist should
     * effectively be invisible. Fluids (water, lava) are large enough to
     * occlude everything beneath them, so we override their opacity check.
     * Returning false from isOpaque() tells the chunk mesher to render the
     * faces of adjacent blocks, allowing ores below a lake to be seen.
     */
    @Inject(method = "isTransparent", at = @At("RETURN"), cancellable = true)
    private void onIsTransparent(BlockState state, BlockView world, BlockPos pos,
                                 CallbackInfoReturnable<Boolean> cir) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        Module xray = client.modules.get("xray").orElse(null);
        if (xray != null && xray.isEnabled()) {
            cir.setReturnValue(true);
        }
    }
}
