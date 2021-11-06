package com.bingorufus.chatitemdisplay.util;

import java.util.HashMap;

public class Cooldown<T> {

    protected final HashMap<T, Long> cooldowns = new HashMap<>();
    private long cooldownTime;

    public Cooldown(long cooldownTimeMillis) {
        this.cooldownTime = cooldownTimeMillis;
    }

    public boolean isOnCooldown(T t) {
        if (!cooldowns.containsKey(t)) return false;
        return getTimeRemaining(t) > 0;
    }

    public long getTimeRemaining(T t) {
        if (!cooldowns.containsKey(t)) return -1;
        return cooldownTime - (System.currentTimeMillis() - cooldowns.get(t));
    }

    public void ensureRemoved(T t) {
        cooldowns.remove(t);
    }

    public void addToCooldown(T t) {
        cooldowns.put(t, System.currentTimeMillis());
    }

    public void setCooldownTime(long newTime) {
        this.cooldownTime = newTime;
    }
}
