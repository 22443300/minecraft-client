package dev.phantom.client.modules.visual;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.util.math.BlockPos;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * StorageESP – highlights storage containers visible in the world.
 *
 * <p>{@code BlockEntityRendererMixin} calls {@link #onBlockEntityRender(BlockEntity)}
 * each frame for every block entity that passes through the render dispatcher.
 * This module records which storage containers are currently being rendered
 * so that it can draw ESP boxes around them during the world render pass.
 */
public class StorageESP extends Module {

    /** Set of block positions of visible storage block entities this frame. */
    private final Set<BlockPos> visibleContainers =
            Collections.newSetFromMap(new ConcurrentHashMap<>());

    public StorageESP() {
        super("StorageESP", "Highlights storage containers", Category.VISUAL);
    }

    @Override
    public void onEnable() {
        visibleContainers.clear();
    }

    @Override
    public void onDisable() {
        visibleContainers.clear();
    }

    // -------------------------------------------------------------------------
    // Called by BlockEntityRendererMixin
    // -------------------------------------------------------------------------

    /**
     * Registers a block entity as visible this render frame if it is a
     * recognised storage type (chest, barrel, shulker box, hopper, furnace,
     * dispenser / dropper).
     *
     * @param blockEntity the block entity being rendered
     */
    public void onBlockEntityRender(BlockEntity blockEntity) {
        if (isStorage(blockEntity)) {
            visibleContainers.add(blockEntity.getPos());
        }
    }

    /** Returns an unmodifiable view of visible container positions. */
    public Set<BlockPos> getVisibleContainers() {
        return Collections.unmodifiableSet(visibleContainers);
    }

    /** Clears the visible-container set at the start of each frame. */
    public void beginFrame() {
        visibleContainers.clear();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static boolean isStorage(BlockEntity be) {
        return be instanceof ChestBlockEntity
                || be instanceof ShulkerBoxBlockEntity
                || be instanceof BarrelBlockEntity
                || be instanceof HopperBlockEntity
                || be instanceof FurnaceBlockEntity
                || be instanceof DispenserBlockEntity;
    }
}
