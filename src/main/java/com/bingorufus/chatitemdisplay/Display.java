package com.bingorufus.chatitemdisplay;

import com.bingorufus.chatitemdisplay.api.ChatItemDisplayAPI;
import com.bingorufus.chatitemdisplay.api.display.DisplayType;
import com.bingorufus.chatitemdisplay.api.display.Displayable;
import com.bingorufus.chatitemdisplay.displayables.DisplayingPlayer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import lombok.NonNull;

import java.io.StringReader;
import java.util.UUID;

public class Display {
    private final Displayable dis;
    private final DisplayingPlayer player;
    private final UUID id;

    public Display(Displayable displayable, UUID id) {
        this.dis = displayable;
        this.player = displayable.getDisplayer();
        this.id = id;
    }

    public static Display deserialize(String json) {
        JsonReader reader = new JsonReader(new StringReader(json));
        reader.setLenient(true);
        JsonObject jo = (JsonObject) new JsonParser().parse(reader);

        UUID id = UUID.fromString(jo.get("id").getAsString());
        JsonObject displayableJSON = jo.getAsJsonObject("displayable");
        Displayable displayable;
        try {
            DisplayType<?> displayType = ChatItemDisplayAPI.getDisplayType((Class<? extends DisplayType<?>>) Class.forName(displayableJSON.get("type").getAsString()));
            if (displayType == null) {
                return null;
            }
            displayable = displayType.initDisplayable(displayableJSON);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        return new Display(displayable, id);
    }

    public @NonNull Displayable getDisplayable() {
        return dis;
    }

    public DisplayingPlayer getPlayer() {
        return player;
    }

    public UUID getId() {
        return id;
    }


    public String getInsertion() {
        JsonObject jo = new JsonObject();
        jo.addProperty("id", id.toString());
        return '\u0007' + "cid" + jo + '\u0007';
    }

    public String serialize() {
        JsonObject jo = new JsonObject();
        jo.addProperty("id", id.toString());
        jo.add("displayable", dis.serialize());
        return jo.toString();
    }

    @Override
    public String toString() {
        return serialize();
    }
}
