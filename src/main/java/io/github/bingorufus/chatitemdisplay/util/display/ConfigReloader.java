package io.github.bingorufus.chatitemdisplay.util.display;

import io.github.bingorufus.chatitemdisplay.ChatItemDisplay;
import io.github.bingorufus.chatitemdisplay.displayables.SerializedDisplayType;
import io.github.bingorufus.chatitemdisplay.listeners.NewVersionDisplayer;
import io.github.bingorufus.chatitemdisplay.util.ChatItemConfig;
import io.github.bingorufus.chatitemdisplay.util.string.VersionComparator;
import io.github.bingorufus.chatitemdisplay.util.updater.UpdateChecker;
import io.github.bingorufus.chatitemdisplay.util.updater.UpdateDownloader;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.InvalidDescriptionException;

import java.io.File;
import java.util.Objects;

public class ConfigReloader {
    private static NewVersionDisplayer newVer;
    private final ChatItemDisplay m;

    public ConfigReloader() {
        this.m = ChatItemDisplay.getInstance();
    }


    public void reload() {
        ChatItemConfig.reloadMessages();
        ChatItemDisplay.getInstance().getRegisteredDisplayables().forEach(displayType -> {
            if (displayType instanceof SerializedDisplayType) {
                SerializedDisplayType<?> type = (SerializedDisplayType<?>) displayType;
                if (ChatItemConfig.getConfig().isConfigurationSection(type.dataPath()))
                    type.loadData(Objects.requireNonNull(ChatItemConfig.getConfig().getConfigurationSection(type.dataPath())));
            }
        });


        m.loadLang();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (m.getDisplayedManager().getChatItemDisplayInventories().containsKey(p.getOpenInventory().getTopInventory())) {
                p.closeInventory();
            }

        }
        Bukkit.getScheduler().runTaskAsynchronously(ChatItemDisplay.getInstance(), this::update);

    }


    private void update() {
        if (!m.getConfig().getBoolean("disable-update-checking")) {
            try {
                new UpdateChecker(77177).getLatestVersion(version -> {
                    VersionComparator.Status s = VersionComparator.isRecent(m.getDescription().getVersion(), version);

                    if (!s.equals(VersionComparator.Status.BEHIND)) {
                        m.getLogger().info("ChatItemDisplay is up to date");
                    } else {

                        m.getLogger().warning("ChatItemDisplay is currently running version "
                                + m.getDescription().getVersion() + " and can be updated to " + version);
                        if (m.getConfig().getBoolean("auto-update")) {
                            Bukkit.getScheduler().runTaskAsynchronously(m, () -> {
                                UpdateDownloader updater = new UpdateDownloader();
                                try {
                                    File f = updater.download(new File("plugins/ChatItemDisplay " + version + ".jar"));
                                    try {
                                        ChatItemDisplay.getInstance().getPluginLoader().getPluginDescription(f);
                                    } catch (InvalidDescriptionException e) {
                                        f.delete();
                                        Bukkit.getLogger().warning("The downloaded version of ChatItemDisplay does not contain a valid plugin description. The download most likely failed. Try downloading the plugin manually");
                                        newVer = new NewVersionDisplayer(m, m.getDescription().getVersion(), version);
                                        Bukkit.getPluginManager().registerEvents(newVer, m);
                                        return;
                                    }
                                    updater.deletePlugin(this);

                                    Bukkit.getLogger().info(
                                            "The newest version of ChatItemDisplay has been downloaded automatically, it will be loaded upon the next startup");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Bukkit.getLogger().severe("Unable to download the newest version of ChatItemDisplay (" + e.getMessage() + ")");

                                }


                            });
                            return;
                        }
                        m.getLogger().warning(
                                "Download the newest version at: //https://www.spigotmc.org/resources/chat-item-display.77177/");
                        m.getLogger().warning("or enable auto-update in your config.yml");

                        newVer = new NewVersionDisplayer(m, m.getDescription().getVersion(), version);
                        Bukkit.getPluginManager().registerEvents(newVer, m);
                    }
                });
            } catch (Exception e) {
                Bukkit.getLogger().warning(String.format("Unable to retrieve the latest version of ChatItemDisplay ({%s})", e.getMessage()));
            }
        }
    }

}
