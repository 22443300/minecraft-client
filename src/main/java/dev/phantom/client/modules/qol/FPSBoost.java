package dev.phantom.client.modules.qol;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class FPSBoost extends Module {
    private final BooleanSetting reduceParticles = register(new BooleanSetting("ReduceParticles","Reduce number of rendered particles",  true));
    private final BooleanSetting entityCull      = register(new BooleanSetting("EntityCull",     "Cull entities outside view frustum",    true));
    private final BooleanSetting reducedRender   = register(new BooleanSetting("ReducedRender",  "Lower rendering detail for far objects",false));
    private final DoubleSetting  smartCull       = register(new DoubleSetting ("SmartCull",      "Entity cull distance in chunks",        8.0, 4.0, 64.0, 4.0));
    private final BooleanSetting skipHidden      = register(new BooleanSetting("SkipHidden",     "Skip rendering fully occluded entities", true));

    public FPSBoost() {
        super("FPSBoost", "Boosts FPS by culling entities and reducing particles", Category.QOL);
    }
}
