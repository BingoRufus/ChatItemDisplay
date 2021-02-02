package com.github.bingorufus.chatitemdisplay.util.loaders;

import com.github.bingorufus.chatitemdisplay.ChatItemDisplay;
import com.github.bingorufus.chatitemdisplay.listeners.DiscordSRVModifier;
import github.scarsz.discordsrv.DiscordSRV;

public class DiscordSRVRegister {
    final ChatItemDisplay m;
    DiscordSRVModifier mod;

    public DiscordSRVRegister(ChatItemDisplay m) {
        this.m = m;
    }

    public void register() {
        mod = new DiscordSRVModifier(m);
        DiscordSRV.api.subscribe(mod);

    }

    public void unregister() {
        DiscordSRV.api.unsubscribe(mod);
    }

}
