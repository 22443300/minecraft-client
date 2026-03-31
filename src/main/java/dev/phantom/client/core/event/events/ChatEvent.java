package dev.phantom.client.core.event.events;

import dev.phantom.client.core.event.CancellableEvent;

public class ChatEvent {

    public static class Send extends CancellableEvent {
        private String message;

        public Send(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class Receive extends CancellableEvent {
        private String message;

        public Receive(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
