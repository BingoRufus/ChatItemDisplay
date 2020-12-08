package me.bingorufus.chatitemdisplay.util.bungee;


import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.Display;
import me.bingorufus.chatitemdisplay.displayables.Displayable;
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
            out.writeUTF("DisplaySender");
            data = dis.serialize();
            out.writeUTF(data);

            out.writeBoolean(isCmd);


        } catch (IOException ignored) {
        }
        if (m.getConfig().getBoolean("debug-mode"))
            Bukkit.getLogger().info("Sent data: " + data);

        Bukkit.getServer().sendPluginMessage(m, "chatitemdisplay:out", b.toByteArray());
    }


}
