package com.bingorufus.chatitemdisplay.util.iteminfo.item;

import org.bukkit.inventory.ItemStack;

public class NMSItemPre17 extends NMSItemStack {
    public NMSItemPre17(ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public NMSNBTTag getTag() {
        return null;
    }

    @Override
    public void setTag(NMSNBTTag tag) {

    }

    @Override
    public boolean hasNBT() {
        return false;
    }
}
