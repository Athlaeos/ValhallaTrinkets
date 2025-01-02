package me.athlaeos.valhallatrinkets;

import me.athlaeos.valhallammo.playerstats.AccumulativeStatManager;
import me.athlaeos.valhallammo.playerstats.EntityCache;
import me.athlaeos.valhallatrinkets.config.ConfigManager;
import me.athlaeos.valhallatrinkets.hooks.ValhallaHook;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class TrinketsManager {
    private static final NamespacedKey ID_KEY = new NamespacedKey(ValhallaTrinkets.getPlugin(), "trinket_id");
    private static final NamespacedKey INVENTORY_KEY = new NamespacedKey(ValhallaTrinkets.getPlugin(), "trinket_inventory");
    private static final NamespacedKey UNSTACKABLE_KEY = new NamespacedKey(ValhallaTrinkets.getPlugin(), "trinket_unstackable");

    private static final Map<Integer, TrinketSlot> trinketSlots = new HashMap<>(); // key corresponds to placement in menu
    private static final Map<Integer, TrinketType> trinketTypes = new HashMap<>();
    private static final ItemStack filler;

    static {
        YamlConfiguration config = ConfigManager.getInstance().getConfig("config.yml").get();
        String type = config.getString("filler_item.type");
        ItemStack fillerItem = null;
        try {
            Material material = Material.valueOf(type);
            int data = config.getInt("filler_item.data", -1);
            String displayName = config.getString("filler_item.display_name");
            List<String> lore = config.getStringList("filler_item.lore");
            fillerItem = Utils.createSimpleItem(material, data, displayName, lore);
        } catch (IllegalArgumentException ignored){
            ValhallaTrinkets.getPlugin().getServer().getLogger().warning("filler item type " + type + " is not valid");
        }

        filler = fillerItem;
    }

    /**
     * Fetches the trinket's trinket type
     * @param meta the item's meta
     * @return the item's trinket type, or null if it has none
     */
    public static TrinketType getTrinketType(ItemMeta meta){
        return trinketTypes.get(meta.getPersistentDataContainer().getOrDefault(ID_KEY, PersistentDataType.INTEGER, -1));
    }

    /**
     * Fetches the entity's trinket inventory, returns it as a map where the key represents the inventory slot the trinket is present in
     * and the value representing the actual trinket.
     * @param p the entity to get their trinket inventory from
     * @return the trinket inventory
     */
    public static Map<Integer, TrinketItem> getTrinketInventory(LivingEntity p){
        Map<Integer, TrinketItem> inventory = new HashMap<>();
        if (p.getPersistentDataContainer().has(INVENTORY_KEY, PersistentDataType.STRING)){
            String value = p.getPersistentDataContainer().get(INVENTORY_KEY, PersistentDataType.STRING);
            if (value == null) return inventory;
            String[] items = value.split("<itemsplitter>");
            for (String itemSlot : items){
                String[] slot = itemSlot.split("<slotsplitter>");
                if (slot.length == 2){
                    try {
                        int s = Integer.parseInt(slot[0]);
                        String item = slot[1];
                        ItemStack i = Utils.deserializeItemStack(item);
                        inventory.put(s, new TrinketItem(i, trinketSlots.get(s)));
                    } catch (IllegalArgumentException ignored){
                        ValhallaTrinkets.getPlugin().getServer().getLogger().severe("Could not fetch one of the items in " + p.getName() + "'s Trinket Inventory!");
                    }
                }
            }
        }
        return inventory;
    }

    /**
     * Returns the first available trinket slot from the given inventory for the given trinket type, if any. Also takes
     * into consideration any permissions that might be required for that slot to be used.
     * @param p the owner of the given trinket inventory
     * @param trinketInventory the entity's trinket inventory
     * @param item the trinket of which to check the first slot in which it fits
     * @return the trinket slot the given trinket can fit in, or null if none are available
     */
    public static TrinketSlot getFirstAvailableTrinketSlot(LivingEntity p, Map<Integer, TrinketItem> trinketInventory, TrinketItem item){
        for (Integer s : trinketSlots.keySet()){
            TrinketSlot slot = trinketSlots.get(s);
            if (canFitTrinketSlot(p, trinketInventory, item, s)) return slot;
        }
        return null;
    }

    /**
     * Checks if the given trinket type fits into the given slot in the given trinket inventory belonging to the given entity.
     * @param p the owner of the trinket inventory
     * @param trinketInventory the trinket inventory
     * @param item the type of trinket to check if it fits the slot
     * @param slot the slot in which to try and see if the trinket fits in it
     * @return true if it fits, false if not
     */
    public static boolean canFitTrinketSlot(LivingEntity p, Map<Integer, TrinketItem> trinketInventory, TrinketItem item, int slot){
        if (item.getType() == null) return false;
        if (!p.hasPermission("trinkets.allowtrinkets")) return false;
        if (item.getID() != null){
            // if the item has an ID and any of the trinket items match in ID where either are marked unique, it cannot fit
            for (TrinketItem i : trinketInventory.values()){
                if (i.getID() == null) continue;
                if ((i.isUnique() || item.isUnique()) && i.getID().intValue() == item.getID().intValue()) return false;
            }
        }
        TrinketSlot s = trinketSlots.get(slot);
        if (s == null) return false;
        if (s.getPermissionRequired() != null && !p.hasPermission(s.getPermissionRequired())) return false;
        if (!s.getValidTypes().contains(item.getType().getID())) return false;

        return trinketInventory.get(slot) == null;
    }

    /**
     * Sets the entity's trinket inventory, and resets the trinket cache.
     * @param p the entity to set their trinket inventory to
     * @param inventory the trinket inventory to set to the entity
     */
    public static void setTrinketInventory(LivingEntity p, Map<Integer, TrinketItem> inventory){
        if (inventory == null || inventory.isEmpty()){
            p.getPersistentDataContainer().remove(INVENTORY_KEY);
        } else {
            Collection<String> stringElements = new HashSet<>();
            for (Integer slot : inventory.keySet()){
                TrinketItem item = inventory.get(slot);
                String element = slot + "<slotsplitter>" + Utils.serializeItemStack(item.getItem());
                stringElements.add(element);
            }
            p.getPersistentDataContainer().set(INVENTORY_KEY, PersistentDataType.STRING, String.join("<itemsplitter>", stringElements));
        }
        TrinketCache.reset(p);
        ValhallaTrinkets.getPlugin().getServer().getScheduler().runTaskLater(ValhallaTrinkets.getPlugin(), () -> {
            if (ValhallaTrinkets.isHooked(ValhallaHook.class)) {
                EntityCache.resetEquipment(p);
                AccumulativeStatManager.updateStats(p);
            }
        }, 2L);
    }

    /**
     * Adds an item to a entity's trinket inventory regardless of any occupied slots
     * If the slot is already occupied, it will be overwritten and thus destroyed
     * @param p the entity to add a trinket to
     * @param i the trinket to add
     * @param slot the slot to add the trinket to
     */
    public static void addTrinketUnsafe(LivingEntity p, ItemStack i, int slot){
        Map<Integer, TrinketItem> currentInventory = getTrinketInventory(p);
        currentInventory.put(slot, new TrinketItem(i, trinketSlots.get(slot)));
        setTrinketInventory(p, currentInventory);
    }

    /**
     * Adds an item to a entity's trinket inventory
     * @param p the entity to add a trinket to
     * @param i the trinket to add
     * @param slot the slot to add the trinket to
     * @return true if the trinket was added, false if the trinket slot was already occupied
     */
    public static boolean addTrinket(LivingEntity p, ItemStack i, int slot){
        Map<Integer, TrinketItem> currentInventory = getTrinketInventory(p);
        if (currentInventory.containsKey(slot)) return false;
        currentInventory.put(slot, new TrinketItem(i, trinketSlots.get(slot)));
        setTrinketInventory(p, currentInventory);
        return true;
    }

    /**
     * Removes an item from a entity's trinket inventory
     * @param p the entity to remove a trinket from
     * @param slot the trinket slot to remove
     * @return true if a trinket was removed, false if the slot was empty
     */
    public static boolean removeTrinket(LivingEntity p, int slot){
        Map<Integer, TrinketItem> currentInventory = getTrinketInventory(p);
        if (!currentInventory.containsKey(slot)) return false;
        currentInventory.remove(slot);
        setTrinketInventory(p, currentInventory);
        return true;
    }

    /**
     * Sets the trinket type of the item
     * @param meta the item's meta
     * @param type the trinket type
     */
    public static void setType(ItemMeta meta, TrinketType type){
        if (type == null) meta.getPersistentDataContainer().remove(ID_KEY);
        else meta.getPersistentDataContainer().set(ID_KEY, PersistentDataType.INTEGER, type.getID());
        setTrinketTypeLore(meta);
    }

    /**
     * Applies a random UUID NBT tag to the item, effectively rendering it unstackable.
     * @param meta the item's meta
     * @param unstackable true if the item should be unstackable, false if it should become stackable again
     */
    public static void setUnstackable(ItemMeta meta, boolean unstackable){
        if (meta == null) return;
        if (!unstackable) meta.getPersistentDataContainer().remove(UNSTACKABLE_KEY);
        else meta.getPersistentDataContainer().set(UNSTACKABLE_KEY, PersistentDataType.STRING, UUID.randomUUID().toString());
    }

    public static void loadTrinketTypes(){
        YamlConfiguration config = ConfigManager.getInstance().getConfig("config.yml").get();
        ConfigurationSection trinketsSection = config.getConfigurationSection("trinket_types");
        if (trinketsSection != null){
            for (String stringId : trinketsSection.getKeys(false)){
                try {
                    int id = Integer.parseInt(stringId);
                    String name = config.getString("trinket_types." + stringId);
                    trinketTypes.put(id, new TrinketType(id, name));
                } catch (IllegalArgumentException ignored){
                    ValhallaTrinkets.getPlugin().getServer().getLogger().warning("invalid ID " + stringId + " given in trinket_types");
                }
            }
        }
        ConfigurationSection slotSection = config.getConfigurationSection("trinket_slots");
        if (slotSection == null) return;
        for (String stringSlot : slotSection.getKeys(false)){
            int slot;
            try {
                slot = Integer.parseInt(stringSlot);
                if (slot < 0) throw new IllegalArgumentException();
            } catch (IllegalArgumentException ignored){
                ValhallaTrinkets.getPlugin().getServer().getLogger().warning("Trinket slot " + stringSlot + " is not valid");
                continue;
            }
            Collection<Integer> validTrinketTypes = new HashSet<>(config.getIntegerList("trinket_slots." + stringSlot + ".valid_trinkets"));
            ItemStack unlockedIcon;
            try {
                Material type = Material.valueOf(config.getString("trinket_slots." + stringSlot + ".placeholder.type"));
                int data = config.getInt("trinket_slots." + stringSlot + ".placeholder.data", -1);
                String displayName = config.getString("trinket_slots." + stringSlot + ".placeholder.display_name");
                List<String> lore = config.getStringList("trinket_slots." + stringSlot + ".placeholder.lore");
                unlockedIcon = Utils.createSimpleItem(type, data, displayName, lore);
            } catch (IllegalArgumentException ignored){
                ValhallaTrinkets.getPlugin().getServer().getLogger().warning("Placeholder item type is not valid");
                continue;
            }
            String permission = config.getString("trinket_slots." + stringSlot + ".permission");
            ItemStack lockedIcon = null;
            if (permission != null){
                try {
                    Material type = Material.valueOf(config.getString("trinket_slots." + stringSlot + ".locked.type"));
                    int data = config.getInt("trinket_slots." + stringSlot + ".locked.data", -1);
                    String displayName = config.getString("trinket_slots." + stringSlot + ".locked.display_name");
                    List<String> lore = config.getStringList("trinket_slots." + stringSlot + ".locked.lore");
                    lockedIcon = Utils.createSimpleItem(type, data, displayName, lore);
                } catch (IllegalArgumentException ignored){
                    ValhallaTrinkets.getPlugin().getServer().getLogger().warning("Locked item type is not valid");
                    continue;
                }
            }
            TrinketSlot trinketSlot = lockedIcon != null ?
                    new TrinketSlot(slot, unlockedIcon, validTrinketTypes, permission, lockedIcon) :
                    new TrinketSlot(slot, unlockedIcon, validTrinketTypes);
            trinketSlots.put(trinketSlot.getSlot(), trinketSlot);
        }
    }

    public static void setTrinketTypeLore(ItemMeta meta){
        List<String> lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();
        List<String> finalLore = new ArrayList<>();

        loreLoop:
        for (String l : lore){
            for (TrinketType type : trinketTypes.values()){
                if (l.contains(ChatColor.stripColor(Utils.chat(type.getLoreTag())))) continue loreLoop;
            }
            finalLore.add(l);
        }
        TrinketType type = getTrinketType(meta);
        if (type != null) finalLore.add(Utils.chat(type.getLoreTag()));

        meta.setLore(finalLore);
    }

    public static Map<Integer, TrinketType> getTrinketTypes() {
        return trinketTypes;
    }

    public static ItemStack getFillerItem() {
        return filler;
    }

    public static Map<Integer, TrinketSlot> getTrinketSlots() {
        return trinketSlots;
    }
}
