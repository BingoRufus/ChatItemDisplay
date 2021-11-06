package com.bingorufus.chatitemdisplay.util.bungee;


import com.bingorufus.chatitemdisplay.Display;
import com.bingorufus.chatitemdisplay.api.ChatItemDisplayAPI;
import com.bingorufus.chatitemdisplay.util.logger.DebugLogger;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;


public class BungeeCordReceiver implements PluginMessageListener {


    @Override
    public void onPluginMessageReceived(String channel, @NotNull Player player, byte[] bytes) {// Subchannel, Serialized display,
        // Is command
        if (!channel.equalsIgnoreCase("chatitemdisplay:in"))
            return;
        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);

        String data = in.readUTF();
        DebugLogger.log("Received info: " + data);
        receiveDisplay(data, in);
    }

    public void receiveDisplay(String data, ByteArrayDataInput in) {
        Display display = Display.deserialize(data);
        ChatItemDisplayAPI.getDisplayedManager().addDisplay(display);

        if (in.readBoolean()) {
            display.getDisplayable().broadcastDisplayable();
        }
    }


}
