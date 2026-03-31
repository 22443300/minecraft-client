package dev.phantom.client.integration;

import dev.phantom.client.PhantomClient;
import net.minecraft.entity.Entity;

public class XaeroMinimapIntegration {

    /**
     * Attempts to add a named waypoint to Xaero's Minimap.
     * Uses reflection to avoid a hard compile-time dependency.
     *
     * @param name  Display name for the waypoint
     * @param x     World X coordinate
     * @param y     World Y coordinate
     * @param z     World Z coordinate
     * @param color ARGB color int for the waypoint marker
     */
    public void addWaypoint(String name, double x, double y, double z, int color) {
        try {
            // Xaero's Minimap exposes WaypointsManager via the session singleton.
            // All access is done via reflection to avoid NoClassDefFoundError when absent.
            Class<?> sessionClass = Class.forName("xaero.common.XaeroMinimapSession");
            Object session = sessionClass.getMethod("getCurrentSession").invoke(null);
            if (session == null) {
                PhantomClient.LOGGER.warn("[Phantom] Xaero addWaypoint: no active session");
                return;
            }

            Object waypointsManager = session.getClass()
                    .getMethod("getWaypointsManager")
                    .invoke(session);
            if (waypointsManager == null) return;

            Object currentSet = waypointsManager.getClass()
                    .getMethod("getCurrentWaypointSet")
                    .invoke(waypointsManager);
            if (currentSet == null) return;

            Class<?> waypointClass = Class.forName("xaero.common.minimap.waypoints.Waypoint");
            String initials = name.isEmpty() ? "?" : String.valueOf(name.charAt(0)).toUpperCase();
            // Common constructor: (int x, int y, int z, String name, String initials, int color, int type, boolean disabled)
            Object waypoint = waypointClass.getDeclaredConstructors()[0].newInstance(
                    (int) x, (int) y, (int) z,
                    name,
                    initials,
                    color & 0xFFFFFF,
                    0,
                    false
            );

            currentSet.getClass().getMethod("addWaypoint", waypointClass).invoke(currentSet, waypoint);
            PhantomClient.LOGGER.info("[Phantom] Xaero waypoint '{}' added at ({}, {}, {})",
                    name, (int) x, (int) y, (int) z);

        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            // Xaero not loaded - expected when absent
        } catch (Exception e) {
            PhantomClient.LOGGER.debug("[Phantom] Xaero addWaypoint failed: {}", e.getMessage());
        }
    }

    /**
     * Adds a custom entity blip to Xaero's Minimap radar.
     * Full blip injection requires a mixin into Xaero's rendering pipeline;
     * this method logs the request and exits gracefully without crashing.
     *
     * @param entity The entity to mark on the minimap
     * @param color  ARGB color int for the blip
     */
    public void addEntityBlip(Entity entity, int color) {
        try {
            if (entity == null) return;
            PhantomClient.LOGGER.debug(
                    "[Phantom] Xaero addEntityBlip: entity '{}' at ({}, {}, {}); injection requires mixin",
                    entity.getName().getString(),
                    (int) entity.getX(), (int) entity.getY(), (int) entity.getZ()
            );
        } catch (Exception | NoClassDefFoundError e) {
            PhantomClient.LOGGER.debug("[Phantom] Xaero addEntityBlip failed: {}", e.getMessage());
        }
    }
}
