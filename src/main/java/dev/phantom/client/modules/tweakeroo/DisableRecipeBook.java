package dev.phantom.client.modules.tweakeroo;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class DisableRecipeBook extends Module {
    private final BooleanSetting alsoOnCrafting = register(new BooleanSetting("AlsoOnCrafting","Hide on crafting tables too",true));
    private final BooleanSetting hideButton = register(new BooleanSetting("HideButton","Hide the recipe book button",true));
    public DisableRecipeBook() { super("DisableRecipeBook","Hides the recipe book",Category.TWEAKEROO); }
}
