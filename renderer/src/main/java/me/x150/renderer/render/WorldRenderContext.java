package me.x150.renderer.render;

import me.x150.renderer.generated.Emitter;
import me.x150.renderer.util.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public record WorldRenderContext(Camera camera, VertexConsumerProvider vcp) {
	public WorldRenderContext(MinecraftClient client, VertexConsumerProvider vcp) {
		this(client.gameRenderer.getCamera(), vcp);
	}

	public void drawFilledQuad(MatrixStack stack, RenderLayer layer, Vec3d position, float w, float h, Color color) {
		VertexConsumer buffer = vcp.getBuffer(layer);
		MatrixStack.Entry transform = stack.peek();
		Vector3f transformedRoot = position.subtract(camera.getPos()).toVector3f();
		float x = transformedRoot.x;
		float y = transformedRoot.y;
		float z = transformedRoot.z;
		float r = color.red();
		float g = color.green();
		float b = color.blue();
		float a = color.alpha();
		Emitter._emit_quad__4xposition_color(transform, buffer, x + 0, y + 0, z + 0, r, g, b, a, x + w, y + 0, z + 0, r, g, b, a, x + w, y + h, z + 0, r, g, b, a, x + 0, y + h, z + 0, r, g, b, a);
	}

	public void drawFilledCube(MatrixStack stack, RenderLayer layer, Vec3d position, float w, float h, float d, Color color) {
		VertexConsumer buffer = vcp.getBuffer(layer);
		MatrixStack.Entry transform = stack.peek();
		Vector3f transformedRoot = position.subtract(camera.getPos()).toVector3f();
		float x = transformedRoot.x;
		float y = transformedRoot.y;
		float z = transformedRoot.z;
		float r = color.red();
		float g = color.green();
		float b = color.blue();
		float a = color.alpha();
		Emitter._emit_cube__8xposition_color(transform, buffer, x + 0, y + 0, z + 0, r, g, b, a, x + w, y + 0, z + 0, r, g, b, a, x + w, y + 0, z + d, r, g, b, a, x + 0, y + 0, z + d, r, g, b, a,

				x + 0, y + h, z + 0, r, g, b, a, x + w, y + h, z + 0, r, g, b, a, x + w, y + h, z + d, r, g, b, a, x + 0, y + h, z + d, r, g, b, a);
	}

	public void drawLine(MatrixStack stack, RenderLayer layer, Vec3d start, Vec3d end, Color color) {
		VertexConsumer buffer = vcp.getBuffer(layer);
		MatrixStack.Entry transform = stack.peek();
		Vector3f tfStart = start.subtract(camera.getPos()).toVector3f();
		Vector3f tfEnd = end.subtract(camera.getPos()).toVector3f();
		Vector3f direction = tfEnd.sub(tfStart, new Vector3f()).normalize();
		float x1 = tfStart.x;
		float y1 = tfStart.y;
		float z1 = tfStart.z;
		float x2 = tfEnd.x;
		float y2 = tfEnd.y;
		float z2 = tfEnd.z;
		float r = color.red();
		float g = color.green();
		float b = color.blue();
		float a = color.alpha();
		Emitter._emit_line__2xposition_color_normal(transform, buffer, x1, y1, z1, r, g, b, a, direction.x, direction.y, direction.z, x2, y2, z2, r, g, b, a, direction.x, direction.y, direction.z);
	}
}
