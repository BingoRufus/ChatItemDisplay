package me.bingorufus.chatitemdisplay.Utils.iteminfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;

public class ToolTipRetriever {
	ChatItemDisplay m;
	boolean debug;
	ItemStackStuff ItemStackStuff;
	boolean roman;

	public ToolTipRetriever(ChatItemDisplay m) {
		this.m = m;
		debug = m.getConfig().getBoolean("debug-mode");
		roman = m.getConfig().getBoolean("enchantments.use-roman-numerals");

		ItemStackStuff = new ItemStackStuff(m);
		
	}
	public TextComponent getLore(ItemStack item) {
		TextComponent ItemInfo = ItemStackStuff.NameFromItem(item);
		if (item.getItemMeta() instanceof EnchantmentStorageMeta) {
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
				TranslatableComponent author = new TranslatableComponent("book.byAuthor");
				author.addWith(new TextComponent(book.getAuthor()));
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
			TranslatableComponent id = new TranslatableComponent("filled_map.id");
			id.addWith(mm.getMapView().getId() + "");
			map.addExtra(id);
			map.addExtra("\n");
			TranslatableComponent scale = new TranslatableComponent("filled_map.scale");
			scale.addWith((int) Math.pow(2, mm.getMapView().getScale().ordinal()) + "");
			map.addExtra(scale);
			map.addExtra("\n");
			TranslatableComponent level = new TranslatableComponent("filled_map.level");
			level.addWith(mm.getMapView().getScale().ordinal() + "");
			level.addWith("4");
			map.addExtra(level);
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

}
