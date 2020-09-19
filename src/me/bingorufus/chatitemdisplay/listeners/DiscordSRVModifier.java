package me.bingorufus.chatitemdisplay.listeners;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageSentEvent;
import github.scarsz.discordsrv.api.events.GameChatMessagePostProcessEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.Display;

public class DiscordSRVModifier {
	ChatItemDisplay m;
	char bell = '\u0007';

	String displayRegex = "(?s).*" + bell + "cid.*" + bell + ".*";

	public DiscordSRVModifier(ChatItemDisplay m) {
		this.m = m;
	}

	@Subscribe
	public void afterSent(DiscordGuildMessageSentEvent e) {

	}
	@Subscribe(priority = ListenerPriority.MONITOR)
	public void onSend(GameChatMessagePostProcessEvent e) {
		if (!e.getProcessedMessage().matches(displayRegex))
			return;
		String msg = e.getProcessedMessage();
		Pattern pattern = Pattern.compile("\u0007cid(.*?)\u0007");

		Matcher matcher = pattern.matcher(msg);
		List<Display> displays = new ArrayList<Display>();
		while (matcher.find()) {

			String json = matcher.group(1);

			JsonObject jo = (JsonObject) new JsonParser().parse(json);

			Display dis = m.getDisplayedManager().getDisplayed(jo.get("id").getAsLong());
			displays.add(dis);
			msg = msg.replaceFirst(Pattern.quote(bell + "cid" + json + bell),
					dis.getDisplayable().getInfo().loggerMessage());
			matcher = pattern.matcher(msg);
		}

		e.setProcessedMessage(msg);
		displays.forEach(display -> {
			MessageChannel m = DiscordUtil.getTextChannelById(e.getChannel());
			if (e.getChannel() == null)
				m = DiscordSRV.getPlugin().getMainTextChannel();

			File f = display.getImage();
			if (f == null)
				return;
			m.sendFile(f, "item.png").queueAfter(1L, TimeUnit.MILLISECONDS);
			;
		});


	}



}
