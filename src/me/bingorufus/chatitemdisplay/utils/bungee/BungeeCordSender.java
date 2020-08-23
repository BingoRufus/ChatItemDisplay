package me.bingorufus.chatitemdisplay.utils.bungee;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.Display;
import me.bingorufus.chatitemdisplay.utils.iteminfo.ItemStackTranslator;

public class BungeeCordSender {

	ChatItemDisplay m;

	public BungeeCordSender(ChatItemDisplay m) {
		this.m = m;
	}

	public void sendItem(Display dis, boolean isCmd) {
		// chatItemDisplay.displays.put(e.getPlayer().getName(), new
		// Display(chatItemDisplay, e.getPlayer()));

		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
//new Display(m, item, playerName, displayName, fromBungee)
		try {
			out.writeUTF("ItemSender");// Subchannel, Material Name, Item ammount, ItemStack nbtdata, Player UUID,
										// Display name, Is command
			ItemStack item = dis.item;

			out.writeUTF(item.getType().name());
			out.writeInt(item.getAmount());
			out.writeUTF(new ItemStackTranslator().getNBT(item));


			out.writeUTF(dis.getUUID().toString());
			out.writeUTF(dis.getPlayerName());

			out.writeUTF(dis.displayName);

			out.writeBoolean(isCmd);


		} catch (IOException e) {
		}
		Bukkit.getServer().sendPluginMessage(m, "chatitemdisplay:out", b.toByteArray());



	}
}
