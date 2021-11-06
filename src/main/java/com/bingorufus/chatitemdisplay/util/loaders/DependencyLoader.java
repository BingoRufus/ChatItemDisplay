package com.bingorufus.chatitemdisplay.util.loaders;

import org.bukkit.Bukkit;

public class DependencyLoader {

    private DiscordSRVRegister discordReg;


    public void unLoadDependencies() {
        if (discordReg != null) {
            discordReg.unregister();
        }
    }

    public void loadDependencies() {
        if (discordReg != null)
            discordReg.unregister();
        if (Bukkit.getPluginManager().getPlugin("DiscordSRV") != null) {
            if (discordReg == null) {
                discordReg = new DiscordSRVRegister();
            }
            discordReg.register();
        }
    }
}
