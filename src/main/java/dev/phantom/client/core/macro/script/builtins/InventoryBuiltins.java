package dev.phantom.client.core.macro.script.builtins;

import dev.phantom.client.core.macro.script.Interpreter;
import dev.phantom.client.util.InventoryUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class InventoryBuiltins {

    private InventoryBuiltins() {}

    public static void registerAll(Interpreter interp) {
        interp.registerBuiltin("swap_to_item", InventoryBuiltins::swapToItem);
        interp.registerBuiltin("swap_to_slot", InventoryBuiltins::swapToSlot);
    }

    // -------------------------------------------------------------------------
    // swap_to_item(itemName)
    // -------------------------------------------------------------------------

    private static Object swapToItem(List<Object> args) throws Exception {
        if (args.isEmpty()) return null;
        String itemName = args.get(0).toString();

        // Resolve the item from its registry name (e.g. "minecraft:diamond_sword" or "diamond_sword")
        String fullName = itemName.contains(":") ? itemName : "minecraft:" + itemName;
        var item = Registries.ITEM.get(Identifier.of(fullName));
        if (item == Items.AIR) {
            dev.phantom.client.PhantomClient.LOGGER.warn("[Phantom Macro] swap_to_item: unknown item '{}'", itemName);
            return null;
        }

        int slot = InventoryUtil.findInHotbar(item);
        if (slot == -1) {
            dev.phantom.client.PhantomClient.LOGGER.warn("[Phantom Macro] swap_to_item: '{}' not found in hotbar", itemName);
            return null;
        }

        final int targetSlot = slot;
        MinecraftClient mc = MinecraftClient.getInstance();
        CompletableFuture<Void> future = new CompletableFuture<>();
        mc.execute(() -> {
            try {
                InventoryUtil.swapToSlot(targetSlot);
                future.complete(null);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            dev.phantom.client.PhantomClient.LOGGER.warn("[Phantom Macro] swap_to_item() failed: {}", e.getMessage());
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // swap_to_slot(slot)  — 0-indexed hotbar slot 0-8
    // -------------------------------------------------------------------------

    private static Object swapToSlot(List<Object> args) throws Exception {
        if (args.isEmpty()) return null;
        int slot = Interpreter.toInt(args.get(0));
        slot = Math.max(0, Math.min(8, slot));

        final int targetSlot = slot;
        MinecraftClient mc = MinecraftClient.getInstance();
        CompletableFuture<Void> future = new CompletableFuture<>();
        mc.execute(() -> {
            try {
                InventoryUtil.swapToSlot(targetSlot);
                future.complete(null);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            dev.phantom.client.PhantomClient.LOGGER.warn("[Phantom Macro] swap_to_slot() failed: {}", e.getMessage());
        }
        return null;
    }
}
