package me.x150.renderer.util;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Range;

/**
 * Helper for colors, specifically for parsing hex colors into rgb(a) components and converting rgb(a) components into hex colors
 */
public class Colors {
	/**
	 * <p>Converts RGBA into one single integer</p>
	 * <p>The output color is 0xAARRGGBB formatted</p>
	 *
	 * @param r The red component
	 * @param g The green component
	 * @param b The blue component
	 * @param a The alpha component
	 * @return The integer describing the color, formatted 0xAARRGGBB
	 */
	public static int ARGBToInt(@Range(from = 0, to = 255) int r, @Range(from = 0, to = 255) int g, @Range(from = 0, to = 255) int b, @Range(from = 0, to = 255) int a) {
		Preconditions.checkArgument(validateColorRange(r), "Expected r to be 0-255, received " + r);
		Preconditions.checkArgument(validateColorRange(g), "Expected g to be 0-255, received " + g);
		Preconditions.checkArgument(validateColorRange(b), "Expected b to be 0-255, received " + b);
		Preconditions.checkArgument(validateColorRange(a), "Expected a to be 0-255, received " + a);
		return a << 8 * 3 | r << 8 * 2 | g << 8 | b;
	}

	/**
	 * <p>Converts RGBA into one single integer</p>
	 * <p>The output color is 0xRRGGBBAA formatted</p>
	 *
	 * @param r The red component
	 * @param g The green component
	 * @param b The blue component
	 * @param a The alpha component
	 * @return The integer describing the color, formatted 0xRRGGBBAA
	 */
	public static int RGBAToInt(@Range(from = 0, to = 255) int r, @Range(from = 0, to = 255) int g, @Range(from = 0, to = 255) int b, @Range(from = 0, to = 255) int a) {
		Preconditions.checkArgument(validateColorRange(r), "Expected r to be 0-255, received " + r);
		Preconditions.checkArgument(validateColorRange(g), "Expected g to be 0-255, received " + g);
		Preconditions.checkArgument(validateColorRange(b), "Expected b to be 0-255, received " + b);
		Preconditions.checkArgument(validateColorRange(a), "Expected a to be 0-255, received " + a);
		return r << 8 * 3 | g << 8 * 2 | b << 8 | a;
	}

	/**
	 * <p>Parses a single RGBA formatted integer into RGBA format</p>
	 *
	 * @param in The input color integer
	 * @return A length 4 array containing the R, G, B and A component of the color
	 */
	public static int[] RGBAIntToRGBA(int in) {
		int red = in >> 8 * 3 & 0xFF;
		int green = in >> 8 * 2 & 0xFF;
		int blue = in >> 8 & 0xFF;
		int alpha = in & 0xFF;
		return new int[]{red, green, blue, alpha};
	}

	/**
	 * <p>Parses a single ARGB formatted integer into RGBA format</p>
	 *
	 * @param in The input color integer
	 * @return A length 4 array containing the R, G, B and A component of the color
	 */
	public static int[] ARGBIntToRGBA(int in) {
		int alpha = in >> 8 * 3 & 0xFF;
		int red = in >> 8 * 2 & 0xFF;
		int green = in >> 8 & 0xFF;
		int blue = in & 0xFF;
		return new int[]{red, green, blue, alpha};
	}

	/**
	 * <p>Parses a single RGB formatted integer into RGB format</p>
	 *
	 * @param in The input color integer
	 * @return A length 3 array containing the R, G and B component of the color
	 */
	public static int[] RGBIntToRGB(int in) {
		int red = in >> 8 * 2 & 0xFF;
		int green = in >> 8 & 0xFF;
		int blue = in & 0xFF;
		return new int[]{red, green, blue};
	}

	/**
	 * <p>Converts an int[4] color array (RGBA) to a float[4] array (RGBA)</p>
	 *
	 * @return The float[4] array
	 */
	public static float[] intArrayToFloatArray(int[] in) {
		Preconditions.checkArgument(in.length == 4, "Expected int[] of size 4, got " + in.length);
		for (int i = 0; i < in.length; i++) {
			Preconditions.checkArgument(validateColorRange(in[i]), "Expected in[" + i + "] to be 0-255, got " + in[i]);
		}
		return new float[]{in[0] / 255f, in[1] / 255f, in[2] / 255f, in[3] / 255f};
	}

	private static boolean validateColorRange(int in) {
		return in >= 0 && in <= 255;
	}
}
