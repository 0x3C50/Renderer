package me.x150.renderer.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class GlIdTexturing extends RenderPhase.TextureBase {

	private final GpuTexture glTex;

	public GlIdTexturing(GpuTexture glId, boolean linear) {
		super(() -> {
			FilterMode filter = linear ? FilterMode.LINEAR : FilterMode.NEAREST;
			glId.setTextureFilter(filter, filter, false);
			RenderSystem.setShaderTexture(0, glId);
		}, () -> {
		});
		this.glTex = glId;
	}

	@Override
	public String toString() {
		return this.name + "[" + this.glTex + "]";
	}

	@Override
	protected Optional<Identifier> getId() {
		return Optional.empty(); // cant produce Identifier for this glId so we're just gonna have to live with it
	}
}
