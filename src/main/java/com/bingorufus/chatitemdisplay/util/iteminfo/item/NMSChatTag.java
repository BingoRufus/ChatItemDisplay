package com.bingorufus.chatitemdisplay.util.iteminfo.item;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

import static com.bingorufus.chatitemdisplay.util.iteminfo.reflection.ReflectionClasses.chatSerializer;
import static com.bingorufus.chatitemdisplay.util.iteminfo.reflection.ReflectionClasses.iChatBaseComponent;

public class NMSChatTag {
    private static Method parseJson;
    private static Method serializeComponent;

    static {
        try {
            parseJson = Arrays.stream(chatSerializer.getDeclaredMethods()).filter(method -> method.getParameterCount() == 1).filter(method -> method.getParameterTypes()[0].equals(String.class)).filter(method -> method.getReturnType().equals(iChatBaseComponent)).max(Comparator.comparing(Method::getName)).orElseThrow(() -> new NoSuchMethodException("Cannot find a method to read chat json"));
            serializeComponent = Arrays.stream(chatSerializer.getDeclaredMethods()).filter(method -> method.getParameterCount() == 1).filter(method -> method.getParameterTypes()[0] == iChatBaseComponent).filter(method -> method.getReturnType().equals(String.class)).findFirst().orElseThrow(() -> new NoSuchMethodException("Cannot find a method to read chat json"));

        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    private Object nmsTag;

    public NMSChatTag(Object tag) {
        nmsTag = tag;
    }

    public NMSChatTag(String jsonTag) {
        try {
            nmsTag = parseJson.invoke(chatSerializer, jsonTag);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public NMSChatTag(BaseComponent tag) {
        this(ComponentSerializer.toString(tag));
    }

    public Object getNmsTag() {
        return nmsTag;
    }

    public String toString() {
        try {
            return (String) serializeComponent.invoke(chatSerializer, nmsTag);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    public BaseComponent[] toBaseComponent() {
        return ComponentSerializer.parse(toString());
    }
}
