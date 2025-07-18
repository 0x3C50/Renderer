package me.x150.renderer.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.ApiStatus;

import java.util.OptionalDouble;
import java.util.function.Function;

import static net.minecraft.client.render.RenderPhase.*;

/**
 * Custom or extended RenderLayers
 */
public class CustomRenderLayers {
	/**
	 * Pipeline rendering text, interpreting the red channel as alpha and applying fog
	 */
	public static final RenderPipeline PIPELINE_TEXT_CUSTOM = RenderPipelines.register(
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


	private static final RenderPipeline LINES_NODEPTH_PIPELINE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.RENDERTYPE_LINES_SNIPPET)
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
	 *         <td>{@link #LINES_NODEPTH_PIPELINE}</td>
	 *     </tr>
	 * </table>
	 */
	public static final Function<Double, RenderLayer> LINES_NO_DEPTH_TEST = Util.memoize(width -> RenderLayer.of(
			"renderer/always_depth_lines",
			1024,
			false, true, LINES_NODEPTH_PIPELINE,
			RenderLayer.MultiPhaseParameters.builder()
					.lineWidth(new LineWidth(width == 0d ? OptionalDouble.empty() : OptionalDouble.of(width)))
					.build(false)
	));

	/**
	 * Pipeline accepting lines, drawing with depth test
	 */
	public static final RenderPipeline LINES_DEPTH_PIPELINE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.RENDERTYPE_LINES_SNIPPET)
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
	 *         <td>{@link #LINES_DEPTH_PIPELINE}</td>
	 *     </tr>
	 * </table>
	 */
	public static final Function<Double, RenderLayer> LINES = Util.memoize(width -> RenderLayer.of(
			"renderer/lines",
			1024,
			false, true, LINES_DEPTH_PIPELINE,
			RenderLayer.MultiPhaseParameters.builder()
					.lineWidth(new LineWidth(width == 0d ? OptionalDouble.empty() : OptionalDouble.of(width)))
					.build(false)
	));

	/**
	 * Pipeline accepting quads, drawing a circle according to texture coordinates given
	 */
	public static final RenderPipeline ELLIPSE_PIPELINE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
			.withBlend(BlendFunction.TRANSLUCENT)
			.withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR, VertexFormat.DrawMode.QUADS)
			.withCull(true)
			.withFragmentShader(Identifier.of("renderer", "core/rendertype_ellipse"))
			.withVertexShader("core/position_tex_color")
			.withLocation(Identifier.of("renderer", "pipeline/2d/quad_ellipse"))
			.build());

	/**
	 * Pipeline drawing the specified quads as rounded rect, this is very specific, don't use this manually if you dont know what you're doing!
	 */
	@ApiStatus.Internal
	public static final RenderPipeline RR_PIPELINE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
			.withBlend(BlendFunction.TRANSLUCENT)
			.withVertexFormat(VertexFormat.builder()
					.add("Position", VertexFormatElement.POSITION)
					.add("UV0", VertexFormatElement.UV0)
					.add("UV1", VertexFormatElement.register(getNextVFId(), 0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2))
					.add("Roundness", VertexFormatElement.register(getNextVFId(), 0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 4))
					.add("Color", VertexFormatElement.COLOR)
					.build(), VertexFormat.DrawMode.QUADS)
			.withCull(true)
			.withFragmentShader(Identifier.of("renderer", "core/rendertype_rr"))
			.withVertexShader(Identifier.of("renderer", "core/rendertype_rr"))
			.withLocation(Identifier.of("renderer", "pipeline/2d/quad_rr"))
			.build());

	private static int getNextVFId() {
		for(int i = 0; i < VertexFormatElement.MAX_COUNT; i++) {
			if (VertexFormatElement.byId(i) == null) return i;
		}
		throw new IllegalStateException("No more free VertexFormatElement slots");
	}

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
		if (throughWalls) return LINES_NO_DEPTH_TEST.apply((double) width);
		else return LINES.apply((double) width);
	}
}
