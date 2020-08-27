package me.bingorufus.chatitemdisplaybungee;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.Server;

public class DisplaySender {



	public void sendMessage(Server from, String subchannel, String item, boolean isCmd) {


		ByteArrayDataOutput out = ByteStreams.newDataOutput(); // Subchannel, Material Name, Item ammount, ItemStack
																// nbtdata, Player name,
		// Display name, Is command
		out.writeUTF(subchannel.equalsIgnoreCase("ItemSender") ? "ItemReceiver"
				: subchannel.equalsIgnoreCase("InventorySender") ? "InventoryReceiver" : subchannel); // Sub Channel

		out.writeUTF(item); // Serialized ItemStack

		out.writeBoolean(isCmd);// Is a command


		BungeeCord.getInstance().getServers().values().forEach(server -> {
			if (server.equals(from.getInfo()))
				return;

			server.sendData("chatitemdisplay:in", out.toByteArray());

		});

	}

	
}
