package com.bingorufus.chatitemdisplay;

import com.bingorufus.chatitemdisplay.api.display.Displayable;
import com.bingorufus.chatitemdisplay.util.ChatItemConfig;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class DisplayedManager {
    private long expirationTime = ChatItemConfig.EXPIRATION_TIME.getCachedValue();

    private Cache<UUID, Display> displayId = CacheBuilder.newBuilder().expireAfterWrite(expirationTime, TimeUnit.SECONDS).build();

    private Cache<String, UUID> mostRecent = CacheBuilder.newBuilder().expireAfterWrite(expirationTime, TimeUnit.SECONDS).build(); // <Player,Id>

    private final List<Inventory> chatItemDisplayInventories = new LinkedList<>();

    public DisplayedManager() {
        Bukkit.getPluginManager().registerEvent(InventoryCloseEvent.class, new Listener() {
        }, EventPriority.NORMAL, (listener, event) -> {
            InventoryCloseEvent ice = (InventoryCloseEvent) event;
            if (!chatItemDisplayInventories.contains(ice.getInventory())) return;
            List<HumanEntity> viewers = new LinkedList<>(ice.getViewers());
            Bukkit.getScheduler().scheduleSyncDelayedTask(ChatItemDisplay.getInstance(), () -> viewers.stream().filter(p -> !p.equals(ice.getPlayer())).forEach(HumanEntity::closeInventory), 1);
            chatItemDisplayInventories.remove(((InventoryCloseEvent) event).getView().getTopInventory());
        }, ChatItemDisplay.getInstance());

    }

    public List<Inventory> getChatItemDisplayInventories() {
        return new LinkedList<>(chatItemDisplayInventories);
    }

    public void addInventory(Inventory inventory) {
        chatItemDisplayInventories.add(inventory);
    }

    public void addDisplayable(Displayable display) {
        Display dis = new Display(display, UUID.randomUUID());
        displayId.put(dis.getId(), dis);
        mostRecent.put(display.getDisplayer().getRegularName().toUpperCase(), dis.getId());
    }

    public void addDisplay(Display d) {
        displayId.put(d.getId(), d);
        mostRecent.put(d.getPlayer().getRegularName().toUpperCase(), d.getId());
    }

    public Display getDisplayed(UUID id) {
        return displayId.asMap().get(id);
    }

    @Nullable
    public Display getMostRecent(@Nullable String player) {
        if (player == null) return null;
        if (!mostRecent.asMap().containsKey(player.toUpperCase())) {
            return getMostRecent(mostRecent.asMap().keySet().stream().filter(name -> name.toUpperCase().startsWith(player.toUpperCase())).sorted().findFirst().orElse(null));

        }
        UUID recent = mostRecent.asMap().get(player.toUpperCase());

        return displayId.asMap().get(recent);
    }

    @Nullable
    public Display getDisplay(Displayable dis) {
        return displayId.asMap().values().stream().filter(display -> display.getDisplayable().equals(dis)).findFirst().orElse(null);

    }

    public void forEach(Consumer<Display> displayConsumer) {
        for (Display d : displayId.asMap().values()) {
            displayConsumer.accept(d);
        }
    }

    public void updateExpirationTime() {
        long newTime = ChatItemConfig.EXPIRATION_TIME.getCachedValue();
        if (expirationTime == newTime) return;
        expirationTime = newTime;
        Bukkit.getScheduler().runTaskAsynchronously(ChatItemDisplay.getInstance(), () -> {
            displayId = copyToCache(displayId);
            mostRecent = copyToCache(mostRecent);

        });
    }

    public Cache copyToCache(Cache<?, ?> from) {
        Cache<Object, Object> newCache = CacheBuilder.newBuilder().expireAfterWrite(expirationTime, TimeUnit.SECONDS).build();
        from.asMap().forEach(newCache::put);
        from.asMap().clear();
        return newCache;
    }

}
