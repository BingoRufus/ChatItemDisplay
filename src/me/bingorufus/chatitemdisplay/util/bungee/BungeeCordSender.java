package me.bingorufus.chatitemdisplay.util.bungee;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.Display;
import me.bingorufus.chatitemdisplay.displayables.Displayable;

public class BungeeCordSender {

	ChatItemDisplay m;

	public BungeeCordSender(ChatItemDisplay m) {
		this.m = m;
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


		} catch (IOException e) {
		}
		if (m.getConfig().getBoolean("debug-mode"))
			Bukkit.getLogger().info("Sent data: " + data);

		Bukkit.getServer().sendPluginMessage(m, "chatitemdisplay:out", b.toByteArray());
	}



	public void pingBungee() {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		m.setBungee(false);
		m.pingTime = System.currentTimeMillis();
		try {
			out.writeUTF("BungeePing");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (m.getConfig().getBoolean("debug-mode")) {
			Bukkit.getLogger().info("Sent a ping to Bungee");
		}

		Bukkit.getServer().sendPluginMessage(m, "chatitemdisplay:out", b.toByteArray());

	}

}
