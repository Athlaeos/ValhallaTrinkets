package me.athlaeos.valhallatrinkets.valhallammo;

import me.athlaeos.valhallammo.commands.Command;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierContext;
import me.athlaeos.valhallammo.item.EquipmentClass;
import me.athlaeos.valhallammo.item.ItemBuilder;
import me.athlaeos.valhallatrinkets.TrinketProperties;
import me.athlaeos.valhallatrinkets.TrinketType;
import me.athlaeos.valhallatrinkets.TrinketsManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class TrinketTypeSetModifier extends DynamicItemModifier {
    private int trinket = 0;
    private int id = -1;
    private boolean unique = false;

    public TrinketTypeSetModifier() {
        super("trinket_type_set");
    }

    @Override
    public ItemStack getModifierIcon() {
        return new ItemStack(Material.GOLD_NUGGET);
    }

    @Override
    public String getDisplayName() {
        return "&eTrinket";
    }

    @Override
    public String getDescription() {
        return "&fTurns the item into a type of trinket";
    }

    @Override
    public String getActiveDescription() {
        TrinketType type = TrinketsManager.getTrinketTypes().get(trinket);
        return String.format("&fTurns the item into%s trinket type %s&f, with%s", unique ? " a unique" : "", type == null ? "&cnone" : type.getID(), id >= 0 ? " id " + id : "out ID");
    }

    @Override
    public Collection<String> getCategories() {
        return Collections.singleton("ITEM_MISC");
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTrinket(int trinket) {
        this.trinket = trinket;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    @Override
    public DynamicItemModifier copy() {
        TrinketTypeSetModifier modifier = new TrinketTypeSetModifier();
        modifier.setId(this.id);
        modifier.setTrinket(this.trinket);
        modifier.setUnique(this.unique);
        modifier.setPriority(this.getPriority());
        return modifier;
    }

    @Override
    public String parseCommand(CommandSender commandSender, String[] strings) {
        if (strings.length != 3) return "Three arguments are expected: the first a trinket type, the second an id, the third a yes/no answer";
        try {
            trinket = Integer.parseInt(strings[0].split("-")[0]);
            id = Integer.parseInt(strings[1]);
            unique = strings[2].equalsIgnoreCase("yes");
        } catch (NumberFormatException ignored){
            return "Three arguments are expected: the first a trinket type, the second an id, the third a yes/no answer. At least one was not a number";
        }
        return null;
    }

    @Override
    public List<String> commandSuggestions(CommandSender commandSender, int i) {
        if (i == 0) return TrinketsManager.getTrinketTypes().values().stream()
                .map(t -> t.getID() + "-" + ChatColor.stripColor(me.athlaeos.valhallatrinkets.Utils.chat(t.getLoreTag())))
                .collect(Collectors.toList());
        if (i == 1) return Collections.singletonList("<id>");
        if (i == 2) return Arrays.asList("<unique?>", "yes", "no");
        return Command.noSubcommandArgs();
    }

    @Override
    public int commandArgsRequired() {
        return 3;
    }

    @Override
    public Map<Integer, ItemStack> getButtons() {
        Map<Integer, ItemStack> buttons = new HashMap<>();
        TrinketType type = TrinketsManager.getTrinketTypes().get(trinket);
        buttons.put(12, new ItemBuilder(Material.GOLD_NUGGET)
                .name("&eWhat trinket type should it be?")
                .lore("&fSet to &e" + trinket + " &7(" + (type == null ? "&cremoval" : type.getLoreTag()) + ")",
                        "&6Click to cycle").get()
        );
        buttons.put(16, new ItemBuilder(Material.NAME_TAG)
                .name("&eWhat id should it have?")
                .lore("&fSet to &e" + (id < 0 ? "none" : id),
                        "&6Click to add/subtract 1",
                        "&6Shift-Click to add/subtract 25").get()
        );
        buttons.put(18, new ItemBuilder(Material.DRAGON_EGG)
                .name("&eShould it be unique in wearing?")
                .lore("&fSet to &e" + (unique ? "yes" : "no"),
                        "&fA trinket being unique means it cannot",
                        "&fbe worn together with other trinkets",
                        "&fwith the same ID",
                        "&6Click to toggle").get()
        );
        return buttons;
    }

    @Override
    public void onButtonPress(InventoryClickEvent inventoryClickEvent, int i) {
        if (i == 12) trinket = Math.max(-1, trinket + (inventoryClickEvent.isRightClick() ? -1 : 1));
        if (i == 16) id = Math.max(-1, id + (inventoryClickEvent.isRightClick() ? -1 : 1) * (inventoryClickEvent.isShiftClick() ? 25 : 1));
        if (i == 18) unique = !unique;
    }

    @Override
    public void processItem(ModifierContext context) {
        TrinketType type = trinket < 0 ? null : TrinketsManager.getTrinketTypes().get(trinket);
        TrinketsManager.setType(context.getItem().getMeta(), type);
        if (id >= 0) TrinketProperties.setTrinketID(context.getItem().getMeta(), id);
        TrinketProperties.setUniqueTrinket(context.getItem().getMeta(), unique);
        EquipmentClass.setEquipmentClass(context.getItem().getMeta(), EquipmentClass.TRINKET);
    }
}
