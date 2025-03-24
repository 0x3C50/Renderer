package me.x150.renderer.fontng;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class GlIdTexturing extends RenderPhase.TextureBase {

	private final int glId;

	public GlIdTexturing(int glId, boolean linear) {
		super(() -> {
			GlStateManager._bindTexture(glId);
			int filter = linear ? GlConst.GL_LINEAR : GlConst.GL_NEAREST;
			GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MIN_FILTER, filter);
			GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MAG_FILTER, filter);
			RenderSystem.setShaderTexture(0, glId);
		}, () -> {});
		this.glId = glId;
	}

	@Override
	public String toString() {
		return this.name + "[" + this.glId + "]";
	}

	@Override
	protected Optional<Identifier> getId() {
		return Optional.empty(); // cant produce Identifier for this glId so we're just gonna have to live with it
	}
}
