package me.athlaeos.valhallatrinkets;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {
    public static void calculateClickedSlot(InventoryClickEvent event) {
        final ItemStack cursor = event.getCursor();
        if (cursor == null) return;
        final ItemStack clickedItem = event.getCurrentItem();
        if (event.getClick().equals(ClickType.LEFT)) {
            if (!Utils.isItemEmptyOrNull(clickedItem)) {
                event.setCancelled(true);
                if (clickedItem.isSimilar(cursor)) {
                    int possibleAmount = clickedItem.getMaxStackSize() - clickedItem.getAmount();
                    clickedItem.setAmount(clickedItem.getAmount() + (Math.min(cursor.getAmount(), possibleAmount)));
                    cursor.setAmount(cursor.getAmount() - possibleAmount);
                    event.setCurrentItem(clickedItem);
                    event.getCursor().setAmount(0);
                } else {
                    event.getCursor().setAmount(0);
                    event.setCurrentItem(cursor);
                }
            } else if (!event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                event.setCancelled(true);
                event.getCursor().setAmount(0);
                event.setCurrentItem(cursor);
            }
        } else {
            if (!Utils.isItemEmptyOrNull(clickedItem)) {
                if (clickedItem.isSimilar(cursor)) {
                    if (clickedItem.getAmount() < clickedItem.getMaxStackSize() && cursor.getAmount() > 0) {
                        event.setCancelled(true);
                        clickedItem.setAmount(clickedItem.getAmount() + 1);
                        cursor.setAmount(cursor.getAmount() - 1);
                    }
                } else {
                    event.setCancelled(true);
                    event.getCursor().setAmount(0);
                    event.setCurrentItem(cursor);
                }
            } else {
                event.setCancelled(true);
                ItemStack itemStack = cursor.clone();
                cursor.setAmount(cursor.getAmount() - 1);
                itemStack.setAmount(1);
                event.setCurrentItem(itemStack);
            }
        }
        if (event.getWhoClicked() instanceof Player) {
            ((Player) event.getWhoClicked()).updateInventory();
        }
    }
}
