package me.athlaeos.valhallatrinkets;

import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {
    public static void calculateClickedSlot(InventoryClickEvent event) {
        ItemStack cursor = event.getCursor();
        if (!me.athlaeos.valhallammo.utility.Utils.isItemEmptyOrNull(cursor)) {
            ItemStack clickedItem = event.getCurrentItem();
            if (event.getClick().equals(ClickType.LEFT)) {
                if (!me.athlaeos.valhallammo.utility.Utils.isItemEmptyOrNull(clickedItem)) {
                    event.setCancelled(true);
                    if (clickedItem.isSimilar(cursor)) {
                        int possibleAmount = clickedItem.getMaxStackSize() - clickedItem.getAmount();
                        clickedItem.setAmount(clickedItem.getAmount() + Math.min(cursor.getAmount(), possibleAmount));
                        cursor.setAmount(cursor.getAmount() - possibleAmount);
                        event.setCurrentItem(clickedItem);
                        //event.getCursor().setAmount(0);
                    } else {
                        ItemStack cursorClone = cursor.clone();
                        event.getWhoClicked().setItemOnCursor(event.getCurrentItem());
                        event.setCurrentItem(cursorClone);
                        //event.getCursor().setAmount(0);
                    }
                } else if (!event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                    event.setCancelled(true);
                    event.setCurrentItem(cursor.clone());
                    event.getCursor().setAmount(0);
                }
            } else if (!me.athlaeos.valhallammo.utility.Utils.isItemEmptyOrNull(clickedItem)) {
                if (clickedItem.isSimilar(cursor)) {
                    if (clickedItem.getAmount() < clickedItem.getMaxStackSize() && cursor.getAmount() > 0) {
                        event.setCancelled(true);
                        clickedItem.setAmount(clickedItem.getAmount() + 1);
                        cursor.setAmount(cursor.getAmount() - 1);
                    }
                } else {
                    event.setCancelled(true);
                    event.setCurrentItem(cursor.clone());
                    event.getCursor().setAmount(0);
                }
            } else {
                event.setCancelled(true);
                ItemStack itemStack = cursor.clone();
                cursor.setAmount(cursor.getAmount() - 1);
                itemStack.setAmount(1);
                event.setCurrentItem(itemStack);
            }

            if (event.getWhoClicked() instanceof Player) {
                ((Player)event.getWhoClicked()).updateInventory();
            }
        }
    }

    public static void calculateClickedSlotOnlyAllow1Placed(InventoryClickEvent event) {
        ItemStack cursor = event.getCursor();
        if (!me.athlaeos.valhallammo.utility.Utils.isItemEmptyOrNull(cursor)) {
            ItemStack clickedItem = event.getCurrentItem();
            if (event.getClick().equals(ClickType.LEFT)) {
                if (!me.athlaeos.valhallammo.utility.Utils.isItemEmptyOrNull(clickedItem)) {
                    event.setCancelled(true);
                    if (!clickedItem.isSimilar(cursor) && cursor.getAmount() == 1) {
                        // you may only swap if the cursor amount is 1
                        ItemStack cursorClone = cursor.clone();
                        event.getWhoClicked().setItemOnCursor(event.getCurrentItem());
                        event.setCurrentItem(cursorClone);
                    }
                    // else neither cursor or clicked items are empty, and both are similar to eachother, but since only 1
                    // should be transferrable anyway nothing should happen
                } else if (!event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                    event.setCancelled(true);
                    ItemStack cursorclone = cursor.clone();
                    cursorclone.setAmount(1);
                    cursor.setAmount(cursor.getAmount() - 1);
                    event.setCurrentItem(cursorclone);
                }
            } else if (!Utils.isItemEmptyOrNull(clickedItem)) {
                if (!clickedItem.isSimilar(cursor) && cursor.getAmount() == 1) {
                    // you may only swap if the cursor amount is 1
                    event.setCancelled(true);
                    event.setCurrentItem(cursor.clone());
                    event.getCursor().setAmount(0);
                }
                // else neither cursor or clicked items are empty, and both are similar to eachother, but since only 1
                // should be transferrable anyway nothing should happen
            } else {
                event.setCancelled(true);
                ItemStack itemStack = cursor.clone();
                cursor.setAmount(cursor.getAmount() - 1);
                itemStack.setAmount(1);
                event.setCurrentItem(itemStack);
            }

            if (event.getWhoClicked() instanceof Player) {
                ((Player)event.getWhoClicked()).updateInventory();
            }
        }
    }
}
