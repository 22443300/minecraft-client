package dev.phantom.client;

import dev.phantom.client.core.config.ConfigManager;
import dev.phantom.client.core.event.EventBus;
import dev.phantom.client.core.event.events.ConnectEvent;
import dev.phantom.client.core.event.events.RenderHudEvent;
import dev.phantom.client.core.event.events.RenderWorldEvent;
import dev.phantom.client.core.friend.FriendManager;
import dev.phantom.client.core.macro.MacroManager;
import dev.phantom.client.core.module.ModuleManager;
import dev.phantom.client.gui.ClickGui;
import dev.phantom.client.gui.hud.HudManager;
import dev.phantom.client.integration.IntegrationManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class PhantomClient implements ClientModInitializer {

    public static final String MOD_ID = "phantom";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static PhantomClient INSTANCE;

    public final EventBus eventBus = new EventBus();
    public final ModuleManager modules = new ModuleManager();
    public final ConfigManager config = new ConfigManager();
    public final FriendManager friends = new FriendManager();
    public final MacroManager macros = new MacroManager();
    public final HudManager hud = new HudManager();
    public final IntegrationManager integrations = new IntegrationManager();

    private KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        integrations.init();
        modules.init();
        config.load();
        hud.init();

        // Register TAB keybind to open ClickGui
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.phantom.opengui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_TAB,
                "category.phantom"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (openGuiKey.wasPressed() && mc.currentScreen == null) {
                mc.setScreen(new ClickGui());
            }
        });

        // Register WorldRenderEvents.AFTER_ENTITIES → RenderWorldEvent
        // WorldRenderContext provides matrixStack(), camera(), and tickCounter().
        // We pass tickCounter().getTickDelta(true) as the float tickDelta.
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            float tickDelta = context.tickCounter().getTickDelta(true);
            PhantomClient.INSTANCE.eventBus.post(
                    new RenderWorldEvent(context.matrixStack(), tickDelta, context.camera())
            );
        });

        // Register HudRenderCallback → RenderHudEvent
        // In 1.21.1 the callback receives (DrawContext, RenderTickCounter).
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            float tickDelta = tickCounter.getTickDelta(true);
            PhantomClient.INSTANCE.eventBus.post(new RenderHudEvent(drawContext, tickDelta));
        });

        // Register ClientPlayConnectionEvents.JOIN → ConnectEvent.Connect
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            String address = client.getCurrentServerEntry() != null
                    ? client.getCurrentServerEntry().address
                    : "unknown";
            PhantomClient.INSTANCE.eventBus.post(new ConnectEvent.Connect(address));
        });

        LOGGER.info("Phantom Client v1.0.0 initialized");
    }
}
