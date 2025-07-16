package me.x150.renderer.fontng;

import lombok.Getter;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.TT_OS2;

import java.util.Arrays;

import static org.lwjgl.util.freetype.FreeType.*;
import static org.lwjgl.util.harfbuzz.HarfBuzz.*;

/**
 * A font file, managing resources for both HarfBuzz and Freetype.
 * Per default, Fonts have a scale of 1 globally, even if the window scale is different.
 * This differs from the {@link GlyphBuffer} behavior, which scales its content based on the window size.
 * To compensate for this, register any newly created fonts in {@link FontScalingRegistry}, to make the library automatically
 * scale the fonts for you.
 */
public class Font extends RefWatcher {

	public final String originalFile;
	/**
	 * The size that was requested by the user, set by {@link #setSourceSize(int)}
	 */
	@Getter
	private int originalSize;

	/**
	 * The scale factor of this font
	 */
	public float lastScale = 1f;

	private static final int N_GLYPHS_PAGE = 128;
	/**
	 * The FreeType font reference
	 */
	public final FT_Face freetypeFont;

	/**
	 * The binary blob to the font data for HarfBuzz
	 */
	public final long hbFontBlob, /**
	 * The HarfBuzz Face
	 */
	hbFace, /**
	 * The HarfBuzz Font
	 */
	hbFont;

	private final GlyphPage[] pages;
	private final int nGlyphs;

	private static void handleFtErr(int e) {
		if (e != FT_Err_Ok) throw new IllegalStateException("FT error: " + FT_Error_String(e) + " (" + e + ")");
	}

	/**
	 * Creates a new Font for the given file and face index. Scale is 1.
	 *
	 * @param freetype  FreeType library
	 * @param file      Font file on the local fs
	 * @param faceIndex Face index in the font file, if multiple faces are in one font
	 * @param size      Initial size of the font
	 */
	public Font(FTLibrary freetype, String file, int faceIndex, int size) {
		originalFile = file;
		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			PointerBuffer ptr = memoryStack.mallocPointer(1);
			handleFtErr(FT_New_Face(freetype.get(), file, faceIndex, ptr));
			freetypeFont = FT_Face.create(ptr.get(0));
			hbFontBlob = hb_blob_create_from_file_or_fail(file);
			hbFace = hb_face_create(hbFontBlob, faceIndex);
			hbFont = hb_font_create(hbFace);

			nGlyphs = (int) freetypeFont.num_glyphs();
			int nMaps = Math.ceilDiv(nGlyphs, N_GLYPHS_PAGE);
			this.pages = new GlyphPage[nMaps];

			long os2Table = FT_Get_Sfnt_Table(freetypeFont, FT_SFNT_OS2);
			if (os2Table != 0) ttOs2Table = TT_OS2.create(os2Table);

			setSourceSize(size);
		} catch (Throwable t) {
			close();
			throw new RuntimeException("Failed to init Font", t);
		}
	}

	long cachedHeight;
	TT_OS2 ttOs2Table;

	/**
	 * Returns the actual scaled height of the font. This is the difference of the Y axis between two baselines; the "logical height" of the font.
	 *
	 * @return The height of the font
	 */
	public float height() {
		return cachedHeight / 64f;
	}

	/**
	 * Returns the unscaled height of the font. Mostly used with {@link GlyphBuffer}.
	 *
	 * @return Unscaled height
	 */
	public float unscaledHeight() {
		return height() / lastScale;
	}

	/**
	 * Returns the scaled Y offset of the center of the underline.
	 *
	 * @return the Scaled Y offset of the center of the underline.
	 */
	public float underlineCenterYOffset() {
		return Math.round(Util.mulFix(freetypeFont.underline_position(), freetypeFont.size().metrics().y_scale()) / 64f);
	}

	/**
	 * Returns the scaled height of the underline.
	 *
	 * @return the scaled height of the underline.
	 */
	public float underlineHeight() {
		return Math.round((Util.mulFix(freetypeFont.underline_thickness(), freetypeFont.size().metrics().y_scale())) / 64f);
	}

	/**
	 * Returns the scaled Y offset of the strikethrough bar if present, otherwise null.
	 *
	 * @return the scaled Y offset of the strikethrough bar if present, otherwise null.
	 */
	public Float strikeoutCenterYOffset() {
		if (ttOs2Table == null) return null;
		return (float) Math.round((Util.mulFix(ttOs2Table.yStrikeoutPosition(), freetypeFont.size().metrics().y_scale())) / 64f);
	}

	/**
	 * Returns the scaled height of the strikethrough bar if present, otherwise null.
	 *
	 * @return the scaled height of the strikethrough bar if present, otherwise null.
	 */
	public Float strikeoutHeight() {
		if (ttOs2Table == null) return null;
		return (float) Math.round((Util.mulFix(ttOs2Table.yStrikeoutSize(), freetypeFont.size().metrics().y_scale())) / 64f);
	}

	/**
	 * Gets the given glyph from the associated page, creating it if it doesnt exist.
	 *
	 * @param glyph Glyph index
	 * @return Glyph
	 */
	public GlyphPage.Glyph getGlyph(int glyph) {
		return getPage(glyph).getGlyph(glyph);
	}

	/**
	 * Gets the page where the given glyph can be found in.
	 *
	 * @param glyph Glyph index
	 * @return Page
	 */
	public GlyphPage getPage(int glyph) {
		checkClosed();
		int pageIndex = glyph / N_GLYPHS_PAGE;
		GlyphPage pg = this.pages[pageIndex];
		if (pg == null) {
			int startGlyph = pageIndex * N_GLYPHS_PAGE;
			int endGlyph = Math.min(startGlyph + N_GLYPHS_PAGE, nGlyphs);
			return this.pages[pageIndex] = new GlyphPage(this, startGlyph, endGlyph);
		} else return pg;
	}

	/**
	 * Requests the given size from the font. This size should be unscaled.
	 *
	 * @param height Size
	 */
	public void setSourceSize(int height) {
		checkClosed();
		originalSize = height;
		applySize();
	}

	private void applySize() {
		for (GlyphPage page : pages) {
			if (page != null) page.close();
		}
		Arrays.fill(pages, null);

		hb_font_set_scale(hbFont, originalSize << 6, originalSize << 6);
		FT_Set_Char_Size(freetypeFont, 0, (long) (originalSize * lastScale * 64d), 0, 0);
		//noinspection resource
		cachedHeight = freetypeFont.size().metrics().height();
	}

	/**
	 * Sets the scale of the font. The actual size is close to {@code originalSize * lastScale}.
	 *
	 * @param f Scale
	 */
	public void setScale(float f) {
		checkClosed();
		lastScale = f;
		applySize();
	}

	@Override
	protected void implClose() {
		if (freetypeFont != null) FT_Done_Face(freetypeFont);

		if (hbFont != 0) hb_font_destroy(hbFont);
		if (hbFace != 0) hb_face_destroy(hbFace);
		if (hbFontBlob != 0) hb_blob_destroy(hbFontBlob);
	}
}
