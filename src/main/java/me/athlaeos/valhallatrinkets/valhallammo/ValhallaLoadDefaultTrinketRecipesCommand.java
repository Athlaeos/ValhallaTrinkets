package me.athlaeos.valhallatrinkets.valhallammo;

import me.athlaeos.valhallammo.commands.Command;
import me.athlaeos.valhallammo.crafting.CustomRecipeRegistry;
import me.athlaeos.valhallammo.dom.Catch;
import me.athlaeos.valhallammo.item.CustomItemRegistry;
import me.athlaeos.valhallammo.loot.LootTableRegistry;
import me.athlaeos.valhallatrinkets.Utils;
import me.athlaeos.valhallatrinkets.ValhallaTrinkets;
import org.bukkit.command.CommandSender;
import org.bukkit.loot.LootTables;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class ValhallaLoadDefaultTrinketRecipesCommand implements Command {

    private static final Collection<String> trinketLootTables = new HashSet<>();

    static {
        trinketLootTables.add("TRAIL_RUINS_ARCHAEOLOGY_RARE");
        trinketLootTables.add("OCEAN_RUIN_WARM_ARCHAEOLOGY");
        trinketLootTables.add("OCEAN_RUIN_COLD_ARCHAEOLOGY");
        trinketLootTables.add("DESERT_PYRAMID_ARCHAEOLOGY");
        trinketLootTables.add("VILLAGE_WEAPONSMITH");
        trinketLootTables.add("UNDERWATER_RUIN_BIG");
        trinketLootTables.add("STRONGHOLD_CORRIDOR");
        trinketLootTables.add("ABANDONED_MINESHAFT");
        trinketLootTables.add("STRONGHOLD_LIBRARY");
        trinketLootTables.add("SHIPWRECK_TREASURE");
        trinketLootTables.add("VILLAGE_TOOLSMITH");
        trinketLootTables.add("SPAWN_BONUS_CHEST");
        trinketLootTables.add("END_CITY_TREASURE");
        trinketLootTables.add("WOODLAND_MANSION");
        trinketLootTables.add("PILLAGER_OUTPOST");
        trinketLootTables.add("BASTION_TREASURE");
        trinketLootTables.add("VILLAGE_ARMORER");
        trinketLootTables.add("BURIED_TREASURE");
        trinketLootTables.add("SIMPLE_DUNGEON");
        trinketLootTables.add("DESERT_PYRAMID");
        trinketLootTables.add("RUINED_PORTAL");
        trinketLootTables.add("NETHER_BRIDGE");
        trinketLootTables.add("JUNGLE_TEMPLE");
        trinketLootTables.add("ANCIENT_CITY");
        trinketLootTables.add("IGLOO_CHEST");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        CustomItemRegistry.loadFromFile(new File(ValhallaTrinkets.getPlugin().getDataFolder(), "/default_trinkets.json"));
        LootTableRegistry.loadLootTable(new File(ValhallaTrinkets.getPlugin().getDataFolder(), "/default_trinket_loot_table.json"));
        CustomRecipeRegistry.loadGridRecipes(new File(ValhallaTrinkets.getPlugin().getDataFolder(), "/default_trinket_recipes.json"), false);
        CustomRecipeRegistry.setChangesMade();

        for (String t : trinketLootTables){
            LootTables table = Catch.catchOrElse(() -> LootTables.valueOf(t), null);
            if (table == null) continue;
            LootTableRegistry.getLootTableAdditions().put(table.getKey(), "trinkets");
        }
        sender.sendMessage(Utils.chat("&aFinished loading default trinkets config!"));
        return true;
    }

    @Override
    public String getFailureMessage(String[] strings) {
        return "&4/valhalla setuptrinkets";
    }

    @Override
    public String[] getRequiredPermissions() {
        return new String[]{"trinkets.setup"};
    }

    @Override
    public String getDescription() {
        return "Loads ValhallaTrinkets' default recipes and loot tables";
    }

    @Override
    public String getCommand() {
        return "/valhalla setuptrinkets";
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.hasPermission("trinkets.setup");
    }

    @Override
    public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
        return Command.noSubcommandArgs();
    }
}
