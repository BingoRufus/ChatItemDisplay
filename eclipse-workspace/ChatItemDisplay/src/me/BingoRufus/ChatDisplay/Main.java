package me.BingoRufus.ChatDisplay;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import me.BingoRufus.ChatDisplay.ListenersAndExecutors.ChatItemReloadExecutor;
import me.BingoRufus.ChatDisplay.ListenersAndExecutors.ItemDisplayer;
import me.BingoRufus.ChatDisplay.ListenersAndExecutors.NewVersionDisplayer;
import me.BingoRufus.ChatDisplay.ListenersAndExecutors.ViewItemExecutor;
import me.BingoRufus.ChatDisplay.Utils.Metrics;
import me.BingoRufus.ChatDisplay.Utils.UpdateChecker;

public class Main extends JavaPlugin {
	ItemDisplayer DisplayListener;
	NewVersionDisplayer NewVer;
	Main plugin;

	/*
	 * TODO: a /version command that shows java version server cversion etc auto
	 * update config
	 */
	@Override
	public void onEnable() {
		plugin = this;

		this.saveDefaultConfig();
		reloadConfigVars();
		this.getCommand("viewitem").setExecutor(new ViewItemExecutor(this));
		this.getCommand("chatitemreload").setExecutor(new ChatItemReloadExecutor(this));
		new Metrics(this, 7229);

	}

	@Override
	public void onDisable() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (ItemDisplayer.DisplayedItem.values().contains(p.getOpenInventory().getTopInventory())) {
				p.closeInventory();
			}
			if (ItemDisplayer.DisplayedShulkerBox.values().contains(p.getOpenInventory().getTopInventory())) {
				p.closeInventory();
			}
		}
	}

	public void reloadConfigVars() {
		this.saveDefaultConfig();
		this.reloadConfig();
		if (DisplayListener != null)
			HandlerList.unregisterAll(DisplayListener);
		if (NewVer != null)
			HandlerList.unregisterAll(NewVer);
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (ItemDisplayer.DisplayedItem.values().contains(p.getOpenInventory().getTopInventory())) {
				p.closeInventory();
			}
			if (ItemDisplayer.DisplayedShulkerBox.values().contains(p.getOpenInventory().getTopInventory())) {
				p.closeInventory();
			}
		}
		if (!plugin.getConfig().getBoolean("disable-update-checking")) {
			new UpdateChecker(plugin, 77177).getLatestVersion(version -> {

				if (UpToDate(this.getDescription().getVersion().split("[.]"), version.split("[.]"))) {
					this.getLogger().info("ChatItemDisplay is up to date");
				} else {

					this.getLogger().warning("ChatItemDisplay is currently running version "
							+ plugin.getDescription().getVersion() + " and can be updated to " + version);
					this.getLogger().warning(
							"Download the newest version at: //https://www.spigotmc.org/resources/chat-item-display.77177/");
					NewVer = new NewVersionDisplayer(this, this.getDescription().getVersion(), version);
					Bukkit.getPluginManager().registerEvents(NewVer, this);
				}
			});
		}

		DisplayListener = new ItemDisplayer(this);
		Bukkit.getPluginManager().registerEvents(DisplayListener, this);
	}

	public Boolean UpToDate(String cur[], String upd[]) {
		Integer[] CurrentVer = new Integer[3];
		Integer[] UpdateVer = new Integer[3];
		int lengthtouse = 0;
		if (cur.length < upd.length)
			lengthtouse = cur.length;
		if (cur.length > upd.length)
			lengthtouse = cur.length;
		if (cur.length == upd.length)
			lengthtouse = cur.length;
		for (int i = 0; i < lengthtouse; i++) {
			CurrentVer[i] = Integer.parseInt(cur[i]);
			UpdateVer[i] = Integer.parseInt(upd[i]);
		}
		if (CurrentVer.equals(UpdateVer)) {
			if (CurrentVer.length < UpdateVer.length)
				return false;
			return true;
		}

		if (CurrentVer[0] < UpdateVer[0])
			return false;
		if (CurrentVer[0] > UpdateVer[0])
			return true;
		// CurrentVer[0] has to be equal to UpdateVer[0]
		if (CurrentVer[1] < UpdateVer[1])
			return false;
		if (CurrentVer[1] > UpdateVer[1])
			return true;
		// Second number is now equal
		if (CurrentVer[2] >= UpdateVer[2])
			return true;

		return false;
	}
}
