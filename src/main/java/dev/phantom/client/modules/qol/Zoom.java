package dev.phantom.client.modules.qol;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class Zoom extends Module {
    private final DoubleSetting  zoomLevel   = register(new DoubleSetting ("ZoomLevel",   "Zoom magnification factor",          4.0, 1.5, 20.0, 0.5));
    private final BooleanSetting smoothZoom  = register(new BooleanSetting("SmoothZoom",  "Smoothly animate zoom in/out",       true));
    private final BooleanSetting scrollZoom  = register(new BooleanSetting("ScrollZoom",  "Adjust zoom level with scroll wheel",true));
    private final BooleanSetting cinematic   = register(new BooleanSetting("Cinematic",   "Enable cinematic camera while zoomed",false));
    private final ModeSetting    zoomMode    = register(new ModeSetting   ("ZoomMode",    "Hold to zoom or toggle",             "Hold", new String[]{"Hold","Toggle"}));

    public static Zoom INSTANCE;

    public Zoom() {
        super("Zoom", "Zoom view like a spyglass", Category.QOL);
        INSTANCE = this;
    }
}
