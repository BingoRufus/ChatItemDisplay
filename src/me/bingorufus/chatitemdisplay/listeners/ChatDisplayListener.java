package me.bingorufus.chatitemdisplay.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.Display;
import me.bingorufus.chatitemdisplay.utils.DisplayPermissionChecker;
import me.bingorufus.chatitemdisplay.utils.bungee.BungeeCordSender;

public class ChatDisplayListener implements Listener {

	char bell = '\u0007';


	public static Map<UUID, Long> DisplayItemCooldowns = new HashMap<UUID, Long>();
	String MsgName;
	String GUIName;
	ChatItemDisplay chatItemDisplay;
	boolean debug;
	String Version;

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

		if (debug)
			Bukkit.getLogger().info(e.getPlayer().getName() + " sent a message");

		if (e.getPlayer().getInventory().getItemInMainHand() == null) {
			if (debug)
				Bukkit.getLogger().info(e.getPlayer().getName() + " is not holding anything");
			return;
		}

		for (String Trigger : chatItemDisplay.getConfig().getStringList("triggers")) {
			if (e.getMessage().toUpperCase().contains(Trigger.toUpperCase())) {

				if (debug)
					Bukkit.getLogger().info(e.getPlayer().getName() + "'s message contains an item display trigger");

				DisplayPermissionChecker dpc = new DisplayPermissionChecker(chatItemDisplay, e.getPlayer());
				if (dpc.hasPermission()) {
					Display d = new Display(chatItemDisplay, e.getPlayer().getInventory().getItemInMainHand(),
							e.getPlayer().getUniqueId(), e.getPlayer().getName(), e.getPlayer().getDisplayName(),
							false);

					chatItemDisplay.displays.put(e.getPlayer().getName().toUpperCase(), d);

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
						chatItemDisplay.displays.get(e.getPlayer().getName().toUpperCase()).cmdMsg();
							e.getPlayer().chat(last.trim());


						});
						if (chatItemDisplay.isBungee()) {
							new BungeeCordSender(chatItemDisplay).sendItem(d, true);
						}
						return;

					}
					if (chatItemDisplay.isBungee()) {
						new BungeeCordSender(chatItemDisplay).sendItem(d, false);
					}

					String newmsg = e.getMessage().replaceFirst("(?i)" + Pattern.quote(Trigger),

							bell + "cid" + e.getPlayer().getName() + bell);

					e.setMessage(newmsg);
					return;

				}
				e.setCancelled(dpc.CancelMessage());
				break;
			}
		}

	}

}
