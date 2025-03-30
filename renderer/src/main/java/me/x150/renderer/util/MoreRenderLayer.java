package me.x150.renderer.util;

import com.mojang.blaze3d.textures.GpuTexture;
import org.joml.Vector4f;

public interface MoreRenderLayer {
	default void setUniform(String u, Vector4f v4f) {
		float[] v = new float[]{v4f.x, v4f.y, v4f.z, v4f.w};
		setUniform(u, v);
	}

	void setUniform(String u, Object v);

	void setSampler(String u, GpuTexture v);
}
