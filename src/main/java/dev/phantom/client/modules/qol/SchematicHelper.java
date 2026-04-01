package dev.phantom.client.modules.qol;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class SchematicHelper extends Module {
    private final BooleanSetting autoLoad    = register(new BooleanSetting("AutoLoad",   "Automatically load schematics on join", false));
    private final BooleanSetting showInfo    = register(new BooleanSetting("ShowInfo",   "Show schematic info overlay",           true));
    private final ModeSetting    integration = register(new ModeSetting   ("Integration","Schematic mod to integrate with",       "Litematica", new String[]{"Litematica","None"}));

    public SchematicHelper() {
        super("SchematicHelper", "Assists with schematics", Category.QOL);
    }
}
