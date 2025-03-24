package me.x150.renderer.fontng;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;

abstract class RefWatcher implements AutoCloseable {
	private static class Watcher extends PhantomReference<RefWatcher> {
		private final Class<? extends RefWatcher> rfw;
		boolean wasClosed = false;
		public Watcher(RefWatcher referent, ReferenceQueue<? super RefWatcher> q) {
			super(referent, q);
			rfw = referent.getClass();
		}

		public void checkWarn() {
			if (!wasClosed) {
				System.err.println("MEMORY LEAK! An instance of "+rfw+" was GCd without being closed. Resources are most likely still reserved.");
				System.err.println("Please make sure to close your references!");
			}
		}
	}

	static {
		Thread t = new Thread(RefWatcher::refWatcherMain, "refWatcher");
		t.setDaemon(true);
		t.start();
	}

	private static void refWatcherMain() {
		while (true) {
			try {
				Watcher ww = ((Watcher) refs.remove());
				synchronized (watchers) {
					watchers.remove(ww);
				}
				ww.checkWarn();
			} catch (InterruptedException e) {
				break;
			}
		}
	}

	private static final ReferenceQueue<RefWatcher> refs = new ReferenceQueue<>();
	private static final ArrayList<Watcher> watchers = new ArrayList<>(128);

	private final Watcher watcher;

	protected void checkClosed() {
		if (closed) throw new IllegalStateException("called while closed");
	}

	public RefWatcher() {
		synchronized (watchers) {
			watchers.add(watcher = new Watcher(this, refs));
		}
	}
	protected volatile boolean closed = false;
	protected abstract void implClose();
	@Override
	public final void close() {
		checkClosed();

		closed = true;
		watcher.wasClosed = true;
		implClose();
	}
}
