package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class PistonPushLimit extends Module {
    private final IntSetting limit = register(new IntSetting("Limit","Max blocks pistons can push",12,1,1000));
    private final BooleanSetting warn = register(new BooleanSetting("Warn","Warn when limit is reached",false));
    public PistonPushLimit() { super("PistonPushLimit","Override the 12-block piston limit",Category.TWEAKEROO); }
}
