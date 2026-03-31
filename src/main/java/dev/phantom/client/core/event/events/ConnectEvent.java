package dev.phantom.client.core.event.events;

import dev.phantom.client.core.event.Event;

public class ConnectEvent {

    public static class Connect extends Event {
        private final String address;

        public Connect(String address) {
            this.address = address;
        }

        public String getAddress() {
            return address;
        }
    }

    public static class Disconnect extends Event {

        public Disconnect() {}
    }
}
