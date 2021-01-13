package me.bingorufus.chatitemdisplay.listeners;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.Display;
import me.bingorufus.chatitemdisplay.util.logger.ConsoleLogEvent;
import org.apache.logging.log4j.message.SimpleMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoggerListener implements Listener {
    final char bell = '\u0007';

    @EventHandler
    public void onLog(ConsoleLogEvent e) {
        if (e.getFormattedMessage() == null) return;
        if (e.isCancelled()) return;
        String msg = e.getFormattedMessage();
        if (!msg.contains(bell + "")) return;


        Pattern pattern = Pattern.compile("\u0007cid(.*?)\u0007");

        Matcher matcher = pattern.matcher(msg);

        while (matcher.find()) {

            String json = matcher.group(1);

            JsonObject jo = (JsonObject) new JsonParser().parse(json);

            Display dis = ChatItemDisplay.getInstance().getDisplayedManager().getDisplayed(UUID.fromString(jo.get("id").getAsString()));

            msg = msg.replaceFirst(Pattern.quote(bell + "cid" + json + bell),
                    dis.getDisplayable().getInfo().loggerMessage());
            matcher = pattern.matcher(msg);

        }
        e.setMessage(new SimpleMessage(msg));


    }
}