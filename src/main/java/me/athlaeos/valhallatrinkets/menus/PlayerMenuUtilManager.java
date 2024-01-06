package me.athlaeos.valhallatrinkets.menus;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlayerMenuUtilManager {
    private static HashMap<Player, PlayerMenuUtility> playerMenuMap = new HashMap<>();

    /**
     * Returns a PlayerMenuUtility object belonging to the given player, or a new blank one if none exist.
     * This PlayerMenuUtility contains some required details in controlling the GUI menu's during usage.
     *
     * @return A PlayerMenuUtility object belonging to a player, or a new blank one if none were found.
     */
    public static PlayerMenuUtility getPlayerMenuUtility(Player p){
        if (!playerMenuMap.containsKey(p)){
            PlayerMenuUtility utility = new PlayerMenuUtility(p);
            playerMenuMap.put(p, utility);
        }
        return playerMenuMap.get(p);
    }
}
