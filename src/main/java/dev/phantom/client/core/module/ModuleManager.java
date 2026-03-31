package dev.phantom.client.core.module;

import dev.phantom.client.modules.combat.*;
import dev.phantom.client.modules.movement.*;
import dev.phantom.client.modules.visual.*;
import dev.phantom.client.modules.world.*;
import dev.phantom.client.modules.utility.*;
import dev.phantom.client.modules.qol.*;
import dev.phantom.client.modules.tweakeroo.*;

import java.util.*;
import java.util.stream.Collectors;

public class ModuleManager {

    private final List<Module> modules = new ArrayList<>();
    private final Map<String, Module> byName = new HashMap<>();

    public void init() {
        registerAll();
    }

    public void register(Module... mods) {
        for (Module mod : mods) {
            modules.add(mod);
            byName.put(mod.getName().toLowerCase(), mod);
        }
    }

    public void registerAll() {
        // Combat
        register(
            new CrystalAura(),
            new KillAura(),
            new AimAssist(),
            new AnchorAura(),
            new AutoTotem(),
            new Surround(),
            new BowAimbot(),
            new Velocity(),
            new Criticals(),
            new AutoArmor(),
            new OffhandSwap(),
            new MaceCombo(),
            new SpearLunge()
        );

        // Movement
        register(
            new ElytraFly(),
            new ElytraSwap(),
            new NoFall(),
            new FastBridge(),
            new Scaffold(),
            new Speed(),
            new Parkour(),
            new NoSlow(),
            new Step(),
            new Glide(),
            new BoatFly()
        );

        // Visual
        register(
            new XRay(),
            new ESP(),
            new Tracers(),
            new FullBright(),
            new NoRender(),
            new CaveMap(),
            new StorageESP(),
            new SearchESP(),
            new Chams(),
            new NameTags(),
            new Trajectories(),
            new HoleESP(),
            new TimeChanger(),
            new WeatherChanger()
        );

        // World
        register(
            new Nuker(),
            new AutoMine(),
            new InstaBreak(),
            new SpeedMine(),
            new Flatten(),
            new AutoBuild(),
            new FastPlace(),
            new NoClip(),
            new WorldEditMod(),
            new LitematicaMod(),
            new FAWEMod()
        );

        // Utility
        register(
            new AutoEat(),
            new AutoFish(),
            new AutoFarm(),
            new ChestStealer(),
            new AutoDrop(),
            new AutoSprint(),
            new AutoRespawn(),
            new NoInteract(),
            new ChatFilter(),
            new AutoReply(),
            new MiddleClickFriend(),
            new PacketFly(),
            new Freecam(),
            new Annoy()
        );

        // QoL
        register(
            new BetterChat(),
            new InventoryTweaks(),
            new BetterTab(),
            new ItemPhysics(),
            new ClearChat(),
            new Zoom(),
            new SchematicHelper(),
            new CoordLogger(),
            new DeathCoords(),
            new TimerModule(),
            new PingSpoof(),
            new AntiAFK()
        );

        // Tweakeroo
        register(
            new FlexibleBlockPlacement(),
            new FastBlockPlacement(),
            new AccurateBlockPlacement(),
            new HandRestock(),
            new EasyPlaceMode(),
            new PickBlockFirst(),
            new SlimeChunkOverlay(),
            new LightLevelOverlay(),
            new MobSpawnOverlay(),
            new SchematicPrinter(),
            new ItemScroller(),
            new StackRefill(),
            new ToolSwap(),
            new ElytraSwapEnhanced(),
            new AutoSneak(),
            new PlacementGhost(),
            new DisableRecipeBook(),
            new LargerInventory(),
            new HotbarScroll(),
            new InventoryPreview(),
            new BetterPickBlock(),
            new AutoClutch(),
            new PistonPushLimit(),
            new RedstoneHelper()
        );
    }

    public List<Module> getModules() {
        return Collections.unmodifiableList(modules);
    }

    public List<Module> getByCategory(Category category) {
        return modules.stream()
            .filter(m -> m.getCategory() == category)
            .collect(Collectors.toList());
    }

    public Optional<Module> get(String name) {
        return Optional.ofNullable(byName.get(name.toLowerCase()));
    }

    public List<Module> getEnabled() {
        return modules.stream()
            .filter(Module::isEnabled)
            .collect(Collectors.toList());
    }
}
