package me.x150.renderer.fontng;

import com.google.common.util.concurrent.AtomicDouble;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.harfbuzz.hb_glyph_info_t;
import org.lwjgl.util.harfbuzz.hb_glyph_position_t;

import java.nio.IntBuffer;
import java.util.*;
import java.util.stream.Collectors;

import static org.lwjgl.util.harfbuzz.HarfBuzz.*;

/**
 * A list of glyphs, positioned and laid out in separate text runs. This is the main way to render text.
 * <h2>Performance</h2>
 * Laying out text with HarfBuzz is expensive. It's recommended to shape text once, and then reuse the GlyphBuffer.
 * If you need to change text, add it as a separate text run, record the id of the run, and replace it later:
 * <pre><code>
 *     GlyphBuffer gb = new GlyphBuffer();
 *     var ref = gb.addString("some changing content: ", 0, 0);
 *     // later:
 *     int runId = ref.then("some content", 0, 0).getRunId();
 *     // draw text
 *     gb.removeRunId(runId);
 * </code></pre>
 */
public class GlyphBuffer {

	record Glyph(Font font, int glyphId, float x, float y, Style style,
				 int runId, int indexInRun) {
	}

	@Override
	public String toString() {
		return String.format("%s{glyphs=%s,%nminX=%s, minY=%s, maxX=%s, maxY=%s}", getClass().getSimpleName(), this.glyphs, this.minX, this.minY, this.maxX, this.maxY);
	}

	private final List<Glyph> glyphs = new ArrayList<>();
	public float minX, minY, maxX, maxY;

	{
		resetBounds();
	}

	/**
	 * Remove all glyphs from this buffer, reset the bounds.
	 */
	public void clear() {
		glyphs.clear();
		resetBounds();
	}

	private void resetBounds() {
		minX = minY = Integer.MAX_VALUE;
		maxX = maxY = Integer.MIN_VALUE;
	}

	public float offsetX, offsetY = 0;

	/**
	 * Move the content such that a call to {@link #draw(VertexConsumerProvider, MatrixStack, float, float)} will position the (-minX, -minY) coordinate at the given render coordinates.
	 */
	public void offsetToTopLeft() {
		offsetX = -minX;
		offsetY = -minY;
	}

	/**
	 * Move the content such that a call to {@link #draw(VertexConsumerProvider, MatrixStack, float, float)} will position the center coordinate at the given render coordinates.
	 */
	public void offsetToCenter() {
		offsetToTopLeft();
		float width = maxX - minX;
		float height = maxY - minY;
		offsetX -= width / 2;
		offsetY -= height / 2;
	}

	public void drawDebuggingInformation(DrawContext dc, float x, float y) {
		if (glyphs.isEmpty()) return;

		MatrixStack stack = dc.getMatrices();

		stack.push();
		stack.translate(x, y, 0);

		dc.drawBorder((int) (minX + offsetX), (int) (minY + offsetY), (int) Math.ceil(maxX - minX), (int) Math.ceil(maxY - minY), 0xFFFF0000);

		float sf = (float) MinecraftClient.getInstance().getWindow().getScaleFactor();
		stack.scale(1f/sf, 1f/sf, 1);


		for (Glyph glyph : glyphs) {
			float glyphBaselineX = (glyph.x + offsetX) * sf;
			float glyphBaselineY = (glyph.y + offsetY) * sf;

			GlyphPage.Glyph theGlyph = glyph.font.getGlyph(glyph.glyphId);

			// draw glyph
			int bmpl = theGlyph.drawOffsetX();
			int bmpt = theGlyph.drawOffsetY();
			int wid = theGlyph.bitmapWidth();
			int hei = theGlyph.bitmapHeight();
			float topLeftX = glyphBaselineX + bmpl;
			float topLeftY = glyphBaselineY - bmpt;

			dc.drawBorder((int) topLeftX, (int) topLeftY, wid, hei, 0xFF00FF00);

			dc.drawHorizontalLine((int) glyphBaselineX, (int) (glyphBaselineX+wid), ((int) glyphBaselineY), 0xFF0000FF);
		}


		stack.pop();
	}

	/**
	 * Draw this GlyphBuffer to buffers given from {@code vc} at the given coordinates.
	 * @param vc VertexConsumerProvider supplying {@link CustomRenderLayers#TEXT_CUSTOM}, {@link CustomRenderLayers#QUADS}
	 * @param stack MatrixStack
	 * @param x Render X coordinate
 	 * @param y Render Y coordinate
	 */
	public void draw(VertexConsumerProvider vc, MatrixStack stack, float x, float y) {
		if (glyphs.isEmpty()) return;

		stack.push();
		stack.translate(x, y, 0);
//		int offsetX = -minX;
//		int offsetY = -minY;

		float sf = (float) MinecraftClient.getInstance().getWindow().getScaleFactor();
		stack.scale(1f/sf, 1f/sf, 1);

		Matrix4f posmat = stack.peek().getPositionMatrix();


		Map<GlyphPage, List<Glyph>> pageToGlyphs = glyphs.stream()
				.collect(Collectors.groupingBy(it -> it.font.getPage(it.glyphId)));

		for (Map.Entry<GlyphPage, List<Glyph>> glyphPageListEntry : pageToGlyphs.entrySet()) {
			GlyphPage page = glyphPageListEntry.getKey();
			int glId = page.tex.getGlId();
			RenderLayer renderLayerForThisGlyph = CustomRenderLayers.TEXT_CUSTOM.apply(glId);
			for (Glyph glyph : glyphPageListEntry.getValue()) {
				VertexConsumer buffer = vc.getBuffer(renderLayerForThisGlyph);
				float glyphBaselineX = (glyph.x + offsetX) * sf;
				float glyphBaselineY = (glyph.y + offsetY) * sf;
				int glyphIndex = glyph.glyphId;
				Style style = glyph.style;
				TextColor textCol = style.getColor();
				int actualColor = (textCol == null ? 0xFFFFFF : textCol.getRgb()) | (0xFF << 24);
				GlyphPage.Glyph theGlyph = page.getGlyph(glyphIndex);



				// draw glyph
				int bmpl = theGlyph.drawOffsetX();
				int bmpt = theGlyph.drawOffsetY();
				int wid = theGlyph.bitmapWidth();
				int hei = theGlyph.bitmapHeight();
				float topLeftX = glyphBaselineX + bmpl;
				float topLeftY = glyphBaselineY - bmpt;

				int glyphY = theGlyph.y().get();
				int glyphX = theGlyph.x().get();

				float w = page.getTexWidth();
				float h = page.getTexHeight();

				// small insets to make sure we're always INSIDE this char's bounds
				buffer
						.vertex(posmat, topLeftX      , topLeftY      , 0).color(actualColor).texture((glyphX + 0.01f) / w, (glyphY + 0.01f) / h).light(0xf000f0)
						.vertex(posmat, topLeftX      , topLeftY + (float) hei, 0).color(actualColor).texture((glyphX + 0.01f) / w, (glyphY + hei - 0.01f) / h).light(0xf000f0)
						.vertex(posmat, topLeftX + (float) wid, topLeftY + (float) hei, 0).color(actualColor).texture((glyphX + wid - 0.01f) / w, (glyphY + hei - 0.01f) / h).light(0xf000f0)
						.vertex(posmat, topLeftX + (float) wid, topLeftY      , 0).color(actualColor).texture((glyphX + wid - 0.01f) / w, (glyphY + 0.01f) / h).light(0xf000f0);
			}
		}

//		stack.pop();
//		posmat = stack.peek().getPositionMatrix();

		Map<Integer, List<Glyph>> runs = glyphs.stream().collect(Collectors.groupingBy(it -> it.runId));
		VertexConsumer quadBuffer = vc.getBuffer(CustomRenderLayers.QUADS);
		for (Map.Entry<Integer, List<Glyph>> integerListEntry : runs.entrySet()) {
			List<Glyph> glyphs = integerListEntry.getValue();
			assert !glyphs.isEmpty();
			List<Rectangle> rects = new ArrayList<>(Math.ceilDiv(glyphs.size(), 2));
			List<Rectangle> rectsStrike = new ArrayList<>(Math.ceilDiv(glyphs.size(), 2));

			Glyph firstGl = glyphs.getFirst();

			Float origSYO, origSH;
			boolean strikeoutSupported =
					(origSYO = firstGl.font.strikeoutCenterYOffset()) != null & (origSH = firstGl.font.strikeoutHeight()) != null;

			for (int i = 0; i < glyphs.size(); i++) {
				Glyph current = glyphs.get(i);
				Glyph prev = null; if (i > 0) prev = glyphs.get(i-1);
				Style currentStyle = current.style;
				Style prevStyle = prev == null ? null : prev.style;

				float underlineY = -current.font.underlineCenterYOffset();
				float underlineHeight = current.font.underlineHeight();



				GlyphPage.GlyphMetrics gMet = current.font.getGlyph(current.glyphId).metrics();

				float left = ((current.x + offsetX) * sf + (gMet.hbX() / 64f));
				float right = (left + (gMet.width() / 64f));

				if (currentStyle.isUnderlined()) {
					if (prevStyle != null && prevStyle.isUnderlined() && Objects.equals(prevStyle.getColor(), currentStyle.getColor())) {
						// we're not the first glyph and the previous one has the same style as we do; add us
						rects.getLast().endX.set(right);
					} else {
						// for some reason we cant merge with the previous rect
						rects.add(new Rectangle(left, (current.y + offsetY) * sf  + underlineY - underlineHeight, underlineHeight, new AtomicDouble(right), currentStyle.getColor()));
					}
				}
				if (strikeoutSupported && currentStyle.isStrikethrough()) {
					float strikeY = -origSYO;
					float strikeHeight = origSH;
					if (prevStyle != null && prevStyle.isStrikethrough() && Objects.equals(prevStyle.getColor(), currentStyle.getColor())) {
						rectsStrike.getLast().endX.set(right);
					} else {
						rectsStrike.add(new Rectangle(left, (current.y + offsetY) * sf  + strikeY, strikeHeight, new AtomicDouble(right), currentStyle.getColor()));
					}
				}
			}

			for(int i = 0; i < 2; i++) {
				List<Rectangle> rcs = switch (i) {
					case 0 -> rects;
					case 1 -> rectsStrike;
					default -> throw new IllegalStateException();
				};
				for (Rectangle rect : rcs) {
					float left = rect.x;
					float right = (float) rect.endX.get();
					float theY = rect.y;
					float height = rect.height;
					int actualColor = 0xFFFFFFFF;
					if (rect.color != null) actualColor = (rect.color.getRgb()) | (0xFF << 24);

					quadBuffer
							.vertex(posmat, left, theY, 0).color(actualColor)
							.vertex(posmat, left, theY + height, 0).color(actualColor)
							.vertex(posmat, right, theY + height, 0).color(actualColor)
							.vertex(posmat, right, theY, 0).color(actualColor);
				}
			}
		}

		stack.pop();
	}

	record Rectangle(float x, float y, float height, AtomicDouble endX, TextColor color) {}

	/**
	 * Shapes and then adds the given glyphs from the string, using the given font. Expand the bounds as needed.
	 * @param font Font to use
	 * @param s String to draw (without formatting)
	 * @param x X coordinate to add at
	 * @param y Y coordinate to add at
	 * @return FollowUp to add more content
	 */
	public FollowUp addString(Font font, String s, float x, float y) {
		long buffer = hb_buffer_create();
		buffer = hb_buffer_reference(buffer);
		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			IntBuffer intBuffer = memoryStack.ints(s.codePoints().toArray());
			hb_buffer_add_codepoints(buffer, intBuffer, 0, -1);
		}
		hb_buffer_guess_segment_properties(buffer);
		hb_shape(font.hbFont, buffer, null);
		FollowUp fw = addShapedRun(font, buffer, null, x, y);
		hb_buffer_destroy(buffer);
		return fw;
	}

	/**
	 * Shapes and adds a styled Text to this GlyphBuffer. Note that the only respected Style properties are:
	 * <ul>
	 *     <li>Text color</li>
	 *     <li>Underline</li>
	 *     <li>Strikethrough <b>only on True- and OpenType fonts with TT_OS2</b></li>
	 * </ul>
	 * All other properties are hard to respect with the context given.
	 * @param font Font to use
	 * @param t Text
	 * @param x X
	 * @param y Y
	 * @return FollowUp to add more content
	 */
	public FollowUp addText(Font font, Text t, float x, float y) {
		long buffer = hb_buffer_reference(hb_buffer_create());
		// HB_BUFFER_CLUSTER_LEVEL_CHARACTERS: do not group, do not remap, keep as is. if merge: first char determines cluster
		hb_buffer_set_cluster_level(buffer, HB_BUFFER_CLUSTER_LEVEL_CHARACTERS);
		List<Style> styles = new ArrayList<>();
		t.visit((style, asString) -> {
			int newIndex = styles.size();
			styles.add(style);
			asString.codePoints().forEach(cp -> hb_buffer_add(buffer, cp, newIndex));
			return Optional.empty();
		}, Style.EMPTY);
		hb_buffer_set_content_type(buffer, HB_BUFFER_CONTENT_TYPE_UNICODE);
		hb_buffer_guess_segment_properties(buffer);
		hb_shape(font.hbFont, buffer, null);
		FollowUp fw = addShapedRun(font, buffer, styles.toArray(Style[]::new), x, y);
		hb_buffer_destroy(buffer);
		return fw;
	}

	/**
	 * Adds a raw shaped HarfBuzz run to this buffer
	 * @param font Font to use
	 * @param shapedHbBuffer (transfer none; you own this pointer!) Pointer to the shaped HarfBuzz buffer.
	 * @param clusterStyles Array of Styles that the clusters in the harfbuzz buffer refer to, or null
	 * @param x X coordinate to add at
	 * @param y Y coordinate to add at
	 * @return FollowUp to add more content
	 */
	public FollowUp addShapedRun(Font font, long shapedHbBuffer, Style[] clusterStyles, float x, float y) {
		int ct;
		if ((ct = hb_buffer_get_content_type(shapedHbBuffer)) != HB_BUFFER_CONTENT_TYPE_GLYPHS)
			throw new IllegalStateException("Content type of buffer wasn't GLYPHS; either invalid or not shaped yet: "+ct);
		// !!! THESE ARE TRANSFER NONE; HARFBUZZ OWNS THESE ARRAYS !!!
		// DO NOT CLOSE TO PREVENT DOUBLE FREE
		@SuppressWarnings("resource") hb_glyph_info_t.Buffer hbGlyphInfoTs = hb_buffer_get_glyph_infos(shapedHbBuffer);
		@SuppressWarnings("resource") hb_glyph_position_t.Buffer hbGlyphPositionTs = hb_buffer_get_glyph_positions(shapedHbBuffer);

		int thisRunId = this.glyphs.size();

		int glyph_count = hbGlyphInfoTs.capacity();
		for (int i = 0; i < glyph_count; i++) {
			hb_glyph_info_t hbGlyphInfoT = hbGlyphInfoTs.get(i);
			int glyphIndex = hbGlyphInfoT.codepoint();
			int cluster = hbGlyphInfoT.cluster();

			Style style = clusterStyles != null && cluster >= 0 && cluster < clusterStyles.length ?
					clusterStyles[cluster] :
					Style.EMPTY;

			hb_glyph_position_t hbGlyphPositionT = hbGlyphPositionTs.get(i);
			int xOffset = hbGlyphPositionT.x_offset();
			int yOffset = hbGlyphPositionT.y_offset();
			float xAdvance = hbGlyphPositionT.x_advance() / 64f;
			float yAdvance = hbGlyphPositionT.y_advance() / 64f;

			float actualX = x + (xOffset / 64f);
			float actualY = y + (yOffset / 64f);

			GlyphPage.GlyphMetrics gMet = font.getGlyph(glyphIndex).metrics();
			// add glyph

			Glyph g = new Glyph(font, glyphIndex, actualX, actualY, style, thisRunId,
					i);
			glyphs.add(g);

			float left = (actualX + (gMet.hbX() / 64f / font.lastScale));
			float top = (actualY - (gMet.hbY() / 64f / font.lastScale));
			float right = (left + (gMet.width() / 64f / font.lastScale));
			float bottom = (top + (gMet.height() / 64f / font.lastScale));

			this.minX = Math.min(this.minX, left);
			this.minY = Math.min(this.minY, top);
			this.maxX = Math.max(this.maxX, right);
			this.maxY = Math.max(this.maxY, bottom);

			x += xAdvance;
			y += yAdvance;
		}
		return new FollowUp(x, y, thisRunId);
	}

	/**
	 * Removes all glyphs from this buffer that relate to the given run id
	 * @param id Run ID to remove
	 */
	public void removeRunId(int id) {
		glyphs.removeIf(it -> it.runId == id);
		recalculateBounds();
	}

	public void recalculateBounds() {
		resetBounds();
		for (Glyph glyph : glyphs) {
			float actualX = glyph.x;
			float actualY = glyph.y;

			GlyphPage.GlyphMetrics gMet = glyph.font.getGlyph(glyph.glyphId).metrics();

			float left = (actualX + (gMet.hbX() / 64f));
			float top = (actualY - (gMet.hbY() / 64f));
			float right = (left + (gMet.width() / 64f));
			float bottom = (top + (gMet.height() / 64f));

			this.minX = Math.min(this.minX, (int) left);
			this.minY = Math.min(this.minY, (int) top);
			this.maxX = Math.max(this.maxX, (int) Math.ceil(right));
			this.maxY = Math.max(this.maxY, (int) Math.ceil(bottom));
		}
	}

	/**
	 * Stored context to add more content after the previous run
	 */
	@AllArgsConstructor
	@Getter
	public final class FollowUp {
		private final float penX, penY;
		private final int runId;

		/**
		 * Calls {@link #addString(Font, String, float, float) addString}{@code (font, text, penX+offsetX, penY+offsetY)}
		 * @see GlyphBuffer#addString(Font, String, float, float)
		 */
		public FollowUp then(Font font, String text, int offsetX, int offsetY) {
			return GlyphBuffer.this.addString(font, text, penX+offsetX, penY+offsetY);
		}
		/**
		 * Calls {@link #addText(Font, Text, float, float) addText}{@code (font, text, penX+offsetX, penY+offsetY)}
		 * @see GlyphBuffer#addText(Font, Text, float, float)
		 */
		public FollowUp then(Font font, Text text, int offsetX, int offsetY) {
			return GlyphBuffer.this.addText(font, text, penX+offsetX, penY+offsetY);
		}
	}
}
