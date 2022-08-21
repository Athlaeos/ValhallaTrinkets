package me.athlaeos.valhallatrinkets;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

public class TrinketType {

    private final int id;
    private final String displayName;
    private final Collection<Integer> validSlots;
    private final ItemStack placeholderItem;

    public TrinketType(int id, String displayName, Collection<Integer> validSlots, Material type, int data, String itemDisplayName, List<String> lore){
        this.id = id;
        this.displayName = displayName;
        this.validSlots = validSlots;

        placeholderItem = Utils.createSimpleItem(type, data, itemDisplayName, lore);
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Collection<Integer> getValidSlots() {
        return validSlots;
    }

    public ItemStack getPlaceholderItem() {
        return placeholderItem;
    }
}
