package dev.phantom.client.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class BlockUtil {

    private BlockUtil() {}

    // -------------------------------------------------------------------------

    private static MinecraftClient mc() { return MinecraftClient.getInstance(); }

    // -------------------------------------------------------------------------

    /**
     * Returns all block positions of the given block type within a radius around center.
     */
    public static List<BlockPos> getBlocksInRadius(BlockPos center, int radius, Block block) {
        MinecraftClient mc = mc();
        if (mc.world == null) return List.of();

        List<BlockPos> result = new ArrayList<>();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos pos = center.add(dx, dy, dz);
                    if (mc.world.getBlockState(pos).getBlock() == block) {
                        result.add(pos);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns true if the block at pos has a solid collision shape (non-air solid block).
     */
    public static boolean isSolid(BlockPos pos) {
        MinecraftClient mc = mc();
        if (mc.world == null) return false;
        BlockState state = mc.world.getBlockState(pos);
        return state.isSolidBlock(mc.world, pos);
    }

    /**
     * Returns true if the block at pos is air or replaceable (can be placed into).
     */
    public static boolean isReplaceable(BlockPos pos) {
        MinecraftClient mc = mc();
        if (mc.world == null) return true;
        BlockState state = mc.world.getBlockState(pos);
        return state.isReplaceable();
    }

    /**
     * Returns the BlockState at the given position.
     */
    public static BlockState getBlockState(BlockPos pos) {
        MinecraftClient mc = mc();
        if (mc.world == null) return Blocks.AIR.getDefaultState();
        return mc.world.getBlockState(pos);
    }

    /**
     * Checks if a 1x1x1 column is a valid crystal-pvp "hole":
     * the 4 horizontal neighbours at ground level and the block below must be obsidian or bedrock,
     * and the two blocks at standing height (pos and pos.up) must be passable.
     */
    public static Optional<BlockPos> findHole(double range) {
        MinecraftClient mc = mc();
        if (mc.player == null || mc.world == null) return Optional.empty();

        BlockPos playerPos = mc.player.getBlockPos();
        int iRange = (int) Math.ceil(range);

        Optional<BlockPos> best = Optional.empty();
        double bestDist = Double.MAX_VALUE;

        for (int dx = -iRange; dx <= iRange; dx++) {
            for (int dz = -iRange; dz <= iRange; dz++) {
                BlockPos candidate = playerPos.add(dx, 0, dz);
                double dist = Vec3d.ofCenter(candidate).distanceTo(mc.player.getPos());
                if (dist > range) continue;

                if (isHoleAt(candidate) && dist < bestDist) {
                    best = Optional.of(candidate);
                    bestDist = dist;
                }
            }
        }
        return best;
    }

    private static boolean isHoleAt(BlockPos pos) {
        MinecraftClient mc = mc();
        if (mc.world == null) return false;

        // The standing space must be free
        if (!isReplaceable(pos) || !isReplaceable(pos.up())) return false;

        // Floor must be hard
        if (!isHardBlock(pos.down())) return false;

        // Four sides must be hard
        if (!isHardBlock(pos.north())) return false;
        if (!isHardBlock(pos.south())) return false;
        if (!isHardBlock(pos.west())) return false;
        if (!isHardBlock(pos.east())) return false;

        return true;
    }

    private static boolean isHardBlock(BlockPos pos) {
        MinecraftClient mc = mc();
        if (mc.world == null) return false;
        Block b = mc.world.getBlockState(pos).getBlock();
        return b == Blocks.OBSIDIAN || b == Blocks.BEDROCK || b == Blocks.CRYING_OBSIDIAN;
    }

    /**
     * Returns true if the player is currently standing in a hole (as defined above).
     */
    public static boolean isInHole(ClientPlayerEntity player) {
        return isHoleAt(player.getBlockPos());
    }

    /**
     * Returns all block positions that the player can interact with within reach distance.
     */
    public static List<BlockPos> getInteractableBlocks(double reach) {
        MinecraftClient mc = mc();
        if (mc.player == null || mc.world == null) return List.of();

        BlockPos playerPos = mc.player.getBlockPos();
        int iReach = (int) Math.ceil(reach);
        List<BlockPos> result = new ArrayList<>();

        for (int dx = -iReach; dx <= iReach; dx++) {
            for (int dy = -iReach; dy <= iReach; dy++) {
                for (int dz = -iReach; dz <= iReach; dz++) {
                    BlockPos pos = playerPos.add(dx, dy, dz);
                    if (Vec3d.ofCenter(pos).distanceTo(mc.player.getEyePos()) <= reach) {
                        if (!isReplaceable(pos)) {
                            result.add(pos);
                        }
                    }
                }
            }
        }
        return result;
    }
}
