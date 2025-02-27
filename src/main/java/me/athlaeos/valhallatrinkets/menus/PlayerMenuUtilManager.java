package me.athlaeos.valhallatrinkets.menus;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerMenuUtilManager {
    private static final HashMap<UUID, PlayerMenuUtility> playerMenuMap = new HashMap<>();

    /**
     * Returns a PlayerMenuUtility object belonging to the given player, or a new blank one if none exist.
     * This PlayerMenuUtility contains some required details in controlling the GUI menu's during usage.
     *
     * @return A PlayerMenuUtility object belonging to a player, or a new blank one if none were found.
     */
    public static PlayerMenuUtility getPlayerMenuUtility(Player p){
        if (!playerMenuMap.containsKey(p.getUniqueId())){
            PlayerMenuUtility utility = new PlayerMenuUtility(p);
            playerMenuMap.put(p.getUniqueId(), utility);
        }
        return playerMenuMap.get(p.getUniqueId());
    }

    public static void removePlayerMenuUtility(UUID uuid){
        playerMenuMap.remove(uuid);
    }
}
