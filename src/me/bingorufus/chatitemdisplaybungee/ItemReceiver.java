package me.bingorufus.chatitemdisplaybungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ItemReceiver implements Listener {

	@EventHandler
	public void on(PluginMessageEvent e) {

		if (!e.getTag().equalsIgnoreCase("chatitemdisplay:out"))
			return;
		ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
		String subChannel = in.readUTF();

		if (!subChannel.equalsIgnoreCase("ItemSender")) {
			return;
		}

		// Subchannel, Material Name, Item ammount, ItemStack nbtdata, Player name,
		// Display name, Is command
		if (!(e.getReceiver() instanceof UserConnection))
			return;
		UserConnection rec = (UserConnection) e.getReceiver();
		
		Server receiver = (Server) rec.getServer();
		String item = in.readUTF();

		String uuid = in.readUTF();
		String playerName = in.readUTF();
		String displayName = in.readUTF();
		boolean isCmd = in.readBoolean();

		new MessageSender().sendMessage(receiver, item, uuid, playerName, displayName, isCmd);


	}

}
