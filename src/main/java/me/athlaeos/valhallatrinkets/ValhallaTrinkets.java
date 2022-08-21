package me.athlaeos.valhallatrinkets;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.crafting.DynamicItemModifierManager;
import me.athlaeos.valhallatrinkets.config.ConfigUpdater;
import me.athlaeos.valhallatrinkets.listener.MenuListener;
import me.athlaeos.valhallatrinkets.listener.TrinketsListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public final class ValhallaTrinkets extends JavaPlugin {
    private static ValhallaTrinkets plugin = null;
    private static boolean valhallaHooked = false;
    private static boolean illHandleTrinketMenu = false;

    @Override
    public void onEnable() {
        plugin = this;
        valhallaHooked = getServer().getPluginManager().isPluginEnabled("ValhallaMMO");
        saveAndUpdateConfig("config.yml");
        TrinketsManager.getInstance().loadTrinketTypes();
        if (valhallaHooked){
            DynamicItemModifierManager.getInstance().register(new TrinketTypeSetModifier());
            DynamicItemModifierManager.getInstance().register(new TrinketTypeRequireModifier());
            ValhallaMMO.setTrinketsHooked(true);
        }
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new TrinketsListener(), this);
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /**
     * You'll implement your own way to open the trinket menu? You don't want clicking outside the inventory to open the trinket menu?
     * Allright buddy, no problem
     * @param illHandleTrinketMenu will you handle the opening of the trinket menu? Cool
     */
    public static void IllHandleTrinketMenu(boolean illHandleTrinketMenu) {
        ValhallaTrinkets.illHandleTrinketMenu = illHandleTrinketMenu;
    }

    public static boolean IllHandleTrinketMenu() {
        return illHandleTrinketMenu;
    }

    public static ValhallaTrinkets getPlugin() {
        return plugin;
    }

    private void saveAndUpdateConfig(String config){
        saveConfig(config);
        updateConfig(config);
    }

    public static boolean isValhallaHooked() {
        return valhallaHooked;
    }

    public void saveConfig(String name){
        File config = new File(this.getDataFolder(), name);
        if (!config.exists()){
            this.saveResource(name, false);
        }
    }

    private void updateConfig(String name){
        File configFile = new File(getDataFolder(), name);
        try {
            ConfigUpdater.update(plugin, name, configFile, new ArrayList<>());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
