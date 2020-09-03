package me.bingorufus.chatitemdisplay.util.loaders;

import github.scarsz.discordsrv.DiscordSRV;
import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.listeners.DiscordSRVModifier;

public class DiscordSRVRegister {
	DiscordSRVModifier mod;
	ChatItemDisplay m;

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
