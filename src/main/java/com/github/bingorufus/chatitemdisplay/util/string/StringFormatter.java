package com.github.bingorufus.chatitemdisplay.util.string;

import com.github.bingorufus.chatitemdisplay.ChatItemDisplay;
import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringFormatter {
    final VersionComparator.Status hasHex;
    private final Pattern pat = Pattern.compile("#[a-fA-F0-9]{6}");

    public StringFormatter() {
        hasHex = new VersionComparator().isRecent(
                ChatItemDisplay.MINECRAFT_VERSION,
                "1.16");
    }

    public String format(String s) {
        if (hasHex.equals(VersionComparator.Status.BEHIND))
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
