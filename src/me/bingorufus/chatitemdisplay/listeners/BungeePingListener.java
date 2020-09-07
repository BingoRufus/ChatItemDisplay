package me.bingorufus.chatitemdisplay.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.util.bungee.BungeeCordSender;

public class BungeePingListener implements Listener {
	boolean hasPinged = false;
	ChatItemDisplay m;

	public BungeePingListener(ChatItemDisplay m) {
		this.m = m;
	}

	public void onJoin(PlayerJoinEvent e) {
		if (!hasPinged) {
			new BungeeCordSender(m).pingBungee();
			hasPinged = true;

		}
	}

	public void reload() {
		this.hasPinged = false;
		if (Bukkit.getServer().getOnlinePlayers().size() > 0) {
			new BungeeCordSender(m).pingBungee();
			hasPinged = true;
		}
	}

}
