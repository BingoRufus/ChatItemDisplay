package me.bingorufus.chatitemdisplay.util;


import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;
import org.bukkit.Bukkit;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.displayables.DisplayInfo;
import me.bingorufus.chatitemdisplay.displayables.DisplayInventory;
import me.bingorufus.chatitemdisplay.displayables.DisplayInventoryInfo;
import me.bingorufus.chatitemdisplay.displayables.DisplayItem;
import me.bingorufus.chatitemdisplay.displayables.DisplayItemInfo;
import me.bingorufus.chatitemdisplay.displayables.Displayable;

public class LoggerFilter extends AbstractFilter { // This just makes it so that "cidPlayer" isn't sent to console, but
													// rather it includes the appropriate text
	char bell = '\u0007';
	ChatItemDisplay m;
	String displayRegex = "(?s).*" + bell + "cid.*" + bell + ".*";
	Logger logger;
	public LoggerFilter(ChatItemDisplay m) {
		this.m = m;
	}

	@Override
	public Result filter(LogEvent event) {
		if (event == null)
			return Result.NEUTRAL;
		Result res = isLoggable(event.getMessage().getFormattedMessage());
		String message = event.getMessage().getFormattedMessage();
		if (res.equals(Result.DENY)) {
			String displaying = getDisplaying(message);
			Displayable dis = m.displayed.get(displaying.toUpperCase());
			DisplayInfo di = null;
			if (dis instanceof DisplayItem)
				di = new DisplayItemInfo(m, (DisplayItem) dis);
			if (dis instanceof DisplayInventory)
				di = new DisplayInventoryInfo(m, (DisplayInventory) dis);
			String loggermsg = message.replaceAll("\u0007cid(.*?)\u0007", di.loggerMessage());
			Bukkit.getLogger().log(new LogRecord(java.util.logging.Level.parse(event.getLevel().name()), loggermsg));
		}
		return res;

	}

	@Override
	public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
		Result res = isLoggable(msg.getFormattedMessage());
		String message = msg.getFormattedMessage();
		if (res.equals(Result.DENY)) {
			String displaying = getDisplaying(message);
			Displayable dis = m.displayed.get(displaying.toUpperCase());
			DisplayInfo di = null;
			if (dis instanceof DisplayItem)
				di = new DisplayItemInfo(m, (DisplayItem) dis);
			if (dis instanceof DisplayInventory)
				di = new DisplayInventoryInfo(m, (DisplayInventory) dis);
			String loggermsg = message.replaceAll("\u0007cid(.*?)\u0007", di.loggerMessage());
			Bukkit.getLogger().log(java.util.logging.Level.parse(level.name()), loggermsg, marker);
		}
		return isLoggable(msg.getFormattedMessage());
	}

	@Override
	public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
		Result res = isLoggable(msg);
		String message = msg;
		if (res.equals(Result.DENY)) {
			String displaying = getDisplaying(message);
			Displayable dis = m.displayed.get(displaying.toUpperCase());
			DisplayInfo di = null;
			if (dis instanceof DisplayItem)
				di = new DisplayItemInfo(m, (DisplayItem) dis);
			if (dis instanceof DisplayInventory)
				di = new DisplayInventoryInfo(m, (DisplayInventory) dis);
			String loggermsg = message.replaceAll("\u0007cid(.*?)\u0007", di.loggerMessage());
			Bukkit.getLogger().log(java.util.logging.Level.parse(level.name()), loggermsg, params);
		}
		return res;
	}

	@Override
	public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
		if (msg == null)
			return Result.NEUTRAL;

		Result res = isLoggable(msg.toString());
		String message = msg.toString();
		if (res.equals(Result.DENY)) {
			String displaying = getDisplaying(message);
			Displayable dis = m.displayed.get(displaying.toUpperCase());
			DisplayInfo di = null;
			if (dis instanceof DisplayItem)
				di = new DisplayItemInfo(m, (DisplayItem) dis);
			if (dis instanceof DisplayInventory)
				di = new DisplayInventoryInfo(m, (DisplayInventory) dis);
			String loggermsg = message.replaceAll("\u0007cid(.*?)\u0007", di.loggerMessage());
			Bukkit.getLogger().log(java.util.logging.Level.parse(level.name()), loggermsg, marker);
		}

		// Bukkit.getPlayer("BingoRufus").sendMessage(message.replaceAll("\u0007cid(.*?)\u0007",
		// "yikes"));
		return res;
	}



	private Result isLoggable(String message) {
		if (message == null || this.isStopped() || !message.contains(bell + "")) {
			return Result.NEUTRAL;
		}
		if (message.matches(displayRegex)) {
			return Result.DENY;
		}
		return Result.NEUTRAL;
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