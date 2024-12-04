package me.x150.renderer.font;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.Closeable;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GlyphPageManager implements Closeable {

	private final ReentrantReadWriteLock rrw = new ReentrantReadWriteLock(false);
	private final Font font;
	private final int nCharacters;

	private GlyphPage[] maps;

	public GlyphPageManager(Font font, int nCharacters) {
		Preconditions.checkArgument(nCharacters >= 32, "should be more than 32 chars per page to not blow up the map array");
		this.font = font;
		this.nCharacters = nCharacters;
		int maxMaps = Math.floorDiv(65536, nCharacters); // max amount of maps we need to create
		int whereIsFF = 0xFF / nCharacters; // index of the page containing the 0xFF character, or 'ÿ'
		// allocate at minimum enough pages to fit to char of 0xFF. should cover most of the maps we need
		// if a crazy request comes through, assuming a reasonable size, we shouldnt blow up that much
		this.maps = new GlyphPage[Math.min(whereIsFF + 1, maxMaps)];
	}

	private static int floorNearestMulN(int x, int n) {
		return n * (int) Math.floor((double) x / (double) n);
	}

	@Nullable
	private GlyphPage getMapNoLock(int index) {
		if (index >= maps.length) {
			resizeToNoLock(index + 1);
			return null; // we already know we dont have one here
		}
		return maps[index];
	}

	public GlyphPage getOrCreateMap(char forChar) {
		int base = floorNearestMulN(forChar, nCharacters);
		int index = base / nCharacters;
		rrw.readLock().lock();
		GlyphPage map = getMapNoLock(index);
		rrw.readLock().unlock();
		if (map != null) return map;
		int until = base + nCharacters;
		GlyphPage gm = new GlyphPage((char) base, (char) until, font);
		gm.generate();
		rrw.writeLock().lock();
		maps[index] = gm;
		rrw.writeLock().unlock();
		return gm;
	}

	private void resizeToNoLock(int len) {
		assert len > maps.length;
		GlyphPage[] newMaps = new GlyphPage[len];
		System.arraycopy(maps, 0, newMaps, 0, maps.length);
		this.maps = newMaps;
	}

	@Override
	public void close() {
		rrw.writeLock().lock();
		for (GlyphPage map : maps) {
			if (map != null) map.destroy();
		}
		Arrays.fill(maps, null);
		rrw.writeLock().unlock();

	}
}
