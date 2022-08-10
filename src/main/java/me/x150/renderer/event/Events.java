/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package me.x150.renderer.event;


import me.x150.renderer.event.events.base.Event;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Event management system
 */
public class Events {
    static final List<ListenerEntry> entries = new CopyOnWriteArrayList<>();

    /**
     * Registers a new event handler
     *
     * @param uniqueId The ID of this event. Used to check if it was previously registered and to un-register it.
     * @param event    The event type to subscribe to
     * @param handler  The event handler
     * @return The ListenerEntry for this event
     */
    public static ListenerEntry registerEventHandler(int uniqueId, EventType event, Shift shift, Consumer<? extends Event> handler) {
        if (entries.stream().noneMatch(listenerEntry -> listenerEntry.id == uniqueId)) {
            ListenerEntry le = new ListenerEntry(uniqueId, event, shift, handler);
            entries.add(le);
            return le;
        } else {
            return entries.stream().filter(listenerEntry -> listenerEntry.id == uniqueId).findFirst().orElseThrow();
        }
    }

    /**
     * Removes a handler
     *
     * @param id The handler ID
     */
    public static void unregister(int id) {
        entries.removeIf(listenerEntry -> listenerEntry.id == id);
    }

    /**
     * Registers a new event handler
     *
     * @param event   The event to subscribe to
     * @param handler The event handler
     * @return The ListenerEntry for this event, with a randomly generated ID
     */
    public static ListenerEntry registerEventHandler(EventType event, Shift shift, Consumer<? extends Event> handler) {
        return registerEventHandler((int) Math.floor(Math.random() * 0xFFFFFF), event, shift, handler);
    }

    /**
     * Registers a new event handler class. All event handler methods must be annotated with {@link EventListener} and have exactly one argument, that argument being the {@link Event} to supply them to.
     *
     * @param instance The instance of the handler class
     */
    public static void registerEventHandlerClass(Object instance) {
        for (Method declaredMethod : instance.getClass().getDeclaredMethods()) {
            for (Annotation declaredAnnotation : declaredMethod.getDeclaredAnnotations()) {
                if (declaredAnnotation.annotationType() == EventListener.class) {
                    EventListener ev = (EventListener) declaredAnnotation;
                    Class<?>[] params = declaredMethod.getParameterTypes();
                    if (params.length != 1 || !Event.class.isAssignableFrom(params[0])) {
                        throw new IllegalArgumentException(String.format("Event handler %s(%s) -> %s from %s is malformed",
                                declaredMethod.getName(),
                                Arrays.stream(params).map(Class::getSimpleName).collect(Collectors.joining(", ")),
                                declaredMethod.getReturnType().getName(),
                                instance.getClass().getName()
                        ));
                    } else {
                        declaredMethod.setAccessible(true);
                        registerEventHandler((instance.getClass().getName() + declaredMethod.getName()).hashCode(),
                                ev.type(),
                                ev.shift(),
                                event -> {
                                    try {
                                        declaredMethod.invoke(instance, event);
                                    } catch (IllegalAccessException | InvocationTargetException e) {
                                        e.printStackTrace();
                                    }
                                }
                        );
                    }
                }
            }
        }
    }

    /**
     * Fires an event handler stack
     *
     * @param event    The event to fire
     * @param argument The argument supplied to the handlers
     * @return If the event has been marked as cancelled
     */
    @SuppressWarnings("unchecked")
    public static boolean fireEvent(EventType event, Shift shift, Event argument) {
        for (ListenerEntry entry : entries) {
            if (entry.type == event && entry.shift == shift) {
                ((Consumer<Event>) entry.eventListener()).accept(argument);
            }
        }
        return argument.isCancelled();
    }

    record ListenerEntry(int id, EventType type, Shift shift, Consumer<? extends Event> eventListener) {
    }
}
