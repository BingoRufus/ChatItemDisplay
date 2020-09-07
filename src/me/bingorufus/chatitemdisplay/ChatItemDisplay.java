package me.bingorufus.chatitemdisplay;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.bingorufus.chatitemdisplay.executors.ChatItemReloadExecutor;
import me.bingorufus.chatitemdisplay.executors.display.DisplayEnderChestExecutor;
import me.bingorufus.chatitemdisplay.executors.display.DisplayInventoryExecutor;
import me.bingorufus.chatitemdisplay.executors.display.DisplayItemExecutor;
import me.bingorufus.chatitemdisplay.executors.display.ViewItemExecutor;
import me.bingorufus.chatitemdisplay.listeners.BungeePingListener;
import me.bingorufus.chatitemdisplay.listeners.ChatDisplayListener;
import me.bingorufus.chatitemdisplay.listeners.MapViewerListener;
import me.bingorufus.chatitemdisplay.listeners.NewVersionDisplayer;
import me.bingorufus.chatitemdisplay.util.LoggerFilter;
import me.bingorufus.chatitemdisplay.util.bungee.BungeeCordReceiver;
import me.bingorufus.chatitemdisplay.util.bungee.BungeeCordSender;
import me.bingorufus.chatitemdisplay.util.loaders.DiscordSRVRegister;
import me.bingorufus.chatitemdisplay.util.loaders.Metrics;
import me.bingorufus.chatitemdisplay.util.loaders.ProtocolLibRegister;

public class ChatItemDisplay extends JavaPlugin {
	ChatDisplayListener DisplayListener;
	public NewVersionDisplayer NewVer;
	ProtocolLibRegister pl;
	public BungeeCordReceiver in;
	BungeeCordSender out;
	DiscordSRVRegister discordReg;
	private DisplayedManager dm;

	BungeePingListener bpl;

	public Map<UUID, Long> DisplayCooldowns = new HashMap<UUID, Long>();


	public HashMap<Player, ItemStack> viewingMap = new HashMap<Player, ItemStack>();

	public HashMap<Inventory, UUID> invs = new HashMap<Inventory, UUID>();


	public boolean hasProtocollib = false;
	public Boolean useOldFormat = false;
	@SuppressWarnings("unused")
	private boolean isBungee = false;

	public Long pingTime;

	/*
	 * TODO: a /version command that shows java version server cversion etc - auto
	 * update config - Command Block GUI
	 */
	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		this.dm = new DisplayedManager();
		this.getCommand("viewitem").setExecutor(new ViewItemExecutor(this));
		this.getCommand("chatitemreload").setExecutor(new ChatItemReloadExecutor(this));
		Metrics metrics = new Metrics(this, 7229);
		this.getCommand("displayitem").setExecutor(new DisplayItemExecutor(this));
		this.getCommand("displayinv").setExecutor(new DisplayInventoryExecutor(this));
		this.getCommand("displayenderchest").setExecutor(new DisplayEnderChestExecutor(this));

		reloadFilters();
		reloadListeners();
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "chatitemreload");
		}, 3L);
		metrics.addCustomChart(new Metrics.SimplePie("old_display_messages", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return getConfig().getString("use-old-format");
			}
		}));

	}

	@Override
	public void onDisable() {
		if (discordReg != null) {
			discordReg.unregister();
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (viewingMap.containsKey(p)) {
				p.getInventory().setItemInMainHand(viewingMap.get(p));
			}
			if (invs.keySet().contains(p.getOpenInventory().getTopInventory())) {
				p.closeInventory();
			}

		}
	}

	public DisplayedManager getDisplayedManager() {
		return dm;
	}

	private void reloadFilters() {
		Logger logger = (Logger) LogManager.getRootLogger();
		Iterator<Filter> filters = logger.getFilters();
		while (filters.hasNext()) { // Prevents duplicate loggers
			Filter f = filters.next();

			if (f.getClass().getName().equals(LoggerFilter.class.getName())) {// Check if the filter is one of mine
				if (!f.isStopped())
				f.stop();
			}

		}
		logger.addFilter(new LoggerFilter(this));
	}

	public void reloadListeners() {
		Bukkit.getPluginManager().registerEvents(new MapViewerListener(this), this);

		useOldFormat = this.getConfig().getBoolean("use-old-format")
				|| Bukkit.getServer().getPluginManager().getPlugin("ProtocolLib") == null;
		if (!useOldFormat) {
			if (pl == null)
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
		if (bpl != null)
			bpl.reload();
		else {
			bpl = new BungeePingListener(this);
			Bukkit.getPluginManager().registerEvents(bpl, this);
		}
		if (discordReg != null)
			discordReg.unregister();
		if (Bukkit.getPluginManager().getPlugin("DiscordSRV") != null) {
			if (discordReg == null) {
				discordReg = new DiscordSRVRegister(this);
			}
			discordReg.register();
		}
		DisplayListener = new ChatDisplayListener(this);
		Bukkit.getPluginManager().registerEvents(DisplayListener, this);

	}


	public boolean isBungee() {
		return true;
	}

	public void setBungee(boolean b) {
		this.isBungee = b;
	}

}
