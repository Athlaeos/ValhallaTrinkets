package me.athlaeos.valhallatrinkets;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TrinketItem {
    private final ItemStack item;
    private final ItemMeta meta;
    private final TrinketSlot slot;
    private final TrinketType type;

    private final boolean unique;
    private final Integer id;

    /**
     * Readonly DTO to cache trinket properties. Any edits made to the item or meta will not be reflected to the trinket itself.
     */
    public TrinketItem(ItemStack i, TrinketSlot slot){
        this.item = i.clone();
        this.slot = slot;
        this.meta = Utils.isEmpty(i) ? null : i.getItemMeta();

        this.type = this.meta != null ? TrinketsManager.getTrinketType(this.meta) : null;
        this.unique = this.meta != null && TrinketProperties.isUnique(this.meta);
        this.id = this.meta != null ? TrinketProperties.getTrinketID(this.meta) : null;
    }

    /**
     * Returns a clone of the trinket
     */
    public ItemStack getItem() { return item.clone(); }
    public ItemMeta getMeta() { return meta; }
    public Integer getID() { return id; }
    public boolean isUnique() { return unique; }
    public TrinketSlot getSlot() { return slot; }
    public TrinketType getType() { return type; }
}
