package me.bingorufus.chatitemdisplay.util;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ChatItemConfig {
    public static String COOLDOWN,
            BLACKLISTED_ITEM,
            GUI_FORMAT,
            MISSING_PERMISSION_GENERIC,
            MISSING_PERMISSION_ITEM,
            MISSING_PERMISSION_INVENTORY,
            MISSING_PERMISSION_ENDERCHEST,
            EMPTY_DISPLAY,
            INVALID_ID,
            GUI_DISABLED,
            MAP,
            EMPTY_HAND,
            CHAT_ITEM_FORMAT,
            CHAT_ITEM_FORMAT_MULTIPLE,
            CHAT_INVENTORY_FORMAT,
            COMMAND_ITEM_FORMAT,
            COMMAND_ITEM_FORMAT_MULTIPLE,
            COMMAND_INVENTORY_FORMAT,
            INVENTORY_TITLE,
            ENDERCHEST_TITLE,
            CONTAINS_BLACKLIST,
            TOO_LARGE_ITEM,
            TOO_LARGE_INVENTORY,
            TOO_LARGE_ENDERCHEST,
            TOO_LARGE_MESSAGE;

    public static boolean DEBUG_MODE,
            BUNGEE;

    public static List<String> ITEM_TRIGGERS,
            ENDERCHEST_TRIGGERS,
            INVENTORY_TRIGGERS;
    public static List<Material> BLACKLISTED_ITEMS;

    public static void reloadMessages() {
        FileConfiguration c = ChatItemDisplay.getInstance().getConfig();
        ConfigurationSection m = c.getConfigurationSection("messages");
        assert m != null;
        GUI_FORMAT = m.getString("gui-format");
        MISSING_PERMISSION_GENERIC = m.getString("missing-permission-item");
        MISSING_PERMISSION_ITEM = m.getString("missing-permission-item");
        MISSING_PERMISSION_ENDERCHEST = m.getString("missing-permission-enderchest");
        MISSING_PERMISSION_INVENTORY = m.getString("missing-permission-inventory");
        BLACKLISTED_ITEM = m.getString("blacklisted-item");
        EMPTY_DISPLAY = m.getString("player-not-displaying-anything");
        INVALID_ID = m.getString("invalid-id");
        COOLDOWN = m.getString("cooldown");
        GUI_DISABLED = m.getString("gui-disabled");
        MAP = m.getString("map-notification");
        CONTAINS_BLACKLIST = m.getString("contains-blacklist");
        TOO_LARGE_ITEM = m.getString("display-too-large-item");
        TOO_LARGE_ENDERCHEST = m.getString("display-too-large-enderchest");
        TOO_LARGE_INVENTORY = m.getString("display-too-large-inventory");
        TOO_LARGE_MESSAGE = m.getString("too-large-display");

        EMPTY_HAND = m.getString("empty-hand");

        ConfigurationSection d = c.getConfigurationSection("display-messages");
        assert d != null;
        CHAT_ITEM_FORMAT = d.getString("inchat-item-format");
        CHAT_ITEM_FORMAT_MULTIPLE = d.getString("inchat-item-format-multiple");
        CHAT_INVENTORY_FORMAT = d.getString("inchat-inventory-format");
        COMMAND_ITEM_FORMAT = d.getString("item-display-format");
        COMMAND_ITEM_FORMAT_MULTIPLE = d.getString("item-display-format-multiple");
        COMMAND_INVENTORY_FORMAT = d.getString("inventory-display-format");
        INVENTORY_TITLE = d.getString("displayed-inventory-title");
        ENDERCHEST_TITLE = d.getString("displayed-enderchest-title");
        DEBUG_MODE = c.getBoolean("debug-mode");
        BUNGEE = c.getBoolean("send-to-bungee");
        ITEM_TRIGGERS = c.getStringList("triggers.item");
        ENDERCHEST_TRIGGERS = c.getStringList("triggers.enderchest");
        INVENTORY_TRIGGERS = c.getStringList("triggers.inventory");
        BLACKLISTED_ITEMS = new ArrayList<>();
        for (String s : c.getStringList("blacklisted-items")) {
            Material mat = Material.matchMaterial(s);
            if (mat == null) continue;
            BLACKLISTED_ITEMS.add(mat);
        }
    }

}
