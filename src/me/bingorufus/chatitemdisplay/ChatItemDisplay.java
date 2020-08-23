package me.bingorufus.chatitemdisplay;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.bingorufus.chatitemdisplay.executors.ChatItemReloadExecutor;
import me.bingorufus.chatitemdisplay.executors.DisplayCommandExecutor;
import me.bingorufus.chatitemdisplay.executors.ViewItemExecutor;
import me.bingorufus.chatitemdisplay.listeners.ChatDisplayListener;
import me.bingorufus.chatitemdisplay.listeners.MapViewerListener;
import me.bingorufus.chatitemdisplay.listeners.NewVersionDisplayer;
import me.bingorufus.chatitemdisplay.utils.VersionComparer;
import me.bingorufus.chatitemdisplay.utils.bungee.BungeeCordReceiver;
import me.bingorufus.chatitemdisplay.utils.bungee.BungeeCordSender;
import me.bingorufus.chatitemdisplay.utils.loaders.Metrics;
import me.bingorufus.chatitemdisplay.utils.loaders.ProtocolLibRegister;
import me.bingorufus.chatitemdisplay.utils.updater.UpdateChecker;
import me.bingorufus.chatitemdisplay.utils.updater.UpdateDownloader;

public class ChatItemDisplay extends JavaPlugin {
	ChatDisplayListener DisplayListener;
	NewVersionDisplayer NewVer;
	ProtocolLibRegister pl;

	BungeeCordReceiver in;
	BungeeCordSender out;

	public HashMap<String, Inventory> displaying = new HashMap<String, Inventory>();
	public HashMap<String, Display> displays = new HashMap<String, Display>();
	public HashMap<Player, ItemStack> viewingMap = new HashMap<Player, ItemStack>();

	public HashMap<Inventory, UUID> invs = new HashMap<Inventory, UUID>();

	public boolean hasProtocollib = false;
	public Boolean useOldFormat = false;

	/*
	 * TODO: a /version command that shows java version server cversion etc auto
	 * update config
	 */
	@Override
	public void onEnable() {


		this.saveDefaultConfig();
		reloadConfigVars();
		this.getCommand("viewitem").setExecutor(new ViewItemExecutor(this));
		this.getCommand("chatitemreload").setExecutor(new ChatItemReloadExecutor(this));
		Metrics metrics = new Metrics(this, 7229);
		this.getCommand("displayitem").setExecutor(new DisplayCommandExecutor(this));
		metrics.addCustomChart(new Metrics.SimplePie("old_display_messages", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return getConfig().getString("use-old-format");
			}
		}));

	}

	@Override
	public void onDisable() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (viewingMap.containsKey(p)) {
				p.getInventory().setItemInMainHand(viewingMap.get(p));
			}
			if (invs.keySet().contains(p.getOpenInventory().getTopInventory())) {
				p.closeInventory();
			}

		}
	}

	public void reloadConfigVars() {
		if (in != null) {
			getServer().getMessenger().unregisterIncomingPluginChannel(this, "chatitemdisplay:in", in);
		}

		if (isBungee()) {

			in = new BungeeCordReceiver(this);
			getServer().getMessenger().registerIncomingPluginChannel(this, "chatitemdisplay:in", in);
			getServer().getMessenger().registerOutgoingPluginChannel(this, "chatitemdisplay:out");

		}

		this.saveDefaultConfig();
		this.reloadConfig();
		Bukkit.getPluginManager().registerEvents(new MapViewerListener(this), this);

		useOldFormat = this.getConfig().getBoolean("use-old-format")
				|| Bukkit.getServer().getPluginManager().getPlugin("ProtocolLib") == null;
		if (!useOldFormat) {
			pl = new ProtocolLibRegister(this);
			pl.registerPacketListener();
			hasProtocollib = true;
		} else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "" + ChatColor.BOLD
					+ "[ChatItemDisplay] In Chat Item Displaying has Been Disabled Because This Server Does Not Have ProtocolLib Or Has Been Disabled in The config.yml");
			hasProtocollib = false;
		}
		if (DisplayListener != null)
			HandlerList.unregisterAll(DisplayListener);
		if (NewVer != null)
			HandlerList.unregisterAll(NewVer);
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (invs.keySet().contains(p.getOpenInventory().getTopInventory())) {
				p.closeInventory();
			}

		}
		if (!getConfig().getBoolean("disable-update-checking")) {
			String checkerError = new UpdateChecker(77177).getLatestVersion(version -> {

				VersionComparer.Status s = new VersionComparer().isRecent(this.getDescription().getVersion(), version);
				if (!s.equals(VersionComparer.Status.BEHIND)) {
					this.getLogger().info("ChatItemDisplay is up to date");
				} else {

					this.getLogger().warning("ChatItemDisplay is currently running version "
							+ getDescription().getVersion() + " and can be updated to " + version);
					if (getConfig().getBoolean("auto-update")) {
						Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
							try {
								UpdateDownloader updater = new UpdateDownloader(version);
								String downloadMsg = updater
										.download(new FileOutputStream("plugins/ChatItemDisplay " + version + ".jar"));
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
					this.getLogger().warning(
							"Download the newest version at: //https://www.spigotmc.org/resources/chat-item-display.77177/");
					this.getLogger().warning("or enable auto-update in your config.yml");

					NewVer = new NewVersionDisplayer(this, this.getDescription().getVersion(), version);
					Bukkit.getPluginManager().registerEvents(NewVer, this);
				}
			});
			if (checkerError != null) {
				Bukkit.getLogger().warning(checkerError);
			}
		}

		DisplayListener = new ChatDisplayListener(this);
		Bukkit.getPluginManager().registerEvents(DisplayListener, this);
	}



	public boolean isBungee() {
		// we check if the server is Spigot/Paper (because of the spigot.yml file)
		if (!getServer().getVersion().contains("Spigot") && !getServer().getVersion().contains("Paper"))
			return false;

		if (getServer().spigot().getConfig().getConfigurationSection("settings").getBoolean("settings.bungeecord"))
			return false;
		return true;

	}

}
