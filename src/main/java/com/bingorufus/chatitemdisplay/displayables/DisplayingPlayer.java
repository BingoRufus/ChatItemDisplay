package com.bingorufus.chatitemdisplay.displayables;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class DisplayingPlayer {
    private final String displayName;
    private final String regularName;
    private final OfflinePlayer offlinePlayer;

    public DisplayingPlayer(String displayName, String regularName, OfflinePlayer offlinePlayer) {
        this.displayName = displayName;
        this.regularName = regularName;
        this.offlinePlayer = offlinePlayer;

    }

    public DisplayingPlayer(JsonObject serializedJson) {
        displayName = serializedJson.get("displayName").getAsString();
        regularName = serializedJson.get("regularName").getAsString();
        offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(serializedJson.get("UUID").getAsString()));

    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRegularName() {
        return regularName;
    }

    public OfflinePlayer getOfflinePlayer() {
        return offlinePlayer;
    }

    public JsonObject serailze() {
        JsonObject jo = new JsonObject();
        jo.addProperty("displayName", displayName);
        jo.addProperty("regularName", regularName);
        jo.addProperty("UUID", offlinePlayer.getUniqueId().toString());
        return jo;
    }

    @Override
    public String toString() {
        return "DisplayingPlayer{" +
                "displayName='" + displayName + '\'' +
                ", regularName='" + regularName + '\'' +
                ", offlinePlayer=" + offlinePlayer +
                '}';
    }
}
