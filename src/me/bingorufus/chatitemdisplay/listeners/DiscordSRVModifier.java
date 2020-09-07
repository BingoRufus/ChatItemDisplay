package me.bingorufus.chatitemdisplay.listeners;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageSentEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.Display;

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

		String message = newMessage(e.getMessage().getContentRaw());

		Message msg = e.getMessage();
		msg.editMessage(message).queue();

	}


	private String newMessage(String msg) {

		Pattern pattern = Pattern.compile("\u0007cid(.*?)\u0007");

		Matcher matcher = pattern.matcher(msg);

		while (matcher.find()) {

			String json = matcher.group(1);

			JsonObject jo = (JsonObject) new JsonParser().parse(json);

			Display dis = m.getDisplayedManager().getDisplayed(jo.get("id").getAsLong());

			msg = msg.replaceFirst(Pattern.quote(bell + "cid" + json + bell),
					dis.getDisplayable().getInfo(m).loggerMessage());
			matcher = pattern.matcher(msg);

		}
		return msg;

	}

}
