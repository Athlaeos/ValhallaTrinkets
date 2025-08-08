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

public class ValhallaHook extends PluginHook {
    public ValhallaHook() {
        super("ValhallaMMO");
    }

    @Override
    public void whenPresent() {
        ValhallaTrinkets.getPlugin().save("default_trinkets.json");
        ValhallaTrinkets.getPlugin().save("default_trinket_loot_table.json");
        ValhallaTrinkets.getPlugin().save("default_trinket_recipes.json");
        ValhallaTrinkets.getPlugin().getLogger().info("ValhallaMMO hooked! Adding a bunch of cool stuff. Use /val setuptrinkets to load in its custom recipes");
        ModifierRegistry.register(new TrinketTypeSetModifier());
        ModifierRegistry.register(new SetUnstackableModifier());
        RecipeOptionRegistry.registerOption(new MaterialWithTrinketIDChoice());
        CommandManager.getCommands().put("setuptrinkets", new ValhallaLoadDefaultTrinketRecipesCommand());

        AccumulativeStatManager.register("HEALTH_BONUS", new TrinketVanillaAttributeSource(Attribute.GENERIC_MAX_HEALTH, AttributeModifier.Operation.ADD_NUMBER));
        AccumulativeStatManager.register("HEALTH_MULTIPLIER_BONUS", new TrinketVanillaAttributeSource(Attribute.GENERIC_MAX_HEALTH, AttributeModifier.Operation.ADD_SCALAR));
        AccumulativeStatManager.register("MOVEMENT_SPEED_BONUS", new TrinketVanillaAttributeSource(Attribute.GENERIC_MOVEMENT_SPEED, AttributeModifier.Operation.ADD_SCALAR));
        AccumulativeStatManager.register("KNOCKBACK_RESISTANCE", new TrinketVanillaAttributeSource(Attribute.GENERIC_KNOCKBACK_RESISTANCE, AttributeModifier.Operation.ADD_NUMBER));
        AccumulativeStatManager.register("TOUGHNESS_BONUS", new TrinketVanillaAttributeSource(Attribute.GENERIC_ARMOR_TOUGHNESS, AttributeModifier.Operation.ADD_NUMBER));
        AccumulativeStatManager.register("ATTACK_DAMAGE_BONUS", new TrinketVanillaAttributeSource(Attribute.GENERIC_ATTACK_DAMAGE, AttributeModifier.Operation.ADD_NUMBER));
        AccumulativeStatManager.register("ATTACK_SPEED_BONUS", new TrinketVanillaAttributeSource(Attribute.GENERIC_ATTACK_SPEED, AttributeModifier.Operation.ADD_SCALAR));
        AccumulativeStatManager.register("LUCK_BONUS", new TrinketVanillaAttributeSource(Attribute.GENERIC_LUCK, AttributeModifier.Operation.ADD_NUMBER));
        AccumulativeStatManager.register("WEIGHTLESS_ARMOR", new TrinketVanillaAttributeSource(Attribute.GENERIC_ARMOR, AttributeModifier.Operation.ADD_NUMBER));
        AccumulativeStatManager.register("ARMOR_MULTIPLIER_BONUS", new TrinketVanillaAttributeSource(Attribute.GENERIC_ARMOR, AttributeModifier.Operation.ADD_SCALAR));

        AccumulativeStatManager.register("BLOCK_REACH", new TrinketVanillaAttributeSource(AttributeMappings.BLOCK_INTERACTION_RANGE.getAttribute(), AttributeModifier.Operation.ADD_NUMBER));
        AccumulativeStatManager.register("STEP_HEIGHT", new TrinketVanillaAttributeSource(AttributeMappings.STEP_HEIGHT.getAttribute(), AttributeModifier.Operation.ADD_NUMBER));
        AccumulativeStatManager.register("SCALE", new TrinketVanillaAttributeSource(AttributeMappings.SCALE.getAttribute(), AttributeModifier.Operation.ADD_SCALAR));
        AccumulativeStatManager.register("GRAVITY", new TrinketVanillaAttributeSource(AttributeMappings.GRAVITY.getAttribute(), AttributeModifier.Operation.ADD_SCALAR));
        AccumulativeStatManager.register("SAFE_FALLING_DISTANCE", new TrinketVanillaAttributeSource(AttributeMappings.SAFE_FALL_DISTANCE.getAttribute(), AttributeModifier.Operation.ADD_NUMBER));
        AccumulativeStatManager.register("FALL_DAMAGE_MULTIPLIER", new TrinketVanillaAttributeSource(AttributeMappings.FALL_DAMAGE_MULTIPLIER.getAttribute(), AttributeModifier.Operation.ADD_SCALAR));
        AccumulativeStatManager.register("WATER_MOVEMENT_EFFICIENCY_BONUS", new TrinketVanillaAttributeSource(AttributeMappings.WATER_MOVEMENT_EFFICIENCY.getAttribute(), AttributeModifier.Operation.ADD_SCALAR));
        AccumulativeStatManager.register("SUBMERGED_MINING_SPEED_BONUS", new TrinketVanillaAttributeSource(AttributeMappings.SUBMERGED_MINING_SPEED.getAttribute(), AttributeModifier.Operation.ADD_NUMBER));
        AccumulativeStatManager.register("FLIGHT_SPEED_BONUS", new TrinketVanillaAttributeSource(AttributeMappings.FLYING_SPEED.getAttribute(), AttributeModifier.Operation.ADD_SCALAR));
        AccumulativeStatManager.register("OXYGEN_BONUS", new TrinketVanillaAttributeSource(AttributeMappings.OXYGEN_BONUS.getAttribute(), AttributeModifier.Operation.ADD_NUMBER));

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
