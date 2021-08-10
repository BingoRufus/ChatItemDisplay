package io.github.bingorufus.chatitemdisplay.listeners;

import io.github.bingorufus.chatitemdisplay.api.ChatItemDisplayAPI;
import io.github.bingorufus.chatitemdisplay.event.ChatItemDisplayConfigReloadEvent;
import io.github.bingorufus.chatitemdisplay.util.ChatItemConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ReloadListener implements Listener {

    @EventHandler
    public void onReload(ChatItemDisplayConfigReloadEvent e) {
        ChatItemDisplayAPI.getDisplayCooldown().setCooldownTime(ChatItemConfig.COOLDOWN_TIME.getCachedValue());
        ChatItemDisplayAPI.getDisplayedManager().updateExpirationTime();
    }

}
