package dev.phantom.client.mixin;

import net.minecraft.client.world.ClientChunkManager;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Mixin targeting {@link ClientChunkManager}.
 *
 * <p>This mixin is intentionally minimal. Its primary role is to serve as a
 * compile-time placeholder that satisfies the mixin configuration entry so
 * that features requiring chunk-load awareness (e.g. XRay chunk reloading)
 * can be wired in later without touching the mixin JSON.
 *
 * <p>If XRay or another module needs to detect when individual chunks are
 * loaded or unloaded, injections into {@code loadChunkFromPacket} or the
 * chunk-unload path can be added here.
 */
@Mixin(ClientChunkManager.class)
public class ClientChunkManagerMixin {
    // No active injections yet. Extend this class with @Inject methods
    // targeting load/unload callbacks when chunk-event support is needed.
}
