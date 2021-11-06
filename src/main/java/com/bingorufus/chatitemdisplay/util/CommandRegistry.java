package com.bingorufus.chatitemdisplay.util;

import com.bingorufus.chatitemdisplay.ChatItemDisplay;
import com.bingorufus.chatitemdisplay.api.display.DisplayType;
import com.bingorufus.chatitemdisplay.executors.display.DisplayCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

import java.lang.reflect.Field;
import java.util.Locale;

public class CommandRegistry {
    private static CommandMap commandMap;

    static {
        try {
            Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public static void registerAlias(DisplayType<?> displayType) {
        if (displayType.getCommand() == null) return;

        commandMap.register(ChatItemDisplay.getInstance().getName().toLowerCase(Locale.ROOT), new DisplayCommand(displayType));
    }

}
