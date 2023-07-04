package me.x150.renderer.util;

import java.util.Stack;

/**
 * An alpha override utility. Has the ability to modify the color of any element on screen, rendered by the library
 */
public class AlphaOverride {
	private static final Stack<Float> alphaMultipliers = new Stack<>();

	/**
	 * Pushes an alpha multiplier (0-1) to the stack
	 *
	 * @param val The alpha multiplier
	 */
	public static void pushAlphaMul(float val) {
		alphaMultipliers.push(val);
	}

	/**
	 * Pops the previously added alpha multiplier from the stack
	 */
	public static void popAlphaMul() {
		alphaMultipliers.pop();
	}

	/**
	 * Computes the new alpha from the initialAlpha, based on the current state of the stack
	 *
	 * @param initialAlpha The initial alpha (0-1)
	 * @return The new alpha
	 */
	public static float compute(float initialAlpha) {
		float alpha = initialAlpha;
		for (Float alphaMultiplier : alphaMultipliers) {
			alpha *= alphaMultiplier;
		}
		return alpha;
	}
}
