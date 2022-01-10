package com.bingorufus.chatitemdisplay.util.iteminfo.item;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

import static com.bingorufus.chatitemdisplay.util.iteminfo.reflection.ReflectionClasses.chatSerializer;

public abstract class NMSChatTag {
    private final Object nmsTag;

    public NMSChatTag(Object tag) {
        nmsTag = tag;
    }

    public NMSChatTag(String jsonTag) throws ReflectiveOperationException {
        Method parseJson = Arrays.stream(chatSerializer.getDeclaredMethods()).filter(method -> method.getParameterCount() == 1).filter(method -> method.getParameterTypes()[0] == String.class).max(Comparator.comparing(Method::getName)).orElseThrow(() -> new NoSuchMethodException("Cannot find a method to read chat json"));
        nmsTag = parseJson.invoke(chatSerializer, jsonTag);
    }

    public NMSChatTag(BaseComponent tag) throws ReflectiveOperationException {
        this(ComponentSerializer.toString(tag));
    }

    public Object getNmsTag() {
        return nmsTag;
    }
}
