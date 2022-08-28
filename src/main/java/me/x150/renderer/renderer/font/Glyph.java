package me.x150.renderer.renderer.font;

import net.minecraft.util.Identifier;

/**
 * A texture glyph
 *
 * @param texWidth  Width of the texture
 * @param texHeight Height of the texture
 * @param glyph     The character this glyph is for
 * @param texture   The texture of the glyph
 */
public record Glyph(int texWidth, int texHeight, char glyph, Identifier texture) {

}
