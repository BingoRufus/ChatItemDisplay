package me.bingorufus.chatitemdisplay.util.bungee;


import org.bukkit.Bukkit;
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
	public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {// Subchannel, Serialized display,
																						// Is command
		if (!channel.equalsIgnoreCase("chatitemdisplay:in"))
			return;
		ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
		String subchannel = in.readUTF();

		if (subchannel.equalsIgnoreCase("BungeePing")) {
			if (m.getConfig().getBoolean("debug-mode")) {
				Bukkit.getLogger().info(
						"Received a ping from bungee ({t}ms)".replace("{t}", System.currentTimeMillis() - m.pingTime + ""));
			}
			m.bungeePing();
			return;

		}
		String data = in.readUTF();
		if (m.getConfig().getBoolean("debug-mode")) {
			Bukkit.getLogger().info("Received info: " + data);
		}
		if (subchannel.equalsIgnoreCase("ItemReceiver")) {
			receiveItem(data, in);
			return;
		}
		if (subchannel.equalsIgnoreCase("InventoryReceiver")) {
			receiveInventory(data, in);
			return;
		}




	}

	public void receiveItem(String data, ByteArrayDataInput in) {
		DisplayItem item = (DisplayItem) Displayable.deserialize(data);
		m.displayed.put(item.getPlayer().toUpperCase(), item);
		if (in.readBoolean() == true) {
			new DisplayItemInfo(m, item).cmdMsg();
		}
	}

	public void receiveInventory(String data, ByteArrayDataInput in) {
		DisplayInventory inv = (DisplayInventory) Displayable.deserialize(data);
		m.displayed.put(inv.getPlayer().toUpperCase(), inv);
		if (in.readBoolean() == true) {
			new DisplayInventoryInfo(m, inv).cmdMsg();
		}
	}

}
