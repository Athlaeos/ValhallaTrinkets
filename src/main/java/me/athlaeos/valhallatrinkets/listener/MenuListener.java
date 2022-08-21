package me.athlaeos.valhallatrinkets.listener;

import me.athlaeos.valhallatrinkets.menus.Menu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.PlayerInventory;

public class MenuListener implements Listener {

    @EventHandler
    public void onMenuClick(InventoryClickEvent e){
        if (e.getView().getTopInventory().getHolder() instanceof Menu && e.getView().getBottomInventory() instanceof PlayerInventory){
            Menu m = (Menu) e.getView().getTopInventory().getHolder();

            if (m == null) return;
            m.handleMenu(e);
        }
    }

    @EventHandler
    public void onMenuDrag(InventoryDragEvent e){
        if (e.getView().getTopInventory().getHolder() instanceof Menu && e.getView().getBottomInventory() instanceof PlayerInventory){
            Menu m = (Menu) e.getView().getTopInventory().getHolder();

            if (m == null) return;
            m.handleMenu(e);
        }
    }
}