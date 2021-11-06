package com.bingorufus.chatitemdisplay.util.string;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.mojang.brigadier.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class ComponentConverter {
    public static Component toAdventureComponent(BaseComponent... bc) {
        return GsonComponentSerializer.colorDownsamplingGson().deserialize(ComponentSerializer.toString(bc));
    }

    public static WrappedChatComponent convertToWrappedComponent(Object component) {
        if (component instanceof BaseComponent || component instanceof BaseComponent[]) {
            return WrappedChatComponent.fromJson(ComponentSerializer.toString(component));
        }
        if (component instanceof Component)
            return WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize((Component) component));
        return WrappedChatComponent.fromHandle(component);
    }

    public static BaseComponent[] convertNMSComponentToBukkit(Object component) {
        return ComponentSerializer.parse(WrappedChatComponent.fromHandle(component).getJson());
    }

    public static Message convertBaseComponentToNMS(BaseComponent bc, BaseComponent... bc2) {
        BaseComponent[] comps = new BaseComponent[bc2.length + 1];
        comps[0] = bc;
        System.arraycopy(bc2, 0, comps, 1, bc2.length);
        return (Message) WrappedChatComponent.fromJson(ComponentSerializer.toString(comps)).getHandle();
    }

    public static BaseComponent[] convertAdventureComponentToBukkit(Component component) {
        return ComponentSerializer.parse(GsonComponentSerializer.gson().serialize(component));
    }

    public static BaseComponent[] getBaseComponent(WrappedChatComponent wrappedChatComponent) {
        return ComponentSerializer.parse(wrappedChatComponent.getJson());
    }
}
