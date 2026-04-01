package dev.phantom.client.modules.visual;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class NoRender extends Module {
    private final BooleanSetting fire = register(new BooleanSetting("Fire", "Remove fire overlay", true));
    private final BooleanSetting fog = register(new BooleanSetting("Fog", "Remove fog", true));
    private final BooleanSetting particles = register(new BooleanSetting("Particles", "Remove particles", false));
    private final BooleanSetting weather = register(new BooleanSetting("Weather", "Remove weather effects", false));
    private final BooleanSetting water = register(new BooleanSetting("Water", "Remove water overlay", false));
    private final BooleanSetting enchantGlint = register(new BooleanSetting("Enchant Glint", "Remove enchantment glint", false));
    private final BooleanSetting potionHud = register(new BooleanSetting("Potion HUD", "Remove potion effect HUD", false));
    private final BooleanSetting bossbar = register(new BooleanSetting("Bossbar", "Remove boss bar", false));
    private final BooleanSetting title = register(new BooleanSetting("Title", "Remove title text", false));
    private final BooleanSetting sounds = register(new BooleanSetting("Sounds", "Remove sounds", false));
    private final BooleanSetting hitAnimations = register(new BooleanSetting("Hit Animations", "Remove hit animations", false));
    private final BooleanSetting vignette = register(new BooleanSetting("Vignette", "Remove vignette overlay", true));
    private final BooleanSetting pumpkinOverlay = register(new BooleanSetting("Pumpkin Overlay", "Remove pumpkin head overlay", true));

    public static NoRender INSTANCE;

    public NoRender() {
        super("NoRender", "Removes various render effects", Category.VISUAL);
        INSTANCE = this;
    }
}
