package com.bingorufus.chatitemdisplay.api.event;


import com.bingorufus.chatitemdisplay.api.display.Displayable;
import com.bingorufus.chatitemdisplay.util.ChatItemConfig;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DisplayPreProcessEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    @Getter
    private final Player player;
    @Getter
    private final Displayable displayable;

    private final boolean inChatDisplay;
    @Getter
    @Setter
    private boolean cancelled;
    @Getter
    @Setter
    private String cancellationMessage = ChatItemConfig.MISSING_PERMISSION_GENERIC.getCachedValue();

    public DisplayPreProcessEvent(Player player, Displayable displayable, boolean fromChat) {
        super(!Bukkit.isPrimaryThread());
        this.player = player;
        this.displayable = displayable;
        this.inChatDisplay = fromChat;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Check if the displayable was displayed through chat or through a command
     *
     * @return true - Displayed in chat | false - Displayed through a command
     */
    public boolean isInChatDisplay() {
        return inChatDisplay;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }


}