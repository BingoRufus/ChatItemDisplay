package com.bingorufus.chatitemdisplay.listeners;

import com.bingorufus.chatitemdisplay.api.ChatItemDisplayAPI;
import com.bingorufus.chatitemdisplay.event.ChatItemDisplayConfigReloadEvent;
import com.bingorufus.chatitemdisplay.util.ChatItemConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ReloadListener implements Listener {

    @EventHandler
    public void onReload(ChatItemDisplayConfigReloadEvent e) {
        ChatItemDisplayAPI.getDisplayCooldown().setCooldownTime(ChatItemConfig.COOLDOWN_TIME.getCachedValue());
        ChatItemDisplayAPI.getDisplayedManager().updateExpirationTime();
    }

}
