package me.BingoRufus.ChatDisplay.Utils;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import net.md_5.bungee.api.ChatColor;

public class PotionInfo {
	List<PotionType> debuffs = Arrays.asList(PotionType.INSTANT_DAMAGE, PotionType.POISON, PotionType.WEAKNESS,
			PotionType.SLOWNESS);
	PotionData pd;
	PotionMeta pm;
	ItemStack item;
	ItemStackStuff itemutil;

	public PotionInfo(ItemStack item, ItemStackStuff is) {
		this.pm = (PotionMeta) item.getItemMeta();
		this.pd = pm.getBasePotionData();
		this.item = item;
		itemutil = is;




	}

	public String getPotionInfo() {
		if (pd.getType().isInstant()) {
			switch (pd.getType()) {
			case INSTANT_DAMAGE:
				return ChatColor.RED + "Instant Damage" + (pd.isUpgraded() ? " II" : "");

			case INSTANT_HEAL:
				return ChatColor.BLUE + "Instant Health" + (pd.isUpgraded() ? " II" : "");
			default:
				break;
			}
		}
		if(pd.getType().equals(PotionType.TURTLE_MASTER)) {
			StringBuilder sb = new StringBuilder(ChatColor.RED + "Slowness" + (pd.isUpgraded() ? " VI" : " IV"));

			sb.append(" (" + timeFromInt(getDuration()) + ")");
			sb.append("\n" + ChatColor.BLUE + "Resistance" + (pd.isUpgraded() ? " IV" : " III"));
			sb.append(" (" + timeFromInt(getDuration()) + ")");
			return sb.toString();
		}

		StringBuilder sb = new StringBuilder((debuffs.contains(pd.getType()) ? ChatColor.RED : ChatColor.BLUE)
				+ itemutil.makeStringPretty(pd.getType().name())
				+ (pd.isUpgraded() ? romanNumeralify((short) pd.getType().getMaxLevel()) : ""));

		sb.append(" (" + timeFromInt(getDuration()) + ")");



		return sb.toString();
	}

	private Integer getDuration() {

		int dur = getPotionDuration(pd.getType(), pd.isExtended(), pd.isUpgraded() ? pd.getType().getMaxLevel() : 1);
		if (item.getType().equals(Material.LINGERING_POTION))
			dur /= 4;
		if (item.getType().equals(Material.TIPPED_ARROW))
			dur /= 8;
		return dur;
	}



	public static String timeFromInt(Integer i) {
		StringBuilder sb = new StringBuilder();
		sb.append((i / 60 > 0) ? i / 60 : "0");
		sb.append(":");
		sb.append(i % 60 / 10 > 0 ? i % 60 : "0" + (i % 60));
		return sb.toString();

	}

	private int getPotionDuration(PotionType effect, Boolean isExtended, Integer level) {
		switch (effect) {
		case AWKWARD:
			return 0;
		case FIRE_RESISTANCE:
			if (isExtended) {
				return 480;
			}
			if (level > 1) {
				return 0;
			}
			return 180;
		case INSTANT_DAMAGE:
			return 0;
		case INSTANT_HEAL:
			return 0;

		case INVISIBILITY:
			if (isExtended) {
				return 480;
			}
			if (level > 1) {
				return 0;
			}
			return 180;
		case JUMP:
			if (isExtended) {
				return 480;
			}
			if (level > 1) {
				return 90;
			}
			return 180;
		case LUCK:
			if (isExtended) {
				return 0;
			}
			if (level > 1) {
				return 0;
			}
			return 300;
		case MUNDANE:
			return 0;
		case NIGHT_VISION:
			if (isExtended) {
				return 480;
			}
			if (level > 1) {
				return 0;
			}
			return 180;
		case POISON:
			if (isExtended) {
				return 90;
			}
			if (level > 1) {
				return 21;
			}
			return 45;
		case REGEN:
			if (isExtended) {
				return 90;
			}
			if (level > 1) {
				return 22;
			}
			return 45;
		case SLOWNESS:
			if (isExtended) {
				return 240;
			}
			if (level > 1) {
				return 20;
			}
			return 90;
		case SLOW_FALLING:
			if (isExtended) {
				return 240;
			}
			if (level > 1) {
				return 0;
			}
			return 90;
		case SPEED:
			if (isExtended) {
				return 480;
			}
			if (level > 1) {
				return 90;
			}
			return 180;
		case STRENGTH:
			if (isExtended) {
				return 480;
			}
			if (level > 1) {
				return 90;
			}
			return 180;
		case THICK:
			return 0;
		case TURTLE_MASTER:
			if (isExtended) {
				return 40;
			}
			if (level > 1) {
				return 20;
			}
			return 20;
		case UNCRAFTABLE:
			return 0;
		case WATER:
			return 0;
		case WATER_BREATHING:
			if (isExtended) {
				return 480;
			}
			if (level > 1) {
				return 0;
			}
			return 180;
		case WEAKNESS:
			if (isExtended) {
				return 240;
			}
			if (level > 1) {
				return 0;
			}
			return 90;
		default:
			return 0;

		}

	}
	
	
	// +Redstone multiplies by 8/3
	// Lingering divides by 4
	// Arrow is 1/2 of lingering potion
	public String romanNumeralify(Short s) {

		Integer level = s.intValue();
		if (s <= 0)
			return s.toString();
		StringBuilder sb = new StringBuilder();

		if (level >= 9) {
			sb.append("IX");
			level = level - 9;
		}
		if (level >= 5) {
			sb.append("V");
			level = level - 5;
		}
		if (level == 4) {
			sb.append("IV");
			level = level - 4;
		}
		if (level >= 1) {
			for (int i = 0; i < level; i++) {
				sb.append("I");
			}
		}

		return sb.toString();

	}
}
