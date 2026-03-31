package dev.phantom.client.core.event.events;

import dev.phantom.client.core.event.Event;
import net.minecraft.entity.damage.DamageSource;

public class PlayerDeathEvent extends Event {

    private final DamageSource source;

    public PlayerDeathEvent(DamageSource source) {
        this.source = source;
    }

    public DamageSource getSource() {
        return source;
    }
}
