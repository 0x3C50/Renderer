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
    private static final VertexBuffer oneUseBuffer = new VertexBuffer();
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
     * <p>Draws this action by creating or reusing the VBO for it, and rendering it off that. This function indicates that you want to reuse this action several times, for example in large renders</p>
     * <b>Read the README for more information</b>
     *
     * @param stack The context MatrixStack
     */
    public void drawWithVBO(MatrixStack stack) {
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
     * <p>Draws this action by reusing a one-off buffer and rendering off that. Can be used multiple times, although use {@link #drawWithVBO(MatrixStack)} for that.</p>
     * <b>Read the README for more information</b>
     *
     * @param stack The context MatrixStack
     */
    public void drawWithoutVBO(MatrixStack stack) {
        VertexBuffer vbo = oneUseBuffer;

        stack.push();
        RendererUtils.alignForRendering(stack);

        RendererUtils.setupRender();
        Renderer3d.setAppropiateGlMode();
        vbo.bind();
        vbo.upload(buffer);
        vbo.draw(stack.peek().getPositionMatrix(), RenderSystem.getProjectionMatrix(), preferredShader);
        VertexBuffer.unbind();
        stack.pop();
        RendererUtils.endRender();
    }

    /**
     * Frees the VBO. VBO can be regenerated afterwards.
     */
    public void delete() {
        getOrCreateVertexBuffer().close();
        vbo = null;
    }
}
