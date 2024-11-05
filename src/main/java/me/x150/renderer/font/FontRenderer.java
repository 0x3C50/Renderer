package me.x150.renderer.font;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.x150.renderer.util.BufferUtils;
import me.x150.renderer.util.Colors;
import me.x150.renderer.util.RendererUtils;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.awt.*;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/*
READERS BEWARE

you are entering the land of
FONT FUCKSHIT

there is no returning. after this point, you will forever be mesmerized at how fucking insane computer typography is
some of the shit in this file is so arcane, not even I, the creator, could coherently explain it with a gun to my head.
good luck.
*/

/**
 * A simple font renderer, supporting latin fonts passed in via a {@link Font} object.
 * <h2>Performance</h2>
 * Glyph maps are lazily computed when needed, to keep memory usage low until needed.
 * Creating glyph pages for large fonts takes a considerable amount of time.
 * If font size is high, it is recommended to decrease the amount of glyphs on each page, to both decrease memory usage
 * and computation time for each page, at the cost of requiring more pages in total to be created.
 * Additionally, characters for glyph pages which should immediately be available may be passed into {@link #FontRenderer(Font, float, int, String)},
 * to bake the glyph pages for those characters on another thread once the font renderer is initialized.
 */
@Slf4j
public class FontRenderer implements Closeable {
	protected static final ExecutorService ASYNC_WORKER = Executors.newCachedThreadPool();
	protected final Int2ObjectMap<ObjectList<DrawEntry>> GLYPH_PAGE_CACHE = new Int2ObjectOpenHashMap<>();
//	protected final ObjectList<DrawEntry> CURRENT_ALL_CHARS = new ObjectArrayList<>();
	protected final float originalSize;
	protected final int charsPerPage;
	protected final String prebakeGlyphs;
	protected GlyphMapPage pageNormal;
	protected GlyphMapPage pageItalic;
	protected GlyphMapPage pageBold;
	protected GlyphMapPage pageBoldItalic;
	protected float scaleMul = 0;
	protected Font font;
	protected int previousGameScale = -1;
	protected Future<Void> prebakeGlyphsFuture;
	protected boolean initialized;

	protected boolean roundCoordinates = false;

	private FontMetrics fontMetrics;

	/**
	 * Initializes a new FontRenderer with the specified font
	 *
	 * @param font              The font to use
	 * @param sizePx            The size of the font in minecraft pixel units. One pixel unit = `guiScale` pixels
	 * @param charactersPerPage How many characters one glyph page should contain. Default 256
	 * @param prebakeCharacters Characters to pre-bake off thread when the font is reinitialized. Glyph pages containing those characters will (most of the time) immediately be available when drawing.
	 */
	public FontRenderer(@NonNull Font font, float sizePx, int charactersPerPage, @Nullable String prebakeCharacters) {
		Preconditions.checkArgument(sizePx > 0, "sizePx <= 0");
		Preconditions.checkArgument(charactersPerPage > 4, "Unreasonable charactersPerPage count (< 4)");
//		Preconditions.checkArgument(paddingBetweenCharacters > 0, "paddingBetweenCharacters <= 0");
		this.originalSize = sizePx;
		this.charsPerPage = charactersPerPage;
		this.prebakeGlyphs = prebakeCharacters;
		init(font, sizePx);
	}

	/**
	 * Initializes a new FontRenderer with the specified fonts. Equivalent to {@link #FontRenderer(Font, float, int, String) FontRenderer}{@code (fonts, sizePx, 256, null)}
	 *
	 * @param font   The font to use
	 * @param sizePx The size of the font in minecraft pixel units. One pixel unit = `guiScale` pixels
	 */
	public FontRenderer(Font font, float sizePx) {
		this(font, sizePx, 256, null);
	}

	/**
	 * Strips all characters prefixed with a § from the given string
	 *
	 * @param text The string to strip
	 * @return The stripped string
	 */
	public static String stripControlCodes(String text) {
		char[] chars = text.toCharArray();
		StringBuilder f = new StringBuilder();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (c == '§') {
				i++;
				continue;
			}
			f.append(c);
		}
		return f.toString();
	}

	/**
	 * Rounds coordinates to pixel coordinates, to make sure the font is drawn correctly, to prevent artifacts
	 *
	 * @param round true to round coordinates, false to skip rounding (default)
	 * @return this
	 */
	@SuppressWarnings("UnusedReturnValue")
	public FontRenderer roundCoordinates(boolean round) {
		this.roundCoordinates = round;
		return this;
	}

	private void sizeCheck() {
		int gs = RendererUtils.getGuiScale();
		if (gs != this.previousGameScale) {
			close(); // delete glyphs and cache
			init(this.font, this.originalSize); // re-init
		}
	}

	protected void init(Font fonts, float sizePx) {
		if (initialized) throw new IllegalStateException("Double call to init()");
		synchronized (this) {
			this.previousGameScale = RendererUtils.getGuiScale();
			this.scaleMul = this.previousGameScale;
			float totalSize = sizePx * this.scaleMul;
			this.font = fonts.deriveFont(totalSize);
			this.fontMetrics = FontMetricsAccessor.getMetrics(this.font);
			this.pageNormal = new GlyphMapPage(font, charsPerPage);
			this.pageBold = new GlyphMapPage(font.deriveFont(Font.BOLD), charsPerPage);
			this.pageItalic = new GlyphMapPage(font.deriveFont(Font.ITALIC), charsPerPage);
			this.pageBoldItalic = new GlyphMapPage(font.deriveFont(Font.BOLD | Font.ITALIC), charsPerPage);
			if (prebakeGlyphs != null && !prebakeGlyphs.isEmpty()) {
				prebakeGlyphsFuture = this.prebake();
			}
			initialized = true;
		}
	}

	private Future<Void> prebake() {
		return ASYNC_WORKER.submit(() -> {
			log.debug("prebake on {}", Thread.currentThread());
			try {
				for (char c : prebakeGlyphs.toCharArray()) {
					if (Thread.interrupted()) break;
					locateGlyph0(c, false, false);
				}
			} finally {
				log.debug("prebake done");
			}
			return null;
		});
	}

	protected Glyph locateGlyph0(char glyph, boolean bold, boolean italic) {
		GlyphMapPage page;
		if (bold && italic) page = this.pageBoldItalic;
		else if (bold) page = this.pageBold;
		else if (italic) page = this.pageItalic;
		else page = this.pageNormal;
		GlyphMap map = page.getOrCreateMap(glyph);
		return map.getGlyph(glyph);
	}

	/**
	 * Draws a string
	 *
	 * @param stack MatrixStack
	 * @param s     String to draw
	 * @param x     X coordinate
	 * @param y     Y coordinate
	 * @param r     Red color component (0-1f)
	 * @param g     Green color component (0-1f)
	 * @param b     Blue color component (0-1f)
	 * @param a     Alpha component (0-1f)
	 * @deprecated Use {@link #drawText(MatrixStack, Text, float, float, float)} instead
	 */
	@Deprecated
	public void drawString(MatrixStack stack, String s, float x, float y, float r, float g, float b, float a) {
		int rgbColor = Colors.ARGBToInt((int) (r * 255f), (int) (g * 255f), (int) (b * 255f), 0);
		drawText(stack, Text.literal(s).styled(it -> it.withParent(Style.EMPTY.withColor(rgbColor))), x, y, a);
	}

	/**
	 * Draws a styled Text
	 *
	 * @param stack The MatrixStack
	 * @param s     Text to draw
	 * @param x     X coordinate to draw at
	 * @param y     Y coordinate to draw at
	 * @param a     Alpha component of the color (0-1f). Other colors are read from the Text style
	 */
	public void drawText(MatrixStack stack, Text s, float x, float y, float a) {
		if (prebakeGlyphsFuture != null && !prebakeGlyphsFuture.isDone()) {
			try {
				prebakeGlyphsFuture.get();
			} catch (InterruptedException | ExecutionException e) {
				// if failed, we just continue
				// the glyphs aren't created, we'll create them later
			}
		}
		sizeCheck();
		if (roundCoordinates) {
			x = (float) Math.round(x * scaleMul) / scaleMul;
			y = (float) Math.round(y * scaleMul) / scaleMul;
		}
		stack.push();
		stack.translate(x, y, 0);
		stack.scale(1f / this.scaleMul, 1f / this.scaleMul, 1f);

		if (!RendererUtils.isSkipSetup()) {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
		}

		RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
		Matrix4f mat = stack.peek().getPositionMatrix();
		final float[] xOffset = {0};
		final float[] yOffset = {0};
		OrderedText orderedText = s.asOrderedText();
		final boolean[] shouldDoLinePass = {false};
		List<List<DrawEntry>> lines = new ArrayList<>();
		lines.add(new ArrayList<>());
		synchronized (GLYPH_PAGE_CACHE) {
			 {
				orderedText.accept((index, style, codePoint) -> {
					char c = (char) codePoint;
					if (c == '\n') {
						yOffset[0] += fontMetrics.getHeight();
						xOffset[0] = 0;
						lines.add(new ArrayList<>());
						return true;
					}
					TextColor textColor = style.getColor();
					int rgbColor = textColor != null ? textColor.getRgb() : 0xFFFFFF;
					int[] colorRGBA = Colors.ARGBIntToRGBA(rgbColor);
					float r2 = colorRGBA[0] / 255f;
					float g2 = colorRGBA[1] / 255f;
					float b2 = colorRGBA[2] / 255f;
					boolean bold = style.isBold();
					boolean ital = style.isItalic();
					Glyph glyph = locateGlyph0(c, bold, ital);
					if (glyph.value() != ' ') { // we only need to really draw the glyph if it's not blank, otherwise we can just skip its width and that'll be it
						NativeImageBackedTexture i1 = glyph.owner().texture;
						DrawEntry entry = new DrawEntry(xOffset[0], yOffset[0], r2, g2, b2, style.isUnderlined(), style.isStrikethrough(), glyph);
						if (!shouldDoLinePass[0]) shouldDoLinePass[0] = style.isUnderlined() || style.isStrikethrough();
						lines.getLast().add(entry);
						GLYPH_PAGE_CACHE.computeIfAbsent(i1.getGlId(), integer -> new ObjectArrayList<>()).add(entry);
					}
					xOffset[0] += glyph.glyphRegion().layout().getAdvance();
					return true;
				});
				for (int glId : GLYPH_PAGE_CACHE.keySet()) {
					RenderSystem.setShaderTexture(0, glId);
					List<DrawEntry> objects = GLYPH_PAGE_CACHE.get(glId);

					BufferBuilder bb = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

					for (DrawEntry object : objects) {
						float xo = object.atX;
						float yo = object.atY;
						float cr = object.r;
						float cg = object.g;
						float cb = object.b;
						Glyph glyph = object.toDraw;
						GlyphMap owner = glyph.owner();
						float hOf = (float) (fontMetrics.getAscent() - object.toDraw.glyphRegion().tlToBaselineY());
						float xOf = (float) -object.toDraw.glyphRegion().tlToBaselineX();
						float w = (float) glyph.texW();
						float h = (float) glyph.texH();
						float u1 = (float) ((glyph.tlX()-2) / owner.width);
						float v1 = (float) ((glyph.tlY()-2) / owner.height);
						float u2 = (float) ((glyph.tlX() + glyph.texW() + 2) / owner.width);
						float v2 = (float) ((glyph.tlY() + glyph.texH() + 2) / owner.height);

						bb.vertex(mat, xo + 0-2+xOf, yo + h+2+hOf, 0).texture(u1, v2).color(cr, cg, cb, a);
						bb.vertex(mat, xo + w+2+xOf, yo + h+2+hOf, 0).texture(u2, v2).color(cr, cg, cb, a);
						bb.vertex(mat, xo + w+2+xOf, yo + 0-2+hOf, 0).texture(u2, v1).color(cr, cg, cb, a);
						bb.vertex(mat, xo + 0-2+xOf, yo + 0-2+hOf, 0).texture(u1, v1).color(cr, cg, cb, a);
					}
					BufferUtils.draw(bb);
				}

				if (shouldDoLinePass[0]) {
					final float strikeHeight = (originalSize / 16f) * scaleMul;
					RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
					BufferBuilder linesBuffer = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);

					boolean strikethroughEnabled = false, underlineEnabled = false;
					float strikethroughStartX = 0, underlineStartX = 0;

					for (List<DrawEntry> line : lines) {
						for (int i = 0; i < line.size(); i++) {
							DrawEntry currentAllChar = line.get(i);
							boolean nextIsNewline = i == line.size()-1;
							float tlX = currentAllChar.atX;
							float tlY = currentAllChar.atY;
							Glyph glyph = currentAllChar.toDraw;
							float hOf = (float) (fontMetrics.getAscent());
							tlY += hOf; // tlY is at baseline
							tlX -= (float) glyph.glyphRegion().tlToBaselineX();

							if (currentAllChar.strike && !strikethroughEnabled) {
								// start strikethrough
								strikethroughEnabled = true;
								strikethroughStartX = tlX;
							} else if ((!currentAllChar.strike || nextIsNewline) && strikethroughEnabled) {
								// end strikethrough
								strikethroughEnabled = false;
								float endX = (float) (tlX + glyph.glyphRegion().width());
								float halfStrikeHeight = strikeHeight / 2f;
								float center = tlY - fontMetrics.getAscent() * 0.2f;
								linesBuffer.vertex(mat, strikethroughStartX, center + halfStrikeHeight, 0).color(1f, 1f, 1f, a);
								linesBuffer.vertex(mat, endX, center + halfStrikeHeight, 0).color(1, 1, 1, a);
								linesBuffer.vertex(mat, endX, center - halfStrikeHeight, 0).color(1, 1, 1, a);
								linesBuffer.vertex(mat, strikethroughStartX, center - halfStrikeHeight, 0).color(1, 1, 1, a);
							}

							if (currentAllChar.underline && !underlineEnabled) {
								underlineEnabled = true;
								underlineStartX = tlX;
							} else if ((!currentAllChar.underline || nextIsNewline) && underlineEnabled) {
								underlineEnabled = false;
								float endX = (float) (tlX + glyph.glyphRegion().width());

								float baseline = tlY + strikeHeight + 1f;
								linesBuffer.vertex(mat, underlineStartX, baseline, 0).color(1, 1, 1, a);
								linesBuffer.vertex(mat, endX, baseline, 0).color(1, 1, 1, a);
								linesBuffer.vertex(mat, endX, baseline - strikeHeight, 0).color(1, 1, 1, a);
								linesBuffer.vertex(mat, underlineStartX, baseline - strikeHeight, 0).color(1, 1, 1, a);
							}
						}

						strikethroughEnabled = underlineEnabled = false;
					}
					BufferUtils.draw(linesBuffer);
				}

				GLYPH_PAGE_CACHE.clear();
			}
		}
		lines.clear();
		stack.pop();
	}

	/**
	 * Draws a string centered on the X coordinate
	 *
	 * @param stack The MatrixStack
	 * @param s     The string to draw
	 * @param x     X center coordinate of the text to draw
	 * @param y     Y coordinate of the text to draw
	 * @param r     Red color component
	 * @param g     Green color component
	 * @param b     Blue color component
	 * @param a     Alpha color component
	 * @deprecated Use {@link #drawCenteredText(MatrixStack, Text, float, float, float)} instead
	 */
	@Deprecated
	public void drawCenteredString(MatrixStack stack, String s, float x, float y, float r, float g, float b, float a) {
		drawString(stack, s, x - getStringWidth(s) / 2f, y, r, g, b, a);
	}

	public void drawCenteredText(MatrixStack stack, Text s, float x, float y, float a) {
		drawText(stack, s, x - getTextWidth(s) / 2f, y, a);
	}

	/**
	 * Calculates the width of the string, if it were drawn on the screen. Accounts for newlines, IGNORES STYLE CODES (§)
	 *
	 * @param text The text to simulate
	 * @return The width of the string if it'd be drawn on the screen
	 * @deprecated Use {@link #getTextHeight(Text)}
	 */
	@Deprecated
	public float getStringWidth(String text) {
		char[] c = stripControlCodes(text).toCharArray();
		float currentLine = 0;
		float maxPreviousLines = 0;
		for (char c1 : c) {
			if (c1 == '\n') {
				maxPreviousLines = Math.max(currentLine, maxPreviousLines);
				currentLine = 0;
				continue;
			}
			Glyph glyph = locateGlyph0(c1, false, false);
			currentLine += glyph.logicalWidth() / this.scaleMul;
		}
		return Math.max(currentLine, maxPreviousLines);
	}

	/**
	 * Calculates the height of the string, if it were drawn on the screen. Accounts for newlines
	 *
	 * @param text The text to simulate
	 * @return The height of the string if it'd be drawn on the screen
	 */
	public float getStringHeight(String text) {
		int heightOneLine = fontMetrics.getHeight();
		long countNls = text.chars().filter(f -> f == '\n').count();
		return (float) (heightOneLine * (countNls + 1)) / this.scaleMul;
	}

	/**
	 * Gets the width of a Text in minecraft units, were it drawn on the screen. Accounts for newlines and style
	 *
	 * @param text Text to measure
	 * @return Width in minecraft pixels
	 */
	public float getTextWidth(Text text) {
		// visit as is done by the normal renderer
		// {lineWidth, maxLineWidth}
		float[] lineDims = new float[2];
		text.asOrderedText().accept((index, style, codePoint) -> {
			// TODO 22 Okt. 2024 08:45: (prereq. bold / bold italic pages) account for larger glyphs
			char c = (char) codePoint;
			if (c == '\n') {
				lineDims[1] = Math.max(lineDims[1], lineDims[0]);
				lineDims[0] = 0;
				return true;
			}
			Glyph glyph = locateGlyph0(c, style.isBold(), style.isItalic());
			lineDims[0] += (float) glyph.logicalWidth() / this.scaleMul;
			return true;
		});
		return Math.max(lineDims[0], lineDims[1]);
	}

	/**
	 * Gets the height of a Text in minecraft units, were it drawn on the screen. Accounts for newlines
	 *
	 * @param t Text to measure
	 * @return Height in minecraft units
	 */
	public float getTextHeight(Text t) {
		int[] nls = new int[]{1};
		t.asOrderedText().accept((index, style, codePoint) -> {
			if (codePoint == '\n') nls[0]++;
			return true;
		});
		return (float) (fontMetrics.getHeight() * nls[0]) / scaleMul;
	}

	/**
	 * Returns the height of one standard line of text, baseline to baseline (or top of a character to bottom).
	 *
	 * @return Height of one standard line of text in this font
	 */
	public float getFontHeight() {
		return (float) fontMetrics.getHeight() / this.scaleMul;
	}

	/**
	 * Clears all glyph maps, and unlinks them. The font can continue to be used, but it will have to regenerate the maps.
	 */
	@SneakyThrows
	@Override
	public void close() {
		if (prebakeGlyphsFuture != null && !prebakeGlyphsFuture.isDone() && !prebakeGlyphsFuture.isCancelled()) {
			// if we have a prebake job running, cancel it to avoid it creating more pages while the font renderer clears
			prebakeGlyphsFuture.cancel(true);
			prebakeGlyphsFuture.get(); // should only ever throw interrupted, which is the parent's job to handle anyway
			prebakeGlyphsFuture = null;
		}
		synchronized (this) {
			pageNormal.close();
			pageItalic.close();
			pageBoldItalic.close();
			pageBold.close();
			initialized = false;
		}
	}

	protected record DrawEntry(float atX, float atY, float r, float g, float b, boolean underline, boolean strike,
							   Glyph toDraw) {
	}
}
