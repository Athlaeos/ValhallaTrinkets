package me.athlaeos.valhallatrinkets.config;


import me.athlaeos.valhallatrinkets.ValhallaTrinkets;

import java.util.HashMap;

//All credit to spigotmc.org user Bimmr for this manager
public class ConfigManager {

    private final ValhallaTrinkets plugin;
    private HashMap<String, Config> configs = new HashMap<String, Config>();
    private static ConfigManager manager = null;

    public ConfigManager() {
        plugin = ValhallaTrinkets.getPlugin();
    }

    public static ConfigManager getInstance() {
        if (manager == null) {
            manager = new ConfigManager();
        }
        return manager;
    }

    public HashMap<String, Config> getConfigs() {
        return configs;
    }

    public Config getConfig(String name) {
        if (!configs.containsKey(name))
            configs.put(name, new Config(name));

        return configs.get(name);
    }

    public Config saveConfig(String name) {
        return getConfig(name).save();
    }

    public Config reloadConfig(String name) {
        return getConfig(name).reload();
    }

}
