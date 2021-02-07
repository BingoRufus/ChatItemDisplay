package com.github.bingorufus.chatitemdisplaybungee;

import com.github.bingorufus.chatitemdisplay.util.string.VersionComparator;
import com.github.bingorufus.chatitemdisplay.util.updater.UpdateChecker;
import com.github.bingorufus.chatitemdisplay.util.updater.UpdateDownloader;
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

        getProxy().getPluginManager().registerListener(this, new DisplayReceiver());
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
                    VersionComparator.Status s = new VersionComparator().isRecent(this.getDescription().getVersion(), ver);

                    if (s.equals(VersionComparator.Status.BEHIND)) {
                        UpdateDownloader downloader = new UpdateDownloader();
                        try {
                            downloader
                                    .download(new File(

                                            "plugins/ChatItemDisplay " + ver + ".jar"));
                            getLogger().info(
                                    "The newest version of ChatItemDisplay has been downloaded automatically, it will be loaded upon the next startup");
                            downloader.deletePlugin(this);
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


