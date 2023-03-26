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
import java.util.List;

public class TrinketTypeSetModifier extends DynamicItemModifier {
    public TrinketTypeSetModifier() {
        super("trinket_type_set", 0, ModifierPriority.NEUTRAL);

        this.category = ModifierCategory.ITEM_STATS_MISC;

        this.bigStepDecrease = 1D;
        this.bigStepIncrease = 1D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 0;
        this.minStrength = -1;
        this.maxStrength = Integer.MAX_VALUE;
        this.description = Utils.chat("&7After this modifier the item will be considered a specific type of trinket, which determines how many the player " +
                "can wear at a time and where they're positioned in the trinket GUI");
        this.displayName = Utils.chat("&7&lSet Trinket Type");
        this.icon = Material.GOLD_NUGGET;
    }

    @Override
    public ItemStack processItem(Player player, ItemStack itemStack, int i) {
        TrinketType type = TrinketsManager.getInstance().getTrinketTypes().get((int) strength);
        if (type == null || (int) strength < 0){
            TrinketsManager.getInstance().setType(itemStack, null);
        } else {
            TrinketsManager.getInstance().setType(itemStack, type);
        }
        return itemStack;
    }

    @Override
    public String toString() {
        TrinketType type = TrinketsManager.getInstance().getTrinketTypes().get((int) strength);
        if (type == null || (int) strength < 0){
            return Utils.chat("&7Removing the trinket type from the item");
        } else {
            return Utils.chat("&7Setting the item's trinket type to " + type.getDisplayName());
        }
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        List<String> args = new ArrayList<>();
        for (int i : TrinketsManager.getInstance().getTrinketTypes().keySet()){
            String type = ChatColor.stripColor(Utils.chat(TrinketsManager.getInstance().getTrinketTypes().get(i).getDisplayName()));
            args.add(i + "-" + type);
        }
        return args;
    }
}
