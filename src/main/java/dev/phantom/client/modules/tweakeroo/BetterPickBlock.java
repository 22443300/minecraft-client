package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class BetterPickBlock extends Module {
    private final BooleanSetting copyNBT = register(new BooleanSetting("CopyNBT","Copy NBT data",true));
    private final BooleanSetting copyEnchants = register(new BooleanSetting("CopyEnchants","Copy enchantments",true));
    private final BooleanSetting fromInventory = register(new BooleanSetting("FromInventory","Search full inventory",true));
    public BetterPickBlock() { super("BetterPickBlock","Enhanced middle-click pick block",Category.TWEAKEROO); }
}
