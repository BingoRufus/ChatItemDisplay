package io.github.bingorufus.chatitemdisplay.listeners.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import io.github.bingorufus.chatitemdisplay.ChatItemDisplay;
import io.github.bingorufus.chatitemdisplay.api.ChatItemDisplayAPI;

public class RecipeSelector extends PacketAdapter {
    public RecipeSelector() {
        super(ChatItemDisplay.getInstance(), ListenerPriority.LOWEST, PacketType.Play.Client.AUTO_RECIPE);
    }

    /**
     * /Prevents items from being duplicated from displayed furnaces by shift clicking a recipe in the recipe booked
     *
     * @param e The PacketEvent
     */
    @Override
    public void onPacketReceiving(final PacketEvent e) {
        if (ChatItemDisplayAPI.getDisplayedManager().getChatItemDisplayInventories().contains(e.getPlayer().getOpenInventory().getTopInventory())) {
            e.setCancelled(true);
        }

    }
}
