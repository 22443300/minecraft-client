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

    public void init() { registerAll(); }

    public void register(Module... mods) {
        for (Module mod : mods) {
            modules.add(mod);
            byName.put(mod.getName().toLowerCase(), mod);
        }
    }

    public void registerAll() {
        // ── Combat (22) ──────────────────────────────────────────────────────
        register(
            new KillAura(), new CrystalAura(), new AimAssist(), new AnchorAura(),
            new AutoTotem(), new Surround(), new BowAimbot(), new Velocity(),
            new Criticals(), new AutoArmor(), new OffhandSwap(), new MaceCombo(),
            new SpearLunge(), new AutoLog(), new Reach(), new HitboxExpand(),
            new AutoGapple(), new BedAura(), new AutoGap(), new AntiBot(),
            new AutoSword(), new TPAura()
        );

        // ── Movement (23) ────────────────────────────────────────────────────
        register(
            new ElytraFly(), new ElytraSwap(), new NoFall(), new FastBridge(),
            new Scaffold(), new Speed(), new Parkour(), new NoSlow(), new Step(),
            new Glide(), new BoatFly(), new Fly(), new Jesus(), new LongJump(),
            new SafeWalk(), new Blink(), new Sprint(), new AirJump(), new IceSpeed(),
            new Dolphin(), new AntiVoid(), new ParkourAssist(), new Phase()
        );

        // ── Visual (24) ──────────────────────────────────────────────────────
        register(
            new XRay(), new ESP(), new Tracers(), new FullBright(), new NoRender(),
            new CaveMap(), new StorageESP(), new SearchESP(), new Chams(),
            new NameTags(), new Trajectories(), new HoleESP(), new TimeChanger(),
            new WeatherChanger(), new AntiBlind(), new FreeLook(), new NoFog(),
            new EntityList(), new BlockHighlight(), new PlayerESP(), new Ambience(),
            new BreakProgress(), new MobESP(), new ItemESP()
        );

        // ── World (21) ───────────────────────────────────────────────────────
        register(
            new Nuker(), new AutoMine(), new InstaBreak(), new SpeedMine(),
            new Flatten(), new AutoBuild(), new FastPlace(), new NoClip(),
            new WorldEditMod(), new LitematicaMod(), new FAWEMod(),
            new TreeCapitator(), new AutoSmelter(), new Excavator(), new AutoCraft(),
            new HighwayBuilder(), new dev.phantom.client.modules.world.AutoFill(),
            new TimerHack(), new AutoSign(), new StripMine(), new PacketMine()
        );

        // ── Utility (26) ─────────────────────────────────────────────────────
        register(
            new AutoEat(), new AutoFish(), new AutoFarm(), new ChestStealer(),
            new AutoDrop(), new AutoSprint(), new AutoRespawn(), new NoInteract(),
            new ChatFilter(), new AutoReply(), new MiddleClickFriend(), new PacketFly(),
            new Freecam(), new Annoy(), new AutoWalk(), new ChatSpammer(),
            new AutoLogin(), new FakePlayer(), new RotationLock(), new AutoTool(),
            new ItemSucker(), new PortalGUI(), new dev.phantom.client.modules.utility.AutoMount(),
            new BookBot(), new Disconnect(), new AutoPotion()
        );

        // ── QoL (19) ─────────────────────────────────────────────────────────
        register(
            new BetterChat(), new InventoryTweaks(), new BetterTab(), new ItemPhysics(),
            new ClearChat(), new dev.phantom.client.modules.qol.Zoom(),
            new SchematicHelper(), new CoordLogger(), new DeathCoords(),
            new TimerModule(), new PingSpoof(), new AntiAFK(), new FPSBoost(),
            new BetterF3(), new WindowTitle(), new EntityCounter(), new PacketLogger(),
            new NoRotate(), new ServerAlert()
        );

        // ── Tweakeroo (34) ───────────────────────────────────────────────────
        register(
            new FlexibleBlockPlacement(), new FastBlockPlacement(),
            new AccurateBlockPlacement(), new HandRestock(), new EasyPlaceMode(),
            new PickBlockFirst(), new SlimeChunkOverlay(), new LightLevelOverlay(),
            new MobSpawnOverlay(), new SchematicPrinter(), new ItemScroller(),
            new StackRefill(), new ToolSwap(), new ElytraSwapEnhanced(),
            new AutoSneak(), new PlacementGhost(), new DisableRecipeBook(),
            new LargerInventory(), new HotbarScroll(), new InventoryPreview(),
            new BetterPickBlock(), new AutoClutch(), new PistonPushLimit(),
            new RedstoneHelper(), new BuildMode(), new SneakMode(), new WalkMode(),
            new SmartReplant(), new AutoSort(), new OptiPlace(), new PearlSimulator(),
            new GappleLogger(), new ContainerSwap()
        );
    }

    public List<Module> getModules() { return Collections.unmodifiableList(modules); }

    public List<Module> getByCategory(Category category) {
        return modules.stream().filter(m -> m.getCategory() == category).collect(Collectors.toList());
    }

    public Optional<Module> get(String name) {
        return Optional.ofNullable(byName.get(name.toLowerCase()));
    }

    public List<Module> getEnabled() {
        return modules.stream().filter(Module::isEnabled).collect(Collectors.toList());
    }
}
