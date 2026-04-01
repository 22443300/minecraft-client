package dev.phantom.client.modules.qol;
import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;
public class PacketLogger extends Module {
    private final BooleanSetting clientbound = register(new BooleanSetting("Clientbound","Log incoming packets",true));
    private final BooleanSetting serverbound = register(new BooleanSetting("Serverbound","Log outgoing packets",true));
    private final ModeSetting filterMode = register(new ModeSetting("FilterMode","Filter mode","All",new String[]{"All","Whitelist","Blacklist"}));
    private final StringSetting filter = register(new StringSetting("Filter","Packet name filters (;-sep)",""));
    private final BooleanSetting logToFile = register(new BooleanSetting("LogToFile","Save packets to file",false));
    public PacketLogger() { super("PacketLogger","Logs all network packets",Category.QOL); }
}
