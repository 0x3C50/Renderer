package me.x150.renderer.util;

import com.mojang.blaze3d.textures.GpuTextureView;
import org.joml.Vector4f;

/**
 * Duck interface for {@link net.minecraft.client.render.RenderLayer} with uniform-related utilities
 */
public interface MoreRenderLayer {
	/**
	 * Set uniform u to the value described by v4f, formatted as a vec4 (4 floats)
	 * @param u Name
	 * @param v4f Value
	 */
	default void setUniform(String u, Vector4f v4f) {
		float[] v = new float[]{v4f.x, v4f.y, v4f.z, v4f.w};
		setUniform(u, v);
	}

	/**
	 * Set uniform u to the value of v, should be an int[], float[] or Matrix4f
	 * @param u Name
	 * @param v Value
	 */
	void setUniform(String u, Object v);

	/**
	 * Set sampler uniform to the given texture
	 * @param u Sampler name
	 * @param v Texture
	 */
	void setSampler(String u, GpuTextureView v);
}
