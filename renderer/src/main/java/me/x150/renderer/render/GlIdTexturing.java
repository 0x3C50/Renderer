package me.x150.renderer.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.util.Identifier;

import java.util.Optional;

/**
 * RenderLayer texturing based on an existing GpuTexture
 */
public class GlIdTexturing extends RenderPhase.TextureBase {

	private final GpuTexture glTex;

	/**
	 * Constructor.
	 * @param glId GpuTexture to bind to texture 0
	 * @param linear Bilinear sampling? NEAREST otherwise
	 */
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
