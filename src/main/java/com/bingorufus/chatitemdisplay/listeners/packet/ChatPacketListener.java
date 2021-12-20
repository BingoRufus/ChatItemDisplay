package com.bingorufus.chatitemdisplay.listeners.packet;

import com.bingorufus.chatitemdisplay.ChatItemDisplay;
import com.bingorufus.chatitemdisplay.api.ChatItemDisplayAPI;
import com.bingorufus.chatitemdisplay.api.display.Displayable;
import com.bingorufus.chatitemdisplay.util.logger.DebugLogger;
import com.bingorufus.chatitemdisplay.util.string.ComponentConverter;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatPacketListener extends PacketAdapter {
    private final static Pattern displayPattern = Pattern.compile("\\acid(.*?)\\a");

    public ChatPacketListener() {
        super(ChatItemDisplay.getInstance(), ListenerPriority.LOWEST, PacketType.Play.Server.CHAT);
    }


    @Override
    public void onPacketSending(final PacketEvent e) {
        BaseComponent[] baseComps;
        BaseComponent[] originalComps = null;
        int field = 0;
        PacketContainer packet = e.getPacket();

        { // Get JSON of message
            WrappedChatComponent chat = packet.getChatComponents().read(0);
            if (chat == null) {
                if (packet.getModifier().read(1) != null) {
                    originalComps = new BaseComponent[]{new TextComponent(ComponentSerializer.parse(ComponentConverter.convertToWrappedComponent(packet.getModifier().read(1)).getJson()))};
                    field = 1;
                }
            } else {
                originalComps = ComponentSerializer.parse(chat.getJson());
            }
            if (originalComps == null) return;
            if (originalComps.length != 1) {
                TextComponent comp = new TextComponent(originalComps);
                originalComps = new BaseComponent[]{comp};
            }
        }

        //Originalcomps now contains all of the message

        if (!ComponentSerializer.toString(originalComps).contains("\\u0007cid"))
            return;

        if (originalComps[0].getExtra() == null)
            return;

        { // Copy formatting from before displays to after displays
            // "§aHello [display] this is cool"  is changed to
            // "§aHello [display]§a this is cool"
            List<BaseComponent> editedExtra = new ArrayList<>();

            for (int i = 0; i < originalComps[0].getExtra().size(); i++) {
                List<BaseComponent> extra = originalComps[0].getExtra();
                BaseComponent bc = extra.get(i);

                if (!bc.toLegacyText().contains("\u0007cid")) {
                    editedExtra.add(bc);
                    continue;
                }

                Matcher matcher = displayPattern.matcher(bc.toLegacyText());
                if (!matcher.find()) {
                    editedExtra.add(bc);
                    continue;
                }

                matcher = displayPattern.matcher(bc.toLegacyText());
                List<String> partsTemp = new ArrayList<>();
                List<String> parts = new ArrayList<>();
                parts.add(bc.toLegacyText());

                while (matcher.find()) {
                    for (String part : parts) {
                        Collections.addAll(partsTemp, part
                                .split("((?<=" + Pattern.quote(matcher.group(0)) + ")|(?=" + Pattern.quote(matcher.group(0)) + "))"));

                    }
                    parts.clear();
                    parts.addAll(partsTemp);
                    partsTemp.clear();
                }
                for (String part : parts) {
                    TextComponent tc = new TextComponent(part);
                    tc.copyFormatting(bc, false);
                    editedExtra.add(tc);
                }


            }
            BaseComponent org = originalComps[0];
            org.setExtra(editedExtra);
            originalComps[0] = org;
            baseComps = originalComps;
        }

        // Replaces display placeholders with displays
        {
            try {
                for (int i = 0; i < baseComps[0].getExtra().size(); i++) {
                    List<BaseComponent> extra = baseComps[0].getExtra();
                    TextComponent bc = (TextComponent) extra.get(i);
                    if (!bc.toLegacyText().contains("\u0007cid"))
                        continue;


                    Matcher matcher = displayPattern.matcher(bc.toLegacyText());

                    while (matcher.find()) {
                        String legacyText = bc.toLegacyText().replace(matcher.group(0), matcher.group(0) + getLastColors(bc.toLegacyText().substring(0, bc.toLegacyText().indexOf(matcher.group(0)))));

                        DebugLogger.log(matcher.group(1) + " is being displayed");

                        JsonObject jo = (JsonObject) new JsonParser().parse(matcher.group(1));
                        Displayable display = ChatItemDisplayAPI.getDisplayedManager().getDisplayed(UUID.fromString(jo.get("id").getAsString()))
                                .getDisplayable();

                        String[] parts = legacyText
                                .split("((?<=" + Pattern.quote(matcher.group(0)) + ")|(?=" + Pattern.quote(matcher.group(0)) + "))");
                        TextComponent component = new TextComponent();

                        for (String part : parts) {
                            if (part.equalsIgnoreCase(matcher.group(0))) {
                                component.addExtra(display.getDisplayComponent());
                                continue;
                            }
                            TextComponent tc = new TextComponent(part);
                            component.addExtra(tc);
                        }
                        extra.set(i, component);
                        baseComps[0].setExtra(extra);
                        bc = new TextComponent(extra.get(i));
                        if (!bc.toLegacyText().contains("\u0007cid"))
                            break;
                        matcher = displayPattern.matcher(bc.toLegacyText());
                    }
                }
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }
        }
        if (field == 0) {
            packet.getChatComponents().write(0, WrappedChatComponent.fromJson(ComponentSerializer.toString(baseComps)));
            return;
        }
        if (packet.getModifier().read(1) instanceof BaseComponent) {
            packet.getModifier().write(1,
                    baseComps);
        } else if (packet.getModifier().read(1) instanceof Component) {
            packet.getModifier().write(1,
                    ComponentConverter.toAdventureComponent(baseComps));
        }


    }

    private String getLastColors(String string) {
        TextComponent tc = new TextComponent(TextComponent.fromLegacyText(string));
        TextComponent colored = new TextComponent();
        colored.copyFormatting(tc, ComponentBuilder.FormatRetention.FORMATTING, true);
        return colored.toLegacyText();
    }


}
