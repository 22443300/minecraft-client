package dev.phantom.client.core.event;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus {

    public static final EventBus INSTANCE = new EventBus();

    private final Map<Class<?>, CopyOnWriteArrayList<ListenerEntry>> listeners = new ConcurrentHashMap<>();
    private final Map<Object, List<ListenerEntry>> listenersByObject = new ConcurrentHashMap<>();
    private final MethodHandles.Lookup lookup = MethodHandles.lookup();

    private EventBus() {}

    public void subscribe(Object listener) {
        List<ListenerEntry> entries = new ArrayList<>();
        Class<?> clazz = listener.getClass();

        for (Method method : clazz.getDeclaredMethods()) {
            EventHandler annotation = method.getAnnotation(EventHandler.class);
            if (annotation == null) continue;
            if (method.getParameterCount() != 1) continue;

            Class<?> eventType = method.getParameterTypes()[0];
            if (!Event.class.isAssignableFrom(eventType)) continue;

            method.setAccessible(true);
            try {
                MethodHandle handle = lookup.unreflect(method).bindTo(listener);
                ListenerEntry entry = new ListenerEntry(listener, handle, annotation.priority(), eventType);
                entries.add(entry);
                listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(entry);
                sortListeners(eventType);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to create MethodHandle for " + method.getName(), e);
            }
        }

        listenersByObject.put(listener, entries);
    }

    public void unsubscribe(Object listener) {
        List<ListenerEntry> entries = listenersByObject.remove(listener);
        if (entries == null) return;

        for (ListenerEntry entry : entries) {
            CopyOnWriteArrayList<ListenerEntry> list = listeners.get(entry.eventType());
            if (list != null) {
                list.remove(entry);
            }
        }
    }

    public <T extends Event> T post(T event) {
        CopyOnWriteArrayList<ListenerEntry> entries = listeners.get(event.getClass());
        if (entries == null) return event;

        for (ListenerEntry entry : entries) {
            if (event instanceof CancellableEvent cancellable && cancellable.isCancelled()) {
                break;
            }
            try {
                entry.handle().invokeWithArguments(event);
            } catch (Throwable e) {
                throw new RuntimeException("Error dispatching event " + event.getClass().getSimpleName(), e);
            }
        }

        return event;
    }

    private void sortListeners(Class<?> eventType) {
        CopyOnWriteArrayList<ListenerEntry> list = listeners.get(eventType);
        if (list == null) return;

        List<ListenerEntry> sorted = new ArrayList<>(list);
        sorted.sort(Comparator.comparingInt(e -> e.priority().ordinal()));
        list.clear();
        list.addAll(sorted);
    }

    public static void postEvent(Event event) {
        INSTANCE.post(event);
    }

    public static void subscribeListener(Object listener) {
        INSTANCE.subscribe(listener);
    }

    public static void unsubscribeListener(Object listener) {
        INSTANCE.unsubscribe(listener);
    }

    private record ListenerEntry(Object owner, MethodHandle handle, Priority priority, Class<?> eventType) {}
}
