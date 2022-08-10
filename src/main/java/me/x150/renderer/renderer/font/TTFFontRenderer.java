package me.x150.renderer.renderer.font;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import me.x150.renderer.renderer.MSAAFramebuffer;
import me.x150.renderer.renderer.RendererUtils;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Matrix4f;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

/**
 * A very crude implementation of a TTF font renderer
 */
public class TTFFontRenderer {
    /**
     * A map with all colors of the vanilla formatting standard
     */
    static final Map<Character, Integer> colorMap = Util.make(() -> {
        Map<Character, Integer> ci = new HashMap<>();
        ci.put('0', 0x000000);
        ci.put('1', 0x0000AA);
        ci.put('2', 0x00AA00);
        ci.put('3', 0x00AAAA);
        ci.put('4', 0xAA0000);
        ci.put('5', 0xAA00AA);
        ci.put('6', 0xFFAA00);
        ci.put('7', 0xAAAAAA);
        ci.put('8', 0x555555);
        ci.put('9', 0x5555FF);
        ci.put('A', 0x55FF55);
        ci.put('B', 0x55FFFF);
        ci.put('C', 0xFF5555);
        ci.put('D', 0xFF55FF);
        ci.put('E', 0xFFFF55);
        ci.put('F', 0xFFFFFF);
        return ci;
    });
    /**
     * The characters to generate glyphs for
     */
    private static final char[] charsToGenerate = Util.make(() -> {
        char[] f = new char[256];
        for (int i = 0; i < f.length; i++) {
            f[i] = (char) i;
        }
        return f;
    });
    float size;
    Font font;
    Identifier texture;
    Char2ObjectMap<Glyph> glyphMap = new Char2ObjectArrayMap<>();
    float cachedHeight;
    float glyphDimensions;

    /**
     * Creates a new FontRenderer
     *
     * @param font The font to use
     * @param size The size to use
     */
    private TTFFontRenderer(Font font, float size) {
        this.size = size;
        this.texture = new Identifier("renderer", "ttf_texture_" + font.hashCode() + "_" + Math.random());

        int scaleFactor = 2;
        glyphDimensions = size * scaleFactor;
        this.font = font.deriveFont(glyphDimensions);

        initGlyphs();
        cachedHeight = (float) glyphMap.values()
                .stream()
                .max(Comparator.comparingDouble(Glyph::texHeight))
                .orElseThrow()
                .texHeight() * getScaleFactor();

    }

    /**
     * <p>Creates a new FontRenderer</p>
     * <p>Use once, then save the font renderer in a cache. Initializing this a lot of times can cause a memory leak down the line</p>
     *
     * @param font   The font to use
     * @param sizePx The size in pixels for the new font to be
     * @return The new FontRenderer instance
     */
    public static TTFFontRenderer create(Font font, float sizePx) {
        return new TTFFontRenderer(font, sizePx);
    }
    public static TTFFontRenderer create(Font font) {
        return create(font, font.getSize());
    }

    /**
     * Gets the font's height in pixels
     *
     * @return The font's height in pixels
     */
    public float getFontHeight() {
        return cachedHeight;
    }

    private void drawMissing(BufferBuilder bufferBuilder, Matrix4f matrix, float width, float height) {
        float r = 1f;
        float g = 1f;
        float b = 1f;
        float a = 1f;
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, 0, height, 0).color(r, g, b, a).next();
        bufferBuilder.vertex(matrix, width, height, 0).color(r, g, b, a).next();
        bufferBuilder.vertex(matrix, width, 0, 0).color(r, g, b, a).next();
        bufferBuilder.vertex(matrix, 0, 0, 0).color(r, g, b, a).next();
        bufferBuilder.vertex(matrix, 0, height, 0).color(r, g, b, a).next();
        BufferRenderer.drawWithShader(bufferBuilder.end());
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
    }

    private double drawChar(BufferBuilder bufferBuilder, Matrix4f matrix, char c, float r, float g, float b, float a) {
        Glyph glyph = glyphMap.get(c);
        if (glyph == null) {
            double missingW = 20;
            drawMissing(bufferBuilder, matrix, (float) missingW, getFontHeight() * 4);
            return missingW;
        }

        RenderSystem.setShaderTexture(0, glyph.texture());
        float height = (float) glyph.texHeight();
        float width = (float) glyph.texWidth();

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(matrix, 0, height, 0).texture(0, 1).color(r, g, b, a).next();
        bufferBuilder.vertex(matrix, width, height, 0).texture(1, 1).color(r, g, b, a).next();
        bufferBuilder.vertex(matrix, width, 0, 0).texture(1, 0).color(r, g, b, a).next();
        bufferBuilder.vertex(matrix, 0, 0, 0).texture(0, 0).color(r, g, b, a).next();
        BufferRenderer.drawWithShader(bufferBuilder.end());

        return width;
    }

    float getScaleFactor() {
        return 1f / (glyphDimensions / size);
    }

    public void drawString(MatrixStack matrices, String s, float x, float y, me.x150.renderer.renderer.color.Color color) {
        drawString(matrices, s, x, y, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    /**
     * <p>Draws a string</p>
     * <p>Best used inside of {@link MSAAFramebuffer#use(int, Runnable)}</p>
     *
     * @param matrices The context MatrixStack
     * @param s        The string to render
     * @param x        The X coordinate
     * @param y        The Y coordinate
     * @param r        The red color component
     * @param g        The green color component
     * @param b        The blue color component
     * @param a        The alpha color component
     */
    public void drawString(MatrixStack matrices, String s, float x, float y, float r, float g, float b, float a) {
        float scaleFactor = getScaleFactor();
        matrices.push();
        matrices.translate(x, y, 0);
        matrices.scale(scaleFactor, scaleFactor, 1f);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableTexture();
        RenderSystem.disableCull();
        GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        boolean isInSelector = false;
        for (char c : s.toCharArray()) {
            if (isInSelector) {
                char upper = String.valueOf(c).toUpperCase().charAt(0);
                int color = colorMap.getOrDefault(upper, 0xFFFFFF);
                r = (float) (color >> 16 & 255) / 255.0F;
                g = (float) (color >> 8 & 255) / 255.0F;
                b = (float) (color & 255) / 255.0F;
                isInSelector = false;
                continue;
            }
            if (c == '§') {
                isInSelector = true;
                continue;
            }

            Matrix4f matrix = matrices.peek().getPositionMatrix();
            double prevWidth = drawChar(bufferBuilder, matrix, c, r, g, b, a);
            matrices.translate(prevWidth, 0, 0);
        }

        matrices.pop();
    }

    /**
     * Removes all color control codes from the string
     *
     * @param in The input string with color codes
     * @return The output without color codes
     */
    public String stripControlCodes(String in) {
        char[] s = in.toCharArray();
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s.length; i++) {
            char current = s[i];
            if (current == '§') {
                i++;
                continue;
            }
            out.append(current);
        }
        return out.toString();
    }

    /**
     * Returns the width of the input, in pixels
     *
     * @param text The input text
     * @return The width of the input, in pixels
     */
    public float getStringWidth(String text) {
        float wid = 0;
        for (char c : stripControlCodes(text).toCharArray()) {
            Glyph g = glyphMap.get(c);
            if (g == null) {
                wid += 20;
            } else {
                wid += g.texWidth();
            }
        }
        return wid * getScaleFactor();
    }

    /**
     * Trims a string to a certain width
     *
     * @param t        The input
     * @param maxWidth The width to trim the string to
     * @return The trimmed string, so that {@link #getStringWidth(String)} of the output is less than the maxWidth
     */
    public String trimStringToWidth(String t, float maxWidth) {
        StringBuilder sb = new StringBuilder();
        for (char c : t.toCharArray()) {
            if (getStringWidth(sb.toString() + c) >= maxWidth) {
                return sb.toString();
            }
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * Draws a centered string
     * <p>Best used inside of {@link MSAAFramebuffer#use(int, Runnable)}</p>
     *
     * @param matrices The context MatrixStack
     * @param s        The input string
     * @param x        The X coordinate of the centered string
     * @param y        The Y coordinate of the string
     * @param r        The red color component
     * @param g        The green color component
     * @param b        The blue color component
     * @param a        The alpha color component
     */
    public void drawCenteredString(MatrixStack matrices, String s, float x, float y, float r, float g, float b, float a) {
        drawString(matrices, s, x - getStringWidth(s) / 2f, y, r, g, b, a);
    }

    void initGlyphs() {
        AffineTransform affineTransform = new AffineTransform();
        FontRenderContext fontRenderContext = new FontRenderContext(affineTransform, true, true);

        for (char c : charsToGenerate) {
            Identifier glyphTex = new Identifier("renderer", "font_" + font.hashCode() + "_glyph_" + ((int) c));
            Rectangle2D d = this.font.getStringBounds(String.valueOf(c), fontRenderContext);
            int w = (int) Math.ceil(d.getWidth());
            int h = (int) Math.ceil(d.getHeight());
            if (w < 1 || h < 1) {
                continue; // empty glyph, ignore
            }
            BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = bi.createGraphics();
            graphics.setFont(this.font);
            graphics.setColor(new Color(255, 255, 255, 0));
            graphics.fillRect(0, 0, bi.getWidth(), bi.getHeight());
            graphics.setColor(Color.WHITE);

            graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            FontMetrics fontMetrics = graphics.getFontMetrics();
            graphics.drawString(String.valueOf(c), 0, fontMetrics.getAscent());
            RendererUtils.registerBufferedImageTexture(glyphTex, bi);
            Glyph glyph = new Glyph(w, h, c, glyphTex);
            glyphMap.put(c, glyph);
        }
    }
}
