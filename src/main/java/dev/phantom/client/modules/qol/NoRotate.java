package dev.phantom.client.modules.qol;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class NoRotate extends Module {
    private final BooleanSetting damage = register(new BooleanSetting("Damage","Cancel damage rotation",true));
    private final BooleanSetting mob = register(new BooleanSetting("Mob","Cancel mob rotation",false));
    private final BooleanSetting bed = register(new BooleanSetting("Bed","Cancel bed rotation",true));
    public NoRotate() { super("NoRotate","Prevents forced camera rotations",Category.QOL); }
}
