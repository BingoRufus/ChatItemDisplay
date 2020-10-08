package me.bingorufus.chatitemdisplay.util.display;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

public class DisplayableBroadcaster {
    //new Display(chatItemDisplay, p.getInventory().getItemInMainHand(), p.getName(), p.getDisplayName())
    public void broadcast(TextComponent... tc) {
        TextComponent msg = new TextComponent();

        for (TextComponent text : tc) {
            msg.addExtra(text);
        }

        Bukkit.spigot().broadcast(msg);

    }
}
