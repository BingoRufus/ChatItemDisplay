package me.bingorufus.chatitemdisplay.util;

import org.bukkit.Bukkit;

import net.md_5.bungee.api.chat.TextComponent;

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
