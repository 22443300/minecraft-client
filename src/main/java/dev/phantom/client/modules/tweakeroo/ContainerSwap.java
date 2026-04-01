package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class ContainerSwap extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode","Swap mode","Quick",new String[]{"Quick","Full","Sort"}));
    private final BooleanSetting shiftAll = register(new BooleanSetting("ShiftAll","Shift-click all items",true));
    private final IntSetting delay = register(new IntSetting("Delay","Delay between clicks",2,0,20));
    public ContainerSwap() { super("ContainerSwap","Quickly swap inventory with containers",Category.TWEAKEROO); }
}
