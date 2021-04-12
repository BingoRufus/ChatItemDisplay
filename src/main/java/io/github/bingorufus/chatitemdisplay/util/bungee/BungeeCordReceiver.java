package io.github.bingorufus.chatitemdisplay.util.bungee;


import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.github.bingorufus.chatitemdisplay.ChatItemDisplay;
import io.github.bingorufus.chatitemdisplay.Display;
import io.github.bingorufus.chatitemdisplay.util.logger.DebugLogger;
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
        ChatItemDisplay.getInstance().getDisplayedManager().addDisplay(display);

        if (in.readBoolean()) {
            display.getDisplayable().broadcastDisplayable();
        }
    }


}
