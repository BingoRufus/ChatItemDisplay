package me.bingorufus.chatitemdisplay.utils;

import org.bukkit.Bukkit;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.Display;
import me.bingorufus.chatitemdisplay.utils.bungee.BungeeCordSender;
import net.md_5.bungee.api.chat.TextComponent;

public class MessageBroadcaster {
//new Display(chatItemDisplay, p.getInventory().getItemInMainHand(), p.getName(), p.getDisplayName())
	public void broadcast(ChatItemDisplay m, Display dis, boolean isCmd, boolean fromBungee, TextComponent... tc) {
		TextComponent msg = new TextComponent();

		for (TextComponent text : tc) {
			msg.addExtra(text);
		}

		if (!fromBungee && m.isBungee()) {
			new BungeeCordSender(m).sendTextComponent(msg, dis, isCmd);
		}
		Bukkit.spigot().broadcast(msg);

	}
}
