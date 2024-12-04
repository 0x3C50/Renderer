package me.x150.renderer.render;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.attributes.ViewBox;
import com.github.weisj.jsvg.parser.SVGLoader;
import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.renderer.RendererMain;
import me.x150.renderer.util.RendererUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.util.math.MatrixStack;
import org.intellij.lang.annotations.Language;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.nio.charset.StandardCharsets;

/**
 * An SVG renderer, that renders the image to a texture beforehand. Uses <a href="https://github.com/weisJ/jsvg">JSVG</a> to parse and draw the SVG.
 * Usage example:
 * <pre>{@code
 * SVGFile svg = new SVGFile("svg source here", 128, 128);
 * // hud render event
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
	AbstractTexture id;
	boolean isMcTexture;

	/**
	 * Creates a new SVG file. The SVG is only parsed when {@link #render(MatrixStack, double, double, float, float)} is called.
	 *
	 * @param svgSource The SVG to draw
	 * @param width     Width of the image to render to. This is automatically adjusted, based on gui scale.
	 * @param height    Height of the image to render to. This is automatically adjusted, based on gui scale.
	 */
	public SVGFile(@Language("SVG") String svgSource, int width, int height) {
		this.svgSource = svgSource;
		this.originalWidth = width;
		this.originalHeight = height;
	}

	private void _redraw(float width, float height) {
		if (this.id != null) {
			close(); // destroy texture
		}

		try {
			SVGLoader loader = new SVGLoader();
			SVGDocument doc = loader.load(new ByteArrayInputStream(svgSource.getBytes(StandardCharsets.UTF_8)));
			assert doc != null;

			BufferedImage bi = new BufferedImage((int) Math.ceil(width), (int) Math.ceil(height), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bi.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

			doc.render(null, g, new ViewBox(width, height));

			g.dispose();

			this.id = RendererUtils.bufferedImageToNIBT(bi);
		} catch (Throwable t) {
			RendererMain.LOGGER.error("Failed to render SVG", t);
			this.isMcTexture = true;
			this.id = MinecraftClient.getInstance().getTextureManager().getTexture(MissingSprite.getMissingSpriteId());
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
		if (!RendererUtils.isSkipSetup()) {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
		}
		// the actual image is ceil(dims), but is actually only drawn to the raw dims. use ceil(dims) for pixel perfect accuracy without overflowing the bounds
		RenderSystem.setShaderTexture(0, this.id.getGlId());
		Renderer2d.renderTexture(stack, x, y, Math.ceil(renderWidth), Math.ceil(renderHeight));
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
		// this might be minecraft's "missingno" texture, we don't want to free that.

		if (!this.isMcTexture) {
			this.id.close();
		}
		this.id = null;
	}
}
