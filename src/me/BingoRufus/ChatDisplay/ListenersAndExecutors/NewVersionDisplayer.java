package me.BingoRufus.ChatDisplay.ListenersAndExecutors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.BingoRufus.ChatDisplay.Main;
import net.md_5.bungee.api.ChatColor;

public class NewVersionDisplayer implements Listener {
	private String current;
	private String update;
	private Main main;

	public NewVersionDisplayer(Main m, String CurrentVersion, String NewVersion) {
		this.current = CurrentVersion;
		this.update = NewVersion;
		this.main = m;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (p.hasPermission("ChatItemDisplay.reload")) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(this.main, new Runnable() {
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