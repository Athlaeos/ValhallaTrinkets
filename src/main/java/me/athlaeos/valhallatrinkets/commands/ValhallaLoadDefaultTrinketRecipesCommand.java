package me.athlaeos.valhallatrinkets.commands;

import me.athlaeos.valhallammo.commands.Command;
import me.athlaeos.valhallammo.crafting.recipetypes.DynamicCraftingTableRecipe;
import me.athlaeos.valhallammo.managers.CustomRecipeManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.managers.TutorialBook;
import me.athlaeos.valhallatrinkets.Utils;
import me.athlaeos.valhallatrinkets.config.ConfigManager;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class ValhallaLoadDefaultTrinketRecipesCommand implements Command {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		YamlConfiguration config = ConfigManager.getInstance().getConfig("valhallatrinkets.yml").get();
		Collection<DynamicCraftingTableRecipe> recipes = CustomRecipeManager.getInstance().getDynamicShapedRecipesFromConfig(config);
		for (DynamicCraftingTableRecipe recipe : recipes){
			DynamicCraftingTableRecipe oldRecipe = CustomRecipeManager.getInstance().getDynamicShapedRecipe(recipe.getName());
			CustomRecipeManager.getInstance().update(oldRecipe, recipe);
		}
		CustomRecipeManager.shouldSaveRecipes();
		sender.sendMessage(Utils.chat("&aRecipes loaded! Check /val recipes"));
		return true;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{"trinkets.setup"};
	}

	@Override
	public String getFailureMessage() {
		return "&4/valhalla setuptrinkets";
	}

	@Override
	public String getDescription() {
		return "Loads ValhallaTrinkets' default recipes";
	}

	@Override
	public String getCommand() {
		return "/valhalla setuptrinkets";
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		return null;
	}
}
