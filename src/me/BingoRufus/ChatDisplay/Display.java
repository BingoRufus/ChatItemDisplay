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
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import me.BingoRufus.ChatDisplay.Utils.ItemStackStuff;
import me.BingoRufus.ChatDisplay.Utils.ItemStackTranslator;
import me.BingoRufus.ChatDisplay.Utils.PotionInfo;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;

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

	public TextComponent getLore() {
		TextComponent ItemInfo = ItemStackStuff.NameFromItem(item);
		if(item.getItemMeta() instanceof EnchantmentStorageMeta) {
			EnchantmentStorageMeta esm = (EnchantmentStorageMeta) item.getItemMeta();
			ItemInfo.addExtra(enchantLore(esm.getStoredEnchants()));
		}
		if (item.getType().isRecord()) {
			ItemInfo.addExtra("\n");
			TextComponent disc = new TextComponent(ChatColor.GRAY + "");
			disc.addExtra(new TranslatableComponent(new ItemStackTranslator().getId(item) + ".desc"));
			ItemInfo.addExtra(disc);
		}

		if (item.getType().equals(Material.WRITTEN_BOOK)) {
			BookMeta book = (BookMeta) item.getItemMeta();

			if (book.hasAuthor()) {
				ItemInfo.addExtra("\n");
				TextComponent author = new TextComponent(new TranslatableComponent("book.byAuthor"));
				author.addExtra(book.getAuthor());
				author.setColor(net.md_5.bungee.api.ChatColor.GRAY);
				ItemInfo.addExtra(author);
			}
			ItemInfo.addExtra("\n");

			TranslatableComponent generation = new TranslatableComponent(
					"book.generation." + (book.hasGeneration() ? book.getGeneration().ordinal() : 0));
			generation.setColor(net.md_5.bungee.api.ChatColor.GRAY);
			ItemInfo.addExtra(generation);
			}

		if (item.getType().equals(Material.FILLED_MAP)) {
			MapMeta mm = (MapMeta) item.getItemMeta();
			TextComponent map = new TextComponent();
			map.addExtra(new TranslatableComponent("filled_map.id"));
			map.addExtra(mm.getMapView().getId() + "");
			map.addExtra("\n");
			map.addExtra(new TranslatableComponent("filled_map.scale"));
			map.addExtra((int) Math.pow(2, mm.getMapView().getScale().ordinal()) + "");
			map.addExtra("\n");
			map.addExtra("(Level " + mm.getMapView().getScale().ordinal() + "/4)");
			map.setColor(net.md_5.bungee.api.ChatColor.GRAY);
			ItemInfo.addExtra("\n");
			ItemInfo.addExtra(map);

		}
		if (!item.getItemMeta().getItemFlags().contains(ItemFlag.HIDE_POTION_EFFECTS)) {
			if (item.getItemMeta() instanceof PotionMeta) {
				PotionMeta pm = (PotionMeta) item.getItemMeta();
				if (!Arrays.asList(PotionType.MUNDANE, PotionType.UNCRAFTABLE, PotionType.AWKWARD, PotionType.THICK)
						.contains(pm.getBasePotionData().getType())) {
				PotionInfo pi = new PotionInfo(item, ItemStackStuff);
					ItemInfo.addExtra(pi.getPotionInfo());
				}

				for (PotionEffect pot : pm.getCustomEffects()) {
					String effectname = "";
					try {
						effectname = new ItemStackTranslator().potionId(PotionType.valueOf(pot.getType().getName()));
					} catch (Exception e) {
						effectname = pot.getType().getName().toLowerCase();

					}
					effectname = effectname.replace("minecraft:", "");
					TranslatableComponent potname = new TranslatableComponent(
							"effect." + (effectname.equals("swiftness") ? "minecraft.speed"
									: effectname.equals("water") ? "none"
											: "minecraft."
													+ (effectname.equals("leaping") ? "jump_boost" : effectname)));

					ItemInfo.addExtra("\n" + ChatColor.BLUE);
					ItemInfo.addExtra(potname);
					String s = pot.getAmplifier() != 0
							? ItemStackStuff.romanNumeralify((short) (pot.getAmplifier() + 1)) + " "
							: "";

					ItemInfo.addExtra(" " + s);
					ItemInfo.addExtra("(" + PotionInfo.timeFromInt(pot.getDuration(), true));
					ItemInfo.addExtra(")");
				}

			}

		}
		if (!item.getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {

			if (item.getItemMeta().hasEnchants()) {
				ItemInfo.addExtra(enchantLore(item.getItemMeta().getEnchants()));
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
						TextComponent items = new TextComponent("\n" + ChatColor.WHITE);
						items.addExtra(new ItemStackTranslator().translateItemStack(Contents.get(i)));
						items.addExtra(new TextComponent(" x" + Contents.get(i).getAmount()));
						ItemInfo.addExtra(items);

					} else {
						ItemInfo.addExtra("\n" + ChatColor.WHITE + "" + ChatColor.ITALIC + "and "
								+ (Contents.size() - 4) + " more...");
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
					ItemInfo.addExtra("\n" + ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + lore.get(i));

				}
			}
			if ((!item.getItemMeta().getItemFlags().contains(ItemFlag.HIDE_UNBREAKABLE)
					&& item.getItemMeta().isUnbreakable())) {
				TextComponent unbreakable = new TextComponent("\n");
				unbreakable.addExtra(new TranslatableComponent("item.unbreakable"));
				unbreakable.setColor(net.md_5.bungee.api.ChatColor.BLUE);
				ItemInfo.addExtra(unbreakable);
			}
			if (debug)
				Bukkit.getLogger().info("Lore has been created");

		}


		ItemInfo.addExtra("\n" + ChatColor.DARK_GRAY + item.getType().getKey().toString());

		return ItemInfo;

	}

	private TextComponent enchantLore(Map<Enchantment, Integer> enchants) {
		TextComponent lore = new TextComponent();
		for (Enchantment ench : enchants.keySet()) {
			lore.addExtra("\n" + ChatColor.GRAY);
			lore.addExtra(new TranslatableComponent(
					"enchantment." + ench.getKey().getNamespace() + "." + ench.getKey().getKey()));
			if (ench.getMaxLevel() == 1 && enchants.get(ench) == ench.getMaxLevel())
				continue;

			if (!roman) {
				lore.addExtra(" " + enchants.get(ench).shortValue());
			} else {
				lore.addExtra(" " + ItemStackStuff.romanNumeralify(enchants.get(ench).shortValue()));
			}

		}
		return lore;
	}

	public TextComponent getName() {
		TextComponent ItemName = ItemStackStuff.NameFromItem(item);
		if (m.getConfig().getBoolean("messages.remove-item-colors"))
			ItemName.setColor(net.md_5.bungee.api.ChatColor.RESET);
		if (m.getConfig().getBoolean("show-item-amount") && item.getAmount() > 1)
			ItemName.addExtra(" x" + item.getAmount());
		return ItemName;
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
