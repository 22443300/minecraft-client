package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class ElytraSwapEnhanced extends Module {
    private final IntSetting durabilityThreshold = register(new IntSetting("DurabilityThreshold","Swap when durability below %",25,5,100));
    private final BooleanSetting equipFull = register(new BooleanSetting("EquipFull","Equip full armor on landing",true));
    private final BooleanSetting swapBack = register(new BooleanSetting("SwapBack","Swap back to chestplate on land",true));
    private final IntSetting delay = register(new IntSetting("Delay","Swap delay in ticks",3,0,20));
    public ElytraSwapEnhanced() { super("ElytraSwapEnhanced","Enhanced elytra/chestplate swapping",Category.TWEAKEROO); }
}
