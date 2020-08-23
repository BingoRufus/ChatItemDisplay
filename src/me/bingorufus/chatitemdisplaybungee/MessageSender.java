package me.bingorufus.chatitemdisplaybungee;

import java.util.Collection;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

public class MessageSender {



	public void sendMessage(Server from, String material, int ammount, String nbt, String UUID, String playerName,
			String displayName,
			boolean isCmd) {


		ByteArrayDataOutput out = ByteStreams.newDataOutput(); // Subchannel, Material Name, Item ammount, ItemStack
																// nbtdata, Player name,
		// Display name, Is command
		out.writeUTF("ItemReceiver"); // Sub Channel

		out.writeUTF(material); // Serialized ItemStack
		out.writeInt(ammount);
		out.writeUTF(nbt);

		out.writeUTF(UUID); // Player name
		out.writeUTF(playerName); // Player name

		out.writeUTF(displayName); // Display name
		out.writeBoolean(isCmd);// Is a command


		BungeeCord.getInstance().getServers().values().forEach(server -> {
			if (server.equals(from.getInfo()))
				return;
			Collection<ProxiedPlayer> players = server.getPlayers();
			if (players == null || players.isEmpty())
				return;
			server.sendData("chatitemdisplay:in", out.toByteArray());

		});

	}

	
}
