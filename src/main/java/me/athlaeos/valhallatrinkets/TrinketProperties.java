package me.athlaeos.valhallatrinkets;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class TrinketProperties {
    private static final NamespacedKey KEY_TRINKET_ID = new NamespacedKey(ValhallaTrinkets.getPlugin(), "trinket_unique_id");
    private static final NamespacedKey KEY_UNIQUE = new NamespacedKey(ValhallaTrinkets.getPlugin(), "unique");

    /**
     * Unique trinkets may only be equipped once. For trinkets to be considered identical they must have the same ID.
     * Trinkets that do not have an ID may be equipped several times even if another one that looks the same is marked unique.
     * @param meta the trinket's meta
     * @param unique whether the trinket may only be equipped once or not, true if unique and false otherwise
     */
    public static void setUniqueTrinket(ItemMeta meta, boolean unique){
        if (unique) meta.getPersistentDataContainer().set(KEY_UNIQUE, PersistentDataType.BYTE, (byte) 1);
        else meta.getPersistentDataContainer().remove(KEY_UNIQUE);
    }

    /**
     * If a trinket is unique it means only one of its ID may be equipped. The trinket must have an ID in order for this to work.
     * @param meta the trinket's meta
     * @return true if the trinket is unique, false if multiple with identical IDs may be equipped.
     */
    public static boolean isUnique(ItemMeta meta){
        return meta.getPersistentDataContainer().has(KEY_UNIQUE, PersistentDataType.BYTE);
    }

    /**
     * Sets the trinket's ID. This ID is used for trinket comparison, like checking if two trinkets have the same ID
     * and can both be equipped if they're tagged as "unique"
     * @param meta the trinket's meta
     * @param id the ID the trinket should have, or null if it should be removed.
     */
    public static void setTrinketID(ItemMeta meta, Integer id){
        if (id != null) meta.getPersistentDataContainer().set(KEY_TRINKET_ID, PersistentDataType.INTEGER, id);
        else meta.getPersistentDataContainer().remove(KEY_TRINKET_ID);
    }

    /**
     * Gets the trinket's ID. Trinkets with the same ID will be considered identical
     * @param meta the trinket's meta
     * @return the trinket's ID, or null if it has none.
     */
    public static Integer getTrinketID(ItemMeta meta){
        return meta.getPersistentDataContainer().get(KEY_TRINKET_ID, PersistentDataType.INTEGER);
    }
}
