package dev.phantom.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

import java.util.function.Predicate;

public final class InventoryUtil {

    private InventoryUtil() {}

    // -------------------------------------------------------------------------

    private static MinecraftClient mc() { return MinecraftClient.getInstance(); }

    // -------------------------------------------------------------------------

    /**
     * Finds the first hotbar slot (0-8) containing the given item, or -1.
     */
    public static int findInHotbar(Item item) {
        return findInHotbar(stack -> !stack.isEmpty() && stack.getItem() == item);
    }

    /**
     * Finds the first hotbar slot (0-8) matching the predicate, or -1.
     */
    public static int findInHotbar(Predicate<ItemStack> predicate) {
        MinecraftClient mc = mc();
        if (mc.player == null) return -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (predicate.test(stack)) return i;
        }
        return -1;
    }

    /**
     * Finds the first inventory slot (0-35) containing the given item, or -1.
     */
    public static int findInInventory(Item item) {
        MinecraftClient mc = mc();
        if (mc.player == null) return -1;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() == item) return i;
        }
        return -1;
    }

    /**
     * Selects the given hotbar slot (0-8).
     */
    public static void swapToSlot(int slot) {
        MinecraftClient mc = mc();
        if (mc.player == null) return;
        mc.player.getInventory().selectedSlot = slot;
    }

    /**
     * Moves an item from an inventory slot to a hotbar slot via a swap packet.
     */
    public static void moveToHotbar(int inventorySlot, int hotbarSlot) {
        MinecraftClient mc = mc();
        if (mc.player == null || mc.interactionManager == null) return;
        // SlotActionType.SWAP with button = hotbarSlot swaps the inventory slot with that hotbar slot
        mc.interactionManager.clickSlot(
                mc.player.playerScreenHandler.syncId,
                inventorySlot,
                hotbarSlot,
                SlotActionType.SWAP,
                mc.player
        );
    }

    /**
     * Counts how many of the given item are in the full inventory (slots 0-35).
     */
    public static int countItem(Item item) {
        MinecraftClient mc = mc();
        if (mc.player == null) return 0;
        int count = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() == item) {
                count += stack.getCount();
            }
        }
        return count;
    }

    /**
     * Returns the item in the player's main hand.
     */
    public static ItemStack getMainHand() {
        MinecraftClient mc = mc();
        if (mc.player == null) return ItemStack.EMPTY;
        return mc.player.getMainHandStack();
    }

    /**
     * Returns the item in the player's offhand.
     */
    public static ItemStack getOffHand() {
        MinecraftClient mc = mc();
        if (mc.player == null) return ItemStack.EMPTY;
        return mc.player.getOffHandStack();
    }

    /**
     * Sends a click packet to the server for the given screen slot.
     */
    public static void clickSlot(int syncId, int slot, int button, SlotActionType actionType) {
        MinecraftClient mc = mc();
        if (mc.player == null || mc.interactionManager == null) return;
        mc.interactionManager.clickSlot(syncId, slot, button, actionType, mc.player);
    }

    /**
     * Drops the item at the given inventory slot by sending a throw packet.
     */
    public static void dropSlot(int slot) {
        MinecraftClient mc = mc();
        if (mc.player == null || mc.interactionManager == null) return;
        mc.interactionManager.clickSlot(
                mc.player.playerScreenHandler.syncId,
                slot,
                1,                      // button 1 = drop whole stack
                SlotActionType.THROW,
                mc.player
        );
    }
}
