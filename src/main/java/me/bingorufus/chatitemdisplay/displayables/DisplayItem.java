package me.bingorufus.chatitemdisplay.displayables;

import com.google.gson.JsonObject;
import me.bingorufus.chatitemdisplay.util.iteminfo.ItemSerializer;
import me.bingorufus.chatitemdisplay.util.iteminfo.ItemStackStuff;
import org.bukkit.inventory.ItemStack;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class DisplayItem implements Displayable {
    private final ItemStack item;
    private final String player;
    private final String displayName;
    private final UUID uuid;
    private final boolean fromBungee;

    public DisplayItem(ItemStack item, String player, String displayName, UUID uuid, boolean fromBungee) {
        this.item = item.clone();
        this.player = player;
        this.displayName = displayName;
        this.uuid = uuid;
        this.fromBungee = fromBungee;

    }

    @Override
    public String serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("item", new ItemSerializer().serialize(item));
        json.addProperty("player", player);
        json.addProperty("displayName", getDisplayName());
        json.addProperty("uuid", uuid.toString());
        json.addProperty("bungee", true);


        return json.toString();
    }


    @Override
    public String getPlayer() {
        return player;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }


    public ItemStack getItem() {
        return item;
    }

    @Override
    public boolean fromBungee() {
        return this.fromBungee;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public File getImage() {

        File f = new File("image.png");
        try {
            if (!f.exists())
                f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            ImageIO.write(new ItemStackStuff().getImage(item, 100, 100), "png", fo);
            fo.close();
            return f;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
