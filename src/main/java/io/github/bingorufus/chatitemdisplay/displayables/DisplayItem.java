package io.github.bingorufus.chatitemdisplay.displayables;

import com.google.gson.JsonObject;
import io.github.bingorufus.chatitemdisplay.ChatItemDisplay;
import io.github.bingorufus.chatitemdisplay.api.display.DisplayType;
import io.github.bingorufus.chatitemdisplay.api.display.Displayable;
import io.github.bingorufus.chatitemdisplay.util.ChatItemConfig;
import io.github.bingorufus.chatitemdisplay.util.iteminfo.ItemSerializer;
import io.github.bingorufus.chatitemdisplay.util.iteminfo.ItemStackReflection;
import io.github.bingorufus.chatitemdisplay.util.iteminfo.ItemStackStuff;
import io.github.bingorufus.chatitemdisplay.util.logger.DebugLogger;
import io.github.bingorufus.chatitemdisplay.util.string.StringFormatter;
import io.github.bingorufus.chatitemdisplay.util.string.VersionComparator;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;
import java.util.regex.Matcher;

public class DisplayItem extends Displayable {
    private ItemStack item;

    public DisplayItem(Player displayer) {
        super(displayer);
        item = displayer.getInventory().getItemInMainHand().clone();
    }

    public DisplayItem(JsonObject data) {
        super(data);
    }

    @Override
    protected Class<? extends DisplayType> getTypeClass() {
        return DisplayItemType.class;
    }

    @Override
    public BaseComponent getDisplayComponent() {
        String format = (item.getAmount() > 1
                ? ChatItemConfig.CHAT_ITEM_FORMAT_MULTIPLE
                : ChatItemConfig.CHAT_ITEM_FORMAT);

        return format(format);
    }

    @Override
    public Inventory onViewDisplay(Player viewer) {
        String guiName = new StringFormatter().format(ChatItemDisplay.getInstance().getConfig().getString("messages.gui-format"));
        Inventory inventory = Bukkit.createInventory(null, 9,
                guiName.replaceAll("%player%",
                        ChatItemDisplay.getInstance().getConfig().getBoolean("use-nicks-in-gui") ? ChatItemDisplay.getInstance().getConfig().getBoolean("strip-nick-colors-gui")
                                ? ChatColor.stripColor(getDisplayer().getDisplayName())
                                : getDisplayer().getDisplayName() : getDisplayer().getRegularName()));
        inventory.setItem(4, item);

        return inventory;
    }


    @Override
    public String getLoggerMessage() {

        String format = (item.getAmount() > 1
                ? ChatItemConfig.CHAT_ITEM_FORMAT_MULTIPLE
                : ChatItemConfig.CHAT_ITEM_FORMAT);
        if (format == null) return "";
        format = format.replaceAll("%amount%", item.getAmount() + "");

        format = format.replaceAll("%item%", Matcher.quoteReplacement(new ItemStackStuff().getLangName(item)));

        return ChatColor.stripColor(new StringFormatter().format(format));
    }

    @Override
    public JsonObject serializeData() {
        JsonObject jo = new JsonObject();
        jo.addProperty("item", new ItemSerializer().serialize(item));
        return jo;
    }

    @Override
    public void deseralizeData(JsonObject data) {
        String nbt = data.get("item").getAsString();
        item = new ItemSerializer().deserialize(nbt);
    }

    @Override
    public void broadcastDisplayable() {
        String format = new StringFormatter().format(item.getAmount() > 1
                ? ChatItemConfig.COMMAND_ITEM_FORMAT_MULTIPLE
                : ChatItemConfig.COMMAND_ITEM_FORMAT);
        Bukkit.spigot().broadcast(format(format));
    }

    @Override
    public boolean hasBlacklistedItem() {
        if (isBlacklisted(item)) return true;

        ItemMeta meta = item.getItemMeta();
        if (meta instanceof BlockStateMeta) {
            BlockStateMeta bsm = (BlockStateMeta) meta;
            if (bsm.getBlockState() instanceof Container) {
                Container c = (Container) bsm.getBlockState();
                return containsBlacklistedItem(c.getInventory());
            }
        }
        return false;
    }

    private TextComponent format(String s) {
        s = s.replaceAll("%player%",
                ChatItemDisplay.getInstance().getConfig().getBoolean("use-nicks-in-display-message")
                        ? ChatItemDisplay.getInstance().getConfig().getBoolean("strip-nick-colors-message")
                        ? ChatColor.stripColor(getDisplayer().getDisplayName())
                        : getDisplayer().getDisplayName()
                        : getDisplayer().getRegularName());

        s = new StringFormatter().format(s);

        String[] parts = s.split("((?<=%item%)|(?=%item%)|(?<=%amount%)|(?=%amount%))");
        TextComponent whole = new TextComponent();
        TextComponent base = baseHover();
        BaseComponent prev = null;

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (i > 0) prev = TextComponent.fromLegacyText(whole.getExtra().get(i - 1).toLegacyText())[0];

            if (part.contains("%item%")) {
                whole.addExtra(base);
                continue;
            }
            if (part.contains("%amount%")) {
                TextComponent tc = new TextComponent(item.getAmount() + "");
                if (i > 0)
                    tc.copyFormatting(prev, ComponentBuilder.FormatRetention.FORMATTING, false);
                whole.addExtra(tc);
                continue;
            }

            TextComponent tc = new TextComponent(TextComponent.fromLegacyText(part));
            if (i > 0 && !part.startsWith("§r"))
                tc.copyFormatting(prev, ComponentBuilder.FormatRetention.FORMATTING, false);
            whole.addExtra(tc);
        }
        whole.setHoverEvent(base.getHoverEvent());
        whole.setClickEvent(base.getClickEvent());
        return whole;
    }

    public TextComponent baseHover() {
        String color = new StringFormatter().format(ChatItemDisplay.getInstance().getConfig().getString("messages.item-color"));
        ItemStackStuff itemStuff = new ItemStackStuff();
        ItemStackReflection itemRetriever = new ItemStackReflection();
        BaseComponent bc = itemStuff.getName(item, color,
                ChatItemDisplay.getInstance().getConfig().getBoolean("messages.force-item-colors"));


        TextComponent hover = new TextComponent(bc);


        VersionComparator.Status s = new VersionComparator().isRecent(ChatItemDisplay.MINECRAFT_VERSION,
                "1.16");

        if (s.equals(VersionComparator.Status.BEHIND)) {
            JsonObject itemJson = new JsonObject();

            itemJson.addProperty("id", item.getType().getKey().toString());
            itemJson.addProperty("Count", item.getAmount());
            boolean hasNbt = itemRetriever.hasNbt(item);
            if (hasNbt)
                itemJson.addProperty("tag", itemRetriever.getNBT(item)); // Only adds the nbt data if there
            // is nbt data

            String jsonString = itemJson.toString();
            jsonString = jsonString.replaceAll("\"id\"", "id").replaceAll("\"Count\"", "Count")

                    .replaceAll("\\\\", "");
            if (hasNbt) {
                jsonString = jsonString.replaceAll("\"tag\":\"", "tag:").replaceFirst("(?s)\"(?!.*?\")", "");
            }


            DebugLogger.log(
                    "From NMS: " + ComponentSerializer
                            .toString(itemRetriever.getOldHover(item).getHoverEvent().getValue()));
            DebugLogger.log(
                    "Created:  " + ComponentSerializer.toString(new ComponentBuilder(jsonString).create()));


            hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(jsonString).create()));

        } else {

            hover.setHoverEvent(
                    new HoverEvent(HoverEvent.Action.SHOW_ITEM, new Item(item.getType().getKey().toString(),
                            item.getAmount(), ItemTag.ofNbt(itemRetriever.getNBT(item)))));
        }
        UUID id = ChatItemDisplay.getInstance().getDisplayedManager().getDisplay(this).getId();
        hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                "/viewitem " + (id)));

        return hover;
    }
}
