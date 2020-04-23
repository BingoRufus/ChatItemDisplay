package me.BingoRufus.ChatDisplay.ListenersAndExecutors;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.BingoRufus.ChatDisplay.Display;
import me.BingoRufus.ChatDisplay.Main;
import net.md_5.bungee.api.ChatColor;

public class ItemDisplayer implements Listener {
	public static Map<String, Inventory> DisplayedItem = new HashMap<String, Inventory>();
	public static Map<Player, Inventory> DisplayedShulkerBox = new HashMap<Player, Inventory>();
	public static Map<UUID, Long> DisplayItemCooldowns = new HashMap<UUID, Long>();
	String MsgName;
	String GUIName;
	Main main;
	boolean debug;
	String Version;

	public ItemDisplayer(Main m) {
		m.reloadConfig();
		main = m;
		debug = main.getConfig().getBoolean("debug-mode");
		Version = Bukkit.getServer().getVersion().substring(Bukkit.getServer().getVersion().indexOf("(MC: ") + 5,
				Bukkit.getServer().getVersion().indexOf(")"));
		Bukkit.getPluginManager().registerEvents(new InventoryClick(main, Version), main);
	}

	@EventHandler()
	public void onChat(AsyncPlayerChatEvent e) {

		if (debug)
			Bukkit.getLogger().info(e.getPlayer().getName() + " sent a message");

		if (e.getPlayer().getInventory().getItemInMainHand() == null) {
			if (debug)
				Bukkit.getLogger().info(e.getPlayer().getName() + " is not holding anything");
			return;
		}

		for (String Trigger : main.getConfig().getStringList("triggers")) {
			if (e.getMessage().toUpperCase().contains(Trigger.toUpperCase())) {
				if (debug)
					Bukkit.getLogger().info(e.getPlayer().getName() + "'s message contains an item display trigger");

				ItemStack HeldItem = e.getPlayer().getInventory().getItemInMainHand().clone();

				if (HeldItem.getItemMeta() == null) {
					if (debug)
						Bukkit.getLogger().info(e.getPlayer().getName() + "'s item has no meta data");
					return;
				}
				if (!e.getPlayer().hasPermission("chatitemdisplay.display")) {
					if (debug)
						Bukkit.getLogger().info(e.getPlayer().getName() + " does not have permission to display items");

					e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
							main.getConfig().getString("messages.missing-permission-to-display")));
					e.setCancelled(true);
					return;
				}
				if (main.getConfig().getStringList("blacklisted-items")
						.contains(HeldItem.getType().getKey().toString())) {
					if (!e.getPlayer().hasPermission("Chatitemdisplay.blacklistbypass")) {
						if (debug)
							Bukkit.getLogger().info(e.getPlayer().getName() + "'s displayed item was blacklisted");

						e.setCancelled(true);

						e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
								main.getConfig().getString("messages.black-listed-item")));

						return;
					}

				}

				if (debug)
					Bukkit.getLogger().info(e.getPlayer().getName() + "'s item is not blacklisted");

				if (DisplayItemCooldowns.containsKey(e.getPlayer().getUniqueId())) {
					Long CooldownRemaining = (main.getConfig().getLong("display-item-cooldown") * 1000)
							- (System.currentTimeMillis() - DisplayItemCooldowns.get(e.getPlayer().getUniqueId()));

					if (CooldownRemaining > 0) {
						if (debug)
							Bukkit.getLogger().info(e.getPlayer().getName() + " is on a chat display cooldown");

						Double SecondsRemaining = (double) (Math.round(CooldownRemaining.doubleValue() / 100)) / 10;
						e.setCancelled(true);
						e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig()
								.getString("messages.cooldown").replaceAll("%seconds%", "" + SecondsRemaining)));

						return;
					}
				}
				new Display(main, debug).doStuff(HeldItem, e.getPlayer(), e.getMessage().replace(Trigger, "%item%"));

				if (!e.getPlayer().hasPermission("chatitemdisplay.cooldownbypass")) {
					DisplayItemCooldowns.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
				}

				e.setCancelled(true);
				break;
			}
		}

	}

}
