package me.x150.renderer.generated;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public final class Emitter {
	public static void _emit_quad__4xposition_color(final MatrixStack.Entry transform, final VertexConsumer consumer, float v0_position_x, float v0_position_y, float v0_position_z, float v0_color_r, float v0_color_g, float v0_color_b, float v0_color_a, float v1_position_x, float v1_position_y, float v1_position_z, float v1_color_r, float v1_color_g, float v1_color_b, float v1_color_a, float v2_position_x, float v2_position_y, float v2_position_z, float v2_color_r, float v2_color_g, float v2_color_b, float v2_color_a, float v3_position_x, float v3_position_y, float v3_position_z, float v3_color_r, float v3_color_g, float v3_color_b, float v3_color_a) {
		{
			// Vertices: 0-1-2-3
			consumer.vertex(transform, v0_position_x, v0_position_y, v0_position_z).color(v0_color_r, v0_color_g, v0_color_b, v0_color_a);
			consumer.vertex(transform, v1_position_x, v1_position_y, v1_position_z).color(v1_color_r, v1_color_g, v1_color_b, v1_color_a);
			consumer.vertex(transform, v2_position_x, v2_position_y, v2_position_z).color(v2_color_r, v2_color_g, v2_color_b, v2_color_a);
			consumer.vertex(transform, v3_position_x, v3_position_y, v3_position_z).color(v3_color_r, v3_color_g, v3_color_b, v3_color_a);
		}
	}

	public static void _emit_cube__8xposition_color(final MatrixStack.Entry transform, final VertexConsumer consumer, float v0_position_x, float v0_position_y, float v0_position_z, float v0_color_r, float v0_color_g, float v0_color_b, float v0_color_a, float v1_position_x, float v1_position_y, float v1_position_z, float v1_color_r, float v1_color_g, float v1_color_b, float v1_color_a, float v2_position_x, float v2_position_y, float v2_position_z, float v2_color_r, float v2_color_g, float v2_color_b, float v2_color_a, float v3_position_x, float v3_position_y, float v3_position_z, float v3_color_r, float v3_color_g, float v3_color_b, float v3_color_a, float v4_position_x, float v4_position_y, float v4_position_z, float v4_color_r, float v4_color_g, float v4_color_b, float v4_color_a, float v5_position_x, float v5_position_y, float v5_position_z, float v5_color_r, float v5_color_g, float v5_color_b, float v5_color_a, float v6_position_x, float v6_position_y, float v6_position_z, float v6_color_r, float v6_color_g, float v6_color_b, float v6_color_a, float v7_position_x, float v7_position_y, float v7_position_z, float v7_color_r, float v7_color_g, float v7_color_b, float v7_color_a) {
		{
			// Vertices: 3-0-1-2
			consumer.vertex(transform, v3_position_x, v3_position_y, v3_position_z).color(v3_color_r, v3_color_g, v3_color_b, v3_color_a);
			consumer.vertex(transform, v0_position_x, v0_position_y, v0_position_z).color(v0_color_r, v0_color_g, v0_color_b, v0_color_a);
			consumer.vertex(transform, v1_position_x, v1_position_y, v1_position_z).color(v1_color_r, v1_color_g, v1_color_b, v1_color_a);
			consumer.vertex(transform, v2_position_x, v2_position_y, v2_position_z).color(v2_color_r, v2_color_g, v2_color_b, v2_color_a);
		}
		{
			// Vertices: 4-7-6-5
			consumer.vertex(transform, v4_position_x, v4_position_y, v4_position_z).color(v4_color_r, v4_color_g, v4_color_b, v4_color_a);
			consumer.vertex(transform, v7_position_x, v7_position_y, v7_position_z).color(v7_color_r, v7_color_g, v7_color_b, v7_color_a);
			consumer.vertex(transform, v6_position_x, v6_position_y, v6_position_z).color(v6_color_r, v6_color_g, v6_color_b, v6_color_a);
			consumer.vertex(transform, v5_position_x, v5_position_y, v5_position_z).color(v5_color_r, v5_color_g, v5_color_b, v5_color_a);
		}
		{
			// Vertices: 0-4-5-1
			consumer.vertex(transform, v0_position_x, v0_position_y, v0_position_z).color(v0_color_r, v0_color_g, v0_color_b, v0_color_a);
			consumer.vertex(transform, v4_position_x, v4_position_y, v4_position_z).color(v4_color_r, v4_color_g, v4_color_b, v4_color_a);
			consumer.vertex(transform, v5_position_x, v5_position_y, v5_position_z).color(v5_color_r, v5_color_g, v5_color_b, v5_color_a);
			consumer.vertex(transform, v1_position_x, v1_position_y, v1_position_z).color(v1_color_r, v1_color_g, v1_color_b, v1_color_a);
		}
		{
			// Vertices: 1-5-6-2
			consumer.vertex(transform, v1_position_x, v1_position_y, v1_position_z).color(v1_color_r, v1_color_g, v1_color_b, v1_color_a);
			consumer.vertex(transform, v5_position_x, v5_position_y, v5_position_z).color(v5_color_r, v5_color_g, v5_color_b, v5_color_a);
			consumer.vertex(transform, v6_position_x, v6_position_y, v6_position_z).color(v6_color_r, v6_color_g, v6_color_b, v6_color_a);
			consumer.vertex(transform, v2_position_x, v2_position_y, v2_position_z).color(v2_color_r, v2_color_g, v2_color_b, v2_color_a);
		}
		{
			// Vertices: 2-6-7-3
			consumer.vertex(transform, v2_position_x, v2_position_y, v2_position_z).color(v2_color_r, v2_color_g, v2_color_b, v2_color_a);
			consumer.vertex(transform, v6_position_x, v6_position_y, v6_position_z).color(v6_color_r, v6_color_g, v6_color_b, v6_color_a);
			consumer.vertex(transform, v7_position_x, v7_position_y, v7_position_z).color(v7_color_r, v7_color_g, v7_color_b, v7_color_a);
			consumer.vertex(transform, v3_position_x, v3_position_y, v3_position_z).color(v3_color_r, v3_color_g, v3_color_b, v3_color_a);
		}
		{
			// Vertices: 3-7-4-0
			consumer.vertex(transform, v3_position_x, v3_position_y, v3_position_z).color(v3_color_r, v3_color_g, v3_color_b, v3_color_a);
			consumer.vertex(transform, v7_position_x, v7_position_y, v7_position_z).color(v7_color_r, v7_color_g, v7_color_b, v7_color_a);
			consumer.vertex(transform, v4_position_x, v4_position_y, v4_position_z).color(v4_color_r, v4_color_g, v4_color_b, v4_color_a);
			consumer.vertex(transform, v0_position_x, v0_position_y, v0_position_z).color(v0_color_r, v0_color_g, v0_color_b, v0_color_a);
		}
	}

	public static void _emit_line__2xposition_color_normal(final MatrixStack.Entry transform, final VertexConsumer consumer, float v0_position_x, float v0_position_y, float v0_position_z, float v0_color_r, float v0_color_g, float v0_color_b, float v0_color_a, float v0_normal_nx, float v0_normal_ny, float v0_normal_nz, float v1_position_x, float v1_position_y, float v1_position_z, float v1_color_r, float v1_color_g, float v1_color_b, float v1_color_a, float v1_normal_nx, float v1_normal_ny, float v1_normal_nz) {
		{
			// Vertices: 0-1
			consumer.vertex(transform, v0_position_x, v0_position_y, v0_position_z).color(v0_color_r, v0_color_g, v0_color_b, v0_color_a).normal(transform, v0_normal_nx, v0_normal_ny, v0_normal_nz);
			consumer.vertex(transform, v1_position_x, v1_position_y, v1_position_z).color(v1_color_r, v1_color_g, v1_color_b, v1_color_a).normal(transform, v1_normal_nx, v1_normal_ny, v1_normal_nz);
		}
	}
}
