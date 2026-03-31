package dev.phantom.client.gui.hud;

import com.google.gson.*;
import dev.phantom.client.PhantomClient;
import dev.phantom.client.core.event.EventBus;
import dev.phantom.client.gui.hud.elements.*;
import net.minecraft.client.gui.DrawContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class HudManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("PhantomHud");
    private final List<HudElement> elements = new ArrayList<>();
    private boolean editing = false;
    private HudElement dragging = null;
    private int dragOffsetX = 0, dragOffsetY = 0;

    public void init() {
        elements.clear();
        CoordinatesHud coords = new CoordinatesHud();
        FpsHud fps = new FpsHud();
        TpsHud tps = new TpsHud();
        PingHud ping = new PingHud();
        EnabledModulesHud modules = new EnabledModulesHud();
        RadarHud radar = new RadarHud();
        HealthArmorHud health = new HealthArmorHud();
        CompassHud compass = new CompassHud();

        coords.setX(5); coords.setY(5);
        fps.setX(5);    fps.setY(30);
        tps.setX(5);    tps.setY(42);
        ping.setX(5);   ping.setY(54);
        modules.setX(0); modules.setY(60);  // Will align right in render
        radar.setX(5);  radar.setY(100);
        health.setX(5); health.setY(70);
        compass.setX(0); compass.setY(5);   // Will center in render

        elements.add(coords);
        elements.add(fps);
        elements.add(tps);
        elements.add(ping);
        elements.add(modules);
        elements.add(radar);
        elements.add(health);
        elements.add(compass);

        // Subscribe elements that need tick events
        EventBus.INSTANCE.subscribe(tps);
    }

    public void render(DrawContext ctx, float tickDelta) {
        for (HudElement el : elements) {
            if (el.isVisible()) {
                el.render(ctx, tickDelta);
            }
        }
        if (editing) {
            // Draw outlines around all elements in editor mode
            for (HudElement el : elements) {
                ctx.drawBorder(el.getX() - 1, el.getY() - 1, el.getWidth() + 2, el.getHeight() + 2, 0xFFFFFFFF);
            }
        }
    }

    public void setEditing(boolean editing) { this.editing = editing; }
    public boolean isEditing() { return editing; }

    public HudElement getElementById(String id) {
        return elements.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
    }

    public List<HudElement> getElements() { return elements; }

    public void toggleElement(String id) {
        HudElement el = getElementById(id);
        if (el != null) el.setVisible(!el.isVisible());
    }

    public void startDrag(HudElement el, int mx, int my) {
        dragging = el;
        dragOffsetX = mx - el.getX();
        dragOffsetY = my - el.getY();
        el.onMousePress(mx, my);
    }

    public void updateDrag(int mx, int my) {
        if (dragging != null) {
            dragging.onMouseDrag(mx, my);
        }
    }

    public void endDrag() { dragging = null; }
    public HudElement getDragging() { return dragging; }

    public void savePositions(Path path) {
        JsonObject root = new JsonObject();
        for (HudElement el : elements) {
            root.add(el.getId(), el.serializePos());
        }
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, new GsonBuilder().setPrettyPrinting().create().toJson(root));
        } catch (IOException e) {
            LOGGER.error("Failed to save HUD positions", e);
        }
    }

    public void loadPositions(Path path) {
        if (!Files.exists(path)) return;
        try {
            JsonObject root = JsonParser.parseString(Files.readString(path)).getAsJsonObject();
            for (HudElement el : elements) {
                if (root.has(el.getId())) {
                    el.deserializePos(root.getAsJsonObject(el.getId()));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load HUD positions", e);
        }
    }
}
