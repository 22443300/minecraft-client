package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class AutoSort extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode","What to sort","Chest",new String[]{"Chest","Inventory","Both"}));
    private final ModeSetting sortBy = register(new ModeSetting("SortBy","Sort method","Type",new String[]{"Type","Name","Count","ID"}));
    private final IntSetting delay = register(new IntSetting("Delay","Sort delay in ticks",5,0,40));
    private final BooleanSetting onOpen = register(new BooleanSetting("OnOpen","Sort when opening container",true));
    public AutoSort() { super("AutoSort","Automatically sorts inventories and chests",Category.TWEAKEROO); }
}
