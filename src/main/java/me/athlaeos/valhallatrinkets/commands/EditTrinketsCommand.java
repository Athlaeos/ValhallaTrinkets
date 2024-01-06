package me.athlaeos.valhallatrinkets.commands;

import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallatrinkets.*;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EditTrinketsCommand implements TabExecutor {
    public EditTrinketsCommand(){
        PluginCommand command = ValhallaTrinkets.getPlugin().getCommand("trinketeditor");
        if (command != null) command.setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.chat("&cOnly players can use this command"));
            return true;
        }
        if (!sender.hasPermission("trinkets.edittrinkets")) {
            sender.sendMessage(Utils.chat("&cNo permission"));
            return true;
        }
        if (args.length > 0){
            ItemStack held = ((Player) sender).getInventory().getItemInMainHand();
            ItemMeta meta = ItemUtils.isEmpty(held) ? null : held.getItemMeta();
            if (meta == null) {
                sender.sendMessage(Utils.chat("&cAn item must be held to edit!"));
                return true;
            }
            switch (args[0]){
                case "setmodeldata": {
                    try {
                        if (args.length <= 1) throw new IllegalArgumentException();
                        meta.setCustomModelData(Integer.parseInt(args[1]));
                        sender.sendMessage(Utils.chat("&aModel data set to " + args[1]));
                    } catch (IllegalArgumentException ignored){
                        meta.setCustomModelData(null);
                        sender.sendMessage(Utils.chat("&aModel data removed"));
                    }
                    break;
                }
                case "setname": {
                    String name = args.length <= 1 ? null : String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    if (name == null){
                        meta.setDisplayName(null);
                        sender.sendMessage(Utils.chat("&aDisplay name removed"));
                    } else {
                        meta.setDisplayName(Utils.chat(name));
                        sender.sendMessage(Utils.chat("&aDisplay name set to &r" + name));
                    }
                    break;
                }
                case "lore": {
                    String option = args.length <= 1 ? null : args[1];
                    if (option == null) {
                        sender.sendMessage(Utils.chat("&4/trinketeditor lore <add/remove/clear>"));
                        return true;
                    }
                    List<String> lore = !meta.hasLore() || meta.getLore() == null ? new ArrayList<>() : meta.getLore();
                    switch (option){
                        case "add": {
                            String line = args.length <= 2 ? null : String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                            if (line != null) lore.add(Utils.chat(line));
                            else sender.sendMessage(Utils.chat("&4/trinketeditor lore add <text>"));
                            break;
                        }
                        case "remove": {
                            try {
                                if (args.length <= 2 || lore.isEmpty()) throw new IllegalArgumentException();
                                int line = Integer.parseInt(args[1]);
                                if (line < lore.size() && line >= 0) lore.remove(line);
                                else throw new IllegalArgumentException();
                                sender.sendMessage(Utils.chat("&aLore line " + args[2] + " removed"));
                            } catch (IllegalArgumentException ignored){
                                sender.sendMessage(Utils.chat("&cLore could not be removed"));
                                return true;
                            }
                            break;
                        }
                        case "clear": {
                            lore.clear();
                            break;
                        }
                    }
                }
                case "settrinkettype": {
                    try {
                        if (args.length <= 1) throw new IllegalArgumentException();
                        int t = Integer.parseInt(args[1].split("-")[0]);
                        TrinketType type = TrinketsManager.getTrinketTypes().get(t);
                        if (type == null) throw new IllegalArgumentException();
                        TrinketsManager.setType(meta, type);
                        sender.sendMessage(Utils.chat("&aTrinket type set to " + type.getLoreTag()));
                    } catch (IllegalArgumentException ignored){
                        TrinketsManager.setType(meta, null);
                        sender.sendMessage(Utils.chat("&aTrinket type removed"));
                    }
                    break;
                }
                case "setid": {
                    try {
                        if (args.length <= 1) throw new IllegalArgumentException();
                        int id = Integer.parseInt(args[1]);
                        if (id < 0) throw new IllegalArgumentException();
                        TrinketProperties.setTrinketID(meta, id);
                        sender.sendMessage(Utils.chat("&aTrinket id set to " + id));
                    } catch (IllegalArgumentException ignored){
                        TrinketProperties.setTrinketID(meta, null);
                        sender.sendMessage(Utils.chat("&aTrinket id removed"));
                    }
                    break;
                }
                case "setunique": {
                    if (args.length <= 1 || (args[1].equalsIgnoreCase("yes") || args[1].equalsIgnoreCase("no"))) {
                        sender.sendMessage(Utils.chat("&4/trinketeditor setunique <yes/no>"));
                        return true;
                    }
                    boolean unique = args[1].equalsIgnoreCase("yes");
                    TrinketProperties.setUniqueTrinket(meta, unique);
                    if (unique) {
                        Integer id = TrinketProperties.getTrinketID(meta);
                        if (id != null) sender.sendMessage(Utils.chat("&aTrinkets with ID " + id + " can no longer be worn repeatedly"));
                        else sender.sendMessage(Utils.chat("&eTrinket configured to not be repeatedly wearable, but it has no ID yet! This property does nothing until an ID is set &6(/trinketeditor setid <id>)"));
                    } else {
                        sender.sendMessage(Utils.chat("&aTrinket can now be equipped several times"));
                    }
                    break;
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length){
            case 0: return Arrays.asList("setmodeldata", "setname", "lore", "settrinkettype", "setid", "setunique");
            case 1: {
                if (args[0].equalsIgnoreCase("setmodeldata")) return Arrays.asList("1000000", "9999999");
                if (args[0].equalsIgnoreCase("setname")) return Collections.singletonList("<name>");
                if (args[0].equalsIgnoreCase("lore")) return Arrays.asList("add", "remove", "clear");
                if (args[0].equalsIgnoreCase("settrinkettype"))
                    return TrinketsManager.getTrinketTypes().values().stream()
                            .map(t -> t.getID() + "-" + ChatColor.stripColor(Utils.chat(t.getLoreTag())))
                            .collect(Collectors.toList());
                if (args[0].equalsIgnoreCase("setid")) return Collections.singletonList("<id>");
                if (args[0].equalsIgnoreCase("setunique")) return Arrays.asList("yes", "no");
            }
            case 2: {
                if (args[0].equalsIgnoreCase("lore") && args[1].equalsIgnoreCase("remove")) return Arrays.asList("0", "1", "2", "...");
            }
        }
        return Collections.emptyList();
    }
}
