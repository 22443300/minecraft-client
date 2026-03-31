package dev.phantom.client.core.event;

public class CancellableEvent extends Event {

    public void cancel() {
        this.cancelled = true;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}
