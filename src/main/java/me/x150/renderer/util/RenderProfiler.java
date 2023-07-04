package me.x150.renderer.util;

import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A render profiler
 */
public class RenderProfiler {
	static final Stack<Entry> s = new Stack<>();
	static final Map<String, Entry> latestTickTimes = new ConcurrentHashMap<>();

	/**
	 * Adds a new element to the profiler
	 *
	 * @param sec The name of the element
	 */
	public static void begin(String sec) {
		long start = System.nanoTime();
		s.push(new Entry(sec, start, start));
	}

	/**
	 * Stops recording the segment currently on the stack
	 */
	public static void pop() {
		Entry pop = s.pop();
		latestTickTimes.put(pop.name, new Entry(pop.name, pop.start, System.nanoTime()));
	}

	/**
	 * Gets all elements recorded
	 *
	 * @return All elements recorded
	 */
	public static Entry[] getAllTickTimes() {
		Entry[] e = new Entry[latestTickTimes.size()];
		String[] ks = latestTickTimes.keySet().toArray(String[]::new);
		for (int i = 0; i < ks.length; i++) {
			e[i] = latestTickTimes.get(ks[i]);
		}
		latestTickTimes.clear();
		return e;
	}

	public record Entry(String name, long start, long end) {
	}
}
