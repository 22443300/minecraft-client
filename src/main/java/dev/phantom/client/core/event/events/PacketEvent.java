package dev.phantom.client.core.event.events;

import dev.phantom.client.core.event.CancellableEvent;
import net.minecraft.network.packet.Packet;

public class PacketEvent {

    public static class Send extends CancellableEvent {
        private Packet<?> packet;

        public Send(Packet<?> packet) {
            this.packet = packet;
        }

        public Packet<?> getPacket() {
            return packet;
        }

        public void setPacket(Packet<?> packet) {
            this.packet = packet;
        }
    }

    public static class Receive extends CancellableEvent {
        private Packet<?> packet;

        public Receive(Packet<?> packet) {
            this.packet = packet;
        }

        public Packet<?> getPacket() {
            return packet;
        }

        public void setPacket(Packet<?> packet) {
            this.packet = packet;
        }
    }
}
