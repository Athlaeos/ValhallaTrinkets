package me.athlaeos.valhallatrinkets.valhallammo;

import me.athlaeos.valhallammo.commands.Command;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierContext;
import me.athlaeos.valhallatrinkets.TrinketsManager;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SetUnstackableModifier extends DynamicItemModifier {
    public SetUnstackableModifier() {
        super("unstackable");
    }

    @Override
    public ItemStack getModifierIcon() {
        return new ItemStack(Material.BARRIER);
    }

    @Override
    public String getDisplayName() {
        return "&cUnstackable";
    }

    @Override
    public String getDescription() {
        return "&fApplies a random unique ID onto the item, effectively rendering it unstackable";
    }

    @Override
    public String getActiveDescription() {
        return "&fApplies a random unique ID onto the item, effectively rendering it unstackable";
    }

    @Override
    public Collection<String> getCategories() {
        return Collections.singleton("ITEM_MISC");
    }

    @Override
    public DynamicItemModifier copy() {
        SetUnstackableModifier modifier = new SetUnstackableModifier();
        modifier.setPriority(this.getPriority());
        return modifier;
    }

    @Override
    public String parseCommand(CommandSender commandSender, String[] strings) {
        return null;
    }

    @Override
    public List<String> commandSuggestions(CommandSender commandSender, int i) {
        return Command.noSubcommandArgs();
    }

    @Override
    public int commandArgsRequired() {
        return 0;
    }

    @Override
    public Map<Integer, ItemStack> getButtons() {
        return new HashMap<>();
    }

    @Override
    public void onButtonPress(InventoryClickEvent inventoryClickEvent, int i) { }

    @Override
    public void processItem(ModifierContext context) {
        TrinketsManager.setUnstackable(context.getItem().getMeta(), true);
    }
}
