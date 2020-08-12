package me.BingoRufus.ChatDisplay.Utils;


import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;

public class BungeeCordSender {
	public void sendTextComponent(TextComponent tc) {
		BungeeCord.getInstance().getServers().values().forEach(server -> {
			server.getPlayers().forEach(p -> {
				p.sendMessage(tc);
			});
		});
	}
}
