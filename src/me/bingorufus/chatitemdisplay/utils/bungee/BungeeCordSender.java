package me.bingorufus.chatitemdisplay.utils.bungee;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.Display;
import me.bingorufus.chatitemdisplay.utils.iteminfo.ItemStackTranslator;
import net.md_5.bungee.api.chat.TextComponent;

public class BungeeCordSender {

	ChatItemDisplay m;

	public BungeeCordSender(ChatItemDisplay m) {
		this.m = m;
	}

	public void sendTextComponent(TextComponent tc, Display dis, boolean isCmd) {
		// chatItemDisplay.displays.put(e.getPlayer().getName(), new
		// Display(chatItemDisplay, e.getPlayer()));

		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
//new Display(m, item, playerName, displayName, fromBungee)
		try {
			out.writeUTF("ItemSender");// Subchannel, Material Name, Item ammount, ItemStack nbtdata, Player name,
										// Display name, Is command
			ItemStack item = dis.item;

			out.writeUTF(item.getType().name());
			out.writeInt(item.getAmount());
			out.writeUTF(new ItemStackTranslator().getNBT(item));


			out.writeUTF(dis.playerName);
			out.writeUTF(dis.displayName);

			out.writeBoolean(isCmd);


		} catch (IOException e) {
		}
		Bukkit.getServer().sendPluginMessage(m, "chatitemdisplay:itemout", b.toByteArray());



	}
}
