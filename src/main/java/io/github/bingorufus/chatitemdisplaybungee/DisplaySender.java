package io.github.bingorufus.chatitemdisplaybungee;

import net.md_5.bungee.api.connection.Server;

public class DisplaySender {
    public void relayMessage(Server from, byte[] data) {
        ChatItemDisplayBungee.getInstance().getProxy().getServers().values().forEach(server -> {
            if (server.equals(from.getInfo()))
                return;
            server.sendData("chatitemdisplay:in", data);
        });

    }


}
