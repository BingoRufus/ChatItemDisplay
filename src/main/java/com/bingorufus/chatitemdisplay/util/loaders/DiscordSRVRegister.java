package com.bingorufus.chatitemdisplay.util.loaders;

import com.bingorufus.chatitemdisplay.listeners.DiscordSRVModifier;
import github.scarsz.discordsrv.DiscordSRV;

public class DiscordSRVRegister {
    DiscordSRVModifier mod;

    public DiscordSRVRegister() {

    }

    public void register() {
        mod = new DiscordSRVModifier();
        DiscordSRV.api.subscribe(mod);

    }

    public void unregister() {
        DiscordSRV.api.unsubscribe(mod);
    }

}
