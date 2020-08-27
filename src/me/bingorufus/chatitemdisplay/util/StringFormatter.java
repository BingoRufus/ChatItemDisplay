package me.bingorufus.chatitemdisplay.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

import net.md_5.bungee.api.ChatColor;

public class StringFormatter {
	VersionComparer.Status hasHex;
	private final Pattern pat = Pattern.compile("#[a-fA-F0-9]{6}");
	public StringFormatter() {
		hasHex = new VersionComparer().isRecent(
				Bukkit.getServer().getVersion().substring(Bukkit.getServer().getVersion().indexOf("(MC: ") + 5,
						Bukkit.getServer().getVersion().indexOf(")")),
				"1.16");
	}

	public String format(String s) {
		if (hasHex.equals(VersionComparer.Status.BEHIND))
			return ChatColor.translateAlternateColorCodes('&', s);
		Matcher match = pat.matcher(s);
		while (match.find()) {
			String color = s.substring(match.start(), match.end());
			s = s.replaceAll(color, ChatColor.of(color) + "");

			match = pat.matcher(s);

		}
		return ChatColor.translateAlternateColorCodes('&', s);
	}
}
