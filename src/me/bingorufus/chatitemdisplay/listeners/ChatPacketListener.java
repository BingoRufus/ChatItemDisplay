package me.bingorufus.chatitemdisplay.listeners;

import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.displayables.DisplayInfo;
import me.bingorufus.chatitemdisplay.displayables.DisplayInventory;
import me.bingorufus.chatitemdisplay.displayables.DisplayInventoryInfo;
import me.bingorufus.chatitemdisplay.displayables.DisplayItem;
import me.bingorufus.chatitemdisplay.displayables.DisplayItemInfo;
import me.bingorufus.chatitemdisplay.displayables.Displayable;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class ChatPacketListener extends PacketAdapter {

	char bell = '\u0007';

	ChatItemDisplay m;

	public ChatPacketListener(Plugin plugin, ListenerPriority listenerPriority, PacketType... types) {
		super(plugin, listenerPriority, types);
		m = (ChatItemDisplay) plugin;
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

		PacketContainer packet = e.getPacket();
		WrappedChatComponent chat = packet.getChatComponents().read(0);
		BaseComponent[] baseComps;
		
		if (chat == null) {
			Object chatPacket = packet.getHandle();
			
			try {
				Field f = chatPacket.getClass().getDeclaredField("components");
				baseComps = (BaseComponent[]) f.get(chatPacket);

			} catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
				ex.printStackTrace();
				return;
			}

		} else {
			baseComps = ComponentSerializer.parse(chat.getJson());
		}
		

		if (!ComponentSerializer.toString(baseComps).contains("\\u0007cid"))
			return;


		try {
			for (int i = 0; i < baseComps[0].getExtra().size(); i++) {
				List<BaseComponent> extra = baseComps[0].getExtra();
				TextComponent bc = (TextComponent) extra.get(i);
				if (!bc.toLegacyText().contains("\u0007cid"))
					continue;

				String replace = null;

				Pattern pattern = Pattern.compile("\u0007(.*?)\u0007"); // Searches for a string that starts and ends
																		// with the bell charachter
				Matcher matcher = pattern.matcher(bc.toLegacyText());
				if (matcher.find()) {
					replace = bell + matcher.group(1) + bell;
				}
				String legacyText = bc.toLegacyText().replace(replace, replace
						+ ChatColor.getLastColors(bc.toLegacyText().substring(0, bc.toLegacyText().indexOf(replace))));



				String displaying = replace.substring(replace.indexOf("cid") + 3, replace.lastIndexOf(bell));
				if (m.getConfig().getBoolean("debug-mode")) {
					Bukkit.getLogger().info(displaying + " is displaying their item");
				}

				Displayable display = m.displayed.get(displaying.toUpperCase());
				if (m.getConfig().getBoolean("debug-mode") && display == null) {
					Bukkit.getLogger().info("Displayed does not contain " + displaying);
					m.displayed.keySet().forEach(key -> {
						Bukkit.getLogger().info(key);

					});
				}

				DisplayInfo disInfo = null;

				if (display instanceof DisplayItem)
					disInfo = new DisplayItemInfo(m, (DisplayItem) display);
				if (display instanceof DisplayInventory)
					disInfo = new DisplayInventoryInfo(m, (DisplayInventory) display);
				if (m.getConfig().getBoolean("debug-mode")) {
					Bukkit.getLogger().info("Displayable is a " + display.getClass().getCanonicalName());
					Bukkit.getLogger().info("Display info is a " + disInfo.getClass().getCanonicalName());

				}

				String[] parts = legacyText.split("((?<=" + replace + ")|(?=" + replace + "))");
				TextComponent hover = disInfo.getHover();

				TextComponent component = new TextComponent();
				for (String part : parts) {
					if (part.equalsIgnoreCase(replace)) {
						component.addExtra(hover);
						continue;
					}
					component.addExtra(part);
				}
				extra.set(i, component);
				baseComps[0].setExtra(extra);

			}
		} catch (NullPointerException npe) {
			npe.printStackTrace();

		}


		packet.getChatComponents().write(0, WrappedChatComponent.fromJson(ComponentSerializer.toString(baseComps)));
		
	}



}
