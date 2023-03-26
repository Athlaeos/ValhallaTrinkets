package me.athlaeos.valhallatrinkets.listener;

import me.athlaeos.valhallatrinkets.*;
import me.athlaeos.valhallatrinkets.config.ConfigManager;
import me.athlaeos.valhallatrinkets.menus.PlayerMenuUtilManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

public class TrinketsListener implements Listener {
    private final boolean dropTrinketsOnDeath;

    public TrinketsListener(){
        dropTrinketsOnDeath = ConfigManager.getInstance().getConfig("config.yml").get().getBoolean("drop_trinkets_on_death", true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent e){
        if (!e.getKeepInventory() && dropTrinketsOnDeath){
            e.getDrops().addAll(TrinketsManager.getInstance().getTrinketInventory(e.getEntity()).values());
            TrinketsManager.getInstance().setTrinketInventory(e.getEntity(), new HashMap<>());
        }
    }

    @EventHandler
    public void onTrinketClick(PlayerInteractEvent e){
        if ((e.useItemInHand() == Event.Result.ALLOW || e.useItemInHand() == Event.Result.DEFAULT) && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.getHand() == EquipmentSlot.HAND) {
            ItemStack inHandItem = e.getItem();
            if (!Utils.isItemEmptyOrNull(inHandItem)){
                TrinketType type = TrinketsManager.getInstance().getTrinketType(inHandItem);
                if (type != null){
                    if (TrinketsManager.getInstance().getValidSlots().isEmpty()) {
                        return;
                    }
                    if (!e.getPlayer().hasPermission("trinkets.allowtrinkets")) {
                        return;
                    }
                    Map<Integer, ItemStack> trinketInventory = TrinketsManager.getInstance().getTrinketInventory(e.getPlayer());
                    for (int i : type.getValidSlots()){
                        ItemStack existingItem = trinketInventory.get(i);
                        if (Utils.isItemEmptyOrNull(existingItem) || TrinketsManager.getInstance().getTrinketType(existingItem) == null){
                            e.setCancelled(true);
                            trinketInventory.put(i, inHandItem);
                            TrinketsManager.getInstance().addTrinket(e.getPlayer(), inHandItem, i);
                            e.getPlayer().getInventory().setItemInMainHand(null);
                            break;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent e){
        if (!e.isCancelled() && !ValhallaTrinkets.IllHandleTrinketMenu()){
            if (e.getView().getBottomInventory() instanceof PlayerInventory && e.getView().getTopInventory() instanceof CraftingInventory){
                if (TrinketsManager.getInstance().getValidSlots().isEmpty()) return;
                if (!e.getWhoClicked().hasPermission("trinkets.allowtrinkets")) return;
                if (!Utils.isItemEmptyOrNull(e.getCursor())) return;
                if (((CraftingInventory) e.getView().getTopInventory()).getMatrix().length == 4) {
                    if (e.getSlot() < 0) {
                        if (e.getWhoClicked() instanceof Player){
                            new TrinketMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) e.getWhoClicked())).open();
                        }
                    }
                }
            }
        }
    }
}
