package me.BingoRufus.ChatDisplay.ListenersAndExecutors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import me.BingoRufus.ChatDisplay.Main;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class ChatPacketListener extends PacketAdapter {
	char bell = '\u0007';

	Main m;

	public ChatPacketListener(Plugin plugin, ListenerPriority listenerPriority, PacketType... types) {
		super(plugin, listenerPriority, types);
		m = (Main) plugin;
	}

	@Override
	public void onPacketSending(final PacketEvent e) {
		PacketContainer packet = e.getPacket();
		WrappedChatComponent chat = packet.getChatComponents().read(0);
		if (chat == null)
			return;

		if (!chat.getJson().contains("\\u0007cid"))
			return;

		TextComponent tc = new TextComponent(ComponentSerializer.parse(chat.getJson()));

		String replace = tc.toLegacyText().substring(tc.toLegacyText().indexOf(bell),
				tc.toLegacyText().lastIndexOf(bell) + 1);

		String[] legacy = tc.toLegacyText().split(replace);

		TextComponent pt1 = new TextComponent(TextComponent.fromLegacyText(legacy[0]));

		TextComponent pt3 = legacy.length > 1 ? new TextComponent(TextComponent.fromLegacyText(legacy[1]))
				: new TextComponent("");
		Player displaying = Bukkit
				.getPlayerExact(replace.substring(replace.indexOf("cid") + 3, replace.lastIndexOf(bell)));

		TextComponent pt2 = m.displays.get(displaying.getName()).getHover();

		pt2.setText(
				ChatColor.translateAlternateColorCodes('&', m.getConfig().getString("messages.inchat-format") + "&r")
						.replaceAll("%item%", pt2.getText()));
		String messageJson = ComponentSerializer.toString(new TextComponent(pt1, pt2, pt3)); // Converts the text
																								// components into 1
																								// text component, and
																								// then turns it into
																								// json text
		packet.getChatComponents().write(0, WrappedChatComponent.fromJson(messageJson));// Turns the json into a
																						// WrappedChatComponent, and
																						// replaces the previous message

	}

}
