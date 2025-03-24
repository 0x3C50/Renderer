package me.x150.renderer.fontng;

import me.x150.renderer.shader.ShaderManager;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Util;

import java.util.function.Function;

import static net.minecraft.client.render.RenderPhase.*;

public class CustomRenderLayers {
	public static final Function<Integer, RenderLayer> TEXT_CUSTOM = Util.memoize(texture -> RenderLayer.of(
			"text_intensity_custom",
			VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
			VertexFormat.DrawMode.QUADS,
			1024,
			false,
			false,
			RenderLayer.MultiPhaseParameters.builder()
					.program(new RenderPhase.ShaderProgram(ShaderManager.SPK_RENDERTYPE_TEXT_LUMI))
					.texture(new GlIdTexturing(texture, false))
					.transparency(RenderLayer.TRANSLUCENT_TRANSPARENCY)
					.lightmap(ENABLE_LIGHTMAP)
					.build(false)));
	public static final RenderLayer QUADS = RenderLayer.of(
			"quads_custom",
			VertexFormats.POSITION_COLOR,
			VertexFormat.DrawMode.QUADS,
			1024,
			false,
			false,
			RenderLayer.MultiPhaseParameters.builder()
					.program(new RenderPhase.ShaderProgram(ShaderProgramKeys.POSITION_COLOR))
					.transparency(RenderLayer.TRANSLUCENT_TRANSPARENCY)
					.build(false)
	);
}
