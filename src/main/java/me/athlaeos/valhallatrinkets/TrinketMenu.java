package me.athlaeos.valhallatrinkets;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallatrinkets.config.ConfigManager;
import me.athlaeos.valhallatrinkets.menus.Menu;
import me.athlaeos.valhallatrinkets.menus.PlayerMenuUtility;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;

public class TrinketMenu extends Menu {
    private static final String title = ConfigManager.getInstance().getConfig("config.yml").get().getString("trinket_title", "");
    private static final int size = ConfigManager.getInstance().getConfig("config.yml").get().getInt("trinket_menu_size", 54);

    public TrinketMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        switch ((size - 9) / 9){
            case 5: return (ValhallaTrinkets.isValhallaHooked() ? (Utils.chat(ValhallaMMO.isPackEnabled() ? "&f\uF808\uF541" : title)) : title);
            case 4: return (ValhallaTrinkets.isValhallaHooked() ? (Utils.chat(ValhallaMMO.isPackEnabled() ? "&f\uF808\uF542" : title)) : title);
            case 3: return (ValhallaTrinkets.isValhallaHooked() ? (Utils.chat(ValhallaMMO.isPackEnabled() ? "&f\uF808\uF543" : title)) : title);
            case 2: return (ValhallaTrinkets.isValhallaHooked() ? (Utils.chat(ValhallaMMO.isPackEnabled() ? "&f\uF808\uF544" : title)) : title);
            case 1: return (ValhallaTrinkets.isValhallaHooked() ? (Utils.chat(ValhallaMMO.isPackEnabled() ? "&f\uF808\uF545" : title)) : title);
            default: return (ValhallaTrinkets.isValhallaHooked() ? (Utils.chat(ValhallaMMO.isPackEnabled() ? "&f\uF808\uF546" : title)) : title);
        }
    }

    @Override
    public int getSlots() {
        return size;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        e.setCancelled(true);
        Inventory clickedInventory = e.getClickedInventory();
        if (clickedInventory != null){
            if (clickedInventory instanceof PlayerInventory && (e.getClick() == ClickType.LEFT || e.getClick() == ClickType.RIGHT)) {
                e.setCancelled(false);
            }
            if (e.getWhoClicked() instanceof Player){
                Player who = (Player) e.getWhoClicked();
                if ((e.isLeftClick() || e.isRightClick()) && !e.isShiftClick()){
                    if (TrinketsManager.getInstance().getValidSlots().contains(e.getSlot()) && clickedInventory.getHolder() instanceof TrinketMenu){
                        boolean emptyTrinketSlot = Utils.isItemEmptyOrNull(e.getCurrentItem()) || TrinketsManager.getInstance().getTrinketType(e.getCurrentItem()) == null;
                        // clicked a valid item slot
                        if (!Utils.isItemEmptyOrNull(e.getCursor()) && !emptyTrinketSlot){
                            // neither items are null
                            TrinketType cursorType = TrinketsManager.getInstance().getTrinketType(e.getCursor());
                            if (cursorType != null){
                                if (cursorType.getValidSlots().contains(e.getSlot())){
                                    if (e.getCursor().isSimilar(e.getCurrentItem())){
                                        e.setCancelled(true);
                                        InventoryUtils.calculateClickedSlot(e);
                                        ValhallaTrinkets.getPlugin().getServer().getScheduler().runTaskLater(ValhallaTrinkets.getPlugin(), () -> {
                                                TrinketsManager.getInstance().addTrinketUnsafe(who, inventory.getItem(e.getSlot()), e.getSlot());
                                                setMenuItems();
                                                }, 1L);
                                    } else {
                                        e.setCancelled(false);
                                        TrinketsManager.getInstance().addTrinketUnsafe(who, e.getCursor(), e.getSlot());
                                        ValhallaTrinkets.getPlugin().getServer().getScheduler().runTaskLater(ValhallaTrinkets.getPlugin(), this::setMenuItems, 1L);
                                    }
                                    return;
//                                InventoryUtils.calculateClickedSlot(e);
                                }
                            }
                        } else if (Utils.isItemEmptyOrNull(e.getCursor()) && !emptyTrinketSlot){
                            // clicking on trinket with empty cursor
                            TrinketsManager.getInstance().removeTrinket(who, e.getSlot());
                            e.setCancelled(false);
                            ValhallaTrinkets.getPlugin().getServer().getScheduler().runTaskLater(ValhallaTrinkets.getPlugin(), this::setMenuItems, 1L);
                            return;
                            //InventoryUtils.calculateClickedSlot(e);
                        } else if (!Utils.isItemEmptyOrNull(e.getCursor()) && emptyTrinketSlot){
                            // clicking on empty trinket slot
                            TrinketType cursorType = TrinketsManager.getInstance().getTrinketType(e.getCursor());
                            if (cursorType != null){
                                if (cursorType.getValidSlots().contains(e.getSlot())){
                                    inventory.setItem(e.getSlot(), null);
                                    TrinketsManager.getInstance().addTrinket(who, e.getCursor(), e.getSlot());
                                    InventoryUtils.calculateClickedSlot(e);
                                }
                            }
                        }
                    }
                } else if ((e.isLeftClick() || e.isRightClick()) && e.isShiftClick()){
                    if (clickedInventory instanceof PlayerInventory){
                        ItemStack clickedItem = e.getCurrentItem();
                        if (!Utils.isItemEmptyOrNull(clickedItem)){
                            TrinketType type = TrinketsManager.getInstance().getTrinketType(clickedItem);
                            if (type != null){
                                for (int i : type.getValidSlots()){
                                    ItemStack existingItem = e.getView().getItem(i);
                                    if (Utils.isItemEmptyOrNull(existingItem) || TrinketsManager.getInstance().getTrinketType(existingItem) == null){
                                        e.setCancelled(true);
                                        e.getView().setItem(i, clickedItem);
                                        TrinketsManager.getInstance().addTrinket(who, clickedItem, i);
                                        e.setCurrentItem(null);
                                        break;
                                    }
                                }
                            }
                        }
                    } else if (clickedInventory.getHolder() instanceof TrinketMenu){
                        ItemStack clickedItem = e.getCurrentItem();
                        if (!Utils.isItemEmptyOrNull(clickedItem) && playerMenuUtility.getOwner().getInventory().firstEmpty() >= 0){
                            TrinketType type = TrinketsManager.getInstance().getTrinketType(clickedItem);
                            if (type != null){
                                e.setCancelled(false);
                                TrinketsManager.getInstance().removeTrinket(who, e.getSlot());
                                ValhallaTrinkets.getPlugin().getServer().getScheduler().runTaskLater(ValhallaTrinkets.getPlugin(), this::setMenuItems, 1L);
                                return;
                            }
                        }
                    }
                } else if (clickedInventory instanceof PlayerInventory && (e.getClick() == ClickType.NUMBER_KEY || e.getClick() == ClickType.DROP || e.getClick() == ClickType.CONTROL_DROP || e.getClick() == ClickType.DOUBLE_CLICK)){
                    e.setCancelled(false);
                }
            }
        }
        setMenuItems();
    }

    @Override
    public void handleMenu(InventoryDragEvent e) {
        boolean draggedInCustomInventory = false;
        for (int i : e.getRawSlots()){
            Inventory draggedIn = e.getView().getInventory(i);
            if (draggedIn != null){
                if (draggedIn.getHolder() instanceof TrinketMenu) {
                    draggedInCustomInventory = true;
                }
            }
        }

        if (draggedInCustomInventory){
            e.setCancelled(true);
        }
    }

    @Override
    public void setMenuItems() {
        if (TrinketsManager.getInstance().getFillerItem() != null){
            for (int i = 0; i < size; i++){
                inventory.setItem(i, TrinketsManager.getInstance().getFillerItem());
            }
        }
        for (TrinketType type : TrinketsManager.getInstance().getTrinketTypes().values()){
            if (type.getPlaceholderItem() != null){
                for (int slot : type.getValidSlots()){
                    inventory.setItem(slot, type.getPlaceholderItem());
                }
            }
        }
        Map<Integer, ItemStack> inventory = TrinketsManager.getInstance().getTrinketInventory(playerMenuUtility.getOwner());
        for (int trinketSlot : inventory.keySet()){
            this.inventory.setItem(trinketSlot, inventory.get(trinketSlot));
        }
    }
}
