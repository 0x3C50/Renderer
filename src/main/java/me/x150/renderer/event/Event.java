package me.x150.renderer.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * A simple event
 */
@RequiredArgsConstructor
public class Event {
    /**
     * Event shift. Dictates when the event has been emitted
     */
    public enum Shift {
        /**
         * Before the event happens. Can be cancelled
         */
        PRE,
        /**
         * After the event happens. Can be cancelled
         */
        POST
    }

    /**
     * The event shift
     */
    @Getter
    private final Shift shift;
    /**
     * Whether this event is cancelled or not
     */
    @Getter
    private boolean cancelled = false;

    /**
     * Cancels this event
     */
    public void cancel() {
        cancelled = true;
    }
}
