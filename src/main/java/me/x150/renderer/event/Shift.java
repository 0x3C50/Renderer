package me.x150.renderer.event;

/**
 * Event shift
 */
public enum Shift {
    /**
     * PRE event. Can (often) be cancelled
     */
    PRE,
    /**
     * POST event. Can (often) not be cancelled, but will not throw when attempted to.
     */
    POST
}
