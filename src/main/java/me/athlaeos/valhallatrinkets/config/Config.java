package me.athlaeos.valhallatrinkets.config;

import me.athlaeos.valhallatrinkets.ValhallaTrinkets;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

public class Config {
    private String name;
    private File file;
    private YamlConfiguration config;
    private final ValhallaTrinkets plugin = ValhallaTrinkets.getPlugin();

    public Config(String name) {
        this.name = name;
    }

    public Config save() {
        if ((this.config == null) || (this.file == null))
            return this;
        try {
            if (config.getConfigurationSection("").getKeys(true).size() != 0)
                config.save(this.file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    public YamlConfiguration get() {
        if (this.config == null)
            reload();

        return this.config;
    }

    public Config saveDefaultConfig() {
        file = new File(plugin.getDataFolder(), this.name);

        plugin.saveResource(this.name, false);

        return this;
    }

    public Config reload() {
        if (file == null)
            this.file = new File(plugin.getDataFolder(), this.name);

        this.config = YamlConfiguration.loadConfiguration(file);

        return this;
    }

    public Config copyDefaults(boolean force) {
        get().options().copyDefaults(force);
        return this;
    }

    public Config set(String key, Object value) {
        get().set(key, value);
        return this;
    }

    public Object get(String key) {
        return get().get(key);
    }
}