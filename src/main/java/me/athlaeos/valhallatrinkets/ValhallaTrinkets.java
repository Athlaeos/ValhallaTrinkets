package me.athlaeos.valhallatrinkets;

import me.athlaeos.valhallatrinkets.commands.EditTrinketsCommand;
import me.athlaeos.valhallatrinkets.commands.TrinketsCommand;
import me.athlaeos.valhallatrinkets.hooks.PluginHook;
import me.athlaeos.valhallatrinkets.hooks.ValhallaHook;
import me.athlaeos.valhallatrinkets.config.ConfigUpdater;
import me.athlaeos.valhallatrinkets.hooks.WorldGuardHook;
import me.athlaeos.valhallatrinkets.listener.MenuListener;
import me.athlaeos.valhallatrinkets.listener.TrinketsListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class ValhallaTrinkets extends JavaPlugin {
    private static ValhallaTrinkets plugin = null;
    private static boolean illHandleTrinketMenu = false;
    private static final Map<Class<? extends PluginHook>, PluginHook> activeHooks = new HashMap<>();

    @Override
    public void onLoad(){
        plugin = this;
        registerHook(new ValhallaHook());
        registerHook(new WorldGuardHook());
    }

    @Override
    public void onEnable() {
        save("config.yml");
        TrinketsManager.loadTrinketTypes();
        for (PluginHook hook : activeHooks.values()) hook.whenPresent();

        new TrinketsCommand();
        new EditTrinketsCommand();
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new TrinketsListener(), this);
    }

    @Override
    public void onDisable() {

    }

    /**
     * You'll implement your own way to open the trinket menu? You don't want clicking outside the inventory to open the trinket menu?
     * Alright buddy, no problem
     * @param illHandleTrinketMenu will you handle the opening of the trinket menu?
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
        save(config);
        updateConfig(config);
    }

    public void save(String name){
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

    public static void logInfo(String message){
        plugin.getServer().getLogger().info("[ValhallaTrinkets] " + message);
    }

    public static void logWarning(String warning){
        plugin.getServer().getLogger().warning("[ValhallaTrinkets] " + warning);
    }
    public static void logFine(String warning){
        plugin.getServer().getLogger().fine("[ValhallaTrinkets] " + warning);
        Utils.sendMessage(plugin.getServer().getConsoleSender(), "&a[ValhallaTrinkets] " + warning);
    }

    public static void logSevere(String help){
        plugin.getServer().getLogger().severe("[ValhallaTrinkets] " + help);
    }

    private static void registerHook(PluginHook hook){
        if (hook.isPresent()) activeHooks.put(hook.getClass(), hook);
    }
    public static boolean isHooked(Class<? extends PluginHook> hook){
        return activeHooks.containsKey(hook);
    }
}
