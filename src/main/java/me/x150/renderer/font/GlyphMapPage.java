package me.x150.renderer.font;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.Closeable;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GlyphMapPage implements Closeable {

	private final ReentrantReadWriteLock rrw = new ReentrantReadWriteLock(false);
	private final Font font;
	private final int nCharacters;
	private final int padding;

	private GlyphMap[] maps;

	public GlyphMapPage(Font font, int nCharacters, int padding) {
		Preconditions.checkArgument(nCharacters >= 32, "should be more than 32 chars per page to not blow up the map array");
		this.font = font;
		this.nCharacters = nCharacters;
		int maxMaps = 65536 / nCharacters;
		int indexOfAlphaNumericLimit = 0xFF / nCharacters;
		// allocate at minimum enough pages to fit to char of 0xFF. should cover most of the maps we need
		// if a crazy request comes through, assuming a reasonable size, we shouldnt blow up that much
		this.maps = new GlyphMap[Math.min(indexOfAlphaNumericLimit + 1, maxMaps)];
		this.padding = padding;
	}

	private static int floorNearestMulN(int x, int n) {
		return n * (int) Math.floor((double) x / (double) n);
	}

	@Nullable
	private GlyphMap getMapNoLock(int index) {
		if (index >= maps.length) {
			resizeToNoLock(index + 1);
			return null; // we already know we dont have one here
		}
		return maps[index];
	}

	public GlyphMap getOrCreateMap(char forChar) {
		int base = floorNearestMulN(forChar, nCharacters);
		int index = base / nCharacters;
		rrw.readLock().lock();
		GlyphMap map = getMapNoLock(index);
		rrw.readLock().unlock();
		if (map != null) return map;
		int until = base + nCharacters;
		GlyphMap gm = new GlyphMap((char) base, (char) until, font, padding);
		gm.generate();
		rrw.writeLock().lock();
		maps[index] = gm;
		rrw.writeLock().unlock();
		return gm;
	}

	private void resizeToNoLock(int len) {
		assert len > maps.length;
		GlyphMap[] newMaps = new GlyphMap[len];
		System.arraycopy(maps, 0, newMaps, 0, maps.length);
		this.maps = newMaps;
	}

	@Override
	public void close() {
		rrw.writeLock().lock();
		for (GlyphMap map : maps) {
			if (map != null) map.destroy();
		}
		Arrays.fill(maps, null);
		rrw.writeLock().unlock();

	}
}
