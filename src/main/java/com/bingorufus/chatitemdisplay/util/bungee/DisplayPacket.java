package com.bingorufus.chatitemdisplay.util.bungee;

import com.bingorufus.chatitemdisplay.Display;
import com.bingorufus.chatitemdisplay.api.ChatItemDisplayAPI;
import com.bingorufus.chatitemdisplay.api.display.Displayable;
import lombok.Getter;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DisplayPacket {
    @Getter
    private byte[] data;
    @Getter
    private UUID UUID;

    public DisplayPacket(UUID id, byte[] data) {
        this.UUID = id;
        this.data = data;
    }

    public static DisplayPacket[] createPackets(Displayable displayable, boolean isCommand) {
        List<ByteArrayOutputStream> packetedData = new ArrayList<>();
        Display dis = ChatItemDisplayAPI.getDisplayedManager().getDisplay(displayable);
        UUID uuid = dis.getId();
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            DataOutputStream dataStream = new DataOutputStream(byteStream);

            dataStream.write(dis.serialize().getBytes(StandardCharsets.UTF_8));


            //Break above byte stream into multiple packets
            int currentIndex = 0;
            for (int i = 0; currentIndex < byteStream.size(); i++) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                DataOutputStream packetDataStream = new DataOutputStream(outputStream);

                packetDataStream.writeUTF(uuid.toString()); // ID
                packetDataStream.writeInt(i); // Packet Count
                int remainingBytes = byteStream.size() - currentIndex;
                int dataSize = Math.min(Short.MAX_VALUE - (outputStream.size() + 1), remainingBytes);
                packetDataStream.write(byteStream.toByteArray(), currentIndex, dataSize); // Data
                currentIndex += dataSize;
                packetedData.add(outputStream);
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            DataOutputStream packetDataStream = new DataOutputStream(outputStream);

            packetDataStream.writeUTF(uuid.toString()); // ID
            packetDataStream.writeInt(Short.MAX_VALUE + 1); // Packet Count
            packetDataStream.writeBoolean(isCommand);
            packetedData.add(outputStream);

        } catch (IOException ignored) {
            ignored.printStackTrace();
        }
        DisplayPacket[] packets = new DisplayPacket[packetedData.size()];
        for (int i = 0; i < packets.length; i++) {
            packets[i] = new DisplayPacket(uuid, packetedData.get(i).toByteArray());
        }
        return packets;
    }

}
