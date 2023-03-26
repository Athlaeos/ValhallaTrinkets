package me.athlaeos.valhallatrinkets;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetUnstackableModifier extends DynamicItemModifier {
    public SetUnstackableModifier() {
        super("unstackable", 0, ModifierPriority.NEUTRAL);

        this.category = ModifierCategory.ITEM_STATS_MISC;

        this.bigStepDecrease = 1D;
        this.bigStepIncrease = 1D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 1;
        this.description = Utils.chat("&7Gives the item a unique identifier upon crafting, making it unstackable with other seemingly identical items. " +
                "Keep in mind that if this modifier is applied on a stack of items the items will remain stackable with eachother, but not others, so when" +
                " creating recipes with this modifier you should make them in a way that the player can only craft 1 at a time. This unstackable tag may also be removed");
        this.displayName = Utils.chat("&7&lSet Unstackable");
        this.icon = Material.BARRIER;
    }

    @Override
    public ItemStack processItem(Player player, ItemStack itemStack, int i) {
        TrinketsManager.getInstance().setUnstackable(itemStack, (int) strength == 0);
        return itemStack;
    }

    @Override
    public String toString() {
        if ((int) strength == 0){
            return Utils.chat("&7Makes the item unstackable");
        } else {
            return Utils.chat("&7Removes the 'unstackable' tag from the item");
        }
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Arrays.asList("0-unstackable", "1-removeunstackable");
    }
}
