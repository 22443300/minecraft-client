package dev.phantom.client.util;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class DamageUtil {

    private DamageUtil() {}

    // -------------------------------------------------------------------------

    private static MinecraftClient mc() { return MinecraftClient.getInstance(); }

    // -------------------------------------------------------------------------

    /**
     * Approximates the explosion damage dealt to {@code entity} by an explosion
     * centered at {@code explosionCenter} with the given {@code power}.
     *
     * <p>Exposure is simplified to a constant 0.75 approximation (a full ray-cast
     * would be prohibitively expensive per frame).
     */
    public static float calculateExplosionDamage(Vec3d explosionCenter, Entity entity, float power) {
        double distance = explosionCenter.distanceTo(entity.getPos().add(0, entity.getHeight() / 2.0, 0));

        double maxDist = power * 2.0;
        if (distance >= maxDist) return 0f;

        float exposure = 0.75f;  // simplified; real MC uses block-level ray sampling
        double intensity = (1.0 - distance / maxDist) * exposure;

        float rawDamage = (float)((intensity * intensity + intensity) / 2.0 * 7.0 * maxDist + 1.0);

        if (entity instanceof LivingEntity living) {
            rawDamage = getArmorReduction(living, rawDamage);
            rawDamage = getBlastProtectionReduction(living, rawDamage);
        }

        return Math.max(0f, rawDamage);
    }

    /**
     * Reduces damage based on the entity's armor value.
     * Uses the vanilla formula approximation: each 5 armor points gives ~20 % reduction,
     * capped at 80 %.
     */
    public static float getArmorReduction(LivingEntity entity, float damage) {
        float armor = entity.getArmor();
        float reduction = MathUtil.clamp(armor / 5.0f, 0f, 0.8f);
        return damage * (1f - reduction);
    }

    /**
     * Reduces damage further based on total Blast Protection enchantment levels
     * across all worn armor pieces.
     * Each level provides 8 % reduction, capped at 80 %.
     */
    public static float getBlastProtectionReduction(LivingEntity entity, float damage) {
        MinecraftClient mc = mc();
        if (mc.world == null) return damage;

        int totalLevel = 0;

        for (ItemStack stack : entity.getArmorItems()) {
            if (stack.isEmpty()) continue;

            ItemEnchantmentsComponent enchantments = stack.getOrDefault(
                    DataComponentTypes.ENCHANTMENTS,
                    ItemEnchantmentsComponent.DEFAULT
            );

            // Look up blast protection entry in the dynamic registry
            var registry = mc.world.getRegistryManager().get(RegistryKeys.ENCHANTMENT);
            var blastProtEntry = registry.getEntry(Enchantments.BLAST_PROTECTION);
            if (blastProtEntry.isPresent()) {
                totalLevel += enchantments.getLevel(blastProtEntry.get());
            }
        }

        float reduction = MathUtil.clamp(totalLevel * 0.08f, 0f, 0.8f);
        return damage * (1f - reduction);
    }

    /**
     * Returns true if the given block position is a valid placement position for an
     * end crystal:  the block below must be obsidian or bedrock, and both {@code pos}
     * and {@code pos.up()} must be air / replaceable.
     */
    public static boolean isValidCrystalPos(BlockPos pos) {
        MinecraftClient mc = mc();
        if (mc.world == null) return false;

        // The block below must be obsidian or bedrock
        BlockPos below = pos.down();
        Block belowBlock = mc.world.getBlockState(below).getBlock();
        if (belowBlock != Blocks.OBSIDIAN && belowBlock != Blocks.BEDROCK) return false;

        // pos itself and the block above must be replaceable (air)
        if (!mc.world.getBlockState(pos).isReplaceable())      return false;
        if (!mc.world.getBlockState(pos.up()).isReplaceable())  return false;

        return true;
    }
}
