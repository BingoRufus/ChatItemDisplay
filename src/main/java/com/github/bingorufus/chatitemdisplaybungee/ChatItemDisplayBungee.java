package com.github.bingorufus.chatitemdisplaybungee;

import com.github.bingorufus.chatitemdisplay.util.string.VersionComparator;
import com.github.bingorufus.chatitemdisplay.util.updater.UpdateChecker;
import com.github.bingorufus.chatitemdisplay.util.updater.UpdateDownloader;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;

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
            String error = new UpdateChecker(77177).getLatestVersion(ver -> {
                VersionComparator.Status s = new VersionComparator().isRecent(this.getDescription().getVersion(), ver);

                if (s.equals(VersionComparator.Status.BEHIND)) {
                    try {
                        UpdateDownloader downloader = new UpdateDownloader();
                        String downloadError = downloader
                                .download(new File(
                                        "plugins/ChatItemDisplay " + ver + ".jar"));
                        if (downloadError != null) {
                            getLogger().warning(
                                    "Could not download the newest version of ChatItemDisplay (" + downloadError + ")");
                            return;
                        }
                        downloader.deletePlugin(this);
                        getLogger().info(
                                "The newest version of ChatItemDisplay has been downloaded automatically, it will be loaded upon the next startup");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        getLogger().warning(
                                "Could not download the newest version of ChatItemDisplay (" + e.getMessage() + ")");

                        return;
                    }
                }
                getLogger().info("ChatItemDisplay is up to date");
            });
            if (error != null) {
                getLogger().warning(error);
            }
        });

    }

}


