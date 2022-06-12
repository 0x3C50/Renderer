package me.x150.renderer.renderer.color;

import com.google.common.base.Preconditions;

/**
 * A RGBA color
 */
public class Color {

    public static final Color white = new Color(255, 255, 255);

    public static final Color WHITE = white;

    public static final Color lightGray = new Color(192, 192, 192);

    public static final Color LIGHT_GRAY = lightGray;

    public static final Color gray = new Color(128, 128, 128);

    public static final Color GRAY = gray;

    public static final Color darkGray = new Color(64, 64, 64);

    public static final Color DARK_GRAY = darkGray;

    public static final Color black = new Color(0, 0, 0);

    public static final Color BLACK = black;

    public static final Color red = new Color(255, 0, 0);

    public static final Color RED = red;

    public static final Color pink = new Color(255, 175, 175);

    public static final Color PINK = pink;

    public static final Color orange = new Color(255, 200, 0);

    public static final Color ORANGE = orange;

    public static final Color yellow = new Color(255, 255, 0);

    public static final Color YELLOW = yellow;

    public static final Color green = new Color(0, 255, 0);

    public static final Color GREEN = green;

    public static final Color magenta = new Color(255, 0, 255);

    public static final Color MAGENTA = magenta;

    public static final Color cyan = new Color(0, 255, 255);

    public static final Color CYAN = cyan;

    public static final Color blue = new Color(0, 0, 255);

    public static final Color BLUE = blue;

    private final float r, g, b, a;

    /**
     * Constructs a new Color
     * @param red The red component (0f-1f)
     * @param green The green component (0f-1f)
     * @param blue The blue component (0f-1f)
     * @param alpha The alpha component (0f-1f)
     */
    public Color(float red, float green, float blue, float alpha) {
        Preconditions.checkArgument(isValid(red), "Expected float >= 0 and float <= 1, got " + red);
        Preconditions.checkArgument(isValid(green), "Expected float >= 0 and float <= 1, got " + green);
        Preconditions.checkArgument(isValid(blue), "Expected float >= 0 and float <= 1, got " + blue);
        Preconditions.checkArgument(isValid(alpha), "Expected float >= 0 and float <= 1, got " + alpha);
        this.r = red;
        this.g = green;
        this.b = blue;
        this.a = alpha;
    }

    /**
     * Constructs a new color
     * @param red The red component (0-255)
     * @param green The green component (0-255)
     * @param blue The blue component (0-255)
     * @param alpha The alpha component (0-255)
     */
    public Color(int red, int green, int blue, int alpha) {
        this(red / 255f, green / 255f, blue / 255f, alpha / 255f);
    }

    /**
     * Constructs a new color with alpha being 100%
     * @param red The red component (0-255)
     * @param green The green component (0-255)
     * @param blue The blue component (0-255)
     */
    public Color(int red, int green, int blue) {
        this(red, green, blue, 255);
    }

    /**
     * Constructs a new Color with alpha being 100%
     * @param red The red component (0f-1f)
     * @param green The green component (0f-1f)
     * @param blue The blue component (0f-1f)
     */
    public Color(float red, float green, float blue) {
        this(red, green, blue, 1f);
    }

    /**
     * Constructs a new Color based on a {@link java.awt.Color}
     * @param color The color to copy
     */
    public Color(java.awt.Color color) {
        this(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    /**
     * Converts this Color to a {@link java.awt.Color}
     * @return The {@link java.awt.Color} representing this color
     */
    public java.awt.Color toAwtColor() {
        return new java.awt.Color(getRedF(), getGreenF(), getBlueF(), getAlphaF());
    }

    /**
     * Gets the green component as a float between 0 and 1
     * @return The green component
     */
    public float getGreenF() {
        return g;
    }

    /**
     * Gets the alpha component as a float between 0 and 1
     * @return The alpha component
     */
    public float getAlphaF() {
        return a;
    }

    /**
     * Gets the blue component as a float between 0 and 1
     * @return The blue component
     */
    public float getBlueF() {
        return b;
    }

    /**
     * Gets the red component as a float between 0 and 1
     * @return The red component
     */
    public float getRedF() {
        return r;
    }

    /**
     * Gets the red component as a int between 0 and 255
     * @return The red component
     */
    public int getRed() {
        return (int) (getRedF() * 255);
    }

    /**
     * Gets the green component as a int between 0 and 255
     * @return The green component
     */
    public int getGreen() {
        return (int) (getGreenF() * 255);
    }

    /**
     * Gets the blue component as a int between 0 and 255
     * @return The blue component
     */
    public int getBlue() {
        return (int) (getBlueF() * 255);
    }


    /**
     * Gets the alpha component as a int between 0 and 255
     * @return The alpha component
     */
    public int getAlpha() {
        return (int) (getAlphaF() * 255);
    }

    /**
     * Converts this color to a 0xRRGGBBAA integer
     * @return The 0xRRGGBBAA integer
     */
    public int toRGBAInt() {
        return Colors.RGBAToInt(getRed(), getGreen(), getBlue(), getAlpha());
    }

    private boolean isValid(float a) {
        return a >= 0 && a <= 1;
    }
}
