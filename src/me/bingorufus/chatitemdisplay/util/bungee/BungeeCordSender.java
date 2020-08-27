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

	private void sendInv(DisplayInventory dis, boolean isCmd) {

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
