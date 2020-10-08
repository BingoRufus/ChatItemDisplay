package me.bingorufus.chatitemdisplay.displayables;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.Inventory;

public interface DisplayInfo {
    Displayable getDisplayable();

    String loggerMessage();

    void cmdMsg();

    Inventory getInventory();

    TextComponent getHover();
}
