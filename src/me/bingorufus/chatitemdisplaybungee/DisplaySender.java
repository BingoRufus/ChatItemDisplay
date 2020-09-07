package me.bingorufus.chatitemdisplaybungee;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.Server;

public class DisplaySender {
	public void ping(Server from) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("BungeePing");
		from.sendData("chatitemdisplay:in", out.toByteArray());
	}


	public void sendMessage(Server from, String subchannel, String item, boolean isCmd) {


		ByteArrayDataOutput out = ByteStreams.newDataOutput(); // Subchannel, Serialized display, Is command

		out.writeUTF(subchannel.equalsIgnoreCase("DisplaySender") ? "DisplayReceiver" : subchannel); // Sub Channel

		out.writeUTF(item); // Serialized Display

		out.writeBoolean(isCmd);// Is a command



		BungeeCord.getInstance().getServers().values().forEach(server -> {
			if (server.equals(from.getInfo()))
				return;

			server.sendData("chatitemdisplay:in", out.toByteArray());

		});

	}

	
}
