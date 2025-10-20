package me.athlaeos.valhallatrinkets.hooks;

public class ValhallaHook extends PluginHook {
    public ValhallaHook() {
        super("ValhallaMMO");
    }

    @Override
    public void whenPresent() {
        ValhallaHookWrapper.init();
    }
}
