package me.athlaeos.valhallatrinkets.hooks;

import me.athlaeos.valhallatrinkets.ValhallaTrinkets;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;

public class WorldGuardHook extends PluginHook{
    public static String TRINKETS_KEEPINVENTORY = "trinkets-keepinventory";

    public WorldGuardHook() {
        super("WorldGuard");
        if (!isPresent()) return;
        WorldGuardWrapper.registerFlag(TRINKETS_KEEPINVENTORY);
    }

    public static boolean inDisabledRegion(Location l, Player p, String flag){
        if (ValhallaTrinkets.isHooked(WorldGuardHook.class)){
            return WorldGuardWrapper.inDisabledRegion(l, p, flag);
        }
        return false;
    }

    public static boolean canPlaceBlocks(Location l, Player p){
        return WorldGuardWrapper.canPlaceBlocks(l, p);
    }

    public static boolean inDisabledRegion(Location l, String flag){
        return inDisabledRegion(l, null, flag);
    }

    public static Collection<String> getRegions(){
        return WorldGuardWrapper.getRegions();
    }

    public static boolean isInRegion(Location l, String region){
        return WorldGuardWrapper.isInRegion(l, region);
    }

    @Override
    public void whenPresent() {
    }
}
