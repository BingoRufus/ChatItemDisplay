package com.github.bingorufus.chatitemdisplay.displayables;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.Inventory;

public interface DisplayInfo {
    Displayable getDisplayable();

    String loggerMessage();

    void broadcastCommandMessage();

    Inventory getInventory();

    TextComponent getHover();
}
