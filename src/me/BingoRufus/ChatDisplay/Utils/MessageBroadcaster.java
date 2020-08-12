package me.BingoRufus.ChatDisplay.Utils;

import org.bukkit.Bukkit;
import org.spigotmc.SpigotConfig;

import net.md_5.bungee.api.chat.TextComponent;

public class MessageBroadcaster {

	public void broadcast(TextComponent... tc) {
		TextComponent msg = new TextComponent();
		for(TextComponent text : tc ) {
			msg.addExtra(text);
		}
		if(!Bukkit.getServer().getOnlineMode() && SpigotConfig.bungee) {
			new BungeeCordSender().sendTextComponent(msg);
			return;
		}
		Bukkit.getOnlinePlayers().forEach(p -> {
			p.spigot().sendMessage(msg);
		});

	}
}
