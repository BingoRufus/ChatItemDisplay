package com.bingorufus.chatitemdisplay.api;

import com.bingorufus.chatitemdisplay.DisplayedManager;
import com.bingorufus.chatitemdisplay.api.display.DisplayType;
import com.bingorufus.chatitemdisplay.util.CommandRegistry;
import com.bingorufus.chatitemdisplay.util.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.LinkedList;

public class ChatItemDisplayAPI {

    private static final DisplayedManager DISPLAYED_MANAGER = new DisplayedManager();
    private static final Cooldown<Player> DISPLAY_COOLDOWN = new Cooldown<>(0);
    private static final LinkedList<DisplayType<?>> registeredDisplayables = new LinkedList<>();

    private ChatItemDisplayAPI() {

    }


    public static DisplayedManager getDisplayedManager() {
        return DISPLAYED_MANAGER;
    }

    public static LinkedList<DisplayType<?>> getRegisteredDisplayables() {
        return registeredDisplayables;
    }


    /**
     * Gets an instance of a display type from the class path of the display type.
     * An instance has to be registered for this to return an instance.
     * If none has been registered with the specified class path the instance will return null
     *
     * @param displayTypeClass The class of the display type
     * @return An instance of the display type with the given class path.
     * @see #registerDisplayable(DisplayType)
     */
    public static DisplayType<?> getDisplayType(Class<? extends DisplayType<?>> displayTypeClass) {
        DisplayType<?> displayType = ChatItemDisplayAPI.getRegisteredDisplayables().stream().filter(type -> type.getClass().equals(displayTypeClass)).findFirst().orElse(null);
        if (displayType == null) {
            Bukkit.getLogger().warning("Cannot find a displaytype that has the class path of: " + displayTypeClass.getCanonicalName());
            return null;
        }
        return displayType;
    }


    /**
     * Register an instance of a {@link DisplayType} so that it can be displayed.
     *
     * @param displayType an instance of {@link DisplayType}
     * @apiNote This method must be called upon server startup.
     */
    public static void registerDisplayable(DisplayType<?> displayType) {
        registeredDisplayables.add(displayType);
        CommandRegistry.registerAlias(displayType);
    }

    public static Cooldown<Player> getDisplayCooldown() {
        return DISPLAY_COOLDOWN;
    }

}
