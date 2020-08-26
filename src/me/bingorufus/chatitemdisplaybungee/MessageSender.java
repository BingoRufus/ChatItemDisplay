package me.bingorufus.chatitemdisplaybungee;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.Server;

public class MessageSender {



	public void sendMessage(Server from, String item, String UUID, String playerName,
			String displayName,
			boolean isCmd) {


		ByteArrayDataOutput out = ByteStreams.newDataOutput(); // Subchannel, Material Name, Item ammount, ItemStack
																// nbtdata, Player name,
		// Display name, Is command
		out.writeUTF("ItemReceiver"); // Sub Channel

		out.writeUTF(item); // Serialized ItemStack
		out.writeUTF(UUID); // Player name
		out.writeUTF(playerName); // Player name

		out.writeUTF(displayName); // Display name
		out.writeBoolean(isCmd);// Is a command


		BungeeCord.getInstance().getServers().values().forEach(server -> {
			if (server.equals(from.getInfo()))
				return;

			server.sendData("chatitemdisplay:in", out.toByteArray());

		});

	}

	
}
