package dev.phantom.client.modules.tweakeroo;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import net.minecraft.util.math.Direction;

/**
 * FlexibleBlockPlacement – allows blocks to be placed on any face regardless
 * of the exact crosshair hit direction.
 *
 * <p>The mixin {@code ItemUsageContextMixin} reads {@link #getPlacementFace()}
 * to override the face returned by {@code ItemUsageContext.getSide()} when
 * this module is enabled.  Modules that compute the best placement direction
 * at interaction time should write to {@link #currentFace} before the block-
 * use packet is sent.
 */
public class FlexibleBlockPlacement extends Module {

    /**
     * The placement-face override for the current interaction frame.
     * {@code null} means "use whatever face vanilla computed".
     */
    private static Direction currentFace = null;

    public FlexibleBlockPlacement() {
        super("FlexibleBlockPlacement", "Allows flexible block placement angles", Category.TWEAKEROO);
    }

    @Override
    public void onEnable() {
        currentFace = null;
    }

    @Override
    public void onDisable() {
        currentFace = null;
    }

    // -------------------------------------------------------------------------
    // Static API used by ItemUsageContextMixin
    // -------------------------------------------------------------------------

    /**
     * Returns the face override for the current interaction, or {@code null}
     * if no override is active.
     */
    public static Direction getPlacementFace() {
        return currentFace;
    }

    /**
     * Sets the face override that will be returned by {@link #getPlacementFace()}
     * for the next block interaction.  Set to {@code null} to clear the override
     * and fall back to vanilla behaviour.
     *
     * @param face the desired placement face, or {@code null}
     */
    public static void setPlacementFace(Direction face) {
        currentFace = face;
    }
}
