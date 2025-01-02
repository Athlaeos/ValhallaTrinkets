package me.athlaeos.valhallatrinkets.listener;

import me.athlaeos.valhallatrinkets.Utils;
import me.athlaeos.valhallatrinkets.menus.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuListener implements Listener {
    private static final Map<UUID, Menu> activeMenus = new HashMap<>();

    public static void setActiveMenu(Player p, Menu menu){
        activeMenus.put(p.getUniqueId(), menu);
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent e){
        Menu activeMenu = activeMenus.get(e.getWhoClicked().getUniqueId());
        if (activeMenu != null && e.getInventory().equals(activeMenu.getInventory())){
            activeMenu.handleMenu(e);
        }
    }

    @EventHandler
    public void onMenuDrag(InventoryDragEvent e){
        Menu activeMenu = activeMenus.get(e.getWhoClicked().getUniqueId());
        if (activeMenu != null && e.getInventory().equals(activeMenu.getInventory())){
            if (!Utils.isEmpty(e.getCursor())){
                activeMenu.handleMenu(e);
            }
        }
    }
}