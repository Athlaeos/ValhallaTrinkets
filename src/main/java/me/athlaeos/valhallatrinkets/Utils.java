package me.athlaeos.valhallatrinkets;

import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utils {
    public static boolean isItemEmptyOrNull(ItemStack i){
        if (i == null) return true;
        return i.getType().isAir();
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
