package dev.phantom.client.core.event.events;

import dev.phantom.client.core.event.Event;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;

public class RenderWorldEvent extends Event {

    private final MatrixStack matrices;
    private final float tickDelta;
    private final Camera camera;

    public RenderWorldEvent(MatrixStack matrices, float tickDelta, Camera camera) {
        this.matrices = matrices;
        this.tickDelta = tickDelta;
        this.camera = camera;
    }

    public MatrixStack getMatrices() {
        return matrices;
    }

    public float getTickDelta() {
        return tickDelta;
    }

    public Camera getCamera() {
        return camera;
    }
}
