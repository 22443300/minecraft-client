package dev.phantom.client.integration;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegrationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("PhantomIntegrations");
    private boolean worldEditLoaded;
    private boolean litematicaLoaded;
    private boolean xaeroMinimapLoaded;
    private boolean voiceChatLoaded;
    public WorldEditIntegration worldEdit;
    public LitematicaIntegration litematica;
    public XaeroMinimapIntegration xaeroMinimap;
    public VoiceChatIntegration voiceChat;

    public void init() {
        FabricLoader fl = FabricLoader.getInstance();
        worldEditLoaded = fl.isModLoaded("worldedit");
        litematicaLoaded = fl.isModLoaded("litematica");
        xaeroMinimapLoaded = fl.isModLoaded("xaerominimap");
        voiceChatLoaded = fl.isModLoaded("voicechat");
        if (worldEditLoaded) { worldEdit = new WorldEditIntegration(); LOGGER.info("[Phantom] WorldEdit integration active"); }
        if (litematicaLoaded) { litematica = new LitematicaIntegration(); LOGGER.info("[Phantom] Litematica integration active"); }
        if (xaeroMinimapLoaded) { xaeroMinimap = new XaeroMinimapIntegration(); LOGGER.info("[Phantom] Xaero's Minimap integration active"); }
        if (voiceChatLoaded) { voiceChat = new VoiceChatIntegration(); LOGGER.info("[Phantom] Simple Voice Chat integration active"); }
        if (!worldEditLoaded && !litematicaLoaded && !xaeroMinimapLoaded && !voiceChatLoaded) {
            LOGGER.info("[Phantom] No optional integrations detected");
        }
    }

    public boolean isWorldEditLoaded() { return worldEditLoaded; }
    public boolean isLitematicaLoaded() { return litematicaLoaded; }
    public boolean isXaeroMinimapLoaded() { return xaeroMinimapLoaded; }
    public boolean isVoiceChatLoaded() { return voiceChatLoaded; }

    public WorldEditIntegration getWorldEdit() { return worldEdit; }
    public LitematicaIntegration getLitematica() { return litematica; }
    public XaeroMinimapIntegration getXaeroMinimap() { return xaeroMinimap; }
    public VoiceChatIntegration getVoiceChat() { return voiceChat; }
}
