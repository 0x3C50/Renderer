package me.x150.renderer.render;

import me.x150.renderer.client.RendererMain;
import me.x150.renderer.util.RendererUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.StringReader;

/**
 * An SVG renderer, that renders the image to a texture beforehand. Uses <a href="https://xmlgraphics.apache.org/batik/">Apache Batik</a> to parse and draw the SVG.
 * Usage example:
 * <pre>{@code
 * SVGFile svg = new SVGFile("svg source here", 128, 128);
 * // hud render event
 * @MessageSubscription
 * void onHud(RenderEvent.Hud hud) {
 *     svg.render(hud.getMatrixStack(), 10, 10, 128, 128);
 * }
 * }</pre>
 */
@SuppressWarnings("unused")
public class SVGFile implements Closeable {
	final String svgSource;
	final int originalWidth;
	final int originalHeight;
	int memoizedGuiScale = -1; // default of -1 means that the svg will get redrawn the first time when render() is called, no matter what
	Identifier id;

	/**
	 * Creates a new SVG file. The SVG is only parsed when {@link #render(MatrixStack, double, double, float, float)} is called.
	 *
	 * @param svgSource The SVG to draw
	 * @param width     Width of the image to render to. This is automatically adjusted, based on gui scale.
	 * @param height    Height of the image to render to. This is automatically adjusted, based on gui scale.
	 */
	public SVGFile(String svgSource, int width, int height) {
		this.svgSource = svgSource;
		this.originalWidth = width;
		this.originalHeight = height;
	}

	private void _redraw(float width, float height) {
		if (this.id != null) {
			close(); // destroy texture
		}
		this.id = RendererUtils.randomIdentifier();
		PNGTranscoder transcoder = new PNGTranscoder();
		transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, width);
		transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, height);
		TranscoderInput transcoderInput = new TranscoderInput(new StringReader(svgSource));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TranscoderOutput transcoderOutput = new TranscoderOutput(out);
		try {
			transcoder.transcode(transcoderInput, transcoderOutput);
			byte[] t = out.toByteArray();
			NativeImageBackedTexture tex = new NativeImageBackedTexture(NativeImage.read(new ByteArrayInputStream(t)));
			MinecraftClient.getInstance()
					.execute(() -> MinecraftClient.getInstance().getTextureManager().registerTexture(this.id, tex));
		} catch (Throwable t) {
			RendererMain.LOGGER.error("Failed to render SVG", t);
			//noinspection SpellCheckingInspection
			this.id = new Identifier("missingno"); // yes, this is real. this points to the "missing" texture
		}
	}

	/**
	 * Renders this SVG onto the screen
	 *
	 * @param stack        MatrixStack
	 * @param x            X coordinate
	 * @param y            Y coordinate
	 * @param renderWidth  Width of the rendered texture. This should be the same as used in the constructor for best results
	 * @param renderHeight Height of the rendered texture. This should be the same as used in the constructor for best results
	 */
	public void render(MatrixStack stack, double x, double y, float renderWidth, float renderHeight) {
		int guiScale = RendererUtils.getGuiScale();
		if (this.memoizedGuiScale != guiScale || this.id == null) { // need to remake the texture
			this.memoizedGuiScale = guiScale;
			_redraw(this.originalWidth * this.memoizedGuiScale, this.originalHeight * this.memoizedGuiScale);
		}
		Renderer2d.renderTexture(stack, this.id, x, y, renderWidth, renderHeight);
	}

	/**
	 * "Closes" this SVG file, freeing the cached texture, if it exists.
	 *
	 * @throws IllegalStateException When the texture is already freed
	 */
	@SuppressWarnings("SpellCheckingInspection")
	@Override
	public void close() {
		if (this.id == null) {
			throw new IllegalStateException("Already closed");
		}
		if (this.id.getNamespace().equals("renderer")) {
			// this might be minecraft's "missingno" texture, we don't want to free that.
			MinecraftClient.getInstance().getTextureManager().destroyTexture(this.id);
		}
		this.id = null;
	}
}
