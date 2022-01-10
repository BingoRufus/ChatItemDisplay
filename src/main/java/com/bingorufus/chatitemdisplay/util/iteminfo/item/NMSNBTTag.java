package com.bingorufus.chatitemdisplay.util.iteminfo.item;

public class NMSNBTTag {
    private final Object tag;

    public NMSNBTTag(Object nbtTag) {
        tag = nbtTag;
    }

    @Override
    public String toString() {
        return tag.toString();
    }

    public Object getNbtTag() {
        return tag;
    }
}
