package me.x150.renderer.util;

import java.util.Stack;

/**
 * An alpha override utility. Has the ability to modify the color of any element on screen, rendered by the library
 * @deprecated Not really maintainable. Use RenderSystem.setShaderColor(r, g, b, a) instead (sadly does not stack by default).
 * Example usage of setShaderColor: {@code setShaderColor(1f, 1f, 1f, getShaderColor()[2]*0.5f)}, alternatively you can shorten it to
 * {@code getShaderColor()[2] *= 0.5f}. To undo, {@code getShaderColor()[2] /= 0.5f}
 */
@Deprecated(forRemoval = true, since = "1.2.3")
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
