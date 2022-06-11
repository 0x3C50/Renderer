package me.x150.renderer.renderer.color;

import com.google.common.base.Preconditions;

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

    public Color(int red, int green, int blue, int alpha) {
        this(red / 255f, green / 255f, blue / 255f, alpha / 255f);
    }

    public Color(int red, int green, int blue) {
        this(red, green, blue, 255);
    }

    public Color(float red, float green, float blue) {
        this(red, green, blue, 1f);
    }

    public Color(java.awt.Color color) {
        this(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public java.awt.Color toAwtColor() {
        return new java.awt.Color(getRedF(), getGreenF(), getBlueF(), getAlphaF());
    }

    public float getGreenF() {
        return g;
    }

    public float getAlphaF() {
        return a;
    }

    public float getBlueF() {
        return b;
    }

    public float getRedF() {
        return r;
    }

    public int getRed() {
        return (int) (getRedF() * 255);
    }

    public int getGreen() {
        return (int) (getGreenF() * 255);
    }

    public int getBlue() {
        return (int) (getBlueF() * 255);
    }

    public int getAlpha() {
        return (int) (getAlphaF() * 255);
    }

    public int toRGBAInt() {
        return Colors.RGBAToInt(getRed(), getGreen(), getBlue(), getAlpha());
    }

    private boolean isValid(float a) {
        return a >= 0 && a <= 1;
    }
}
