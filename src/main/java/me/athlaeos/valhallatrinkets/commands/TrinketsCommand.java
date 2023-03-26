package me.athlaeos.valhallatrinkets.commands;

import me.athlaeos.valhallatrinkets.TrinketMenu;
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
        if (command != null){
            command.setExecutor(this);
        }
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            new TrinketMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender)).open();
        }
        return true;
    }
}
