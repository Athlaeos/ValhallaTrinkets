package me.athlaeos.valhallatrinkets;

import me.athlaeos.valhallatrinkets.config.ConfigManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.stream.Collectors;

public class TrinketsManager {
    private static TrinketsManager manager = null;
    private static final NamespacedKey trinketIDKey = new NamespacedKey(ValhallaTrinkets.getPlugin(), "trinket_id");
    private static final NamespacedKey trinketInventoryKey = new NamespacedKey(ValhallaTrinkets.getPlugin(), "trinket_inventory");
    private static final NamespacedKey unstackableKey = new NamespacedKey(ValhallaTrinkets.getPlugin(), "trinket_unstackable");

    private final Map<Integer, TrinketType> trinketTypes = new HashMap<>();
    private final Collection<Integer> validSlots = new HashSet<>();
    private final ItemStack fillerItem;

    public static TrinketsManager getInstance(){
        if (manager == null) manager = new TrinketsManager();
        return manager;
    }

    public TrinketsManager(){
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

        this.fillerItem = fillerItem;
    }

    public TrinketType getTrinketType(ItemStack i){
        if (i == null) return null;
        ItemMeta meta = i.getItemMeta();
        if (meta == null) return null;
        if (meta.getPersistentDataContainer().has(trinketIDKey, PersistentDataType.INTEGER)) {
            int value = meta.getPersistentDataContainer().get(trinketIDKey, PersistentDataType.INTEGER);
            return trinketTypes.get(value);
        }
        return null;
    }

    public Map<Integer, ItemStack> getTrinketInventory(Player p){
        Map<Integer, ItemStack> inventory = new HashMap<>();
        if (p.getPersistentDataContainer().has(trinketInventoryKey, PersistentDataType.STRING)){
            String value = p.getPersistentDataContainer().get(trinketInventoryKey, PersistentDataType.STRING);
            if (value == null) return inventory;
            String[] items = value.split("<itemsplitter>");
            for (String itemSlot : items){
                String[] slot = itemSlot.split("<slotsplitter>");
                if (slot.length == 2){
                    try {
                        int s = Integer.parseInt(slot[0]);
                        String item = slot[1];
                        ItemStack i = Utils.deserializeItemStack(item);
                        inventory.put(s, i);
                    } catch (IllegalArgumentException ignored){
                        ValhallaTrinkets.getPlugin().getServer().getLogger().severe("Could not fetch one of the items in " + p.getName() + "'s Trinket Inventory!");
                    }
                }
            }
        }
        return inventory;
    }

    public void setTrinketInventory(Player p, Map<Integer, ItemStack> inventory){
        Collection<String> stringElements = new HashSet<>();
        for (Integer slot : inventory.keySet()){
            ItemStack item = inventory.get(slot);
            String element = slot + "<slotsplitter>" + Utils.serializeItemStack(item);
            stringElements.add(element);
        }
        p.getPersistentDataContainer().set(trinketInventoryKey, PersistentDataType.STRING, String.join("<itemsplitter>", stringElements));
    }

    /**
     * Adds an item to a player's trinket inventory regardless of any occupied slots
     * If the slot is already occupied, it will be overwritten and thus destroyed
     * @param p the player to add a trinket to
     * @param i the trinket to add
     * @param slot the slot to add the trinket to
     */
    public void addTrinketUnsafe(Player p, ItemStack i, int slot){
        Map<Integer, ItemStack> currentInventory = getTrinketInventory(p);
        currentInventory.put(slot, i);
        setTrinketInventory(p, currentInventory);
    }

    /**
     * Adds an item to a player's trinket inventory
     * @param p the player to add a trinket to
     * @param i the trinket to add
     * @param slot the slot to add the trinket to
     * @return true if the trinket was added, false if the trinket slot was already occupied
     */
    public boolean addTrinket(Player p, ItemStack i, int slot){
        Map<Integer, ItemStack> currentInventory = getTrinketInventory(p);
        if (currentInventory.containsKey(slot)) return false;
        currentInventory.put(slot, i);
        setTrinketInventory(p, currentInventory);
        return true;
    }

    /**
     * Removes an item from a player's trinket inventory
     * @param p the player to remove a trinket from
     * @param slot the trinket slot to remove
     * @return true if a trinket was removed, false if the slot was empty
     */
    public boolean removeTrinket(Player p, int slot){
        Map<Integer, ItemStack> currentInventory = getTrinketInventory(p);
        if (!currentInventory.containsKey(slot)) return false;
        currentInventory.remove(slot);
        setTrinketInventory(p, currentInventory);
        return true;
    }

    public void setType(ItemStack i, TrinketType type){
        if (i == null) return;
        ItemMeta meta = i.getItemMeta();
        if (meta == null) return;
        if (type == null){
            meta.getPersistentDataContainer().remove(trinketIDKey);
            meta.getPersistentDataContainer().remove(unstackableKey);
        } else {
            meta.getPersistentDataContainer().set(trinketIDKey, PersistentDataType.INTEGER, type.getId());
            meta.getPersistentDataContainer().set(unstackableKey, PersistentDataType.STRING, UUID.randomUUID().toString());
        }
        i.setItemMeta(meta);
        setTrinketTypeLore(i);
    }

    public void randomizeUUID(ItemStack i){
        if (i == null) return;
        ItemMeta meta = i.getItemMeta();
        if (meta == null) return;
        meta.getPersistentDataContainer().set(unstackableKey, PersistentDataType.STRING, UUID.randomUUID().toString());
        i.setItemMeta(meta);
    }

    public void loadTrinketTypes(){
        YamlConfiguration config = ConfigManager.getInstance().getConfig("config.yml").get();
        ConfigurationSection trinketsSection = config.getConfigurationSection("trinket_types");
        if (trinketsSection != null){
            for (String stringId : trinketsSection.getKeys(false)){
                try {
                    int id = Integer.parseInt(stringId);
                    String name = config.getString("trinket_types." + stringId + ".name");
                    Collection<Integer> validSlots = new HashSet<>(config.getIntegerList("trinket_types." + stringId + ".valid_slots"));
                    this.validSlots.addAll(validSlots);

                    String type = config.getString("trinket_types." + stringId + ".placeholder.type");
                    try {
                        Material material = Material.valueOf(type);
                        int data = config.getInt("trinket_types." + stringId + ".placeholder.data", -1);
                        String displayName = config.getString("trinket_types." + stringId + ".placeholder.display_name");
                        List<String> lore = config.getStringList("trinket_types." + stringId + ".placeholder.lore");
                        trinketTypes.put(id, new TrinketType(id, name, validSlots, material, data, displayName, lore));
                    } catch (IllegalArgumentException ignored){
                        ValhallaTrinkets.getPlugin().getServer().getLogger().warning("filler item type " + type + " is not valid");
                        return;
                    }
                } catch (IllegalArgumentException ignored){
                    ValhallaTrinkets.getPlugin().getServer().getLogger().warning("invalid ID " + stringId + " given in trinket_types");
                }
            }
        }
    }

    public void setTrinketTypeLore(ItemStack i){
        if (i == null) return;
        ItemMeta meta = i.getItemMeta();
        if (meta == null) return;
        List<String> lore;
        List<String> finalLore = new ArrayList<>();
        if (meta.hasLore()){
            assert meta.getLore() != null;
            lore = new ArrayList<>(meta.getLore());
        } else {
            lore = new ArrayList<>();
        }

        for (String l : lore){
            if (!trinketTypes.values().stream().map(TrinketType::getDisplayName).map(Utils::chat).collect(Collectors.toSet()).contains(l)){
                finalLore.add(l);
            }
        }
        TrinketType type = getTrinketType(i);
        if (type != null){
            if (finalLore.size() == 0){
                finalLore.add(Utils.chat(type.getDisplayName()));
            }
        }
        meta.setLore(finalLore);
        i.setItemMeta(meta);
    }

    public Map<Integer, TrinketType> getTrinketTypes() {
        return trinketTypes;
    }

    public ItemStack getFillerItem() {
        return fillerItem;
    }

    public Collection<Integer> getValidSlots() {
        return validSlots;
    }
}
