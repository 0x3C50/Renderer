package me.x150.renderer.renderer;

import com.google.common.base.Preconditions;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;

public class BufferUtils {
    /**
     * Draws a buffer
     *
     * @param builder The buffer
     */
    public static void draw(BufferBuilder builder) {
        BufferRenderer.drawWithGlobalProgram(builder.end());
    }

    /**
     * Creates a VBO for this buffer
     *
     * @param builder The buffer
     *
     * @return The VBO
     */
    public static VertexBuffer createVbo(BufferBuilder.BuiltBuffer builder) {
        VertexBuffer buffer = new VertexBuffer();
        buffer.bind();
        buffer.upload(builder);
        VertexBuffer.unbind();
        return buffer;
    }

    /**
     * Uplodads this buffer to a VBO
     *
     * @param builder The buffer to upload
     * @param buffer  The VBO to upload to
     */
    public static void uploadToVbo(BufferBuilder.BuiltBuffer builder, VertexBuffer buffer) {
        Preconditions.checkArgument(!buffer.isClosed(), "VBO is closed");
        buffer.bind();
        buffer.upload(builder);
        VertexBuffer.unbind();
    }
}
