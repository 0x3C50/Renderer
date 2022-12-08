package me.x150.renderer.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.RequiredArgsConstructor;
import me.x150.renderer.renderer.util.CameraContext3D;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

/**
 * A render action
 */
@RequiredArgsConstructor
public class RenderAction {
    private static final VertexBuffer oneUseBuffer = new VertexBuffer();
    final BufferBuilder.BuiltBuffer buffer;
    final ShaderProgram preferredShader;
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
     * <p>Draws this action by reusing a one-off buffer and rendering off that, and using a fake 3d camera. To be used on hud rendering.</p>
     * <b>Read the README for more information</b>
     *
     * @param context The 3D context to apply
     */
    public void drawWithoutVboWith3DContext(CameraContext3D context) {
        MatrixStack stack = context.createProjectionStack();
        VertexBuffer vbo = oneUseBuffer;

        stack.push();
        Vec3d camPos = context.getPosition();
        stack.translate(-camPos.x, -camPos.y, -camPos.z);

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
