package dev.phantom.client.gui.hud.elements;

import dev.phantom.client.gui.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;

/**
 * HUD element showing health, armour points, and totem-of-undying count from
 * the local player's inventory.
 *
 * Layout (three compact rows):
 * <pre>
 *   ❤ 20.0
 *   ⛉ 20
 *   ✦ 3
 * </pre>
 */
public class HealthArmorHud extends HudElement {

    private static final int PADDING    = 4;
    private static final int LINE_HEIGHT = 11;
    private static final int BG_COLOR   = 0xAA000000;

    public HealthArmorHud() {
        super("health_armor", "Health & Armor", 4, 200);
    }

    @Override
    public void updateSize() {
        width  = 90;
        height = LINE_HEIGHT * 3 + PADDING * 2;
    }

    @Override
    public void render(DrawContext ctx, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        PlayerEntity player = mc.player;

        float health       = player.getHealth();
        float maxHealth    = player.getMaxHealth();
        int   armorPoints  = player.getArmor();
        int   totemCount   = countTotems(player);

        updateSize();

        // Background
        ctx.fill(x, y, x + width, y + height, BG_COLOR);

        int textX = x + PADDING;
        int ly    = y + PADDING;

        // Health row – colour changes red→yellow→green with health percentage
        int healthColor = healthColor(health, maxHealth);
        ctx.drawText(mc.textRenderer,
                "\u2764 %.1f / %.1f".formatted(health, maxHealth),
                textX, ly, healthColor, true);
        ly += LINE_HEIGHT;

        // Armour row
        int armorColor = armorPoints >= 15 ? 0xFF4CAF50 : (armorPoints >= 8 ? 0xFFFFEB3B : 0xFFAAAAAA);
        ctx.drawText(mc.textRenderer,
                "\u26E9 " + armorPoints,
                textX, ly, armorColor, true);
        ly += LINE_HEIGHT;

        // Totem row
        int totemColor = totemCount > 0 ? 0xFFFF9800 : 0xFF555555;
        ctx.drawText(mc.textRenderer,
                "\u2726 " + totemCount,
                textX, ly, totemColor, true);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static int countTotems(PlayerEntity player) {
        int count = 0;
        // Main/off hand
        if (player.getMainHandStack().getItem() == Items.TOTEM_OF_UNDYING) count++;
        if (player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) count++;
        // Inventory
        for (ItemStack stack : player.getInventory().main) {
            if (stack.getItem() == Items.TOTEM_OF_UNDYING) count++;
        }
        return count;
    }

    private static int healthColor(float health, float maxHealth) {
        float pct = maxHealth > 0 ? health / maxHealth : 0f;
        if (pct > 0.6f) return 0xFF4CAF50;  // green
        if (pct > 0.3f) return 0xFFFFEB3B;  // yellow
        return 0xFFF44336;                   // red
    }
}
