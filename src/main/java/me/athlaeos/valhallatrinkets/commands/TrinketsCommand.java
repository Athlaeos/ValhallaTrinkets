package me.athlaeos.valhallatrinkets.commands;

import me.athlaeos.valhallatrinkets.Utils;
import me.athlaeos.valhallatrinkets.menus.TrinketMenu;
import me.athlaeos.valhallatrinkets.ValhallaTrinkets;
import me.athlaeos.valhallatrinkets.menus.PlayerMenuUtilManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

public class TrinketsCommand implements CommandExecutor {
    public TrinketsCommand(){
        PluginCommand command = ValhallaTrinkets.getPlugin().getCommand("trinkets");
        if (command != null) command.setExecutor(this);

    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player target = (Player) sender;
            if (args.length > 0 && sender.hasPermission("trinkets.viewothers")){
                target = ValhallaTrinkets.getPlugin().getServer().getPlayer(args[0]);
                if (target == null){
                    sender.sendMessage(Utils.chat("&cPlayer not online"));
                    return true;
                }
            }
            new TrinketMenu(PlayerMenuUtilManager.getPlayerMenuUtility(target)).open((Player) sender);
        }
        return true;
    }
}
