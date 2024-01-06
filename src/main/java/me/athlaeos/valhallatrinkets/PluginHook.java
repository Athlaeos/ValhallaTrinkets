package me.athlaeos.valhallatrinkets;

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