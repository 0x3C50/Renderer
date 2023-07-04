package me.x150.renderer.util;

import com.google.common.base.Preconditions;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferBuilder.BuiltBuffer;
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
	 * @param builder       The buffer
	 * @param expectedUsage The expected usage of this vertex buffer. {@link VertexBuffer.Usage#STATIC} will upload this buffer to VRAM as soon as possible, whereas {@link VertexBuffer.Usage#DYNAMIC} will also keep it in local memory. Setting the correct flag is <b>only a suggestion to the GL driver</b>, nothing will happen if you use the wrong flag. The optimizations for this flag are minuscule.
	 * @return The VBO
	 */
	public static VertexBuffer createVbo(BuiltBuffer builder, VertexBuffer.Usage expectedUsage) {
		VertexBuffer buffer = new VertexBuffer(expectedUsage);
		buffer.bind();
		buffer.upload(builder);
		VertexBuffer.unbind();
		return buffer;
	}

	/**
	 * Uploads this buffer to a VBO
	 *
	 * @param builder The buffer to upload
	 * @param buffer  The VBO to upload to
	 */
	public static void uploadToVbo(BuiltBuffer builder, VertexBuffer buffer) {
		Preconditions.checkArgument(!buffer.isClosed(), "VBO is closed");
		buffer.bind();
		buffer.upload(builder);
		VertexBuffer.unbind();
	}
}
