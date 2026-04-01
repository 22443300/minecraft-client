package dev.phantom.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public final class Renderer3D {

    private Renderer3D() {}

    // -------------------------------------------------------------------------
    // Color helpers
    // -------------------------------------------------------------------------

    private static float red(int color)   { return ((color >> 16) & 0xFF) / 255f; }
    private static float green(int color) { return ((color >> 8)  & 0xFF) / 255f; }
    private static float blue(int color)  { return (color         & 0xFF) / 255f; }
    private static float alpha(int color) { return ((color >> 24) & 0xFF) / 255f; }

    // -------------------------------------------------------------------------
    // Draw box outline (12 edges)
    // -------------------------------------------------------------------------

    public static void drawBox(MatrixStack matrices, Box box, int color, float lineWidth) {
        float r = red(color), g = green(color), b = blue(color), a = alpha(color);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.lineWidth(lineWidth);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Matrix4f posMatrix = matrices.peek().getPositionMatrix();

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        float x1 = (float) box.minX, y1 = (float) box.minY, z1 = (float) box.minZ;
        float x2 = (float) box.maxX, y2 = (float) box.maxY, z2 = (float) box.maxZ;

        // Bottom face edges
        buf.vertex(posMatrix, x1, y1, z1).color(r, g, b, a);
        buf.vertex(posMatrix, x2, y1, z1).color(r, g, b, a);

        buf.vertex(posMatrix, x2, y1, z1).color(r, g, b, a);
        buf.vertex(posMatrix, x2, y1, z2).color(r, g, b, a);

        buf.vertex(posMatrix, x2, y1, z2).color(r, g, b, a);
        buf.vertex(posMatrix, x1, y1, z2).color(r, g, b, a);

        buf.vertex(posMatrix, x1, y1, z2).color(r, g, b, a);
        buf.vertex(posMatrix, x1, y1, z1).color(r, g, b, a);

        // Top face edges
        buf.vertex(posMatrix, x1, y2, z1).color(r, g, b, a);
        buf.vertex(posMatrix, x2, y2, z1).color(r, g, b, a);

        buf.vertex(posMatrix, x2, y2, z1).color(r, g, b, a);
        buf.vertex(posMatrix, x2, y2, z2).color(r, g, b, a);

        buf.vertex(posMatrix, x2, y2, z2).color(r, g, b, a);
        buf.vertex(posMatrix, x1, y2, z2).color(r, g, b, a);

        buf.vertex(posMatrix, x1, y2, z2).color(r, g, b, a);
        buf.vertex(posMatrix, x1, y2, z1).color(r, g, b, a);

        // Vertical edges
        buf.vertex(posMatrix, x1, y1, z1).color(r, g, b, a);
        buf.vertex(posMatrix, x1, y2, z1).color(r, g, b, a);

        buf.vertex(posMatrix, x2, y1, z1).color(r, g, b, a);
        buf.vertex(posMatrix, x2, y2, z1).color(r, g, b, a);

        buf.vertex(posMatrix, x2, y1, z2).color(r, g, b, a);
        buf.vertex(posMatrix, x2, y2, z2).color(r, g, b, a);

        buf.vertex(posMatrix, x1, y1, z2).color(r, g, b, a);
        buf.vertex(posMatrix, x1, y2, z2).color(r, g, b, a);

        BufferRenderer.drawWithGlobalProgram(buf.end());

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.lineWidth(1f);
    }

    // -------------------------------------------------------------------------
    // Draw filled box (6 faces, QUADS)
    // -------------------------------------------------------------------------

    public static void drawFilledBox(MatrixStack matrices, Box box, int color) {
        float r = red(color), g = green(color), b = blue(color), a = alpha(color);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Matrix4f posMatrix = matrices.peek().getPositionMatrix();

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        float x1 = (float) box.minX, y1 = (float) box.minY, z1 = (float) box.minZ;
        float x2 = (float) box.maxX, y2 = (float) box.maxY, z2 = (float) box.maxZ;

        // Bottom (-Y)
        buf.vertex(posMatrix, x1, y1, z1).color(r, g, b, a);
        buf.vertex(posMatrix, x2, y1, z1).color(r, g, b, a);
        buf.vertex(posMatrix, x2, y1, z2).color(r, g, b, a);
        buf.vertex(posMatrix, x1, y1, z2).color(r, g, b, a);

        // Top (+Y)
        buf.vertex(posMatrix, x1, y2, z1).color(r, g, b, a);
        buf.vertex(posMatrix, x1, y2, z2).color(r, g, b, a);
        buf.vertex(posMatrix, x2, y2, z2).color(r, g, b, a);
        buf.vertex(posMatrix, x2, y2, z1).color(r, g, b, a);

        // North (-Z)
        buf.vertex(posMatrix, x1, y1, z1).color(r, g, b, a);
        buf.vertex(posMatrix, x1, y2, z1).color(r, g, b, a);
        buf.vertex(posMatrix, x2, y2, z1).color(r, g, b, a);
        buf.vertex(posMatrix, x2, y1, z1).color(r, g, b, a);

        // South (+Z)
        buf.vertex(posMatrix, x1, y1, z2).color(r, g, b, a);
        buf.vertex(posMatrix, x2, y1, z2).color(r, g, b, a);
        buf.vertex(posMatrix, x2, y2, z2).color(r, g, b, a);
        buf.vertex(posMatrix, x1, y2, z2).color(r, g, b, a);

        // West (-X)
        buf.vertex(posMatrix, x1, y1, z1).color(r, g, b, a);
        buf.vertex(posMatrix, x1, y1, z2).color(r, g, b, a);
        buf.vertex(posMatrix, x1, y2, z2).color(r, g, b, a);
        buf.vertex(posMatrix, x1, y2, z1).color(r, g, b, a);

        // East (+X)
        buf.vertex(posMatrix, x2, y1, z1).color(r, g, b, a);
        buf.vertex(posMatrix, x2, y2, z1).color(r, g, b, a);
        buf.vertex(posMatrix, x2, y2, z2).color(r, g, b, a);
        buf.vertex(posMatrix, x2, y1, z2).color(r, g, b, a);

        BufferRenderer.drawWithGlobalProgram(buf.end());

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    // -------------------------------------------------------------------------
    // Draw a single line
    // -------------------------------------------------------------------------

    public static void drawLine(MatrixStack matrices, Vec3d start, Vec3d end, int color, float lineWidth) {
        float r = red(color), g = green(color), b = blue(color), a = alpha(color);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.lineWidth(lineWidth);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Matrix4f posMatrix = matrices.peek().getPositionMatrix();

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        buf.vertex(posMatrix, (float) start.x, (float) start.y, (float) start.z).color(r, g, b, a);
        buf.vertex(posMatrix, (float) end.x,   (float) end.y,   (float) end.z).color(r, g, b, a);

        BufferRenderer.drawWithGlobalProgram(buf.end());

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.lineWidth(1f);
    }

    // -------------------------------------------------------------------------
    // Draw horizontal circle outline
    // -------------------------------------------------------------------------

    public static void drawCircle(MatrixStack matrices, Vec3d center, double radius, int segments, int color, float lineWidth) {
        float r = red(color), g = green(color), b = blue(color), a = alpha(color);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.lineWidth(lineWidth);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Matrix4f posMatrix = matrices.peek().getPositionMatrix();

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        float cx = (float) center.x;
        float cy = (float) center.y;
        float cz = (float) center.z;

        for (int i = 0; i < segments; i++) {
            double angle1 = (2 * Math.PI * i)       / segments;
            double angle2 = (2 * Math.PI * (i + 1)) / segments;

            float x1 = cx + (float)(Math.cos(angle1) * radius);
            float z1 = cz + (float)(Math.sin(angle1) * radius);
            float x2 = cx + (float)(Math.cos(angle2) * radius);
            float z2 = cz + (float)(Math.sin(angle2) * radius);

            buf.vertex(posMatrix, x1, cy, z1).color(r, g, b, a);
            buf.vertex(posMatrix, x2, cy, z2).color(r, g, b, a);
        }

        BufferRenderer.drawWithGlobalProgram(buf.end());

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.lineWidth(1f);
    }

    // -------------------------------------------------------------------------
    // Draw filled horizontal circle
    // -------------------------------------------------------------------------

    public static void drawFilledCircle(MatrixStack matrices, Vec3d center, double radius, int segments, int color) {
        float r = red(color), g = green(color), b = blue(color), a = alpha(color);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Matrix4f posMatrix = matrices.peek().getPositionMatrix();

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);

        float cx = (float) center.x;
        float cy = (float) center.y;
        float cz = (float) center.z;

        // Fan manually: each triangle is (center, point[i], point[i+1])
        for (int i = 0; i < segments; i++) {
            double angle1 = (2 * Math.PI * i)       / segments;
            double angle2 = (2 * Math.PI * (i + 1)) / segments;

            float x1 = cx + (float)(Math.cos(angle1) * radius);
            float z1 = cz + (float)(Math.sin(angle1) * radius);
            float x2 = cx + (float)(Math.cos(angle2) * radius);
            float z2 = cz + (float)(Math.sin(angle2) * radius);

            buf.vertex(posMatrix, cx, cy, cz).color(r, g, b, a);
            buf.vertex(posMatrix, x1, cy, z1).color(r, g, b, a);
            buf.vertex(posMatrix, x2, cy, z2).color(r, g, b, a);
        }

        BufferRenderer.drawWithGlobalProgram(buf.end());

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    // -------------------------------------------------------------------------
    // Draw billboard text in 3D space
    // -------------------------------------------------------------------------

    public static void drawText3D(MatrixStack matrices, String text, Vec3d pos, float scale, int color, boolean shadow) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.textRenderer == null) return;

        Camera camera = mc.getEntityRenderDispatcher().camera;
        if (camera == null) return;

        matrices.push();
        matrices.translate(pos.x, pos.y, pos.z);

        // Billboard: rotate to face camera
        matrices.multiply(camera.getRotation());

        // Flip y-axis (MC text renders upside-down in world space otherwise)
        matrices.scale(-scale, -scale, scale);

        Matrix4f posMatrix = matrices.peek().getPositionMatrix();

        int textWidth = mc.textRenderer.getWidth(text);
        float xOffset = -textWidth / 2f;

        // Use immediate mode text rendering via the matrix
        net.minecraft.client.render.VertexConsumerProvider.Immediate immediate =
                mc.getBufferBuilders().getEntityVertexConsumers();

        mc.textRenderer.draw(
                text,
                xOffset,
                0f,
                color,
                shadow,
                posMatrix,
                immediate,
                net.minecraft.client.font.TextRenderer.TextLayerType.SEE_THROUGH,
                0,
                0xF000F0
        );

        immediate.draw();
        matrices.pop();
    }
}
