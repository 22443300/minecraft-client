package dev.phantom.client.mixin;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockMixin {

    /**
     * Modifies the block-breaking delta (speed) returned to the game.
     *
     * InstaBreak: returns 1.0f so every block breaks in a single tick.
     * SpeedMine:  multiplies the vanilla value by 5.0x for faster (not instant)
     *             mining without the visual pop of InstaBreak.
     *
     * InstaBreak takes priority over SpeedMine if both are somehow active.
     */
    @Inject(method = "calcBlockBreakingDelta",
            at = @At("RETURN"), cancellable = true)
    private void onCalcBlockBreakingDelta(BlockState state, PlayerEntity player,
                                          BlockView world, BlockPos pos,
                                          CallbackInfoReturnable<Float> cir) {
        PhantomClient client = PhantomClient.INSTANCE;
        if (client == null) return;

        Module instaBreak = client.modules.get("instabreak").orElse(null);
        if (instaBreak != null && instaBreak.isEnabled()) {
            cir.setReturnValue(1.0f);
            return;
        }

        Module speedMine = client.modules.get("speedmine").orElse(null);
        if (speedMine != null && speedMine.isEnabled()) {
            float original = cir.getReturnValue();
            cir.setReturnValue(original * 5.0f);
        }
    }
}
