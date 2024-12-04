package me.x150.renderer.font;

public record Glyph(double tlX, double tlY, double texW, double texH, float differenceToFontBaseline, double baselineX,
					double baselineY,

					int logicalWidth, char value, GlyphPage.PreGlyphRegion glyphRegion, GlyphPage owner) {
}
