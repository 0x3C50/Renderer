package me.x150.renderer.fontng;

import org.jetbrains.annotations.ApiStatus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Manages automatic scaling of Font objects in relation to the window scaling factor
 */
public class FontScalingRegistry {
	private static final List<WeakReference<Font>> entries = new ArrayList<>();
	private static int lastSize = -1;

	/**
	 * Registers the given fonts into the registry. They're kept as weak references.
	 *
	 * @param first First font to add
	 * @param more  More fonts to add
	 */
	public static void register(Font first, Font... more) {
		synchronized (entries) {
			entries.add(new WeakReference<>(first));
			for (Font font : more) {
				entries.add(new WeakReference<>(font));
			}
		}
		resize(lastSize);
	}

	/**
	 * (Internal method) Sizes the fonts to the given scale
	 *
	 * @param to Scale
	 */
	@ApiStatus.Internal
	public static void resize(int to) {
		synchronized (entries) {
			for (Iterator<WeakReference<Font>> iterator = entries.iterator(); iterator.hasNext(); ) {
				Font font = iterator.next().get();
				if (font == null) {
					iterator.remove();
					continue;
				}
				if (font.lastScale == to) continue;
				font.setScale(to);
			}
		}
		lastSize = to;
	}
}
