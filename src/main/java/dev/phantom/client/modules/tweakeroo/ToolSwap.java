package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class ToolSwap extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode","Tool selection mode","Best",new String[]{"Best","Pickaxe","Shovel","Axe","Hoe","Sword"}));
    private final BooleanSetting switchBack = register(new BooleanSetting("SwitchBack","Switch back after use",true));
    private final IntSetting delay = register(new IntSetting("Delay","Delay before swapping",0,0,10));
    private final BooleanSetting checkDurability = register(new BooleanSetting("CheckDurability","Skip tools with low durability",true));
    public ToolSwap() { super("ToolSwap","Auto-swaps to the best tool for mined block",Category.TWEAKEROO); }
}
