package me.athlaeos.valhallatrinkets;

import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;

import java.util.Collection;

public class TrinketSlot {
    private final int slot;
    private final ItemStack icon;
    private final Collection<Integer> validTypes;
    private final String permissionRequired;
    private final ItemStack lockedIcon;

    public TrinketSlot(int slot, ItemStack icon, Collection<Integer> validTypes){
        this.slot = slot;
        this.icon = icon;
        this.validTypes = validTypes;
        this.permissionRequired = null;
        this.lockedIcon = null;
    }

    public TrinketSlot(int slot, ItemStack icon, Collection<Integer> validTypes, String permissionRequired, ItemStack lockedIcon){
        this.slot = slot;
        this.icon = icon;
        this.validTypes = validTypes;
        this.permissionRequired = permissionRequired;
        this.lockedIcon = lockedIcon;
        if (permissionRequired != null && ValhallaTrinkets.getPlugin().getServer().getPluginManager().getPermission(permissionRequired) == null){
            ValhallaTrinkets.getPlugin().getServer().getPluginManager().addPermission(new Permission(permissionRequired));
        }
    }

    public ItemStack getIcon() { return icon; }
    public Collection<Integer> getValidTypes() { return validTypes; }
    public int getSlot() { return slot; }
    public ItemStack getLockedIcon() { return lockedIcon; }
    public String getPermissionRequired() { return permissionRequired; }
}
