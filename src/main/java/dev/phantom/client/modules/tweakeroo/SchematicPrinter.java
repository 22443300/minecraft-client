package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class SchematicPrinter extends Module {
    private final IntSetting delay = register(new IntSetting("Delay","Ticks between placements",3,1,40));
    private final BooleanSetting rotate = register(new BooleanSetting("Rotate","Rotate to face placement",false));
    private final BooleanSetting checkMaterial = register(new BooleanSetting("CheckMaterial","Verify block materials",true));
    private final ModeSetting placementMode = register(new ModeSetting("PlacementMode","Printing mode","All",new String[]{"All","Layer"}));
    private final IntSetting maxPerTick = register(new IntSetting("MaxPerTick","Max blocks per tick",1,1,20));
    public SchematicPrinter() { super("SchematicPrinter","Auto-prints Litematica schematics",Category.TWEAKEROO); }
}
