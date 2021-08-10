package io.github.bingorufus.chatitemdisplay.util.loaders;

import github.scarsz.discordsrv.DiscordSRV;
import io.github.bingorufus.chatitemdisplay.listeners.DiscordSRVModifier;

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
