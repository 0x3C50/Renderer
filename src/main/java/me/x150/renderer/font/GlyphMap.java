package me.x150.renderer.font;

import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import lombok.RequiredArgsConstructor;
import me.x150.renderer.util.RendererUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
class GlyphMap {
	private static final int PADDING = 5; // 5 px padding per char
	final char fromIncl, toExcl;
	final Font[] font;
	final Identifier bindToTexture;
	private final Char2ObjectArrayMap<Glyph> glyphs = new Char2ObjectArrayMap<>();
	int width, height;

	boolean generated = false;

	public Glyph getGlyph(char c) {
		if (!generated) {
			generate();
		}
		return glyphs.get(c);
	}

	public void destroy() {
		MinecraftClient.getInstance().getTextureManager().destroyTexture(this.bindToTexture);
		this.glyphs.clear();
		this.width = -1;
		this.height = -1;
		generated = false;
	}

	public boolean contains(char c) {
		return c >= fromIncl && c < toExcl;
	}

	private Font getFontForGlyph(char c) {
		for (Font font1 : this.font) {
			if (font1.canDisplay(c)) {
				return font1;
			}
		}
		return this.font[0]; // no font can display it, so it doesn't matter which one we pick; it'll always be missing
	}

	public void generate() {
		if (generated) {
			return;
		}
		int range = toExcl - fromIncl - 1;
		int charsVert = (int) (Math.ceil(Math.sqrt(range)) * 1.5);  // double as many chars wide as high
		glyphs.clear();
		int generatedChars = 0;
		int charNX = 0;
		int maxX = 0, maxY = 0;
		int currentX = 0, currentY = 0;
		int currentRowMaxY = 0;
		List<Glyph> glyphs1 = new ArrayList<>();
		AffineTransform af = new AffineTransform();
		FontRenderContext frc = new FontRenderContext(af, true, false);
		while (generatedChars <= range) {
			char currentChar = (char) (fromIncl + generatedChars);
			Font font = getFontForGlyph(currentChar);
			Rectangle2D stringBounds = font.getStringBounds(String.valueOf(currentChar), frc);

			int width = (int) Math.ceil(stringBounds.getWidth());
			int height = (int) Math.ceil(stringBounds.getHeight());
			generatedChars++;
			maxX = Math.max(maxX, currentX + width);
			maxY = Math.max(maxY, currentY + height);
			if (charNX >= charsVert) {
				currentX = 0;
				currentY += currentRowMaxY + PADDING; // add height of highest glyph, and reset
				charNX = 0;
				currentRowMaxY = 0;
			}
			currentRowMaxY = Math.max(currentRowMaxY, height); // calculate the highest glyph in this row
			glyphs1.add(new Glyph(currentX, currentY, width, height, currentChar, this));
			currentX += width + PADDING;
			charNX++;
		}
		BufferedImage bi = new BufferedImage(Math.max(maxX + PADDING, 1), Math.max(maxY + PADDING, 1),
				BufferedImage.TYPE_INT_ARGB);
		width = bi.getWidth();
		height = bi.getHeight();
		Graphics2D g2d = bi.createGraphics();
		g2d.setColor(new Color(255, 255, 255, 0));
		g2d.fillRect(0, 0, width, height);
		g2d.setColor(Color.WHITE);

		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		for (Glyph glyph : glyphs1) {
			g2d.setFont(getFontForGlyph(glyph.value()));
			FontMetrics fontMetrics = g2d.getFontMetrics();
			g2d.drawString(String.valueOf(glyph.value()), glyph.u(), glyph.v() + fontMetrics.getAscent());
			glyphs.put(glyph.value(), glyph);
		}
		RendererUtils.registerBufferedImageTexture(bindToTexture, bi);
		generated = true;
	}
}
