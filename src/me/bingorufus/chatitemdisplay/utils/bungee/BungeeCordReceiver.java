package me.bingorufus.chatitemdisplay.utils.bungee;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.Display;
import me.bingorufus.chatitemdisplay.utils.iteminfo.ItemStackTranslator;


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
		if(!channel.equalsIgnoreCase("chatitemdisplay:itemin")) return;
		ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
		String subchannel = in.readUTF();
		if (!subchannel.equalsIgnoreCase("ItemReceiver"))
			return;
		Material mat = Material.getMaterial(in.readUTF());
		int amt = in.readInt();
		String nbt = in.readUTF();
		String pname = in.readUTF();
		String displayName = in.readUTF();
		boolean isCmd = in.readBoolean();

		ItemStack item = new ItemStackTranslator().fromNBT(new ItemStack(mat, amt), nbt);
		Display dis = new Display(m, item, pname, displayName, true);
		m.displays.put(pname, dis);

		if (isCmd) {
			dis.cmdMsg();
		}


	}
}
