package me.bingorufus.chatitemdisplay.utils.bungee;


import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.displayables.DisplayInventory;
import me.bingorufus.chatitemdisplay.displayables.DisplayInventoryInfo;
import me.bingorufus.chatitemdisplay.displayables.DisplayItem;
import me.bingorufus.chatitemdisplay.displayables.DisplayItemInfo;
import me.bingorufus.chatitemdisplay.displayables.Displayable;


public class BungeeCordReceiver implements PluginMessageListener {
	ChatItemDisplay m;

	public BungeeCordReceiver(ChatItemDisplay m) {
		this.m = m;
	}




	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {// Subchannel, Material Name, Item
																						// ammount, ItemStack nbtdata,
																						// Player name, Display name, Is
																						// command
		if (!channel.equalsIgnoreCase("chatitemdisplay:in"))
			return;
		ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
		String subchannel = in.readUTF();
		if (subchannel.equalsIgnoreCase("ItemReceiver")) {
			receiveItem(in);
			return;
		}
		if (subchannel.equalsIgnoreCase("InventoryReceiver")) {
			receiveInventory(in);
			return;
		}



	}

	public void receiveItem(ByteArrayDataInput in) {
		DisplayItem item = (DisplayItem) Displayable.deserialize(in.readUTF());
		m.displayed.put(item.getPlayer().toUpperCase(), item);
		if (in.readBoolean() == true) {
			new DisplayItemInfo(m, item).cmdMsg();
		}
	}

	public void receiveInventory(ByteArrayDataInput in) {
		DisplayInventory inv = (DisplayInventory) Displayable.deserialize(in.readUTF());
		m.displayed.put(inv.getPlayer().toUpperCase(), inv);
		if (in.readBoolean() == true) {
			new DisplayInventoryInfo(m, inv).cmdMsg();
		}
	}

}
