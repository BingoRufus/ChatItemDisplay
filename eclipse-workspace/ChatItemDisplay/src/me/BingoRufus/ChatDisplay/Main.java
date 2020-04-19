package me.BingoRufus.ChatDisplay;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import me.BingoRufus.ChatDisplay.ListenersAndExecutors.ItemDisplayer;
import me.BingoRufus.ChatDisplay.ListenersAndExecutors.ViewItemExecutor;
import me.BingoRufus.ChatDisplay.Utils.Metrics;
import me.BingoRufus.ChatDisplay.Utils.UpdateChecker;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin {
	ItemDisplayer DisplayListener;

	@Override
	public void onEnable() {

		this.saveDefaultConfig();
		reloadConfigVars();
		this.getCommand("viewitem").setExecutor(new ViewItemExecutor(this));
		new Metrics(this, 7229);
		new UpdateChecker(this, 77177).getLatestVersion(version -> {
			if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
				this.getLogger().info("Plugin is up to date");
			} else {
				this.getLogger().warning("Plugin needs to be updated");
			}
		});

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
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (ItemDisplayer.DisplayedItem.values().contains(p.getOpenInventory().getTopInventory())) {
				p.closeInventory();
			}
			if (ItemDisplayer.DisplayedShulkerBox.values().contains(p.getOpenInventory().getTopInventory())) {
				p.closeInventory();
			}
		}
		DisplayListener = new ItemDisplayer(this);
		Bukkit.getPluginManager().registerEvents(DisplayListener, this);

	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("chatitemreload")) {
			if (sender.hasPermission("ChatItemDisplay.reload") || sender instanceof ConsoleCommandSender) {
				reloadConfigVars();
				sender.sendMessage(ChatColor.GREEN + "ChatItemDisplay Reloaded");
				return true;
			}
			sender.sendMessage(
					ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.missing-permission")));
			return true;
		}
		return false;
	}

}
