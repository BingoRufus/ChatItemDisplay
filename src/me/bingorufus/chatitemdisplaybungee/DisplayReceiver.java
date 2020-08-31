package me.bingorufus.chatitemdisplaybungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class DisplayReceiver implements Listener {

	@EventHandler
	public void on(PluginMessageEvent e) {

		if (!e.getTag().equalsIgnoreCase("chatitemdisplay:out"))
			return;
		ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());


		if (!(e.getReceiver() instanceof UserConnection))
			return;

		UserConnection rec = (UserConnection) e.getReceiver();
		String subChannel = in.readUTF();
		if (subChannel.equalsIgnoreCase("BungeePing")) {
			new DisplaySender().ping((Server) rec.getServer());
			return;
		}
		Server receiver = (Server) rec.getServer();
		String item = in.readUTF();

		boolean isCmd = in.readBoolean();

		new DisplaySender().sendMessage(receiver, subChannel, item, isCmd);


	}

}
