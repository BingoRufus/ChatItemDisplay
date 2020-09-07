package me.bingorufus.chatitemdisplay.executors.display;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.displayables.DisplayInventory;
import me.bingorufus.chatitemdisplay.displayables.DisplayInventoryInfo;
import me.bingorufus.chatitemdisplay.util.PlayerInventoryReplicator;
import me.bingorufus.chatitemdisplay.util.StringFormatter;
import me.bingorufus.chatitemdisplay.util.bungee.BungeeCordSender;
import net.md_5.bungee.api.ChatColor;

public class DisplayInventoryExecutor implements CommandExecutor {
	ChatItemDisplay m;

	public DisplayInventoryExecutor(ChatItemDisplay m) {
		this.m = m;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You can't do that!");
				return true;
			}

			Player p = (Player) sender;
		if (!p.hasPermission("chatitemdisplay.display.inventory")) {
			p.sendMessage(
					new StringFormatter().format(m.getConfig().getString("messages.missing-permission-inventory")));
			return true;
		}
		PlayerInventoryReplicator.InventoryData data = new PlayerInventoryReplicator(m).replicateInventory(p);

		DisplayInventory d = new DisplayInventory(data.getInventory(), data.getTitle(), p.getName(),
					p.getDisplayName(), p.getUniqueId(), false);

		m.getDisplayedManager().addDisplayable(p.getName().toUpperCase(), d);
			new BungeeCordSender(m).send(d, true);
			new DisplayInventoryInfo(m, d).cmdMsg();

			return true;

		
	}
}
