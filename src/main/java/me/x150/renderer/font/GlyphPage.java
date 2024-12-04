package me.x150.renderer.font;

import lombok.extern.slf4j.Slf4j;
import me.x150.renderer.util.RendererUtils;
import net.minecraft.client.texture.NativeImageBackedTexture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class GlyphPage {
	private static final Path DUMP_PATH;
	private static final int PADDING = 2;

	static {
		String property = System.getProperty("renderer.dumpGlyphMapsPath");
		if (property != null) {
			DUMP_PATH = Path.of(property);
			log.info("Dumping glyph maps to dir '{}'", DUMP_PATH);
		} else {
			DUMP_PATH = null;
		}
	}

	final char fromIncl, toExcl;
	final Font font;
	private final Glyph[] glyphs;
	NativeImageBackedTexture texture;
	int width, height;

	boolean generated = false;

	public GlyphPage(char fromIncl, char toExcl, Font font) {
		this.fromIncl = fromIncl;
		this.toExcl = toExcl;
		this.font = font;
		this.glyphs = new Glyph[toExcl - fromIncl];
	}

	public Glyph getGlyph(char c) {
		synchronized (this) {
			if (!generated) {
				privateGenerate();
			}
			return glyphs[c - fromIncl];
		}
	}

	public void destroy() {
		synchronized (this) {
			generated = false;
			if (texture != null) texture.close();
			Arrays.fill(glyphs, null);
			this.width = -1;
			this.height = -1;
		}
	}

	public boolean contains(char c) {
		return c >= fromIncl && c < toExcl;
	}

	public void generate() {
		synchronized (this) {
			privateGenerate();
		}
	}

	private void privateGenerate() {
		if (generated) {
			return;
		}
		int maxX = 0, maxY;
		int currentX = 0, currentY = 0;
		int currentRowMaxY = 0;
		List<Glyph> glyphs1 = new ArrayList<>();
		AffineTransform af = new AffineTransform();
		FontRenderContext frc = new FontRenderContext(af, true, false);
		FontMetrics fm = FontMetricsAccessor.getMetrics(font);
		List<PreGlyphRegion> glyphRegions = new ArrayList<>();
		for (char currentChar = fromIncl; currentChar < toExcl; currentChar++) {
			if (!font.canDisplay(currentChar)) continue;
			TextLayout layout = new TextLayout(String.valueOf(currentChar), font, frc);
			Rectangle2D bounds = layout.getBounds();
			PreGlyphRegion pgr = new PreGlyphRegion(bounds.getWidth(), bounds.getHeight(), -bounds.getX(), -bounds.getY(), layout, currentChar);
			glyphRegions.add(pgr);
		}
		double optimalWidth = glyphRegions.stream().mapToDouble(it -> it.width + 4).sum();
		// find optimal width to balance width and height, for a near-1:1 texture
		// max 10 attempts or until the delta between width and height is below 50 pixels
		for (int i = 0; i < 10; i++) {
			double heightWithThatWidth = 0;
			double fx = 0;
			double maxHeightHere = 0;
			for (PreGlyphRegion glyphRegion : glyphRegions) {
				if (fx > optimalWidth) {
					heightWithThatWidth += maxHeightHere;
					maxHeightHere = 0;
					fx = 0;
				}
				maxHeightHere = Math.max(maxHeightHere, glyphRegion.height);
				fx += glyphRegion.width + PADDING * 2 + 1;
			}
			heightWithThatWidth += maxHeightHere; // account for last line
			if (Math.abs(optimalWidth - heightWithThatWidth) < 50) break; // good enough
			optimalWidth = Math.ceil((heightWithThatWidth + optimalWidth) / 2f); // try again with that width, to slowly balance the dims out
		}
		for (PreGlyphRegion glyphRegion : glyphRegions) {
			if (currentX >= optimalWidth) {
				currentY += currentRowMaxY;
				currentRowMaxY = 0;
				maxX = Math.max(maxX, currentX);
				currentX = 0;
			}

			double drawAtX = currentX + glyphRegion.tlToBaselineX + PADDING;
			double drawAtY = currentY + glyphRegion.tlToBaselineY + PADDING;

			float theCharAscent = glyphRegion.layout.getAscent();
			int theNormalAscent = fm.getAscent();
			float ascentAdd = theNormalAscent - theCharAscent;

			glyphs1.add(new Glyph(currentX, currentY,
					glyphRegion.width, glyphRegion.height,
					ascentAdd,
					drawAtX, drawAtY, (int) Math.ceil(this.font.getStringBounds(String.valueOf(glyphRegion.c), frc).getWidth()),
					glyphRegion.c, glyphRegion, this));

			int height = (int) (Math.ceil(glyphRegion.height) + PADDING * 2 + 1);
			currentRowMaxY = Math.max(currentRowMaxY, height);
			currentX += (int) (Math.ceil(glyphRegion.width) + PADDING * 2 + 1);
		}

		maxY = currentY + currentRowMaxY;

		BufferedImage bi = new BufferedImage(Math.max(maxX, 1), Math.max(maxY, 1),
				BufferedImage.TYPE_INT_ARGB);
		width = bi.getWidth();
		height = bi.getHeight();
		Graphics2D g2d = bi.createGraphics();
		g2d.setColor(new Color(255, 255, 255, 0));
		g2d.fillRect(0, 0, width, height);
		g2d.setColor(Color.WHITE);

		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g2d.setFont(font);
		//		FontMetrics fontMetrics = g2d.getFontMetrics();
		for (Glyph glyph : glyphs1) {
			glyph.glyphRegion().layout.draw(g2d, (float) glyph.baselineX(), (float) glyph.baselineY());
			glyphs[glyph.value() - fromIncl] = glyph;
		}
		if (DUMP_PATH != null) {
			Path dmpD = DUMP_PATH.resolve(font.getFontName());
			Path to = dmpD.resolve(String.format("page %d to %d.png", (int) fromIncl, (int) toExcl));
			try {
				Files.createDirectories(dmpD);
				ImageIO.write(bi, "png", to.toFile());
			} catch (IOException e) {
				log.error("couldn't dump to '{}'", to, e);
			}
		}
		this.texture = RendererUtils.bufferedImageToNIBT(bi);
		generated = true;
	}

	record PreGlyphRegion(double width, double height, double tlToBaselineX, double tlToBaselineY, TextLayout layout,
						  char c) {
	}
}
