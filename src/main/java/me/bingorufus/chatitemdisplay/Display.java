package me.bingorufus.chatitemdisplay;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.bingorufus.chatitemdisplay.displayables.Displayable;

import java.io.File;
import java.util.UUID;

public class Display {
    private final Displayable dis;
    private final String player;
    private final UUID id; // Just allows for a big number to prevent overflow, as long as less than 9
    // quintillion items are displayed before the server restarts.

    public Display(Displayable displayable, String player, UUID id) {
        this.dis = displayable;
        this.player = player;
        this.id = id;
    }

    public static Display deserialize(String json) {
        JsonObject jo = (JsonObject) new JsonParser().parse(json);
        String player = jo.get("player").getAsString();
        UUID id = UUID.fromString(jo.get("id").getAsString());
        Displayable dis = Displayable.deserialize(jo.get("displayable").getAsString());
        return new Display(dis, player, id);
    }

    public Displayable getDisplayable() {
        return dis;
    }

    public String getPlayer() {
        return player;
    }

    public UUID getId() {
        return id;
    }

    public File getImage() {
        return dis.getImage();
    }

    public String getInsertion() {
        JsonObject jo = new JsonObject();
        jo.addProperty("player", player);
        jo.addProperty("id", id.toString());
        return '\u0007' + "cid" + jo.toString() + '\u0007';
    }

    public String serialize() {
        JsonObject jo = new JsonObject();
        jo.addProperty("player", player);
        jo.addProperty("id", id.toString());
        jo.addProperty("displayable", dis.serialize());
        return jo.toString();
    }

    @Override
    public String toString() {
        return serialize();
    }
}
