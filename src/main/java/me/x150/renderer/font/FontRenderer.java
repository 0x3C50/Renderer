package me.x150.renderer.font;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.chars.Char2IntArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import me.x150.renderer.util.BufferUtils;
import me.x150.renderer.util.Colors;
import me.x150.renderer.util.RendererUtils;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.Closeable;
import java.util.List;

/**
 * A simple font renderer. Supports multiple fonts, passed in as {@link Font} instances.
 * <h2>Constructor</h2>
 * {@link FontRenderer#FontRenderer(Font[], float)} takes in a {@link Font} array with the fonts to use. All fonts in this array will be asked, in order,
 * if they have a glyph corresponding to the one being drawn. If none of them have it, the missing "square" is drawn.
 */
public class FontRenderer implements Closeable {
	private static final Char2IntArrayMap colorCodes = new Char2IntArrayMap() {{
		put('0', 0x000000);
		put('1', 0x0000AA);
		put('2', 0x00AA00);
		put('3', 0x00AAAA);
		put('4', 0xAA0000);
		put('5', 0xAA00AA);
		put('6', 0xFFAA00);
		put('7', 0xAAAAAA);
		put('8', 0x555555);
		put('9', 0x5555FF);
		put('A', 0x55FF55);
		put('B', 0x55FFFF);
		put('C', 0xFF5555);
		put('D', 0xFF55FF);
		put('E', 0xFFFF55);
		put('F', 0xFFFFFF);
	}};
	private static final int BLOCK_SIZE = 256;
	private static final Object2ObjectArrayMap<Identifier, ObjectList<DrawEntry>> GLYPH_PAGE_CACHE = new Object2ObjectArrayMap<>();
	private final float originalSize;
	private final ObjectList<GlyphMap> maps = new ObjectArrayList<>();
	private final Char2ObjectArrayMap<Glyph> allGlyphs = new Char2ObjectArrayMap<>();
	private int scaleMul = 0;
	private Font[] fonts;
	private int previousGameScale = -1;

	/**
	 * Initializes a new FontRenderer with the specified fonts
	 *
	 * @param fonts  The fonts to use. The font renderer will go over each font in this array, search for the glyph, and render it if found. If no font has the specified glyph, it will draw the missing font symbol.
	 * @param sizePx The size of the font in minecraft pixel units. One pixel unit = `guiScale` pixels
	 */
	public FontRenderer(Font[] fonts, float sizePx) {
		Preconditions.checkArgument(fonts.length > 0, "fonts.length == 0");
		this.originalSize = sizePx;
		init(fonts, sizePx);
	}

	private static int floorNearestMulN(int x, int n) {
		return n * (int) Math.floor((double) x / (double) n);
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

	private void sizeCheck() {
		int gs = RendererUtils.getGuiScale();
		if (gs != this.previousGameScale) {
			close(); // delete glyphs and cache
			init(this.fonts, this.originalSize); // re-init
		}
	}

	private void init(Font[] fonts, float sizePx) {
		this.previousGameScale = RendererUtils.getGuiScale();
		this.scaleMul = this.previousGameScale;
		this.fonts = new Font[fonts.length];
		for (int i = 0; i < fonts.length; i++) {
			this.fonts[i] = fonts[i].deriveFont(sizePx * this.scaleMul);
		}
	}

	private GlyphMap generateMap(char from, char to) {
		GlyphMap gm = new GlyphMap(from, to, this.fonts, RendererUtils.randomIdentifier());
		maps.add(gm);
		return gm;
	}

	private Glyph locateGlyph0(char glyph) {
		for (GlyphMap map : maps) { // go over existing ones
			if (map.contains(glyph)) { // do they have it? good
				return map.getGlyph(glyph);
			}
		}
		int base = floorNearestMulN(glyph, BLOCK_SIZE); // if not, generate a new page and return the generated glyph
		GlyphMap glyphMap = generateMap((char) base, (char) (base + BLOCK_SIZE));
		return glyphMap.getGlyph(glyph);
	}

	private Glyph locateGlyph1(char glyph) {
		return allGlyphs.computeIfAbsent(glyph, this::locateGlyph0);
	}

	/**
	 * Draws a string
	 *
	 * @param stack The MatrixStack
	 * @param s     The string to draw
	 * @param x     X coordinate to draw at
	 * @param y     Y coordinate to draw at
	 * @param r     Red color component of the text to draw
	 * @param g     Green color component of the text to draw
	 * @param b     Blue color component of the text to draw
	 * @param a     Alpha color component of the text to draw
	 */
	public void drawString(MatrixStack stack, String s, float x, float y, float r, float g, float b, float a) {
		sizeCheck();
		float r2 = r, g2 = g, b2 = b;
		stack.push();
		stack.translate(x, y, 0);
		stack.scale(1f / this.scaleMul, 1f / this.scaleMul, 1f);

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableCull();
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

		RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
		BufferBuilder bb = Tessellator.getInstance().getBuffer();
		Matrix4f mat = stack.peek().getPositionMatrix();
		char[] chars = s.toCharArray();
		float xOffset = 0;
		float yOffset = 0;
		boolean inSel = false;
		int lineStart = 0;
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (inSel) {
				inSel = false;
				char c1 = Character.toUpperCase(c);
				if (colorCodes.containsKey(c1)) {
					int ii = colorCodes.get(c1);
					int[] col = Colors.RGBIntToRGB(ii);
					r2 = col[0] / 255f;
					g2 = col[1] / 255f;
					b2 = col[2] / 255f;
				} else if (c1 == 'R') {
					r2 = r;
					g2 = g;
					b2 = b;
				}
				continue;
			}
			if (c == '§') {
				inSel = true;
				continue;
			} else if (c == '\n') {
				yOffset += getStringHeight(s.substring(lineStart, i)) * scaleMul;
				xOffset = 0;
				lineStart = i + 1;
				continue;
			}
			Glyph glyph = locateGlyph1(c);
			if (glyph.value() != ' ') { // we only need to really draw the glyph if it's not blank, otherwise we can just skip its width and that'll be it
				Identifier i1 = glyph.owner().bindToTexture;
				DrawEntry entry = new DrawEntry(xOffset, yOffset, r2, g2, b2, glyph);
				GLYPH_PAGE_CACHE.computeIfAbsent(i1, integer -> new ObjectArrayList<>()).add(entry);
			}
			xOffset += glyph.width();
		}
		for (Identifier identifier : GLYPH_PAGE_CACHE.keySet()) {
			RenderSystem.setShaderTexture(0, identifier);
			List<DrawEntry> objects = GLYPH_PAGE_CACHE.get(identifier);

			bb.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

			for (DrawEntry object : objects) {
				float xo = object.atX;
				float yo = object.atY;
				float cr = object.r;
				float cg = object.g;
				float cb = object.b;
				Glyph glyph = object.toDraw;
				GlyphMap owner = glyph.owner();
				float w = glyph.width();
				float h = glyph.height();
				float u1 = (float) glyph.u() / owner.width;
				float v1 = (float) glyph.v() / owner.height;
				float u2 = (float) (glyph.u() + glyph.width()) / owner.width;
				float v2 = (float) (glyph.v() + glyph.height()) / owner.height;

				bb.vertex(mat, xo + 0, yo + h, 0).texture(u1, v2).color(cr, cg, cb, a).next();
				bb.vertex(mat, xo + w, yo + h, 0).texture(u2, v2).color(cr, cg, cb, a).next();
				bb.vertex(mat, xo + w, yo + 0, 0).texture(u2, v1).color(cr, cg, cb, a).next();
				bb.vertex(mat, xo + 0, yo + 0, 0).texture(u1, v1).color(cr, cg, cb, a).next();
			}
			BufferUtils.draw(bb);
		}

		stack.pop();
		GLYPH_PAGE_CACHE.clear();
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
	 */
	public void drawCenteredString(MatrixStack stack, String s, float x, float y, float r, float g, float b, float a) {
		drawString(stack, s, x - getStringWidth(s) / 2f, y, r, g, b, a);
	}

	/**
	 * Calculates the width of the string, if it were drawn on the screen
	 *
	 * @param text The text to simulate
	 * @return The width of the string if it'd be drawn on the screen
	 */
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
			Glyph glyph = locateGlyph1(c1);
			currentLine += glyph.width() / (float) this.scaleMul;
		}
		return Math.max(currentLine, maxPreviousLines);
	}

	/**
	 * Calculates the height of the string, if it were drawn on the screen. This is necessary, because the fonts in this FontRenderer might have a different height for each char.
	 *
	 * @param text The text to simulate
	 * @return The height of the string if it'd be drawn on the screen
	 */
	public float getStringHeight(String text) {
		char[] c = stripControlCodes(text).toCharArray();
		if (c.length == 0) {
			c = new char[]{' '};
		}
		float currentLine = 0;
		float previous = 0;
		for (char c1 : c) {
			if (c1 == '\n') {
				if (currentLine == 0) {
					// empty line, assume space
					currentLine = locateGlyph1(' ').height() / (float) this.scaleMul;
				}
				previous += currentLine;
				currentLine = 0;
				continue;
			}
			Glyph glyph = locateGlyph1(c1);
			currentLine = Math.max(glyph.height() / (float) this.scaleMul, currentLine);
		}
		return currentLine + previous;
	}

	/**
	 * Clears all glyph maps, and unlinks them. The font can continue to be used, but it will have to regenerate the maps.
	 */
	@Override
	public void close() {
		for (GlyphMap map : maps) {
			map.destroy();
		}
		maps.clear();
		allGlyphs.clear();
	}

	record DrawEntry(float atX, float atY, float r, float g, float b, Glyph toDraw) {
	}
}
