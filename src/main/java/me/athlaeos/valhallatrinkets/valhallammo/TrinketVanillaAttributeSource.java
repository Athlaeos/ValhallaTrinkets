package me.athlaeos.valhallatrinkets.valhallammo;

import me.athlaeos.valhallammo.playerstats.AccumulativeStatSource;
import me.athlaeos.valhallatrinkets.TrinketCache;
import me.athlaeos.valhallatrinkets.TrinketItem;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;

public class TrinketVanillaAttributeSource implements AccumulativeStatSource {
    private final Attribute attribute;
    private final AttributeModifier.Operation validOperation;

    public TrinketVanillaAttributeSource(Attribute attribute, AttributeModifier.Operation validOperation){
        this.attribute = attribute;
        this.validOperation = validOperation;
    }

    @Override
    public double fetch(Entity statPossessor, boolean use) {
        if (statPossessor instanceof Player){
            Player p = (Player) statPossessor;
            Map<Integer, TrinketItem> trinketInventory = TrinketCache.getOrCache(p);
            double value = 0;
            for (TrinketItem item : trinketInventory.values()){
                Collection<AttributeModifier> modifiers = item.getMeta().getAttributeModifiers(attribute);
                if (modifiers == null) continue;
                for (AttributeModifier modifier : modifiers){
                    if (modifier.getOperation() == validOperation) value += modifier.getAmount();
                }
            }
            return value;
        }
        return 0;
    }
}
