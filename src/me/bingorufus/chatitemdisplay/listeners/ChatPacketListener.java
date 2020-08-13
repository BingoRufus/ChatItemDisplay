package me.bingorufus.chatitemdisplay.listeners;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.utils.MessageBroadcaster;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class ChatPacketListener extends PacketAdapter {
	HashMap<String, Long> msgs = new HashMap<String, Long>(); // <JSONMessage,Time>

	char bell = '\u0007';

	ChatItemDisplay m;
	World w;

	public ChatPacketListener(Plugin plugin, ListenerPriority listenerPriority, PacketType... types) {
		super(plugin, listenerPriority, types);
		m = (ChatItemDisplay) plugin;
		w = Bukkit.getWorlds().get(0);
	}
	@Override
	public void onPacketReceiving(final PacketEvent e) {
		if (m.invs.contains(e.getPlayer().getOpenInventory().getTopInventory())) {
			e.setCancelled(true);
			return;
		}
	}

	@Override
	public void onPacketSending(final PacketEvent e) {


		PacketContainer packet = e.getPacket();
		WrappedChatComponent chat = packet.getChatComponents().read(0);
		if (chat == null)
			return;

		if (!chat.getJson().contains("\\u0007cid"))
			return;

		if (msgs.containsKey(chat.getJson()) && msgs.get(chat.getJson()) == w.getFullTime()) { // Check if
																											// packet is
																											// being
																											// sent
																											// another
																											// time to
																											// someone
																											// else

			e.setCancelled(true);
			return;
		}
		TextComponent tc = new TextComponent(ComponentSerializer.parse(chat.getJson()));

		String replace = tc.toLegacyText().substring(tc.toLegacyText().indexOf(bell),
				tc.toLegacyText().lastIndexOf(bell) + 1);

		String[] legacy = tc.toLegacyText().split(replace);

		TextComponent pt1 = new TextComponent(TextComponent.fromLegacyText(legacy[0]));

		TextComponent pt3 = legacy.length > 1 ? new TextComponent(TextComponent.fromLegacyText(legacy[1]))
				: new TextComponent("");
		Player displaying = Bukkit
				.getPlayerExact(replace.substring(replace.indexOf("cid") + 3, replace.lastIndexOf(bell)));

		String format = ChatColor.translateAlternateColorCodes('&',
				m.getConfig().getString("messages.inchat-format") + "&r");
		TextComponent pt2 = new TextComponent(format.substring(0, format.indexOf("%item%")));
		pt2.addExtra(m.displays.get(displaying.getName()).getHover());
		pt2.addExtra(format.substring(format.indexOf("%item%") + 6, format.length()));

		new MessageBroadcaster().broadcast(m, m.displays.get(displaying.getName()), false, false,
				new TextComponent(pt1, pt2, pt3));
		msgs.put(chat.getJson(), w.getFullTime());
		e.setCancelled(true);
	}

}
