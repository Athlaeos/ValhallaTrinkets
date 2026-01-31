package me.athlaeos.valhallatrinkets.hooks;

import me.athlaeos.valhallammo.commands.CommandManager;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierRegistry;
import me.athlaeos.valhallammo.crafting.ingredientconfiguration.RecipeOptionRegistry;
import me.athlaeos.valhallammo.playerstats.AccumulativeStatManager;
import me.athlaeos.valhallammo.utility.EntityUtils;
import me.athlaeos.valhallammo.version.AttributeMappings;
import me.athlaeos.valhallatrinkets.TrinketCache;
import me.athlaeos.valhallatrinkets.TrinketItem;
import me.athlaeos.valhallatrinkets.ValhallaTrinkets;
import me.athlaeos.valhallatrinkets.valhallammo.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ValhallaHookWrapper {
    public static void init(){
        ValhallaTrinkets.getPlugin().save("default_trinkets.json");
        ValhallaTrinkets.getPlugin().save("default_trinket_loot_table.json");
        ValhallaTrinkets.getPlugin().save("default_trinket_recipes.json");
        ValhallaTrinkets.getPlugin().getLogger().info("ValhallaMMO hooked! Adding a bunch of cool stuff. Use /val setuptrinkets to load in its custom recipes");
        ModifierRegistry.register(new TrinketTypeSetModifier());
        ModifierRegistry.register(new SetUnstackableModifier());
        RecipeOptionRegistry.registerOption(new MaterialWithTrinketIDChoice());
        CommandManager.getCommands().put("setuptrinkets", new ValhallaLoadDefaultTrinketRecipesCommand());

        EntityUtils.registerEquipmentFetcher(entity -> {
            if (entity instanceof Player)
                return TrinketCache.getOrCache((Player) entity)
                        .values().stream().filter(i -> {
                            if (i.getSlot() == null) return true;
                            if (i.getSlot().getPermissionRequired() == null) return true;
                            return entity.hasPermission(i.getSlot().getPermissionRequired());
                        }).map(TrinketItem::getItem)
                        .collect(Collectors.toList());
            return new ArrayList<>();
        });
    }
}
