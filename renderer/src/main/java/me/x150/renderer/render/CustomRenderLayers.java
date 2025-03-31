package me.x150.renderer.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import me.x150.renderer.util.Color;
import me.x150.renderer.util.MoreRenderLayer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector4f;

import java.util.OptionalDouble;
import java.util.function.Function;

import static net.minecraft.client.render.RenderPhase.*;

/**
 * Custom or extended RenderLayers
 */
public class CustomRenderLayers {
	private static final RenderPipeline TEXT_CUSTOM_PIPELINE = RenderPipelines.register(
			RenderPipeline.builder(RenderPipelines.TEXT_SNIPPET, RenderPipelines.FOG_SNIPPET)
					.withLocation(Identifier.of("renderer", "pipeline/text_lumi"))
					.withVertexShader(Identifier.of("renderer", "core/custom_text"))
					.withFragmentShader(Identifier.of("renderer", "core/custom_text"))
					.withSampler("Sampler0")
					.withSampler("Sampler2")
					.withDepthBias(-1.0F, -10.0F)
					.build()
	);
	/**
	 * Text drawing via luminosity, with more lenient alpha cutoff and proper transparency.
	 * <table>
	 *     <caption>Render layer settings</caption>
	 *     <tr>
	 *         <th>Vertex Format</th>
	 *         <th>Draw Mode</th>
	 *         <th>Pipeline</th>
	 *     </tr>
	 *     <tr>
	 *         <td>{@link VertexFormats#POSITION_COLOR_TEXTURE_LIGHT}</td>
	 *         <td>{@link com.mojang.blaze3d.vertex.VertexFormat.DrawMode#QUADS}</td>
	 *         <td>{@link #TEXT_CUSTOM_PIPELINE}</td>
	 *     </tr>
	 * </table>
	 */
	public static final Function<GpuTexture, RenderLayer> TEXT_CUSTOM = Util.memoize(texture -> RenderLayer.of(
			"renderer/text_intensity_custom",
			1024,
			false,
			false, TEXT_CUSTOM_PIPELINE,
			RenderLayer.MultiPhaseParameters.builder()
					.texture(new GlIdTexturing(texture, false))
					.lightmap(ENABLE_LIGHTMAP)
					.build(false)));

	/**
	 * Position, color quads, with depth test set to ALWAYS
	 * <table>
	 *     <caption>Render layer settings</caption>
	 *     <tr>
	 *         <th>Vertex Format</th>
	 *         <th>Draw Mode</th>
	 *         <th>Pipeline</th>
	 *     </tr>
	 *     <tr>
	 *         <td>{@link VertexFormats#POSITION_COLOR}</td>
	 *         <td>{@link com.mojang.blaze3d.vertex.VertexFormat.DrawMode#QUADS}</td>
	 *         <td>(inline definition)</td>
	 *     </tr>
	 * </table>
	 */
	public static final RenderLayer POS_COL_QUADS_NO_DEPTH_TEST = RenderLayer.of(
			"renderer/always_depth_pos_color",
			1024,
			false, true,
			RenderPipelines.register(RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
						.withLocation(Identifier.of("renderer", "pipeline/pos_col_quads_nodepth"))
						.withCull(true)
						.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
						.withDepthWrite(true)
						.build()
			),
			RenderLayer.MultiPhaseParameters.builder()
					.build(false)
	);
	/**
	 * Position, color quads, with depth test set to GL_LEQUAL (default)
	 * <table>
	 *     <caption>Render layer settings</caption>
	 *     <tr>
	 *         <th>Vertex Format</th>
	 *         <th>Draw Mode</th>
	 *         <th>Pipeline</th>
	 *     </tr>
	 *     <tr>
	 *         <td>{@link VertexFormats#POSITION_COLOR}</td>
	 *         <td>{@link com.mojang.blaze3d.vertex.VertexFormat.DrawMode#QUADS}</td>
	 *         <td>(inline definition)</td>
	 *     </tr>
	 * </table>
	 */
	public static final RenderLayer POS_COL_QUADS_WITH_DEPTH_TEST = RenderLayer.of(
			"renderer/lequal_depth_pos_color",
			1024,
			false, true,
			RenderPipelines.register(RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
						.withLocation(Identifier.of("renderer", "pipeline/pos_col_quads_depth"))
						.withCull(true)
						.withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
						.withDepthWrite(true)
						.build()
			),
			RenderLayer.MultiPhaseParameters.builder()
					.build(false)
	);


	private static final RenderPipeline NDT_LINES_NODEPTH = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.RENDERTYPE_LINES_SNIPPET)
			.withLocation(Identifier.of("renderer", "pipeline/lines_nodepth"))
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.withDepthWrite(true)
			.withVertexFormat(VertexFormats.POSITION_COLOR_NORMAL, VertexFormat.DrawMode.LINES)

			.build()
	);
	/**
	 * Lines with a given width (or 0 for default), with depth test set to ALWAYS
	 * <table>
	 *     <caption>Render layer settings</caption>
	 *     <tr>
	 *         <th>Vertex Format</th>
	 *         <th>Draw Mode</th>
	 *         <th>Pipeline</th>
	 *     </tr>
	 *     <tr>
	 *         <td>{@link VertexFormats#POSITION_COLOR_NORMAL}</td>
	 *         <td>{@link com.mojang.blaze3d.vertex.VertexFormat.DrawMode#LINES}</td>
	 *         <td>{@link #NDT_LINES_NODEPTH}</td>
	 *     </tr>
	 * </table>
	 */
	public static final Function<Double, RenderLayer> LINES_NO_DEPTH_TEST = Util.memoize(width -> RenderLayer.of(
			"renderer/always_depth_lines",
			1024,
			false, true, NDT_LINES_NODEPTH,
			RenderLayer.MultiPhaseParameters.builder()
					.lineWidth(new LineWidth(width == 0d ? OptionalDouble.empty() : OptionalDouble.of(width)))
					.build(false)
	));

	private static final RenderPipeline NDT_LINES_DEPTH = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.RENDERTYPE_LINES_SNIPPET)
			.withLocation(Identifier.of("renderer", "pipeline/lines_depth"))
			.withVertexFormat(VertexFormats.POSITION_COLOR_NORMAL, VertexFormat.DrawMode.LINES)
			.build()
	);
	/**
	 * Lines with a given width (or 0 for default), with depth test set to GL_LEQUAL (default)
	 * <table>
	 *     <caption>Render layer settings</caption>
	 *     <tr>
	 *         <th>Vertex Format</th>
	 *         <th>Draw Mode</th>
	 *         <th>Pipeline</th>
	 *     </tr>
	 *     <tr>
	 *         <td>{@link VertexFormats#POSITION_COLOR_NORMAL}</td>
	 *         <td>{@link com.mojang.blaze3d.vertex.VertexFormat.DrawMode#LINES}</td>
	 *         <td>{@link #NDT_LINES_DEPTH}</td>
	 *     </tr>
	 * </table>
	 */
	public static final Function<Double, RenderLayer> LINES = Util.memoize(width -> RenderLayer.of(
			"renderer/lines",
			1024,
			false, true, NDT_LINES_DEPTH,
			RenderLayer.MultiPhaseParameters.builder()
					.lineWidth(new LineWidth(width == 0d ? OptionalDouble.empty() : OptionalDouble.of(width)))
					.build(false)
	));

	/**
	 * Position, (texture), color quads, with a shader producing ellipses instead of quads.
	 * The texture component is used by the shader to determine the position of the current vertex.
	 * No texture is being sampled.
	 * <table>
	 *     <caption>Render layer settings</caption>
	 *     <tr>
	 *         <th>Vertex Format</th>
	 *         <th>Draw Mode</th>
	 *         <th>Pipeline</th>
	 *     </tr>
	 *     <tr>
	 *         <td>{@link VertexFormats#POSITION_TEXTURE_COLOR}</td>
	 *         <td>{@link com.mojang.blaze3d.vertex.VertexFormat.DrawMode#QUADS}</td>
	 *         <td>(inline definition)</td>
	 *     </tr>
	 * </table>
	 */
	public static final RenderLayer ELLIPSE_QUADS = RenderLayer.of(
			"renderer/2d/quad_ellipse",
			1024,
			false,
			false,
			RenderPipelines.register(RenderPipeline.builder(RenderPipelines.MATRICES_COLOR_SNIPPET)
					.withVertexShader("core/position_tex_color")
					.withBlend(BlendFunction.TRANSLUCENT)
					.withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR, VertexFormat.DrawMode.QUADS)
					.withCull(true)
					.withFragmentShader(Identifier.of("renderer", "core/rendertype_ellipse"))
					.withLocation(Identifier.of("renderer", "pipeline/2d/quad_ellipse"))
					.build()),
			RenderLayer.MultiPhaseParameters.builder()
					.build(false)
	);
	private static final RenderPipeline NDT_RR = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.MATRICES_COLOR_SNIPPET)
			.withBlend(BlendFunction.TRANSLUCENT)
			.withVertexFormat(VertexFormat.builder()
					.add("Position", VertexFormatElement.POSITION)
					.add("UV0", VertexFormatElement.UV0)
					.add("UV1", VertexFormatElement.register(6, 0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2))
					.add("Color", VertexFormatElement.COLOR)
					.build(), VertexFormat.DrawMode.QUADS)
			.withCull(true)
			.withFragmentShader(Identifier.of("renderer", "core/rendertype_rr"))
			.withVertexShader(Identifier.of("renderer", "core/rendertype_rr"))
			.withLocation(Identifier.of("renderer", "pipeline/2d/quad_ellipse"))
			.build());
	/**
	 * Position, (texture, texture), color quads, with a shader producing rounded rects with a given "roundness" instead of quads.
	 * The texture components are used by the shader to determine the position of the current vertex.
	 * No texture is being sampled.
	 * This Render layer should ONLY be used by {@link ExtendedDrawContext#drawRoundedRect(DrawContext, float, float, float, float, Vector4f, Color)},
	 * or by you if you know what you're doing.
	 * <table>
	 *     <caption>Render layer settings</caption>
	 *     <tr>
	 *         <th>Vertex Format</th>
	 *         <th>Draw Mode</th>
	 *         <th>Pipeline</th>
	 *     </tr>
	 *     <tr>
	 *         <td>POSITION_TEXTURE_TEXTURE_COLOR (inline definition)</td>
	 *         <td>{@link com.mojang.blaze3d.vertex.VertexFormat.DrawMode#QUADS}</td>
	 *         <td>{@link #NDT_RR}</td>
	 *     </tr>
	 * </table>
	 */
	@ApiStatus.Internal
	public static final Function<Vector4f, RenderLayer> ROUNDED_RECT = Util.memoize(vector4f -> {
		RenderLayer rl = RenderLayer.of(
				"renderer/2d/quad_rounded",
				1024,
				false,
				false,
				NDT_RR,
				RenderLayer.MultiPhaseParameters.builder()
						.build(false)
		);
		((MoreRenderLayer) rl).setUniform("Roundness", vector4f);
		return rl;
	});

	/**
	 * Gets a RenderLayer accepting Position, Color in a Quad format.
	 * @param throughWalls Should the RenderLayer draw through walls?
	 * @return RenderLayer
	 */
	public static RenderLayer getPositionColorQuads(boolean throughWalls) {
		if (throughWalls) return POS_COL_QUADS_NO_DEPTH_TEST;
		else return POS_COL_QUADS_WITH_DEPTH_TEST;
	}

	/**
	 * Gets a RenderLayer accepting Position, Normal, Color in a Line format.
	 * @param width Width of the lines being drawn, or 0 for default.
	 * @param throughWalls Should the RenderLayer draw through walls?
	 * @return RenderLayer
	 */
	public static RenderLayer getLines(float width, boolean throughWalls) {
		if (throughWalls) return LINES.apply((double) width);
		else return LINES_NO_DEPTH_TEST.apply((double) width);
	}

}
