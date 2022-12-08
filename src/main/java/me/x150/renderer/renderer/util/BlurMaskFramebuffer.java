package me.x150.renderer.renderer.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import org.lwjgl.opengl.GL30C;

/**
 * A framebuffer which acts as a mask for blurring content behind it
 */
public class BlurMaskFramebuffer extends Framebuffer {
    private static BlurMaskFramebuffer instance;

    private BlurMaskFramebuffer(int width, int height) {
        super(false);
        RenderSystem.assertOnRenderThreadOrInit();
        this.resize(width, height, true);
        this.setClearColor(0f, 0f, 0f, 0f);
    }

    public static BlurMaskFramebuffer getInstance() {
        if (instance == null) {
            instance = new BlurMaskFramebuffer(MinecraftClient.getInstance().getFramebuffer().textureWidth, MinecraftClient.getInstance().getFramebuffer().textureHeight);
        }
        return instance;
    }

    /**
     * <p>Draws to this framebuffer</p>
     * <p>The color of the element doesn't matter, the alpha does.</p>
     * <table>
     *     <thead>
     *     <tr>
     *         <td>Alpha</td>
     *         <td>Blur radius</td>
     *     </tr>
     *     </thead>
     *     <tbody>
     *     <tr>
     *         <td>255 (100%)</td>
     *         <td>Full radius (100%)</td>
     *     </tr>
     *     <tr>
     *         <td>100 (40%)</td>
     *         <td>Half the radius (40%)</td>
     *     </tr>
     *     <tr>
     *         <td>0 (0%)</td>
     *         <td>No blur (0% radius, default)</td>
     *     </tr>
     *     </tbody>
     * </table>
     *
     * @param r The action with rendering calls to write to this framebuffer
     */
    public static void use(Runnable r) {
        Framebuffer mainBuffer = MinecraftClient.getInstance().getFramebuffer();
        RenderSystem.assertOnRenderThreadOrInit();
        BlurMaskFramebuffer buffer = getInstance();
        if (buffer.textureWidth != mainBuffer.textureWidth || buffer.textureHeight != mainBuffer.textureHeight) {
            buffer.resize(mainBuffer.textureWidth, mainBuffer.textureHeight, false);
        }

        //        GlStateManager._glBindFramebuffer(GL30C.GL_READ_FRAMEBUFFER, mainBuffer.fbo);
        GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, buffer.fbo);

        buffer.beginWrite(true);
        r.run();
        buffer.endWrite();

        //        GlStateManager._glBindFramebuffer(GL30C.GL_READ_FRAMEBUFFER, buffer.fbo);
        GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, mainBuffer.fbo);

        mainBuffer.beginWrite(false);
    }

    /**
     * <p>Applies this mask to the main buffer</p>
     *
     * @param radius The blur
     */
    public static void draw(float radius) {
        Framebuffer mainBuffer = MinecraftClient.getInstance().getFramebuffer();
        BlurMaskFramebuffer buffer = getInstance();
        ShaderManager.BLUR_MASK_SHADER.setUniformSampler("MaskSampler", buffer);
        ShaderManager.BLUR_MASK_SHADER.setUniformf("Radius", radius);
        ShaderManager.BLUR_MASK_SHADER.render(MinecraftClient.getInstance().getTickDelta());
        GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, buffer.fbo);
        buffer.clear(true);
        GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, mainBuffer.fbo);
        mainBuffer.beginWrite(true);
    }

    /**
     * Uses this framebuffer and applies the mask
     *
     * @param r      The action to run within this framebuffer
     * @param radius The blur radius
     *
     * @see #use(Runnable)
     */
    public static void useAndDraw(Runnable r, float radius) {
        use(r);
        draw(radius);
    }
}
