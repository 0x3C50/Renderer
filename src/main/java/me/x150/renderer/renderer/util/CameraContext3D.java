package me.x150.renderer.renderer.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

/**
 * <p>A pseudo 3D-Camera to be used in the hud</p>
 * <p>Working example:</p>
 * <pre>
 * {@code
 * CameraContext3D build = CameraContext3D.builder().position(new Vec3d(-5, 0, 0)).fov(90f).build(); // create camera at -5 0 0 with 90 fov
 * @EventListener(shift = Shift.POST, type = EventType.HUD_RENDER)
 * void hudRendered(RenderEvent event) {
 *     double v = Math.toRadians((System.currentTimeMillis() % 4000) / 4000d * 360d); // animation x and z
 *     double v1 = Math.toRadians((System.currentTimeMillis() % 7000) / 7000d * 360d); // animation y
 *     build.setPosition(new Vec3d(Math.sin(v) * 10, Math.sin(v1) * 5, Math.cos(v) * 10)); // apply animation, rotating in circle with bob up and down
 *     build.faceTowards(new Vec3d(.5, .5, .5)); // face towards 0.5 0.5 0.5
 *     build.use(() -> { // apply view matrix and use
 *         Renderer3d.renderOutline(Vec3d.ZERO, new Vec3d(1, 1, 1), Color.RED).drawWithoutVboWith3DContext(build);
 *         Renderer3d.renderFilled(new Vec3d(2, 2, 2), new Vec3d(1, 1, 1), Color.BLUE).drawWithoutVboWith3DContext(build);
 *     });
 * }
 * }
 * </pre>
 */
@Builder
@Getter
@Setter
public class CameraContext3D {
    /**
     * Position of this camera
     */
    Vec3d position;
    /**
     * Fov of this camera
     */
    float fov;
    /**
     * Pitch of this camera
     */
    float pitch;
    /**
     * Yaw of this camera
     */
    float yaw;

    /**
     * Creates the MatrixStack for this camera
     *
     * @return A new MatrixStack translated to fit this camera
     */
    public MatrixStack createProjectionStack() {
        MatrixStack matrices = new MatrixStack();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(getYaw() + 180.0F));
        return matrices;
    }

    /**
     * Faces the camera towards a target in 3D space
     *
     * @param target The target to face towards
     */
    public void faceTowards(Vec3d target) {
        double d = target.x - getPosition().x;
        double e = target.y - getPosition().y;
        double f = target.z - getPosition().z;
        double g = Math.sqrt(d * d + f * f);
        this.setPitch(MathHelper.wrapDegrees((float) (-(MathHelper.atan2(e, g) * 57.2957763671875))));
        this.setYaw(MathHelper.wrapDegrees((float) (MathHelper.atan2(f, d) * 57.2957763671875) - 90.0F));
    }

    /**
     * Applies a generic transformation matrix and viewport, then draws with this camera
     *
     * @param action The rendering calls to draw
     * @param x      The X coordinate of the new viewport
     * @param y      The Y coordinate of the new viewport
     * @param endX   The end X coordinate of the new viewport
     * @param endY   The end Y coordinate of the new viewport
     */
    public void use(Runnable action, double x, double y, double endX, double endY) {
        MinecraftClient client = MinecraftClient.getInstance();
        double width = endX - x;
        double height = endY - y;
        width = Math.max(0, width);
        height = Math.max(0, height);
        float d = (float) client.getWindow().getScaleFactor();
        int ay = (int) ((client.getWindow().getScaledHeight() - (y + height)) * d);

        Matrix4f basicProjectionMatrix = MinecraftClient.getInstance().gameRenderer.getBasicProjectionMatrix(getFov());
        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix(basicProjectionMatrix);
        int x1 = GlStateManager.Viewport.getX();
        int y1 = GlStateManager.Viewport.getY();
        int width1 = GlStateManager.Viewport.getWidth();
        int height1 = GlStateManager.Viewport.getHeight();
        RenderSystem.viewport((int) (x * d), ay, (int) (width * d), (int) (height * d));
        action.run();
        RenderSystem.restoreProjectionMatrix();
        RenderSystem.viewport(x1, y1, width1, height1);
    }
}
