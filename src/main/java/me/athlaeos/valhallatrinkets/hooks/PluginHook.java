package me.athlaeos.valhallatrinkets.hooks;

import me.athlaeos.valhallatrinkets.ValhallaTrinkets;

public abstract class PluginHook {
    private final boolean isPresent;
    public PluginHook(String name){
        this.isPresent = ValhallaTrinkets.getPlugin().getServer().getPluginManager().getPlugin(name) != null;
    }

    public boolean isPresent(){
        return isPresent;
    }

    public abstract void whenPresent();
}