package com.bingorufus.chatitemdisplaybungee;

import com.bingorufus.chatitemdisplay.util.string.VersionComparator;
import com.bingorufus.common.updater.UpdateChecker;
import com.bingorufus.common.updater.UpdateDownloader;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class ChatItemDisplayBungee extends Plugin {
    @Getter
    private static ChatItemDisplayBungee instance;

    @Override
    public void onEnable() {
        instance = this;
        downloadUpdate();
        getProxy().registerChannel("chatitemdisplay:out");
        getProxy().registerChannel("chatitemdisplay:in");

        getProxy().getPluginManager().registerListener(this, new DisplayRelay());
    }

    @Override
    public void onDisable() {
        getProxy().getPluginManager().unregisterCommands(this);
        getProxy().getPluginManager().unregisterListeners(this);
    }

    public void downloadUpdate() {
        getProxy().getScheduler().runAsync(this, () -> {
            try {
                new UpdateChecker(77177).getLatestVersion(ver -> {
                    VersionComparator.Status s = VersionComparator.isRecent(this.getDescription().getVersion(), ver);

                    if (s.equals(VersionComparator.Status.BEHIND)) {
                        try {
                            UpdateDownloader
                                    .download(new File(

                                            "plugins/ChatItemDisplay " + ver + ".jar"));
                            getLogger().info(
                                    "The newest version of ChatItemDisplay has been downloaded automatically, it will be loaded upon the next startup");
                            UpdateDownloader.deletePlugin(this);
                        } catch (IOException e) {
                            e.printStackTrace();
                            getLogger().warning(
                                    "Could not download the newest version of ChatItemDisplay (" + e.getMessage() + ")");
                            return;
                        }

                    }
                    getLogger().info("ChatItemDisplay is up to date");
                });
            } catch (Exception e) {
                getProxy().getLogger().warning(String.format("Unable to retrieve the latest version of ChatItemDisplay ({%s})", e.getMessage()));
            }
        });

    }

}


