package me.athlaeos.valhallatrinkets.listener;

import me.athlaeos.valhallammo.events.ValhallaLoadModifiersEvent;
import me.athlaeos.valhallatrinkets.SetUnstackableModifier;
import me.athlaeos.valhallatrinkets.TrinketTypeRequireModifier;
import me.athlaeos.valhallatrinkets.TrinketTypeSetModifier;
import me.athlaeos.valhallatrinkets.ValhallaTrinkets;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ValhallaLoadModifiersListener implements Listener {

    @EventHandler
    public void onValhallaLoadModifiers(ValhallaLoadModifiersEvent e){
        e.addModifierToRegister(new TrinketTypeSetModifier());
        e.addModifierToRegister(new TrinketTypeRequireModifier());
        e.addModifierToRegister(new SetUnstackableModifier());
        ValhallaTrinkets.getPlugin().getLogger().info("Registered new ValhallaTrinkets modifiers");
    }
}
