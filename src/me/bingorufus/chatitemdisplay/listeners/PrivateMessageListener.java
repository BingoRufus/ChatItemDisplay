package me.bingorufus.chatitemdisplay.listeners;

import java.util.List;

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
	private ChatItemDisplay m;
	private String message;
	List<String> messageCommand;
	public PrivateMessageListener(Plugin plugin, ListenerPriority listenerPriority, PacketType... types) {
		super(plugin, listenerPriority, types);
		m = (ChatItemDisplay) plugin;
		messageCommand = m.getConfig().getStringList("message-command");
	}

	@Override
	public void onPacketReceiving(final PacketEvent e) {
		if (m.useOldFormat)
			return;
		PacketContainer packet = e.getPacket();
		message = packet.getStrings().read(0);
		boolean edit = false;
		for (String cmd : messageCommand) {
			if (message.startsWith(cmd)) {
				DisplayParser dp = new DisplayParser(m, message, e.getPlayer(), true);
				message = dp.parse();
				if (dp.cancelMessage())
					return;
				edit = true;
				break;
			}
		}
		if (edit) {
			e.setCancelled(true);
			new BukkitRunnable() {
				@Override
				public void run() {
					e.getPlayer().performCommand(message.substring(1, message.length()));
				}
			}.runTaskLater(m, 1L);

		}

	}

}
