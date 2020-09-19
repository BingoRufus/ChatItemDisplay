package me.bingorufus.chatitemdisplay.listeners;

import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.DisplayParser;

public class PrivateMessageListener extends PacketAdapter {
	HashMap<Player, String> cmdMessage = new HashMap<Player, String>();
	private ChatItemDisplay m;
	List<String> messageCommand;
	public PrivateMessageListener(Plugin plugin, ListenerPriority listenerPriority, PacketType... types) {
		super(plugin, listenerPriority, types);
		m = (ChatItemDisplay) plugin;
		messageCommand = m.getConfig().getStringList("message-command");
		messageCommand.replaceAll(cmd -> {
			return cmd + " ";
		});

	}

	@Override
	public void onPacketReceiving(final PacketEvent e) {
		if (m.useOldFormat)
			return;
		PacketContainer packet = e.getPacket();
		String message = packet.getStrings().read(0);
		boolean edit = false;
		cmdMessage.put(e.getPlayer(), message);

		if (!message.startsWith("/"))
			return;
		for (String cmd : messageCommand) {
			if (message.startsWith(cmd)) {
				DisplayParser dp = new DisplayParser(m, message, e.getPlayer(), true);
				message = dp.parse();
				if (dp.cancelMessage())
					return;
				if (!dp.containsDisplay())
					break;
				edit = true;
				break;
			}
		}
		if (edit) {
			e.setCancelled(true);
			new BukkitRunnable() {
				String msg = cmdMessage.get(e.getPlayer());
				@Override
				public void run() {
					e.getPlayer().performCommand(msg.substring(1, msg.length()));
				}
			}.runTaskLater(m, 1L);

		}

	}

}
