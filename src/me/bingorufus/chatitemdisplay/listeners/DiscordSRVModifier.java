package me.bingorufus.chatitemdisplay.listeners;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageSentEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.displayables.DisplayInfo;
import me.bingorufus.chatitemdisplay.displayables.DisplayInventory;
import me.bingorufus.chatitemdisplay.displayables.DisplayInventoryInfo;
import me.bingorufus.chatitemdisplay.displayables.DisplayItem;
import me.bingorufus.chatitemdisplay.displayables.DisplayItemInfo;
import me.bingorufus.chatitemdisplay.displayables.Displayable;

public class DiscordSRVModifier {
	ChatItemDisplay m;
	char bell = '\u0007';

	String displayRegex = "(?s).*" + bell + "cid.*" + bell + ".*";

	public DiscordSRVModifier(ChatItemDisplay m) {
		this.m = m;
	}
	@Subscribe(priority = ListenerPriority.LOWEST)
	public void onSend(DiscordGuildMessageSentEvent e) {

		if (!e.getMessage().getContentDisplay().matches(displayRegex))
			return;
		String message = e.getMessage().getContentRaw();

		String displaying = getDisplaying(message);
		Displayable dis = m.displayed.get(displaying.toUpperCase());
		DisplayInfo di = null;
		if (dis instanceof DisplayItem)
			di = new DisplayItemInfo(m, (DisplayItem) dis);
		if (dis instanceof DisplayInventory)
			di = new DisplayInventoryInfo(m, (DisplayInventory) dis);
		String out = message.replaceAll("\u0007cid(.*?)\u0007", di.loggerMessage());
		Message msg = e.getMessage();
		msg.editMessage(out).queue();

	}

	private String getDisplaying(String s) {
		String player = null;

		Pattern pattern = Pattern.compile("\u0007cid(.*?)\u0007"); // Searches for a string that starts and ends
																	// with the bell character
		Matcher matcher = pattern.matcher(s);

		if (matcher.find()) {
			player = matcher.group(1);
		}

		return player == null ? "" : player;
	}

}
