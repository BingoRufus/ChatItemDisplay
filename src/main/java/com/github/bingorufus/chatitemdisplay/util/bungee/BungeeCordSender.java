package com.github.bingorufus.chatitemdisplay.util.bungee;


import com.github.bingorufus.chatitemdisplay.ChatItemDisplay;
import com.github.bingorufus.chatitemdisplay.Display;
import com.github.bingorufus.chatitemdisplay.api.display.Displayable;
import com.github.bingorufus.chatitemdisplay.util.ChatItemConfig;
import org.bukkit.Bukkit;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BungeeCordSender {

    final ChatItemDisplay m;

    public BungeeCordSender() {
        this.m = ChatItemDisplay.getInstance();
    }

    public void send(Displayable displayable, boolean isCmd) {
        Display dis = m.getDisplayedManager().getDisplay(displayable);
        String data = null;
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            data = dis.serialize();
            out.writeUTF(data);
            out.writeBoolean(isCmd);


        } catch (IOException ignored) {
        }
        if (ChatItemConfig.DEBUG_MODE)
            Bukkit.getLogger().info("Sent data: " + data);

        Bukkit.getServer().sendPluginMessage(m, "chatitemdisplay:out", b.toByteArray());
    }


}
