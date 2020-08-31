package me.bingorufus.chatitemdisplay.util.bungee;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.displayables.DisplayInventory;
import me.bingorufus.chatitemdisplay.displayables.DisplayItem;
import me.bingorufus.chatitemdisplay.displayables.Displayable;

public class BungeeCordSender {

	ChatItemDisplay m;

	public BungeeCordSender(ChatItemDisplay m) {
		this.m = m;
	}

	public void send(Displayable dis, boolean isCmd) {
		if (dis instanceof DisplayItem)
			sendItem((DisplayItem) dis, isCmd);
		if (dis instanceof DisplayInventory)
			sendInv((DisplayInventory) dis, isCmd);

	}

	private void sendItem(DisplayItem dis, boolean isCmd) {
String data = null;
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);

		try {
			out.writeUTF("ItemSender");
			data = dis.serialize();
			out.writeUTF(data);

			out.writeBoolean(isCmd);


		} catch (IOException e) {
		}
		if (m.getConfig().getBoolean("debug-mode"))
			Bukkit.getLogger().info("Sent data: " + data);

		Bukkit.getServer().sendPluginMessage(m, "chatitemdisplay:out", b.toByteArray());

	}

	private void sendInv(DisplayInventory dis, boolean isCmd) {
		String data = null;
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);

		try {
			out.writeUTF("InventorySender");

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
