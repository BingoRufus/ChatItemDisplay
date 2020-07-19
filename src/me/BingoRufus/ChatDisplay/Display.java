package me.BingoRufus.ChatDisplay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import me.BingoRufus.ChatDisplay.Utils.ItemStackStuff;
import me.BingoRufus.ChatDisplay.Utils.PotionInfo;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Display {
	Boolean debug;
	Main m;
	Boolean roman;
	TextComponent PreMsg = new TextComponent();
	TextComponent EndMsg = new TextComponent();
	public ItemStack item;
	Player p;
	Inventory inventory;
	ItemStackStuff ItemStackStuff;


	public Display(Main m, Player p) {
		ItemStackStuff = new ItemStackStuff(m);
		this.m = m;
		debug = m.getConfig().getBoolean("debug-mode");
		roman = m.getConfig().getBoolean("enchantments.use-roman-numerals");
		this.item = p.getInventory().getItemInMainHand();
		this.p = p;
		String guiname = m.getConfig().getString("messages.gui-format");
		guiname = ChatColor.translateAlternateColorCodes('&', guiname);
		;

		inventory = Bukkit.createInventory(p, 9, guiname.replaceAll("%player%",
				m.getConfig().getBoolean("use-nicks-in-gui") ? p.getDisplayName() : p.getName()));
		inventory.setItem(4, item);

		Bukkit.getScheduler().runTask(m, () -> {
			if (!m.invs.contains(inventory)) {
				m.invs.add(inventory);
				m.displaying.put(p.getName(), inventory);
			}
		});


	}

	public String getLore() {
		String ItemInfo = ItemStackStuff.NameFromItem(item, false);

		if (item.getType().equals(Material.WRITTEN_BOOK)) {
			BookMeta book = (BookMeta) item.getItemMeta();
			if (book.hasAuthor())
				ItemInfo += ChatColor.GRAY + "\nby " + book.getAuthor();
			if (book.hasGeneration()) {
				ItemInfo += ChatColor.GRAY + "\n" + ItemStackStuff.makeStringPretty(book.getGeneration().toString());
			} else {
				ItemInfo += ChatColor.GRAY + "\nOriginal";

			}
		}
		if (item.getType().equals(Material.FILLED_MAP)) {
			MapMeta mm = (MapMeta) item.getItemMeta();
			ItemInfo += ChatColor.GRAY + "\nId #" + mm.getMapView().getId();
			ItemInfo += ChatColor.GRAY + "\nScaling at 1:" + (int) Math.pow(2, mm.getMapView().getScale().ordinal());
			ItemInfo += ChatColor.GRAY + "\n(Level " + mm.getMapView().getScale().ordinal() + "/4)";

		}
		if (!item.getItemMeta().getItemFlags().contains(ItemFlag.HIDE_POTION_EFFECTS)) {
			if (item.getItemMeta() instanceof PotionMeta) {
				PotionMeta pm = (PotionMeta) item.getItemMeta();
				if (!Arrays.asList(PotionType.MUNDANE, PotionType.UNCRAFTABLE, PotionType.AWKWARD, PotionType.THICK)
						.contains(pm.getBasePotionData().getType())) {
				PotionInfo pi = new PotionInfo(item, ItemStackStuff);
				ItemInfo += "\n" + pi.getPotionInfo();
				}

				for (PotionEffect pot : pm.getCustomEffects()) {

					ItemInfo += "\n" + ChatColor.BLUE + ItemStackStuff.makeStringPretty(pot.getType().getName());
					String s = pot.getAmplifier() != 0
							? ItemStackStuff.romanNumeralify((short) (pot.getAmplifier() + 1)) + " "
							: "";

					ItemInfo += " " + s;
					ItemInfo += "(" + PotionInfo.timeFromInt(pot.getDuration());
					ItemInfo += ")";
				}

			}

		}
		if (!item.getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {

			if (item.getItemMeta().hasEnchants()) {
				Map<Enchantment, Integer> enchants = item.getItemMeta().getEnchants();
				for (Enchantment ench : item.getItemMeta().getEnchants().keySet()) {
					if (ench.getMaxLevel() == 1 && enchants.get(ench) == ench.getMaxLevel()) {
						ItemInfo += "\n" + ChatColor.GRAY
								+ ItemStackStuff.makeStringPretty(ench.getKey().getKey().toString());
						continue;
					}
					if (!roman) {
						ItemInfo += "\n" + ChatColor.GRAY
								+ ItemStackStuff.makeStringPretty(ench.getKey().getKey().toString()) + " "
								+ enchants.get(ench).shortValue();
					} else {
						ItemInfo += "\n" + ChatColor.GRAY
								+ ItemStackStuff.makeStringPretty(ench.getKey().getKey().toString()) + " "
								+ ChatColor.GRAY + ItemStackStuff.romanNumeralify(enchants.get(ench).shortValue());
					}

				}
			}
			if (debug)
				Bukkit.getLogger().info("Enchants have been created");
		}
		if (item.getItemMeta() instanceof BlockStateMeta) {
			BlockStateMeta im = (BlockStateMeta) item.getItemMeta();
			if (im.getBlockState() instanceof ShulkerBox) {
				if (debug)
					Bukkit.getLogger().info("Item is a Shulker Box");
				ShulkerBox shulker = (ShulkerBox) im.getBlockState();

				List<ItemStack> Contents = new ArrayList<ItemStack>();


				try {
					for (ItemStack i : shulker.getInventory().getContents()) {
						if (!(i == null))
							Contents.add(i);
					}
				} catch (NullPointerException e) {
					if (debug)
						Bukkit.getLogger().info("Shulker Box is empty");
				}

				if (debug)
					Bukkit.getLogger()
							.info("Shulker box contents have been saved, there are " + Contents.size() + " items");
				for (int i = 0; i < Contents.size(); i++) {
					if (debug)
						Bukkit.getLogger().info("In For loop");
					if (i < 5) {
						ItemInfo += "\n" + ChatColor.WHITE + ItemStackStuff.NameFromItem(Contents.get(i), false) + " x"
								+ Contents.get(i).getAmount();

					} else {
						ItemInfo += "\n" + ChatColor.WHITE + "" + ChatColor.ITALIC + "and " + (Contents.size() - 4)
								+ " more...";
						break;

					}
				}

			}

		}
		if (!item.getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ATTRIBUTES)) {
			// LORE
			if (item.getItemMeta().hasLore()) {
				List<String> lore = item.getItemMeta().getLore();
				for (int i = 0; i < lore.size(); i++) {
					ItemInfo += "\n" + ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + lore.get(i);

				}
			}
			if ((!item.getItemMeta().getItemFlags().contains(ItemFlag.HIDE_UNBREAKABLE)
					&& item.getItemMeta().isUnbreakable())) {
				ItemInfo += "\n" + ChatColor.BLUE + "Unbreakable";
			}
			if (debug)
				Bukkit.getLogger().info("Lore has been created");

		}


		ItemInfo += "\n" + ChatColor.DARK_GRAY + item.getType().getKey().toString();

		return ItemInfo;

	}

	public String getName() {
		String ItemName = ItemStackStuff.NameFromItem(item, false);
		if (m.getConfig().getBoolean("messages.remove-item-colors"))
			ItemName = ChatColor.stripColor(ItemName);
		if (m.getConfig().getBoolean("show-item-amount") && item.getAmount() > 1)
			ItemName += " x" + item.getAmount();
		return ItemName + ChatColor.RESET;
	}

	public TextComponent getHover() {
		TextComponent Hover = new TextComponent(getName());
		Hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(getLore()).create()));
		Hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/viewitem " + p.getName()));

		return Hover;
	}

	public void cmdMsg() {

		String format = m.getConfig().getString("messages.display-format");
		format = format.replaceAll("%player%",
				m.getConfig().getBoolean("use-nicks-in-display-message") ? p.getDisplayName() : p.getName());
		format = ChatColor.translateAlternateColorCodes('&', format);
		String[] sects = format.split("%item%");
		PreMsg = format.indexOf("%item%") > 0 ? new TextComponent(sects[0]) : new TextComponent("");
		EndMsg = sects.length == 2 ? new TextComponent(sects[1])
				: PreMsg.getText() == null ? new TextComponent(sects[0]) : new TextComponent("");
		Bukkit.spigot().broadcast(PreMsg, getHover(), EndMsg);

	}



}
