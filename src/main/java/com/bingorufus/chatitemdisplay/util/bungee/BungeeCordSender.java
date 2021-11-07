package com.bingorufus.chatitemdisplay.util.bungee;


import com.bingorufus.chatitemdisplay.ChatItemDisplay;
import com.bingorufus.chatitemdisplay.Display;
import com.bingorufus.chatitemdisplay.api.ChatItemDisplayAPI;
import com.bingorufus.chatitemdisplay.api.display.Displayable;
import com.bingorufus.chatitemdisplay.util.logger.DebugLogger;
import org.bukkit.Bukkit;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BungeeCordSender {


    private BungeeCordSender() {

    }

    public static void send(Displayable displayable, boolean isCmd) {
        List<ByteArrayOutputStream> packetedData = new ArrayList<>();
        Display dis = ChatItemDisplayAPI.getDisplayedManager().getDisplay(displayable);
        UUID uuid = dis.getId();

        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            DataOutputStream dataStream = new DataOutputStream(byteStream);

            dataStream.writeUTF(dis.serialize());
            dataStream.writeBoolean(isCmd);
            dataStream.writeUTF("Done");

            //Break above byte stream into multiple packets
            int currentIndex = 0;
            for (int i = 0; currentIndex < byteStream.size(); i++) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                DataOutputStream packetDataStream = new DataOutputStream(outputStream);

                packetDataStream.writeUTF(uuid.toString()); // ID
                packetDataStream.write(i); // Packet Count
                int remainingBytes = byteStream.size() - currentIndex;
                int dataSize = Math.min(Short.MAX_VALUE - (outputStream.size() + 1), remainingBytes);
                packetDataStream.write(byteStream.toByteArray(), currentIndex, dataSize); // Data
                currentIndex += dataSize;

                packetedData.add(outputStream);
            }


        } catch (IOException ignored) {
        }
        for (ByteArrayOutputStream byteStream : packetedData) {
            DebugLogger.log("Sent a packet to bungee proxy");
            Bukkit.broadcastMessage(byteStream.toByteArray().length + "");
            Bukkit.getServer().sendPluginMessage(ChatItemDisplay.getInstance(), "chatitemdisplay:out", byteStream.toByteArray());
        }
    }


}
