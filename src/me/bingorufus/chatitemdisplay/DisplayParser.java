package me.bingorufus.chatitemdisplay;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import me.bingorufus.chatitemdisplay.displayables.DisplayInventory;
import me.bingorufus.chatitemdisplay.displayables.DisplayItem;
import me.bingorufus.chatitemdisplay.util.DisplayPermissionChecker;
import me.bingorufus.chatitemdisplay.util.PlayerInventoryReplicator;
import me.bingorufus.chatitemdisplay.util.StringFormatter;
import me.bingorufus.chatitemdisplay.util.bungee.BungeeCordSender;

public class DisplayParser {
	private ChatItemDisplay m;
	boolean debug;
	String s;
	Player p;
	boolean isMsg;
	boolean displayed;
	boolean cancel = false;

	public DisplayParser(ChatItemDisplay m,String s,Player p, boolean isMsg) {
		this.m = m;
		debug = m.getConfig().getBoolean("debug-mode");
		this.s = s;
		this.p = p;
		this.isMsg = isMsg;
	}

	public boolean cancelMessage() {
		return cancel;
	}

	public String parse() {
		for (String Trigger : m.getConfig().getStringList("triggers.item")) {
			if (s.toUpperCase().contains(Trigger.toUpperCase())) {

		

				DisplayPermissionChecker dpc = new DisplayPermissionChecker(m, p);
				switch (dpc.displayItem()) {
				case DISPLAY:
					DisplayItem dis = new DisplayItem(p.getInventory().getItemInMainHand(), p.getName(),
							p.getDisplayName(), p.getUniqueId(), false);
					m.getDisplayedManager().addDisplayable(p.getName().toUpperCase(), dis);

					if (m.isBungee())
						new BungeeCordSender(m).send(dis, false);




					s = s.replaceAll("(?i)" + Pattern.quote(Trigger),

							m.getDisplayedManager().getDisplay(dis).getInsertion());
					displayed = true;
					break;
				case BLACKLISTED:
					p.sendMessage(new StringFormatter().format(m.getConfig().getString("messages.black-listed-item")));
					cancel = true;
					return s;
				case COOLDOWN:
					Long CooldownRemaining = (m.getConfig().getLong("display-cooldown") * 1000)
							- (System.currentTimeMillis() - m.DisplayCooldowns.get(p.getUniqueId()));
					Double SecondsRemaining = (double) (Math.round(CooldownRemaining.doubleValue() / 100)) / 10;
					p.sendMessage(new StringFormatter().format(m.getConfig().getString("messages.cooldown")
							.replaceAll("%seconds%", "" + SecondsRemaining)));
					cancel = true;
					return s;
				case NO_PERMISSON:
					p.sendMessage(
							new StringFormatter().format(m.getConfig().getString("messages.missing-permission-item")));
					cancel = true;
					return s;

				case NULL_ITEM:
					p.sendMessage(
							new StringFormatter().format(m.getConfig().getString("messages.not-holding-anything")));
					// Do not cancel
					break;
				}

			}
		}
		List<String> invTriggers = m.getConfig().getStringList("triggers.inventory");
		List<String> ecTriggers = m.getConfig().getStringList("triggers.enderchest");
		for (String Trigger : Stream.concat(invTriggers.stream(), ecTriggers.stream()).collect(Collectors.toList())) {
			if (s.toUpperCase().contains(Trigger.toUpperCase())) {
				DisplayInventory dis;

				invTriggers.replaceAll(String::toUpperCase); // Turns all the triggers to UPPERCASE
				if (invTriggers.contains(Trigger.toUpperCase())) {
					if (debug)
						Bukkit.getLogger()
								.info(p.getName() + "'s message contains an inventory / enderchest display trigger");
					if (!p.hasPermission("chatitemdisplay.display.inventory")) {
						p.sendMessage(new StringFormatter()
								.format(m.getConfig().getString("messages.missing-permission-inventory")));
						cancel = true;

						Bukkit.getLogger().info(p.getName() + "does not have permission to display their inventory");
						return s;

					}
					PlayerInventoryReplicator.InventoryData data = new PlayerInventoryReplicator(m)
							.replicateInventory(p);
					dis = new DisplayInventory(data.getInventory(), data.getTitle(), p.getName(), p.getDisplayName(),
							p.getUniqueId(), false);
					m.getDisplayedManager().addDisplayable(p.getName().toUpperCase(), dis);

				} else {
					if (debug)
						Bukkit.getLogger().info(p.getName() + "'s message contains an enderchest display trigger");
					if (!p.hasPermission("chatitemdisplay.display.enderchest")) {
						p.sendMessage(new StringFormatter()
								.format(m.getConfig().getString("messages.missing-permission-enderchest")));
						cancel = true;

						Bukkit.getLogger().info(p.getName() + "does not have permission to display their Ender Chest");
						return s;
					}
					String title = new StringFormatter()
							.format(m.getConfig().getString("display-messages.displayed-enderchest-title")
									.replaceAll("%player%",
											m.getConfig().getBoolean("use-nicks-in-gui")
													? m.getConfig().getBoolean("strip-nick-colors-gui")
															? ChatColor.stripColor(p.getDisplayName())
															: p.getDisplayName()
													: p.getName()));
					Inventory inv = Bukkit.createInventory(p, InventoryType.ENDER_CHEST, title);

					inv.setContents(p.getEnderChest().getContents());
					dis = new DisplayInventory(inv, title, p.getName(), p.getDisplayName(), p.getUniqueId(), false);
					m.getDisplayedManager().addDisplayable(p.getName().toUpperCase(), dis);

					displayed = true;
				}

				if (m.isBungee())
					new BungeeCordSender(m).send(dis, true);


				s = s.replaceAll("(?i)" + Pattern.quote(Trigger),

						m.getDisplayedManager().getDisplay(dis).getInsertion());



			}

		}

		if (displayed && !p.hasPermission("chatitemdisplay.cooldownbypass")) {
			m.DisplayCooldowns.put(p.getUniqueId(), System.currentTimeMillis());
		}
		return s;

	}

	public String getPlayer() {
		return null;
	}

}
