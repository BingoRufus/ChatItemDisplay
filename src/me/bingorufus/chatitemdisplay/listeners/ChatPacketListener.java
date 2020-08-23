package me.bingorufus.chatitemdisplay.listeners;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.utils.MessageBroadcaster;
import net.md_5.bungee.api.chat.BaseComponent;
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
		if (m.invs.keySet().contains(e.getPlayer().getOpenInventory().getTopInventory())) {
			e.setCancelled(true);
			return;
		}
	}

	@Override
	public void onPacketSending(final PacketEvent e) {

		String json;
		PacketContainer packet = e.getPacket();
		WrappedChatComponent chat = packet.getChatComponents().read(0);
		TextComponent tc = new TextComponent();
		BaseComponent[] baseComps;
		
		if (chat == null) {
			Object chatPacket = packet.getHandle();
			
			try {
				Field f = chatPacket.getClass().getDeclaredField("components");
				baseComps = (BaseComponent[]) f.get(chatPacket);
				tc = new TextComponent(baseComps);
				json = ComponentSerializer.toString(tc);

			} catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
				ex.printStackTrace();
				return;
			}

		} else {
			json = chat.getJson();
			baseComps = ComponentSerializer.parse(chat.getJson());
			tc = new TextComponent(baseComps);
		}
		

		if (!json.contains("\\u0007cid"))
			return;

		if (msgs.containsKey(json) && msgs.get(json) == w.getFullTime()) { // Check if
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




		String replace = tc.toLegacyText().substring(tc.toLegacyText().indexOf(bell),
				tc.toLegacyText().lastIndexOf(bell) + 1);
		String legacyText = tc.toLegacyText().replace(replace,
				replace + ChatColor.getLastColors(tc.toLegacyText().substring(0, tc.toLegacyText().indexOf(replace))));
		String[] legacy = legacyText.split(replace);

		TextComponent pt1 = new TextComponent(TextComponent.fromLegacyText(legacy[0]));

		TextComponent pt3 = legacy.length > 1 ? new TextComponent(TextComponent.fromLegacyText(legacy[1]))
				: new TextComponent("");
		String displaying = replace.substring(replace.indexOf("cid") + 3, replace.lastIndexOf(bell));

		String format = ChatColor.translateAlternateColorCodes('&',
				m.getConfig().getString("messages.inchat-format") + "&r");
		TextComponent pt2 = new TextComponent(format.substring(0, format.indexOf("%item%")));
		pt2.addExtra(m.displays.get(displaying.toUpperCase()).getHover());
		pt2.addExtra(format.substring(format.indexOf("%item%") + 6, format.length()));

		new MessageBroadcaster().broadcast(
				new TextComponent(pt1, pt2, pt3));
		msgs.put(json, w.getFullTime());
		e.setCancelled(true);
		
	}



}
