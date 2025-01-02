package me.athlaeos.valhallatrinkets.menus;

import me.athlaeos.valhallatrinkets.listener.MenuListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class Menu implements InventoryHolder {
    protected Inventory inventory;
    protected PlayerMenuUtility playerMenuUtility;

    public Menu(PlayerMenuUtility playerMenuUtility){
        this.playerMenuUtility = playerMenuUtility;
    }

    public abstract String getMenuName();

    public abstract int getSlots();

    public abstract void handleMenu(InventoryClickEvent e);

    public abstract void handleMenu(InventoryDragEvent e);

    public abstract void setMenuItems();

    public void open(){
        inventory = Bukkit.createInventory(this, getSlots(), getMenuName());

        this.setMenuItems();
        MenuListener.setActiveMenu(playerMenuUtility.getOwner(), this);

        playerMenuUtility.getOwner().openInventory(inventory);
    }

    public void open(Player viewer){
        inventory = Bukkit.createInventory(this, getSlots(), getMenuName());

        this.setMenuItems();
        MenuListener.setActiveMenu(viewer, this);

        viewer.openInventory(inventory);
    }

    @Override
    public Inventory getInventory(){
        return inventory;
    }
}

//Credit for menu and manager go to Kody Simpson
