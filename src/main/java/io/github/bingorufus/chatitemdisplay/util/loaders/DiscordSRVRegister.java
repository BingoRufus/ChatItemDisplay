package io.github.bingorufus.chatitemdisplay.util.loaders;

import github.scarsz.discordsrv.DiscordSRV;
import io.github.bingorufus.chatitemdisplay.ChatItemDisplay;
import io.github.bingorufus.chatitemdisplay.listeners.DiscordSRVModifier;

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
