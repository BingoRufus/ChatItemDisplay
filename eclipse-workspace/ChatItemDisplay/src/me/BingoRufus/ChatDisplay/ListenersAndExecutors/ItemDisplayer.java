package me.BingoRufus.ChatDisplay.ListenersAndExecutors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;

import me.BingoRufus.ChatDisplay.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ItemDisplayer implements Listener {
	public static Map<String, Inventory> DisplayedItem = new HashMap<String, Inventory>();
	public static Map<Player, Inventory> DisplayedShulkerBox = new HashMap<Player, Inventory>();
	public Map<UUID, Long> DisplayItemCooldowns = new HashMap<UUID, Long>();
	String MsgName;
	String GUIName;
	Main main;
	boolean debug;
	Inventory ShulkerBoxInventory;
	String Version;

	public ItemDisplayer(Main m) {
		m.reloadConfig();
		main = m;
		debug = main.getConfig().getBoolean("debug-mode");
		Version = Bukkit.getServer().getVersion().substring(Bukkit.getServer().getVersion().indexOf("(MC: ") + 5,
				Bukkit.getServer().getVersion().indexOf(")"));
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
				String ItemInfo = null;
				String ItemName = null;
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
				if (debug)
					Bukkit.getLogger().info(e.getPlayer().getName() + " is not on cooldown");

				ItemInfo = NameFromItem(HeldItem);
				TextComponent Hover = new TextComponent();
				Hover.setText(ItemInfo);
				ItemName = ItemInfo;

				if (HeldItem.getType().equals(Material.WRITTEN_BOOK)) {
					BookMeta book = (BookMeta) HeldItem.getItemMeta();
					if (book.hasAuthor())
						ItemInfo = ItemInfo + ChatColor.GRAY + "\nby " + book.getAuthor();
					if (book.hasGeneration()) {
						ItemInfo = ItemInfo + ChatColor.GRAY + "\n" + makeStringPretty(book.getGeneration().toString());
					} else {
						ItemInfo = ItemInfo + ChatColor.GRAY + "\nOriginal";

					}
				}

				if (main.getConfig().getBoolean("use-nicks-in-display-message")) {
					MsgName = e.getPlayer().getDisplayName();
				} else {
					MsgName = e.getPlayer().getName();
				}
				if (main.getConfig().getBoolean("use-nicks-in-gui")) {
					GUIName = e.getPlayer().getDisplayName();
				} else {
					GUIName = e.getPlayer().getName();
				}
				if (debug)
					Bukkit.getLogger().info("Name Formats have been established");
				TextComponent PreMsg = new TextComponent();
				TextComponent EndMsg = new TextComponent();

				String Message = ChatColor.translateAlternateColorCodes('&',
						main.getConfig().getString("messages.display-format").replace("%player%", MsgName));
				if (Message.split("%item%")[0] != null) {
					PreMsg.setText(Message.split("%item%")[0]);
				} else {
					PreMsg.setText("");
				}
				if (Message.split("%item%").length == 2) {
					EndMsg.setText(Message.split("%item%")[1]);
				} else {
					EndMsg.setText("");
				}

				// ENCHANTS
				if (HeldItem.getItemMeta().hasEnchants()) {
					Map<Enchantment, Integer> enchants = HeldItem.getItemMeta().getEnchants();
					for (Enchantment ench : HeldItem.getItemMeta().getEnchants().keySet()) {
						if (ench.getKey().getKey().toString().equals("lure") && enchants.get(ench) == 1
								&& !ench.canEnchantItem(HeldItem))
							continue;
						ItemInfo = ItemInfo + "\n" + ChatColor.GRAY
								+ makeStringPretty(ench.getKey().getKey().toString()) + " "
								+ enchants.get(ench).toString();
					}
				}
				if (debug)
					Bukkit.getLogger().info("Enchants have been created");
				if (HeldItem.getItemMeta() instanceof BlockStateMeta) {
					BlockStateMeta im = (BlockStateMeta) HeldItem.getItemMeta();
					if (im.getBlockState() instanceof ShulkerBox) {
						if (debug)
							Bukkit.getLogger().info("Item is a Shulker Box");
						ShulkerBox shulker = (ShulkerBox) im.getBlockState();
						ShulkerBoxInventory = Bukkit.createInventory(e.getPlayer(), 27, ItemName);
						ShulkerBoxInventory.setContents(shulker.getInventory().getContents());
						List<ItemStack> Contents = new ArrayList<ItemStack>();

						if (debug)
							Bukkit.getLogger().info("Shulker Box inventory has been created");
						for (ItemStack i : ShulkerBoxInventory.getContents()) {
							if (!(i == null))
								Contents.add(i);
						}
						if (debug)
							Bukkit.getLogger().info(
									"Shulker box contents have been saved, there are " + Contents.size() + " items");
						for (int i = 0; i < Contents.size(); i++) {
							if (debug)
								Bukkit.getLogger().info("In For loop");
							if (i < 5) {
								ItemInfo = ItemInfo + "\n" + ChatColor.WHITE + NameFromItem(Contents.get(i)) + " x"
										+ Contents.get(i).getAmount();

							} else {
								ItemInfo = ItemInfo + "\n" + ChatColor.WHITE + "" + ChatColor.ITALIC + "and "
										+ (Contents.size() - 4) + " more...";
								break;

							}
						}

					}
				}
				// LORE
				if (HeldItem.getItemMeta().hasLore()) {
					List<String> lore = HeldItem.getItemMeta().getLore();
					for (int i = 0; i < lore.size(); i++) {
						ItemInfo = ItemInfo + "\n" + ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + lore.get(i);
					}
				}
				if (debug)
					Bukkit.getLogger().info("Lore has been created");

				// Create Item Display GUI

				Inventory DisplayGUI = Bukkit.createInventory(e.getPlayer(), 9,
						ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("messages.gui-format"))
								.replace("%player%", GUIName));
				if (debug)
					Bukkit.getLogger().info("GUI has been created");

				DisplayGUI.setItem(4, HeldItem);

				if (debug)
					Bukkit.getLogger().info("Added item to GUI");
				resetViews(e.getPlayer(), DisplayGUI);
				if (debug)
					Bukkit.getLogger().info("Closed all open inventories of player's GUI");
				if (ShulkerBoxInventory != null) {
					DisplayedShulkerBox.put(e.getPlayer(), ShulkerBoxInventory);

				}
				DisplayedItem.put(e.getPlayer().getName(), DisplayGUI);
				ItemInfo = ItemInfo + "\n" + ChatColor.DARK_GRAY + HeldItem.getType().getKey().toString();

				Hover.setHoverEvent(
						new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ItemInfo).create()));
				Hover.setClickEvent(
						new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewitem " + e.getPlayer().getName()));
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.spigot().sendMessage(PreMsg, Hover, EndMsg);
				}
				if (debug)
					Bukkit.getLogger()
							.info(e.getPlayer().getName() + "'s item display message has been sent to everyone");

				if (!e.getPlayer().hasPermission("chatitemdisplay.cooldownbypass")) {
					DisplayItemCooldowns.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
				}
				e.setCancelled(true);

			}
			break;
		}

	}

	public String makeStringPretty(String s) {
		String out = null;
		String[] Nameparts = s.toLowerCase().split("_");
		for (String part : Nameparts) {
			part = part.substring(0, 1).toUpperCase() + part.substring(1);
			if (out == null) {
				out = part;
				continue;
			}

			out = out + " ";
			out = out + part;
		}
		return out;
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {

		if (DisplayedShulkerBox.values().contains(e.getInventory())
				|| DisplayedItem.values().contains(e.getInventory())) {
			e.setCancelled(true);
			if (e.getClickedInventory() == null)
				return;
			if (e.getCurrentItem() == null)
				return;
			Player p = (Player) e.getWhoClicked();

			if (!e.getClickedInventory().equals(p.getInventory())) {
				if (e.getCurrentItem().getItemMeta() instanceof BlockStateMeta) {
					if (((BlockStateMeta) e.getCurrentItem().getItemMeta()).getBlockState() instanceof ShulkerBox) {
						p.openInventory(DisplayedShulkerBox.get((Player) e.getInventory().getHolder()));
					}
				}

				if (main.UpToDate(Version.split("[.]"), "1.14.2".split("[.]"))) { // The player.openBook() was added in
																					// Spigot for version 1.14.2 this
																					// checks to make sure the version
																					// is past 1.14.2

					if (e.getCurrentItem().getType().equals(Material.WRITTEN_BOOK)) {
						BookMeta bm = (BookMeta) e.getCurrentItem().getItemMeta().clone();

						if (bm.getPages().isEmpty()) {
							bm.setPages("");
						}
						ItemStack book = e.getCurrentItem().clone();
						book.setItemMeta(bm);
						p.closeInventory();
						p.openBook(book);
					}
					if (e.getCurrentItem().getType().equals(Material.WRITABLE_BOOK)) {
						ItemStack item = e.getCurrentItem().clone();
						BookMeta BookAndQuill = (BookMeta) item.getItemMeta();
						BookAndQuill.setTitle("Your Mom");
						BookAndQuill.setAuthor("Your Mom");
						ItemStack WrittenBook = new ItemStack(Material.WRITTEN_BOOK);
						if (BookAndQuill.getPages().isEmpty()) {
							BookAndQuill.setPages("");
						}
						WrittenBook.setItemMeta(BookAndQuill);
						p.closeInventory();
						p.openBook(WrittenBook);

					}
				}

			}
		}
	}

	public void resetViews(Player player, Inventory DisplayGUI) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (DisplayedItem.values().contains(p.getOpenInventory().getTopInventory())) {
				if (player.equals((Player) p.getOpenInventory().getTopInventory().getHolder())) {
					p.openInventory(DisplayGUI);
					if (debug)
						Bukkit.getLogger().info("Opened update inventory for " + player.getName());
				}

			}
			if (DisplayedShulkerBox.values().contains(p.getOpenInventory().getTopInventory())) {
				if (player.equals((Player) p.getOpenInventory().getTopInventory().getHolder())) {
					p.openInventory(DisplayGUI);
				}
			}
		}
	}

	public String NameFromItem(ItemStack item) {
		if (item.getType().equals(Material.WRITTEN_BOOK)) {

			BookMeta book = (BookMeta) item.getItemMeta();
			if (book.hasTitle()) {
				return book.getTitle();
			}

		}
		if (item.getItemMeta().hasDisplayName()) {
			return item.getItemMeta().getDisplayName();
		} else {
			return makeStringPretty(item.getType().name());
		}

	}
}
