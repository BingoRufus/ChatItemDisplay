package com.bingorufus.chatitemdisplay.util.string;

import com.bingorufus.chatitemdisplay.ChatItemDisplay;
import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringFormatter {

    public final static boolean HEX_AVAILABLE = VersionComparator.isRecent(
            ChatItemDisplay.MINECRAFT_VERSION,
            "1.16") != VersionComparator.Status.BEHIND;
    private final static Pattern PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");

    private StringFormatter() {
    }

    public static String format(String s) {
        if (!HEX_AVAILABLE)
            return ChatColor.translateAlternateColorCodes('&', s);
        Matcher match = PATTERN.matcher(s);
        while (match.find()) {
            String color = s.substring(match.start(), match.end());
            s = s.replaceAll(color, ChatColor.of(color) + "");

            match = PATTERN.matcher(s);

        }
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
