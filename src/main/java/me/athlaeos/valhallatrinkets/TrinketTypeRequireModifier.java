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

public class TrinketTypeRequireModifier extends DynamicItemModifier {
    public TrinketTypeRequireModifier() {
        super("trinket_type_require", 0, ModifierPriority.NEUTRAL);

        this.category = ModifierCategory.ITEM_CONDITIONALS;

        this.bigStepDecrease = 1D;
        this.bigStepIncrease = 1D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 0;
        this.minStrength = -1;
        this.maxStrength = Integer.MAX_VALUE;
        this.description = Utils.chat("&7Requires the item to have a certain trinket type");
        this.displayName = Utils.chat("&7&lRequire Trinket Type");
        this.icon = Material.GOLD_NUGGET;
    }

    @Override
    public ItemStack processItem(Player player, ItemStack itemStack, int amount) {
        TrinketType type = TrinketsManager.getInstance().getTrinketType(itemStack);
        if ((type == null && (int) strength < 0) || (type != null && type.getId() == (int) strength)){
            return itemStack;
        }
        return null;
    }

    @Override
    public String toString() {
        TrinketType type = TrinketsManager.getInstance().getTrinketTypes().get((int) strength);
        if (type == null || (int) strength < 0){
            return Utils.chat("&7Requires the item to have &eno&7 trinket type");
        } else {
            return Utils.chat("&7Requires the item's trinket type to be " + type.getDisplayName());
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
