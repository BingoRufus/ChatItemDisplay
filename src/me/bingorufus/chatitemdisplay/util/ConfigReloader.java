package me.bingorufus.chatitemdisplay.util;

import java.io.File;
import java.io.FileNotFoundException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.listeners.NewVersionDisplayer;
import me.bingorufus.chatitemdisplay.util.bungee.BungeeCordReceiver;
import me.bingorufus.chatitemdisplay.util.updater.UpdateChecker;
import me.bingorufus.chatitemdisplay.util.updater.UpdateDownloader;

public class ConfigReloader {
	private ChatItemDisplay m;

	public ConfigReloader(ChatItemDisplay m) {
		this.m = m;
	}


	public void reload() {
		if (m.in != null) {
			Bukkit.getServer().getMessenger().unregisterIncomingPluginChannel(m, "chatitemdisplay:in", m.in);
		}
		m.in = new BungeeCordReceiver(m);
		Bukkit.getServer().getMessenger().registerIncomingPluginChannel(m, "chatitemdisplay:in", m.in);
		Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(m, "chatitemdisplay:out");


		m.saveDefaultConfig();
		m.reloadConfig();
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (m.invs.keySet().contains(p.getOpenInventory().getTopInventory())) {
				p.closeInventory();
			}

		}
		m.reloadListeners();

		update();

	}



	private void update() {
		if (!m.getConfig().getBoolean("disable-update-checking")) {
			String checkerError = new UpdateChecker(77177).getLatestVersion(version -> {
				VersionComparer.Status s = new VersionComparer().isRecent(m.getDescription().getVersion(), version);

				if (!s.equals(VersionComparer.Status.BEHIND)) {
					m.getLogger().info("ChatItemDisplay is up to date");
				} else {

					m.getLogger().warning("ChatItemDisplay is currently running version "
							+ m.getDescription().getVersion() + " and can be updated to " + version);
					if (m.getConfig().getBoolean("auto-update")) {
						Bukkit.getScheduler().runTaskAsynchronously(m, () -> {
							try {
								UpdateDownloader updater = new UpdateDownloader(version);
								String downloadMsg = updater
										.download(new File("plugins/ChatItemDisplay " + version + ".jar"));
								if (downloadMsg != null) {
									Bukkit.getLogger().severe(downloadMsg);
									return;
								}

								updater.deletePlugin(this);
								Bukkit.getLogger().info(
										"The newest version of ChatItemDisplay has been downloaded automatically, it will be loaded upon the next startup");
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}

						});
						return;
					}
					m.getLogger().warning(
							"Download the newest version at: //https://www.spigotmc.org/resources/chat-item-display.77177/");
					m.getLogger().warning("or enable auto-update in your config.yml");

					m.NewVer = new NewVersionDisplayer(m, m.getDescription().getVersion(), version);
					Bukkit.getPluginManager().registerEvents(m.NewVer, m);
				}
			});
			if (checkerError != null) {
				Bukkit.getLogger().warning(checkerError);
			}
		}
	}

}
