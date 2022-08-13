package me.x150.renderer.renderer.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import org.lwjgl.opengl.GL30C;

/**
 * <p>A glowing framebuffer</p>
 * <p>Will make everything drawn to it glow, in the color being drawn</p>
 */
public class GlowFramebuffer extends Framebuffer {
    private static GlowFramebuffer instance;

    private GlowFramebuffer(int width, int height) {
        super(true);
        RenderSystem.assertOnRenderThreadOrInit();
        this.resize(width, height, true);
        this.setClearColor(0f, 0f, 0f, 0f);
    }

    private static GlowFramebuffer obtain() {
        if (instance == null) {
            instance = new GlowFramebuffer(
                    MinecraftClient.getInstance().getFramebuffer().textureWidth,
                    MinecraftClient.getInstance().getFramebuffer().textureHeight
            );
        }
        return instance;
    }

    /**
     * Draws to this framebuffer
     *
     * @param r The action with rendering calls to write to this framebuffer
     */

    public static void use(Runnable r) {
        Framebuffer mainBuffer = MinecraftClient.getInstance().getFramebuffer();
        RenderSystem.assertOnRenderThreadOrInit();
        GlowFramebuffer buffer = obtain();
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
     * <p>Draws this framebuffer to the main buffer</p>
     *
     * @param radius The glow radius. Recommended: 8 px. Shader will do (n*2)^2 iterations, keep this small
     */
    public static void draw(float radius) {
        Framebuffer mainBuffer = MinecraftClient.getInstance().getFramebuffer();
        GlowFramebuffer buffer = obtain();
        ShaderManager.GLOW_SHADER.setSamplerUniform("fbo", buffer);
        ShaderManager.GLOW_SHADER.setUniformValue("radius", radius);
        ShaderManager.GLOW_SHADER.render(MinecraftClient.getInstance().getTickDelta());
        GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, buffer.fbo);
        buffer.clear(true);
        GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, mainBuffer.fbo);
        mainBuffer.beginWrite(true);
    }

    /**
     * Uses this framebuffer and draws it
     *
     * @param r      The action to run within this framebuffer
     * @param radius The glow radius. Recommended: 8 px. Shader will do (n*2)^2 iterations, keep this small
     * @see #use(Runnable)
     */
    public static void useAndDraw(Runnable r, float radius) {
        use(r);
        draw(radius);
    }
}
