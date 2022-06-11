package me.x150.renderer.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Shader;
import net.minecraft.client.util.math.MatrixStack;

/**
 * A render action
 */
@RequiredArgsConstructor
public class RenderAction {
    final BufferBuilder.BuiltBuffer buffer;
    final Shader preferredShader;
    VertexBuffer vbo = null;

    /**
     * Gets (or creates) a VBO for the current buffer
     *
     * @return The newly created or cached VBO
     */
    public VertexBuffer getOrCreateVertexBuffer() {
        if (vbo == null) {
            vbo = BufferUtils.createVbo(buffer);
        }
        return vbo;
    }

    /**
     * <p>Draws this action.</p>
     * <b>This indicates that you want to render this buffer multiple times.</b>
     * <b>Read the readme for more information</b>
     *
     * @param stack The context MatrixStack
     */
    public void draw(MatrixStack stack) {
        VertexBuffer vbo = getOrCreateVertexBuffer();

        stack.push();
        RendererUtils.alignForRendering(stack);

        RendererUtils.setupRender();
        Renderer3d.setAppropiateGlMode();
        vbo.bind();
        vbo.draw(stack.peek().getPositionMatrix(), RenderSystem.getProjectionMatrix(), preferredShader);
        VertexBuffer.unbind();
        stack.pop();
        RendererUtils.endRender();
    }

    /**
     * <p>Draws this action</p>
     * <b>This will delete the buffer after rendering.</b>
     * <b>Indicates that this buffer is to be rendered once</b>
     *
     * @param stack The context MatrixStack
     */
    public void drawOnce(MatrixStack stack) {
        draw(stack);
        delete();
    }

    /**
     * Frees the VBO. VBO can be regenerated afterwards.
     */
    public void delete() {
        getOrCreateVertexBuffer().close();
        vbo = null;
    }
}
