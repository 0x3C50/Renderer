package me.x150.renderer.util;

import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

public class RenderProfiler {
    static Stack<Entry> s = new Stack<>();
    static Map<String, Entry> latestTickTimes = new ConcurrentHashMap<>();

    public static void begin(String sec) {
        long start = System.nanoTime();
        s.push(new Entry(sec, start, start));
    }

    public static void pop() {
        Entry pop = s.pop();
        latestTickTimes.put(pop.name, new Entry(pop.name, pop.start, System.nanoTime()));
    }

    public static Entry[] getAllTickTimes() {
        Entry[] e = new Entry[latestTickTimes.size()];
        String[] ks = latestTickTimes.keySet().toArray(String[]::new);
        for (int i = 0; i < ks.length; i++) {
            e[i] = latestTickTimes.get(ks[i]);
        }
        return e;
    }

    public record Entry(String name, long start, long end) {
    }
}
