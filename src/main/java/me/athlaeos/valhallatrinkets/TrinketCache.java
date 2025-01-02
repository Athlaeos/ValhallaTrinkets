package me.athlaeos.valhallatrinkets;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class TrinketCache {
    private static final Map<UUID, CacheEntry> cache = new HashMap<>();

    public static Map<Integer, TrinketItem> getOrCache(Player p){
        CacheEntry entry = cache.get(p.getUniqueId());
        if (entry == null || entry.cachedAt + 10000 < System.currentTimeMillis()) {
            entry = new CacheEntry(p, TrinketsManager.getTrinketInventory(p));
            cache.put(p.getUniqueId(), entry);
        }
        return entry.trinkets;
    }

    public static void reset(LivingEntity p){
        cache.remove(p.getUniqueId());
    }

    private static class CacheEntry{
        private final UUID player;
        private final Map<Integer, TrinketItem> trinkets;
        private final long cachedAt;

        public CacheEntry(Player player, Map<Integer, TrinketItem> trinkets){
            this.player = player.getUniqueId();
            this.trinkets = trinkets;
            this.cachedAt = System.currentTimeMillis();
        }

        public UUID getPlayer() {
            return player;
        }

        public long getCachedAt() {
            return cachedAt;
        }

        public Map<Integer, TrinketItem> getTrinkets() {
            return trinkets;
        }
        public List<TrinketItem> getActiveTrinkets(Player owner){
            return trinkets.values().stream().filter(i -> {
                if (i.getSlot() == null) return true;
                if (i.getSlot().getPermissionRequired() == null) return true;
                return owner.hasPermission(i.getSlot().getPermissionRequired());
            }).collect(Collectors.toList());
        }
    }
}
