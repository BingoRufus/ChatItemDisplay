package com.bingorufus.chatitemdisplay.listeners;


import com.bingorufus.chatitemdisplay.Display;
import com.bingorufus.chatitemdisplay.api.ChatItemDisplayAPI;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.GameChatMessagePostProcessEvent;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordSRVModifier {



    @Subscribe(priority = ListenerPriority.MONITOR)
    public void onSend(GameChatMessagePostProcessEvent e) {

        String msg = e.getProcessedMessage();
        Pattern pattern = Pattern.compile("\u0007cid([{](.*?)[}])\u0007");

        Matcher matcher = pattern.matcher(msg);

        while (matcher.find()) {

            String json = matcher.group(1);

            JsonObject jo = (JsonObject) new JsonParser().parse(json);

            Display dis = ChatItemDisplayAPI.getDisplayedManager().getDisplayed(UUID.fromString(jo.get("id").getAsString()));

            msg = matcher.replaceFirst(dis.getDisplayable().getLoggerMessage());
            matcher = pattern.matcher(msg);
        }

        e.setProcessedMessage(msg);


    }


}
