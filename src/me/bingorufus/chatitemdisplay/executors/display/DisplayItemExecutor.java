package me.bingorufus.chatitemdisplay.executors.display;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.displayables.DisplayItem;
import me.bingorufus.chatitemdisplay.displayables.DisplayItemInfo;
import me.bingorufus.chatitemdisplay.util.DisplayPermissionChecker;
import me.bingorufus.chatitemdisplay.util.StringFormatter;
import me.bingorufus.chatitemdisplay.util.bungee.BungeeCordSender;
import net.md_5.bungee.api.ChatColor;

public class DisplayItemExecutor implements CommandExecutor {
	Boolean debug;
	ChatItemDisplay m;

	public DisplayItemExecutor(ChatItemDisplay m) {
		debug = m.getConfig().getBoolean("debug-mode");
		this.m = m;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "You can't do that!");
				return true;
			}

			Player p = (Player) sender;
		switch (new DisplayPermissionChecker(m, p).displayItem()) {
			case DISPLAY:
				DisplayItem d = new DisplayItem(p.getInventory().getItemInMainHand(), p.getName(), p.getDisplayName(),
						p.getUniqueId(),
						false);
			m.getDisplayedManager().addDisplayable(p.getName().toUpperCase(), d);
			new BungeeCordSender(m).send(d, true);
			new DisplayItemInfo(m, d).cmdMsg();
				break;
			case BLACKLISTED:
				p.sendMessage(new StringFormatter()
					.format(m.getConfig().getString("messages.black-listed-item")));
				break;
			case COOLDOWN:
			Long CooldownRemaining = (m.getConfig().getLong("display-cooldown") * 1000)
						- (System.currentTimeMillis()
							- m.DisplayCooldowns.get(p.getUniqueId()));
				Double SecondsRemaining = (double) (Math.round(CooldownRemaining.doubleValue() / 100)) / 10;
			p.sendMessage(new StringFormatter().format(m.getConfig()
						.getString("messages.cooldown").replaceAll("%seconds%", "" + SecondsRemaining)));
				break;
			case NO_PERMISSON:
				p.sendMessage(new StringFormatter()
					.format(m.getConfig().getString("messages.missing-permission-to-display")));
				break;
			case NULL_ITEM:
				p.sendMessage(new StringFormatter()
					.format(m.getConfig().getString("messages.not-holding-anything")));
				break;
			}

			return true;


	}

}
