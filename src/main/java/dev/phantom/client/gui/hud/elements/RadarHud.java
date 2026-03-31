package dev.phantom.client.gui.hud.elements;

import dev.phantom.client.PhantomClient;
import dev.phantom.client.gui.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

/**
 * Circular mini-map radar that shows nearby players, mobs, and passive
 * entities as coloured dots relative to the local player.
 *
 * <ul>
 *   <li>Self: white dot in the centre</li>
 *   <li>Friendly player (on friend list): green dot</li>
 *   <li>Other players: red dot</li>
 *   <li>Hostile mob: orange dot</li>
 *   <li>Passive mob: yellow dot</li>
 * </ul>
 *
 * The radar rotates so that "up" always represents the direction the local
 * player is facing.
 */
public class RadarHud extends HudElement {

    // -------------------------------------------------------------------------
    // Settings
    // -------------------------------------------------------------------------
    private static final int   RADAR_DISPLAY_SIZE = 100;  // pixels (diameter)
    private static final float RADAR_RANGE        = 64f;  // blocks

    // -------------------------------------------------------------------------
    // Colors
    // -------------------------------------------------------------------------
    private static final int COLOR_BG       = 0xCC0A0A0A;
    private static final int COLOR_BORDER   = 0xFF333333;
    private static final int COLOR_SELF     = 0xFFFFFFFF;
    private static final int COLOR_FRIEND   = 0xFF4CAF50;
    private static final int COLOR_ENEMY    = 0xFFF44336;
    private static final int COLOR_HOSTILE  = 0xFFFF9800;
    private static final int COLOR_PASSIVE  = 0xFFFFEB3B;
    private static final int COLOR_CROSSHAIR = 0x44FFFFFF;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public RadarHud() {
        super("radar", "Radar", 4, 90);
    }

    @Override
    public void updateSize() {
        width  = RADAR_DISPLAY_SIZE;
        height = RADAR_DISPLAY_SIZE;
    }

    // -------------------------------------------------------------------------
    // Render
    // -------------------------------------------------------------------------

    @Override
    public void render(DrawContext ctx, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;

        int cx = x + RADAR_DISPLAY_SIZE / 2;  // screen-space centre X
        int cy = y + RADAR_DISPLAY_SIZE / 2;  // screen-space centre Y
        int radius = RADAR_DISPLAY_SIZE / 2;

        // Background circle (approximated with a filled square + circular mask via
        // scissor is not straightforward in DrawContext, so we draw a dark square
        // and rely on the border ring for the circular visual).
        ctx.fill(x, y, x + RADAR_DISPLAY_SIZE, y + RADAR_DISPLAY_SIZE, COLOR_BG);

        // Border ring (thin approximation via outline rect)
        ctx.fill(x,              y,              x + RADAR_DISPLAY_SIZE, y + 1,              COLOR_BORDER);
        ctx.fill(x,              y + RADAR_DISPLAY_SIZE - 1, x + RADAR_DISPLAY_SIZE, y + RADAR_DISPLAY_SIZE, COLOR_BORDER);
        ctx.fill(x,              y,              x + 1,              y + RADAR_DISPLAY_SIZE, COLOR_BORDER);
        ctx.fill(x + RADAR_DISPLAY_SIZE - 1, y, x + RADAR_DISPLAY_SIZE, y + RADAR_DISPLAY_SIZE, COLOR_BORDER);

        // Cross-hair lines
        ctx.fill(cx - 1, y + 2, cx + 1, y + RADAR_DISPLAY_SIZE - 2, COLOR_CROSSHAIR);
        ctx.fill(x + 2, cy - 1, x + RADAR_DISPLAY_SIZE - 2, cy + 1, COLOR_CROSSHAIR);

        // Local player yaw for rotation (-yaw because we rotate the map to face player)
        float yaw = mc.player.getYaw();

        // Gather nearby entities
        double px = mc.player.getX();
        double py = mc.player.getY();
        double pz = mc.player.getZ();

        float scale = radius / RADAR_RANGE;

        List<Entity> entities = mc.world.getEntitiesByClass(Entity.class,
                mc.player.getBoundingBox().expand(RADAR_RANGE),
                e -> e != mc.player);

        for (Entity entity : entities) {
            double dx = entity.getX() - px;
            double dz = entity.getZ() - pz;

            // Rotate dx/dz by -yaw so "up" = facing direction
            double rotated = Math.toRadians(-yaw);
            double rdx = dx * Math.cos(rotated) - dz * Math.sin(rotated);
            double rdz = dx * Math.sin(rotated) + dz * Math.cos(rotated);

            // Map to radar pixels
            int dotX = cx + (int)(rdx * scale);
            int dotY = cy + (int)(rdz * scale);

            // Clip to radar bounds
            if (dotX < x + 2 || dotX > x + RADAR_DISPLAY_SIZE - 3
                    || dotY < y + 2 || dotY > y + RADAR_DISPLAY_SIZE - 3) {
                continue;
            }

            int color = getDotColor(entity);
            int dotSize = entity instanceof PlayerEntity ? 2 : 1;

            ctx.fill(dotX - dotSize, dotY - dotSize, dotX + dotSize + 1, dotY + dotSize + 1, color);
        }

        // Self – white dot in centre
        ctx.fill(cx - 2, cy - 2, cx + 3, cy + 3, COLOR_SELF);

        // Label
        ctx.drawText(mc.textRenderer, "Radar",
                x + 2, y + RADAR_DISPLAY_SIZE - mc.textRenderer.fontHeight - 2,
                0xFF888888, false);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private int getDotColor(Entity entity) {
        if (entity instanceof PlayerEntity) {
            // Check friend list
            boolean isFriend = PhantomClient.INSTANCE.friends.isFriend(entity.getName().getString());
            return isFriend ? COLOR_FRIEND : COLOR_ENEMY;
        }
        if (entity instanceof HostileEntity) return COLOR_HOSTILE;
        if (entity instanceof PassiveEntity) return COLOR_PASSIVE;
        return 0xFF888888;
    }
}
