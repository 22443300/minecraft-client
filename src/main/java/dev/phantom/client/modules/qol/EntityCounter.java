package dev.phantom.client.modules.qol;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class EntityCounter extends Module {
    private final BooleanSetting players = register(new BooleanSetting("Players","Count players",true));
    private final BooleanSetting mobs = register(new BooleanSetting("Mobs","Count mobs",true));
    private final BooleanSetting animals = register(new BooleanSetting("Animals","Count animals",true));
    private final BooleanSetting items = register(new BooleanSetting("Items","Count dropped items",false));
    private final IntSetting range = register(new IntSetting("Range","Count range in blocks",64,8,256));
    private final ModeSetting position = register(new ModeSetting("Position","HUD position","TopLeft",new String[]{"TopLeft","TopRight","BottomLeft","BottomRight"}));
    public EntityCounter() { super("EntityCounter","Shows nearby entity counts on HUD",Category.QOL); }
}
