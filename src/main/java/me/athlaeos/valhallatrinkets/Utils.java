package me.athlaeos.valhallatrinkets;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utils {
    public static boolean isEmpty(ItemStack i){
        return i == null || i.getType().isAir() || i.getAmount() <= 0;
    }

    private static final Collection<ClickType> legalClickTypes = new HashSet<>(Arrays.asList(ClickType.DROP, ClickType.CONTROL_DROP,
            ClickType.MIDDLE, ClickType.WINDOW_BORDER_LEFT, ClickType.WINDOW_BORDER_RIGHT, ClickType.UNKNOWN, ClickType.CREATIVE, ClickType.SHIFT_LEFT, ClickType.SHIFT_RIGHT));
    private static final Collection<ClickType> illegalClickTypes = new HashSet<>(Arrays.asList(ClickType.SWAP_OFFHAND, ClickType.NUMBER_KEY, ClickType.DOUBLE_CLICK));

    public static void calculateClickEvent(InventoryClickEvent e, int maxAmount, Integer... slotsToCover){
        Player p = (Player) e.getWhoClicked();
        ItemStack cursor = p.getItemOnCursor();
        ItemStack clickedItem = e.getCurrentItem();
        if (e.getClickedInventory() == null) return;
        Inventory openInventory = e.getView().getTopInventory();
        e.setCancelled(true);
        if (e.getClickedInventory() instanceof PlayerInventory){
            // player inventory item clicked
            if (e.isShiftClick() && !isEmpty(clickedItem)){
                // shift click, check if slotsToCalculate are available for new items, otherwise do nothing more as there's no slot to transfer to
                for (Integer i : slotsToCover){
                    ItemStack slotItem = openInventory.getItem(i);
                    if (isEmpty(slotItem)) {
                        if (clickedItem.getAmount() <= maxAmount) {
                            openInventory.setItem(i, clickedItem);
                            e.setCurrentItem(null);
                        }
                        else {
                            ItemStack itemToPut = clickedItem.clone();
                            itemToPut.setAmount(maxAmount);
                            if (clickedItem.getAmount() - maxAmount <= 0) e.setCurrentItem(null);
                            else clickedItem.setAmount(clickedItem.getAmount() - maxAmount);
                            openInventory.setItem(i, itemToPut);
                        }
                        return;
                    } else if (slotItem.isSimilar(clickedItem)) {
                        // similar slot item, add as much as possible
                        if (slotItem.getAmount() < maxAmount) {
                            int amountToTransfer = Math.min(clickedItem.getAmount(), maxAmount - slotItem.getAmount());
                            if (clickedItem.getAmount() == amountToTransfer) {
                                e.setCurrentItem(null);
                            } else {
                                if (clickedItem.getAmount() - amountToTransfer <= 0) e.setCurrentItem(null);
                                else clickedItem.setAmount(clickedItem.getAmount() - amountToTransfer);
                            }
                            slotItem.setAmount(slotItem.getAmount() + amountToTransfer);
                            return;
                        }
                    }
                }
                // no available slot found, do nothing more
            } else e.setCancelled(false); // regular inventory click, do nothing special
        } else if (e.getClickedInventory().equals(e.getView().getTopInventory())){
            // opened inventory clicked
            if (legalClickTypes.contains(e.getClick())) { // inconsequential action used, allow event and do nothing more
                e.setCancelled(false);
                return;
            }
            if (illegalClickTypes.contains(e.getClick())) return; // incalculable action used, event is cancelled and do nothing more
            // other actions have to be calculated
            if (e.isLeftClick() || e.isRightClick()) {
                // transfer or swap all if not similar
                if (isEmpty(cursor)){
                    // pick up clicked item, should be fine
                    e.setCancelled(false);
                } else {
                    if (isEmpty(clickedItem)){
                        int amountToTransfer = (e.isRightClick() ? 1 : maxAmount);
                        if (cursor.getAmount() > amountToTransfer){
                            ItemStack itemToTransfer = cursor.clone();
                            itemToTransfer.setAmount(amountToTransfer);
                            e.setCurrentItem(itemToTransfer);
                            cursor.setAmount(cursor.getAmount() - amountToTransfer);
                            p.setItemOnCursor(cursor);
                        } else {
                            e.setCurrentItem(cursor);
                            p.setItemOnCursor(null);
                        }
                    } else {
                        // swap or transfer items
                        if (cursor.isSimilar(clickedItem)){
                            // are similar, transfer as much as possible
                            int clickedMax = Math.min(clickedItem.getType().getMaxStackSize(), maxAmount);
                            if (clickedItem.getAmount() < clickedMax){
                                int amountToTransfer = e.isRightClick() ? 1 : Math.min(cursor.getAmount(), clickedMax - clickedItem.getAmount());
                                if (cursor.getAmount() == amountToTransfer) {
                                    p.setItemOnCursor(null);
                                } else {
                                    cursor.setAmount(cursor.getAmount() - amountToTransfer);
                                    p.setItemOnCursor(cursor);
                                }
                                clickedItem.setAmount(clickedItem.getAmount() + amountToTransfer);
                            } // clicked item already equals or exceeds max amount, do nothing more
                        } else {
                            // not similar, swap items if cursor has valid amount
                            if (cursor.getAmount() <= maxAmount){
                                // valid amount, swap items
                                ItemStack temp = cursor.clone();
                                p.setItemOnCursor(clickedItem);
                                e.setCurrentItem(temp);
                            } // invalid amount, do nothing more
                        }
                    }
                }
            }
        }
    }

    public static ItemStack createSimpleItem(Material type, int data, String itemDisplayName, List<String> lore){
        ItemStack icon = new ItemStack(type);
        ItemMeta meta = icon.getItemMeta();
        if (meta != null){
            meta.setCustomModelData(data);
            meta.setDisplayName(Utils.chat(itemDisplayName));
            meta.setLore(lore.stream().map(Utils::chat).collect(Collectors.toList()));
        } else return null;
        icon.setItemMeta(meta);
        return icon;
    }

    /**
     * Sends a message to the CommandSender, but only if the message isn't null or empty
     * @param whomst the CommandSender whomst to message
     * @param message the message to send
     */
    public static void sendMessage(CommandSender whomst, String message){
        if (!StringUtils.isEmpty(message)) {
            if (message.startsWith("ACTIONBAR") && whomst instanceof Player) {
                Player p = (Player) whomst;
                sendActionBar(p, message.replaceFirst("ACTIONBAR", ""));
            } else if (message.startsWith("TITLE") && whomst instanceof Player){
                Player p = (Player) whomst;
                String title = message.replaceFirst("TITLE", "");
                String subtitle = "";
                int titleDuration = 40;
                int fadeDuration = 5;
                String subString = StringUtils.substringBetween(message, "TITLE(", ")");
                if (subString != null){
                    String[] args = subString.split(";");
                    if (args.length > 0) title = args[0];
                    if (args.length > 1) subtitle = args[1];
                    if (args.length > 2) titleDuration = Catch.catchOrElse(() -> Integer.parseInt(args[2]), 100);
                    if (args.length > 3) fadeDuration = Catch.catchOrElse(() -> Integer.parseInt(args[2]), 10);
                }
                sendTitle(p, title, subtitle, titleDuration, fadeDuration);
            } else {
                whomst.sendMessage(chat(message));
            }
        }
    }

    public static void sendActionBar(Player whomst, String message){
        if (!StringUtils.isEmpty(ChatColor.stripColor(chat(message)))) whomst.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(chat(message)));
    }

    public static void sendTitle(Player whomst, String title, String subtitle, int duration, int fade){
        if (!StringUtils.isEmpty(title)) whomst.sendTitle(chat(title), chat(subtitle), fade, duration, fade);
    }

    public static String simpleChat(String message) {
        return ChatColor.translateAlternateColorCodes('&', message + "");
    }
    static final Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static String chat(String message) {
        char COLOR_CHAR = ChatColor.COLOR_CHAR;
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find())
        {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        return Utils.simpleChat(StringEscapeUtils.unescapeJava(matcher.appendTail(buffer).toString()));
    }

    public static String serializeItemStack(ItemStack itemStack){
        return ItemSerializer.toBase64(itemStack);
    }

    public static ItemStack deserializeItemStack(String base64){
        if (base64.startsWith("i:")) return deserializeItemStackYaml(base64);
        return ItemSerializer.itemStackFromBase64(base64);
    }

    public static String serializeItemStackYaml(ItemStack itemStack) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("i", itemStack);
        return config.saveToString();
    }

    public static ItemStack deserializeItemStackYaml(String yml){
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(yml);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return config.getItemStack("i", null);
    }
}
