package me.bingorufus.chatitemdisplay.listeners;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.displayables.DisplayInfo;
import me.bingorufus.chatitemdisplay.displayables.DisplayInventory;
import me.bingorufus.chatitemdisplay.displayables.DisplayInventoryInfo;
import me.bingorufus.chatitemdisplay.displayables.DisplayItem;
import me.bingorufus.chatitemdisplay.displayables.DisplayItemInfo;
import me.bingorufus.chatitemdisplay.displayables.Displayable;
import me.bingorufus.chatitemdisplay.util.DisplayPermissionChecker;
import me.bingorufus.chatitemdisplay.util.PlayerInventoryReplicator;
import me.bingorufus.chatitemdisplay.util.StringFormatter;
import me.bingorufus.chatitemdisplay.util.bungee.BungeeCordSender;

public class ChatDisplayListener implements Listener {

	char bell = '\u0007';


	String MsgName;
	String GUIName;
	ChatItemDisplay chatItemDisplay;
	boolean debug;
	String Version;
	boolean displayed;

	public ChatDisplayListener(ChatItemDisplay m) {
		m.reloadConfig();
		chatItemDisplay = m;
		debug = chatItemDisplay.getConfig().getBoolean("debug-mode");
		Version = Bukkit.getServer().getVersion().substring(Bukkit.getServer().getVersion().indexOf("(MC: ") + 5,
				Bukkit.getServer().getVersion().indexOf(")"));
		Bukkit.getPluginManager().registerEvents(new InventoryClick(chatItemDisplay, Version), chatItemDisplay);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();

		displayed = false;
		if (debug)
			Bukkit.getLogger().info(p.getName() + " sent a message");


		for (String Trigger : chatItemDisplay.getConfig().getStringList("triggers.item")) {
			if (e.getMessage().toUpperCase().contains(Trigger.toUpperCase())) {

				if (debug)
					Bukkit.getLogger().info(p.getName() + "'s message contains an item display trigger");

				DisplayPermissionChecker dpc = new DisplayPermissionChecker(chatItemDisplay, p);
				switch (dpc.displayItem()) {
				case DISPLAY:

					chatItemDisplay.displayed.put(p.getName().toUpperCase(),
							new DisplayItem(p.getInventory().getItemInMainHand(), p.getName(), p.getDisplayName(),
									p.getUniqueId(), false));
					if (chatItemDisplay.isBungee())
					new BungeeCordSender(chatItemDisplay).send(chatItemDisplay.displayed.get(p.getName().toUpperCase()),
							false);
					if (chatItemDisplay.useOldFormat) {

						e.setCancelled(true);
						Bukkit.getScheduler().runTask(chatItemDisplay, () -> {
							String newmsg = e.getMessage().replaceFirst("(?i)" + Pattern.quote(Trigger),
									bell + "split");

							String[] parts = newmsg.split(bell + "split");
							String first = newmsg.indexOf(bell + "split") > 0 ? parts[0] : "";
							String last = parts.length == 0 ? ""
									: parts.length == 2 ? parts[1] : first.equals("") ? parts[0] : "";
							e.getPlayer().chat(first.trim());
							DisplayInfo di = null;
							Displayable d = chatItemDisplay.displayed.get(e.getPlayer().getName().toUpperCase());
							if (d instanceof DisplayItem)
								di = new DisplayItemInfo(chatItemDisplay, (DisplayItem) d);
							if (d instanceof DisplayInventory)
								di = new DisplayInventoryInfo(chatItemDisplay, (DisplayInventory) d);
							di.cmdMsg();
							e.getPlayer().chat(last.trim());
							displayed = true;

						});

						return;

					}



					String newmsg = e.getMessage().replaceAll("(?i)" + Pattern.quote(Trigger),

							bell + "cid" + p.getName() + bell);

					e.setMessage(newmsg);
					displayed = true;
					break;
				case BLACKLISTED:
					p.sendMessage(new StringFormatter()
							.format(chatItemDisplay.getConfig().getString("messages.black-listed-item")));
					e.setCancelled(true);
					break;
				case COOLDOWN:
					Long CooldownRemaining = (chatItemDisplay.getConfig().getLong("display-cooldown") * 1000)
							- (System.currentTimeMillis()
									- chatItemDisplay.DisplayCooldowns.get(p.getUniqueId()));
					Double SecondsRemaining = (double) (Math.round(CooldownRemaining.doubleValue() / 100)) / 10;
					p.sendMessage(new StringFormatter().format(chatItemDisplay.getConfig()
							.getString("messages.cooldown").replaceAll("%seconds%", "" + SecondsRemaining)));
					e.setCancelled(true);
					break;
				case NO_PERMISSON:
					p.sendMessage(new StringFormatter()
							.format(chatItemDisplay.getConfig().getString("messages.missing-permission-item")));

					e.setCancelled(true);
					break;
				case NULL_ITEM:
					p.sendMessage(new StringFormatter()
							.format(chatItemDisplay.getConfig().getString("messages.not-holding-anything")));
					// Do not cancel
					break;
				}

			}
		}
		List<String> invTriggers = chatItemDisplay.getConfig().getStringList("triggers.inventory");
		List<String> ecTriggers = chatItemDisplay.getConfig().getStringList("triggers.enderchest");
		for (String Trigger : Stream.concat(invTriggers.stream(), ecTriggers.stream()).collect(Collectors.toList())) {
			if (e.getMessage().toUpperCase().contains(Trigger.toUpperCase())) {
				p = e.getPlayer();

				invTriggers.replaceAll(String::toUpperCase); // Turns all the triggers to UPPERCASE
				if (invTriggers.contains(Trigger.toUpperCase())) {
					if (debug)
						Bukkit.getLogger()
								.info(p.getName() + "'s message contains an inventory / enderchest display trigger");
				if (!p.hasPermission("chatitemdisplay.display.inventory")) {
					p.sendMessage(new StringFormatter()
							.format(chatItemDisplay.getConfig().getString("messages.missing-permission-inventory")));
					e.setCancelled(true);

					Bukkit.getLogger().info(p.getName() + "does not have permission to display their inventory");
					return;


				}
					PlayerInventoryReplicator.InventoryData data = new PlayerInventoryReplicator(chatItemDisplay)
							.replicateInventory(p);

					chatItemDisplay.displayed.put(p.getName().toUpperCase(), new DisplayInventory(data.getInventory(),
							data.getTitle(), p.getName(), p.getDisplayName(), p.getUniqueId(), false));
				} else {
					if (debug)
						Bukkit.getLogger().info(p.getName() + "'s message contains an enderchest display trigger");
					if (!p.hasPermission("chatitemdisplay.display.enderchest")) {
						p.sendMessage(new StringFormatter().format(
								chatItemDisplay.getConfig().getString("messages.missing-permission-enderchest")));
						e.setCancelled(true);

						Bukkit.getLogger().info(p.getName() + "does not have permission to display their Ender Chest");
						return;
				}
					String title = new StringFormatter().format(chatItemDisplay.getConfig()
							.getString("display-messages.displayed-enderchest-title").replaceAll("%player%",
									chatItemDisplay.getConfig().getBoolean("use-nicks-in-gui")
											? chatItemDisplay.getConfig().getBoolean("strip-nick-colors-gui")
													? ChatColor.stripColor(p.getDisplayName())
													: p.getDisplayName()
											: p.getName()));
					Inventory inv = Bukkit.createInventory(p, InventoryType.ENDER_CHEST, title);

					inv.setContents(p.getEnderChest().getContents());

					chatItemDisplay.displayed.put(p.getName().toUpperCase(),
							new DisplayInventory(inv, title, p.getName(), p.getDisplayName(), p.getUniqueId(), false));

				}


				if (chatItemDisplay.isBungee())
					new BungeeCordSender(chatItemDisplay)
				.send(chatItemDisplay.displayed.get(p.getName().toUpperCase()), true);

					if (chatItemDisplay.useOldFormat) {

						e.setCancelled(true);
						Bukkit.getScheduler().runTask(chatItemDisplay, () -> {
							String newmsg = e.getMessage().replaceFirst("(?i)" + Pattern.quote(Trigger),
									bell + "split");
							String[] parts = newmsg.split(bell + "split");
							String first = newmsg.indexOf(bell + "split") > 0 ? parts[0] : "";
							String last = parts.length == 0 ? ""
									: parts.length == 2 ? parts[1] : first.equals("") ? parts[0] : "";
						e.getPlayer().chat(first.trim());
							DisplayInfo di = null;
						Displayable d = chatItemDisplay.displayed.get(e.getPlayer().getName().toUpperCase());
							if (d instanceof DisplayItem)
								di = new DisplayItemInfo(chatItemDisplay, (DisplayItem) d);
							if (d instanceof DisplayInventory)
								di = new DisplayInventoryInfo(chatItemDisplay, (DisplayInventory) d);
							di.cmdMsg();
						e.getPlayer().chat(last.trim());
							displayed = true;

						});
						return;

					}
						
					

					String newmsg = e.getMessage().replaceAll("(?i)" + Pattern.quote(Trigger),

						bell + "cid" + p.getName() + bell);

					e.setMessage(newmsg);
					displayed = true;

		}

		}

		if (displayed && !p.hasPermission("chatitemdisplay.cooldownbypass")) {
			chatItemDisplay.DisplayCooldowns.put(p.getUniqueId(), System.currentTimeMillis());
		}


	}



}
