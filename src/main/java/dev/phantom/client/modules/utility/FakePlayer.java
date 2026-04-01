package dev.phantom.client.modules.utility;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class FakePlayer extends Module {
    private final StringSetting name = register(new StringSetting("Name", "Fake player name", "Phantom"));
    private final BooleanSetting copyPose = register(new BooleanSetting("Copy Pose", "Copy the player's current pose and rotation", true));
    private final BooleanSetting copyInventory = register(new BooleanSetting("Copy Inventory", "Copy the player's current inventory", true));
    private final BooleanSetting showInESP = register(new BooleanSetting("Show In ESP", "Show the fake player in ESP overlays", false));

    public FakePlayer() {
        super("FakePlayer", "Spawns a fake client-side player entity for testing", Category.UTILITY);
    }
}
