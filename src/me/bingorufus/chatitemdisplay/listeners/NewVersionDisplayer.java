package me.bingorufus.chatitemdisplay.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import net.md_5.bungee.api.ChatColor;

public class NewVersionDisplayer implements Listener {
	private String current;
	private String update;
	private ChatItemDisplay chatItemDisplay;

	public NewVersionDisplayer(ChatItemDisplay m, String CurrentVersion, String NewVersion) {
		this.current = CurrentVersion;
		this.update = NewVersion;
		this.chatItemDisplay = m;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (p.hasPermission("ChatItemDisplay.reload")) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(this.chatItemDisplay, new Runnable() {
				public void run() {
					p.sendMessage(ChatColor.GREEN + "ChatItemDisplay is currently running " + ChatColor.BLUE + "v."
							+ ChatColor.WHITE + "" + ChatColor.BOLD + current + ChatColor.GREEN
							+ " and should be updated to " + ChatColor.BLUE + "v." + ChatColor.WHITE + ""
							+ ChatColor.BOLD + update);
				}
			}, 3);

		}
	}
}