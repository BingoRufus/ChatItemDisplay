package me.bingorufus.chatitemdisplay.utils.bungee;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.displayables.DisplayInventory;
import me.bingorufus.chatitemdisplay.displayables.DisplayItem;

public class BungeeCordSender {

	ChatItemDisplay m;

	public BungeeCordSender(ChatItemDisplay m) {
		this.m = m;
	}

	public void sendItem(DisplayItem dis, boolean isCmd) {

		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);

		try {
			out.writeUTF("ItemSender");

			out.writeUTF(dis.serialize());

			out.writeBoolean(isCmd);


		} catch (IOException e) {
		}
		Bukkit.getServer().sendPluginMessage(m, "chatitemdisplay:out", b.toByteArray());

	}

	public void sendInv(DisplayInventory dis, boolean isCmd) {

		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);

		try {
			out.writeUTF("InventorySender");

			out.writeUTF(dis.serialize());

			out.writeBoolean(isCmd);

		} catch (IOException e) {
		}
		Bukkit.getServer().sendPluginMessage(m, "chatitemdisplay:out", b.toByteArray());

	}
}
