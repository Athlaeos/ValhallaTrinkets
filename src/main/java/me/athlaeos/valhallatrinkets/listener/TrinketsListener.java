package me.athlaeos.valhallatrinkets.listener;

import me.athlaeos.valhallatrinkets.*;
import me.athlaeos.valhallatrinkets.config.ConfigManager;
import me.athlaeos.valhallatrinkets.hooks.WorldGuardHook;
import me.athlaeos.valhallatrinkets.hooks.WorldGuardWrapper;
import me.athlaeos.valhallatrinkets.menus.PlayerMenuUtilManager;
import me.athlaeos.valhallatrinkets.menus.TrinketMenu;
import org.bukkit.GameRule;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TrinketsListener implements Listener {
    private final boolean dropTrinketsOnDeath;

    public TrinketsListener(){
        dropTrinketsOnDeath = ConfigManager.getInstance().getConfig("config.yml").get().getBoolean("drop_trinkets_on_death", true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDeath(PlayerDeathEvent e){
        Boolean keepInventory = e.getEntity().getWorld().getGameRuleValue(GameRule.KEEP_INVENTORY);
        if (!e.getKeepInventory() && dropTrinketsOnDeath && !e.getEntity().hasPermission("trinkets.keepinventory") &&
                !WorldGuardHook.inDisabledRegion(e.getEntity().getLocation(), e.getEntity(), WorldGuardHook.TRINKETS_KEEPINVENTORY)){
            e.getDrops().addAll(TrinketsManager.getTrinketInventory(e.getEntity()).values().stream().filter(
                    itemStack -> {
                        if (keepInventory != null && keepInventory) return true; // if keepinventory is enabled, trinkets with curse of vanishing are saved anyway
                        ItemMeta meta = itemStack.getMeta();
                        if (meta != null) return !meta.hasEnchant(Enchantment.VANISHING_CURSE);
                        return true;
                    }).map(TrinketItem::getItem).collect(Collectors.toSet()));
            TrinketsManager.setTrinketInventory(e.getEntity(), new HashMap<>());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent e){
        if (e.getEntityType() == EntityType.PLAYER) return;
        TrinketCache.reset(e.getEntity());
    }

    @EventHandler
    public void onTrinketClick(PlayerInteractEvent e){
        if ((e.useItemInHand() == Event.Result.ALLOW || e.useItemInHand() == Event.Result.DEFAULT) &&
                (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.getHand() == EquipmentSlot.HAND) {
            ItemStack inHandItem = e.getItem();
            if (Utils.isEmpty(inHandItem) || !inHandItem.hasItemMeta()) return;
            ItemMeta meta = inHandItem.getItemMeta();
            if (meta == null) return;
            if (TrinketsManager.getTrinketSlots().isEmpty() || !e.getPlayer().hasPermission("trinkets.allowtrinkets")) return;

            TrinketItem item = new TrinketItem(inHandItem, null);

            Map<Integer, TrinketItem> trinketInventory = TrinketCache.getOrCache(e.getPlayer());
            TrinketSlot firstAvailable = TrinketsManager.getFirstAvailableTrinketSlot(e.getPlayer(), trinketInventory, item);
            if (firstAvailable == null) return;
            ItemStack trinketToPut = inHandItem.clone();
            trinketToPut.setAmount(1);
            e.setCancelled(true);
            if (!TrinketsManager.addTrinket(e.getPlayer(), trinketToPut, firstAvailable.getSlot())) return;

            if (inHandItem.getAmount() <= 1) e.getPlayer().getInventory().setItemInMainHand(null);
            else inHandItem.setAmount(inHandItem.getAmount() - 1);
        }
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent e){
        System.out.println("clicked slot: " + e.getSlot());
        if (e.isCancelled() || ValhallaTrinkets.IllHandleTrinketMenu() || TrinketsManager.getTrinketSlots().isEmpty() ||
                e.getSlot() >= 0 || !(e.getWhoClicked() instanceof Player) || !Utils.isEmpty(e.getCursor())) return;
        if (e.getView().getBottomInventory() instanceof PlayerInventory && e.getView().getTopInventory() instanceof CraftingInventory){
            if (!e.getWhoClicked().hasPermission("trinkets.allowtrinkets")) return;
            if (((CraftingInventory) e.getView().getTopInventory()).getMatrix().length != 4) return;
            new TrinketMenu(PlayerMenuUtilManager.getPlayerMenuUtility((Player) e.getWhoClicked())).open();
        }
    }
}
