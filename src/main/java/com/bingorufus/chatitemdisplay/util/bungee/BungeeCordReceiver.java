package com.bingorufus.chatitemdisplay.util.bungee;


import com.bingorufus.chatitemdisplay.Display;
import com.bingorufus.chatitemdisplay.api.ChatItemDisplayAPI;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


public class BungeeCordReceiver implements PluginMessageListener {
    LinkedHashMap<DisplayPacket, Integer> displayPacketMap = new LinkedHashMap<>();


    @Override
    public void onPluginMessageReceived(String channel, @NotNull Player player, byte[] bytes) {// Subchannel, Serialized display,
        // Is command
        if (!channel.equalsIgnoreCase("chatitemdisplay:in"))
            return;
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ByteArrayDataInput in = ByteStreams.newDataInput(byteArrayInputStream);
        String uuid = in.readUTF();

        int packetNum = in.readInt();
        if (packetNum == Short.MAX_VALUE + 1) {
            receiveDisplay(UUID.fromString(uuid), in.readBoolean());
            return;
        }
        byte[] data = new byte[byteArrayInputStream.available()];
        in.readFully(data);
        DisplayPacket packet = new DisplayPacket(UUID.fromString(uuid), data);
        displayPacketMap.put(packet, packetNum);
    }

    public void receiveDisplay(UUID uuid, boolean isCommand) {
        List<DisplayPacket> packetList = displayPacketMap.keySet().stream().filter(packet -> packet.getUUID().equals(uuid)).sorted(Comparator.comparingInt(p -> displayPacketMap.get(p))).collect(Collectors.toList());
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);

        for (DisplayPacket packet : packetList) {
            displayPacketMap.remove(packet);
            try {
                dataStream.write(packet.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String serializedDisplay = byteStream.toString();
        Display display = Display.deserialize(serializedDisplay);
        ChatItemDisplayAPI.getDisplayedManager().addDisplay(display);

        if (isCommand) {
            display.getDisplayable().broadcastDisplayable();
        }
    }


}
