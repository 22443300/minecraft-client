package dev.phantom.client.modules.combat;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
import dev.phantom.client.core.event.EventHandler;
import dev.phantom.client.core.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class CrystalAura extends Module {
    private final DoubleSetting placeRange = register(new DoubleSetting("Place Range", "Range for placing crystals", 4.0, 2.0, 6.0, 0.1));
    private final DoubleSetting breakRange = register(new DoubleSetting("Break Range", "Range for breaking crystals", 4.0, 2.0, 6.0, 0.1));
    private final IntSetting breakDelay = register(new IntSetting("Break Delay", "Delay between crystal breaks in ticks", 2, 0, 20));
    private final IntSetting placeDelay = register(new IntSetting("Place Delay", "Delay between crystal placements in ticks", 2, 0, 20));
    private final DoubleSetting minDamage = register(new DoubleSetting("Min Damage", "Minimum damage required to place/break", 6.0, 1.0, 36.0, 0.5));
    private final DoubleSetting maxSelf = register(new DoubleSetting("Max Self", "Maximum self-damage allowed", 8.0, 1.0, 36.0, 0.5));
    private final BooleanSetting switchBack = register(new BooleanSetting("Switch Back", "Switch back to previous item after attacking", true));
    private final ModeSetting mode = register(new ModeSetting("Mode", "Crystal aura operation mode", "Normal", new String[]{"Normal", "Packet", "Suicide"}));

    public CrystalAura() {
        super("CrystalAura", "Automatically targets and explodes end crystals", Category.COMBAT);
    }
}
