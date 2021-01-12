package me.bingorufus.chatitemdisplay;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.bingorufus.chatitemdisplay.displayables.Displayable;

import java.io.File;

public class Display {
    private final Displayable dis;
    private final String player;
    private final Long id; // Just allows for a big number to prevent overflow, as long as less than 9
    // quintillion items are displayed before the server restarts.

    public Display(Displayable displayable, String player, Long id) {
        this.dis = displayable;
        this.player = player;
        this.id = id;
    }

    public static Display deserialize(String json) {
        JsonObject jo = (JsonObject) new JsonParser().parse(json);
        String player = jo.get("player").getAsString();
        Long id = jo.get("id").getAsLong();
        Displayable dis = Displayable.deserialize(jo.get("displayable").getAsString());
        return new Display(dis, player, id);
    }

    public Displayable getDisplayable() {
        return dis;
    }

    public String getPlayer() {
        return player;
    }

    public Long getId() {
        return id;
    }

    public File getImage() {
        return dis.getImage();
    }

    public String getInsertion() {
        JsonObject jo = new JsonObject();
        jo.addProperty("player", player);
        jo.addProperty("id", id);
        return '\u0007' + "cid" + jo.toString() + '\u0007';
    }

    public String serialize() {
        JsonObject jo = new JsonObject();
        jo.addProperty("player", player);
        jo.addProperty("id", id);
        jo.addProperty("displayable", dis.serialize());
        return jo.toString();
    }

    @Override
    public String toString() {
        return serialize();
    }
}
