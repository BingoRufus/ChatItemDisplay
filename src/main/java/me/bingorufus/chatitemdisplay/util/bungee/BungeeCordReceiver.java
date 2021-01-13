package me.bingorufus.chatitemdisplay.util.bungee;


import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.Display;
import me.bingorufus.chatitemdisplay.util.ChatItemConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;


public class BungeeCordReceiver implements PluginMessageListener {
    private final ChatItemDisplay m;

    public BungeeCordReceiver() {
        m = ChatItemDisplay.getInstance();
    }


    @Override
    public void onPluginMessageReceived(String channel, @NotNull Player player, byte[] bytes) {// Subchannel, Serialized display,
        // Is command
        if (!channel.equalsIgnoreCase("chatitemdisplay:in"))
            return;
        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);

        String data = in.readUTF();
        if (ChatItemConfig.DEBUG_MODE) {
            Bukkit.getLogger().info("Received info: " + data);
        }
        receiveDisplay(data, in);


    }

    public void receiveDisplay(String data, ByteArrayDataInput in) {
        Display display = Display.deserialize(data);
        m.getDisplayedManager().addDisplay(display);

        if (in.readBoolean()) {
            display.getDisplayable().getInfo().cmdMsg();
        }
    }


}
