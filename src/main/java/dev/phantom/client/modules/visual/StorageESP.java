package dev.phantom.client.modules.visual;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class StorageESP extends Module {
    private final BooleanSetting chests = register(new BooleanSetting("Chests", "Highlight chests", true));
    private final BooleanSetting shulkers = register(new BooleanSetting("Shulkers", "Highlight shulker boxes", true));
    private final BooleanSetting barrels = register(new BooleanSetting("Barrels", "Highlight barrels", true));
    private final BooleanSetting hoppers = register(new BooleanSetting("Hoppers", "Highlight hoppers", false));
    private final BooleanSetting dispensers = register(new BooleanSetting("Dispensers", "Highlight dispensers", false));
    private final BooleanSetting droppers = register(new BooleanSetting("Droppers", "Highlight droppers", false));
    private final BooleanSetting furnaces = register(new BooleanSetting("Furnaces", "Highlight furnaces", false));
    private final ModeSetting mode = register(new ModeSetting("Mode", "Storage ESP render mode", "Box", new String[]{"Box", "Outline", "Fill"}));
    private final ColorSetting color = register(new ColorSetting("Color", "Storage ESP color", 1.0f, 0.84f, 0.0f, 1.0f));

    public static StorageESP INSTANCE;

    public StorageESP() {
        super("StorageESP", "Highlights storage blocks like chests and shulkers", Category.VISUAL);
        INSTANCE = this;
    }
}
