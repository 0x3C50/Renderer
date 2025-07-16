package me.x150.renderer.fontng;

import lombok.Getter;
import me.x150.renderer.mixin.NativeImageAccessor;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.freetype.FT_Bitmap;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FT_GlyphSlot;
import org.lwjgl.util.freetype.FT_Glyph_Metrics;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.util.freetype.FreeType.*;

/**
 * A page of glyphs drawn from a {@link Font}
 */
public class GlyphPage implements AutoCloseable {
	private static final Logger log = LogManager.getLogger(GlyphPage.class);
	private final Font font;
	private final int glyphStart;
	private final int glyphEnd;

	@Override
	public void close() {
		tex.close();
	}

	public record GlyphMetrics(long width, long height, long hbX, long hbY, long hbA) {
	}

	public record Glyph(int id, int bitmapWidth, int bitmapHeight, int drawOffsetX, int drawOffsetY, AtomicInteger x,
						AtomicInteger y, GlyphMetrics metrics) {
	}

	Glyph[] glyphs;
	public NativeImageBackedTexture tex;

	public GlyphPage(Font font, int glyphStart, int glyphEnd) {
		glyphs = new Glyph[glyphEnd - glyphStart];
		this.font = font;
		this.glyphStart = glyphStart;
		this.glyphEnd = glyphEnd;
		log.debug("Font {} ({}-{}) init page from glyph {} to {}", font.toString(), font.freetypeFont.family_nameString(), font.freetypeFont.style_nameString(), glyphStart, glyphEnd);
		fill();
	}

	@Getter
	private int texWidth, texHeight;

	public Glyph getGlyph(int n) {
		return glyphs[n - glyphStart];
	}

	private void layout(int rowWidth) {
		int w = 0, pX = 0, pY = 0;
		Iterator<Glyph> iterator = Arrays.stream(glyphs).sorted(Comparator.comparingInt(it -> it.bitmapHeight)).iterator();
		int rowHeight = 0;
		while (iterator.hasNext()) {
			Glyph glyph = iterator.next();
			glyph.x.set(pX);
			glyph.y.set(pY);
			pX += glyph.bitmapWidth;
			rowHeight = Math.max(rowHeight, glyph.bitmapHeight);

			if (pX >= rowWidth) {
				pY += rowHeight;
				rowHeight = 0;
				w = Math.max(w, pX);
				pX = 0;
			}
		}

		pY += rowHeight;

		texWidth = Math.max(w, pX);
		texHeight = pY;
	}


	private void fill() {
		FT_Face ftf = font.freetypeFont;
		for (int i = glyphStart; i < glyphEnd; i++) {
			FT_Load_Glyph(ftf, i, FT_LOAD_BITMAP_METRICS_ONLY);
			FT_GlyphSlot gl = ftf.glyph();
			FT_Bitmap bmp = gl.bitmap();
			int width = bmp.width();
			int height = bmp.rows();
			int xO = gl.bitmap_left();
			int yO = gl.bitmap_top();
			FT_Glyph_Metrics met = gl.metrics();
			GlyphMetrics metrics = new GlyphMetrics(met.width(), met.height(), met.horiBearingX(), met.horiBearingY(), met.horiAdvance());
			glyphs[i - glyphStart] = new Glyph(i, width, height, xO, yO, new AtomicInteger(), new AtomicInteger(), metrics);
		}
		int currentWidth = Integer.MAX_VALUE;
		for (int i = 0; i < 10; i++) {
			layout(currentWidth);
			if (Math.abs(texWidth - texHeight) < 50) break;
			currentWidth = (texWidth + texHeight) / 2;
		}

		NativeImage ni = new NativeImage(NativeImage.Format.LUMINANCE, texWidth, texHeight, false);
		tex = new NativeImageBackedTexture(() -> String.format("renderer/glyphPage/%s-%s/%d-%d", font.freetypeFont.family_nameString(), font.freetypeFont.style_nameString(), glyphStart, glyphEnd), ni);
		long ptr = ((NativeImageAccessor) (Object) ni).getPointer();

		for (Glyph glyph : glyphs) {
			FT_Load_Glyph(ftf, glyph.id, FT_LOAD_RENDER);
			FT_GlyphSlot gl = ftf.glyph();
			FT_Bitmap bmp = gl.bitmap();
			int width = bmp.width();
			int height = bmp.rows();
			ByteBuffer buffer = bmp.buffer(width * height);
			for (int y = 0; y < height; y++) {
				int rowOffset = (glyph.y.get() + y) * texWidth; // which row are we in?
				MemoryUtil.memCopy(MemoryUtil.memAddress(buffer) + (long) y * width, ptr + rowOffset + glyph.x.get(), width);
			}
		}
		tex.upload();
	}
}
