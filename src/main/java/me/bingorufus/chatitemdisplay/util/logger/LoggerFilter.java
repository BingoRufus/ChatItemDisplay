package me.bingorufus.chatitemdisplay.util.logger;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.Display;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;
import org.bukkit.Bukkit;

import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoggerFilter extends AbstractFilter { // This just makes it so that "cidPlayer" isn't sent to console, but
    // rather it includes the appropriate text
    final char bell = '\u0007';
    final ChatItemDisplay m;
    final String displayRegex = "(?s).*" + bell + "cid.*" + bell + ".*";

    public LoggerFilter(ChatItemDisplay m) {
        this.m = m;
    }

    @Override
    public Result filter(LogEvent event) {
        if (event == null)
            return Result.NEUTRAL;
        Result res = isLoggable(event.getMessage().getFormattedMessage());

        if (res.equals(Result.DENY)) {
            Bukkit.getLogger().log(new LogRecord(getJavaLevel(event.getLevel()),
                    newMessage(event.getMessage().getFormattedMessage())));
        }
        return res;

    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
        Result res = isLoggable(msg.getFormattedMessage());
        if (res.equals(Result.DENY)) {
            Bukkit.getLogger().log(getJavaLevel(level), newMessage(msg.getFormattedMessage()),
                    marker);
        }
        return isLoggable(msg.getFormattedMessage());
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
        Result res = isLoggable(msg);
        if (res.equals(Result.DENY)) {
            Bukkit.getLogger().log(getJavaLevel(level), newMessage(msg), params);
        }
        return res;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
        if (msg == null)
            return Result.NEUTRAL;

        Result res = isLoggable(msg.toString());
        if (res.equals(Result.DENY)) {

            Bukkit.getLogger().log(getJavaLevel(level), newMessage(msg.toString()), marker);
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

    private String newMessage(String msg) {

        Pattern pattern = Pattern.compile("\u0007cid(.*?)\u0007");

        Matcher matcher = pattern.matcher(msg);

        while (matcher.find()) {

            String json = matcher.group(1);

            JsonObject jo = (JsonObject) new JsonParser().parse(json);

            Display dis = m.getDisplayedManager().getDisplayed(jo.get("id").getAsLong());

            msg = msg.replaceFirst(Pattern.quote(bell + "cid" + json + bell),
                    dis.getDisplayable().getInfo().loggerMessage());
            matcher = pattern.matcher(msg);

        }
        return msg;

    }

    public java.util.logging.Level getJavaLevel(Level l) {

        return JULConverter.fromLog4j(l).toJUL();

    }


}