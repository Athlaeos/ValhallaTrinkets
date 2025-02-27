package me.athlaeos.valhallatrinkets.menus;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallatrinkets.*;
import me.athlaeos.valhallatrinkets.config.ConfigManager;
import me.athlaeos.valhallatrinkets.hooks.ValhallaHook;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class TrinketMenu extends Menu {
    private static final String title = ConfigManager.getInstance().getConfig("config.yml").get().getString("trinket_title", "");
    private static final int size = ConfigManager.getInstance().getConfig("config.yml").get().getInt("trinket_menu_size", 54);

    public TrinketMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return switch ((size - 9) / 9) {
            case 5 -> (ValhallaTrinkets.isHooked(ValhallaHook.class) ? (Utils.chat(ValhallaMMO.isResourcePackConfigForced() ? "&f\uF808\uF546" : title)) : title);
            case 4 -> (ValhallaTrinkets.isHooked(ValhallaHook.class) ? (Utils.chat(ValhallaMMO.isResourcePackConfigForced() ? "&f\uF808\uF545" : title)) : title);
            case 3 -> (ValhallaTrinkets.isHooked(ValhallaHook.class) ? (Utils.chat(ValhallaMMO.isResourcePackConfigForced() ? "&f\uF808\uF544" : title)) : title);
            case 2 -> (ValhallaTrinkets.isHooked(ValhallaHook.class) ? (Utils.chat(ValhallaMMO.isResourcePackConfigForced() ? "&f\uF808\uF543" : title)) : title);
            case 1 -> (ValhallaTrinkets.isHooked(ValhallaHook.class) ? (Utils.chat(ValhallaMMO.isResourcePackConfigForced() ? "&f\uF808\uF542" : title)) : title);
            default -> (ValhallaTrinkets.isHooked(ValhallaHook.class) ? (Utils.chat(ValhallaMMO.isResourcePackConfigForced() ? "&f\uF808\uF541" : title)) : title);
        };
    }

    @Override
    public int getSlots() {
        return size;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        e.setCancelled(true);
        if (e.getClick() == ClickType.DOUBLE_CLICK) return;
        Inventory clickedInventory = e.getClickedInventory();
        if (clickedInventory == null || !(e.getWhoClicked() instanceof Player who)) return;

        if (clickedInventory instanceof PlayerInventory && (e.getClick() == ClickType.LEFT || e.getClick() == ClickType.RIGHT)) e.setCancelled(false);

        Map<Integer, TrinketItem> trinketInventory = TrinketCache.getOrCache(who);
        ItemStack clicked = e.getCurrentItem();
        ItemStack cursor = e.getCursor();
        ItemMeta clickedMeta = ItemUtils.isEmpty(clicked) ? null : clicked.getItemMeta();
        ItemMeta cursorMeta = ItemUtils.isEmpty(cursor) ? null : cursor.getItemMeta();
        if ((e.isLeftClick() || e.isRightClick()) && !e.isShiftClick()){
            if (TrinketsManager.getTrinketSlots().containsKey(e.getSlot()) && clickedInventory.getHolder() instanceof TrinketMenu){
                // clicked a valid item slot
                boolean emptyTrinketSlot = clickedMeta == null || TrinketsManager.getTrinketType(clickedMeta) == null;
                if (cursorMeta != null && !emptyTrinketSlot){
                    // neither items are null
                    if (clickedMeta.hasEnchant(Enchantment.BINDING_CURSE)){
                        e.setCancelled(true);
                        return;
                    }
                    TrinketItem item = new TrinketItem(cursor, null);
                    if (item.getType() == null){
                        e.setCancelled(true);
                        return;
                    }
                    boolean canFit = TrinketsManager.canFitTrinketSlot(who, trinketInventory, item, e.getSlot());
                    if (!canFit){
                        e.setCancelled(true);
                        return;
                    }
                    e.setCancelled(true);
                    Utils.calculateClickEvent(e, 1, e.getSlot());
                    ValhallaTrinkets.getPlugin().getServer().getScheduler().runTaskLater(ValhallaTrinkets.getPlugin(), () -> {
                        TrinketsManager.addTrinketUnsafe(playerMenuUtility.getOwner(), inventory.getItem(e.getSlot()), e.getSlot());
                        setMenuItems();
                    }, 1L);
                } else if (cursorMeta == null && !emptyTrinketSlot){
                    // clicking trinket with empty cursor
                    if (clickedMeta.hasEnchant(Enchantment.BINDING_CURSE)){
                        e.setCancelled(true);
                        return;
                    }

                    // clicking on trinket with empty cursor
                    TrinketsManager.removeTrinket(playerMenuUtility.getOwner(), e.getSlot());
                    e.setCancelled(false);
                    ValhallaTrinkets.getPlugin().getServer().getScheduler().runTaskLater(ValhallaTrinkets.getPlugin(), this::setMenuItems, 1L);
                    return;
                    //InventoryUtils.calculateClickedSlot(e);
                } else if (cursorMeta != null){
                    // clicking on empty trinket slot
                    TrinketItem item = new TrinketItem(cursor, null);
                    boolean canFit = TrinketsManager.canFitTrinketSlot(who, trinketInventory, item, e.getSlot());
                    if (!canFit){
                        e.setCancelled(true);
                        return;
                    }
                    e.setCancelled(true);
                    inventory.setItem(e.getSlot(), null);
                    Utils.calculateClickEvent(e, 1, e.getSlot());
                    if (!ItemUtils.isEmpty(inventory.getItem(e.getSlot()))) TrinketsManager.addTrinket(playerMenuUtility.getOwner(), inventory.getItem(e.getSlot()), e.getSlot());
                    setMenuItems();
                }
            }
        } else if ((e.isLeftClick() || e.isRightClick()) && e.isShiftClick()){
            if (clickedInventory instanceof PlayerInventory && clickedMeta != null){
                TrinketType type = TrinketsManager.getTrinketType(clickedMeta);
                if (type != null){
                    TrinketItem item = new TrinketItem(clicked, null);

                    TrinketSlot firstEmpty = TrinketsManager.getFirstAvailableTrinketSlot(who, trinketInventory, item);
                    if (firstEmpty != null){
                        ItemStack trinketToPut = clicked.clone();
                        trinketToPut.setAmount(1);
                        e.setCancelled(true);
                        e.getView().getTopInventory().setItem(firstEmpty.getSlot(), trinketToPut);
                        TrinketsManager.addTrinket(playerMenuUtility.getOwner(), trinketToPut, firstEmpty.getSlot());
                        if (clicked.getAmount() <= 1) e.setCurrentItem(null);
                        else clicked.setAmount(clicked.getAmount() - 1);
                    }
                }
            } else if (clickedInventory.getHolder() instanceof TrinketMenu){
                if (clickedMeta != null && playerMenuUtility.getOwner().getInventory().firstEmpty() >= 0){
                    if (clickedMeta.hasEnchant(Enchantment.BINDING_CURSE)) {
                        e.setCancelled(true);
                        return;
                    }

                    TrinketType type = TrinketsManager.getTrinketType(clickedMeta);
                    if (type != null){
                        e.setCancelled(false);
                        TrinketsManager.removeTrinket(playerMenuUtility.getOwner(), e.getSlot());
                        ValhallaTrinkets.getPlugin().getServer().getScheduler().runTaskLater(ValhallaTrinkets.getPlugin(), this::setMenuItems, 1L);
                        return;
                    }
                }
            }
        } else if (clickedInventory instanceof PlayerInventory && (e.getClick() == ClickType.NUMBER_KEY || e.getClick() == ClickType.DROP ||
                e.getClick() == ClickType.CONTROL_DROP || e.getClick() == ClickType.DOUBLE_CLICK)) e.setCancelled(false);
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
        if (TrinketsManager.getFillerItem() != null){
            for (int i = 0; i < size; i++) inventory.setItem(i, TrinketsManager.getFillerItem());
        }
        for (TrinketSlot type : TrinketsManager.getTrinketSlots().values()){
            if (type.getPermissionRequired() != null && !Utils.isEmpty(type.getLockedIcon()) &&
                    !playerMenuUtility.getOwner().hasPermission(type.getPermissionRequired())) inventory.setItem(type.getSlot(), type.getLockedIcon());
            else if (!Utils.isEmpty(type.getIcon())) inventory.setItem(type.getSlot(), type.getIcon());
        }
        Map<Integer, TrinketItem> inventory = TrinketCache.getOrCache(playerMenuUtility.getOwner());
        for (int trinketSlot : inventory.keySet()){
            this.inventory.setItem(trinketSlot, inventory.get(trinketSlot).getItem());
        }
    }
}
