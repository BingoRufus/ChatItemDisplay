package io.github.bingorufus.chatitemdisplay.util.display;

import io.github.bingorufus.chatitemdisplay.ChatItemDisplay;
import io.github.bingorufus.chatitemdisplay.listeners.NewVersionDisplayer;
import io.github.bingorufus.chatitemdisplay.util.ChatItemConfig;
import io.github.bingorufus.chatitemdisplay.util.bungee.BungeeCordReceiver;
import io.github.bingorufus.chatitemdisplay.util.string.VersionComparator;
import io.github.bingorufus.chatitemdisplay.util.updater.UpdateChecker;
import io.github.bingorufus.chatitemdisplay.util.updater.UpdateDownloader;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.File;

public class ConfigReloader {
    private static PluginMessageListener bungeeIn;
    private static NewVersionDisplayer newVer;
    private final ChatItemDisplay m;

    public ConfigReloader() {
        this.m = ChatItemDisplay.getInstance();
    }


    public void reload() {
        m.saveDefaultConfig();
        m.reloadConfig();

        ChatItemConfig.reloadMessages();

        if (bungeeIn != null) {
            Bukkit.getServer().getMessenger().unregisterIncomingPluginChannel(m, "chatitemdisplay:in", bungeeIn);
        }

        if (ChatItemConfig.BUNGEE) {
            bungeeIn = new BungeeCordReceiver();
            Bukkit.getServer().getMessenger().registerIncomingPluginChannel(m, "chatitemdisplay:in", bungeeIn);
            Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(m, "chatitemdisplay:out");

        }

        m.loadLang();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (m.getChatItemDisplayInventories().containsKey(p.getOpenInventory().getTopInventory())) {
                p.closeInventory();
            }

        }
        m.reloadListeners();
        Bukkit.getScheduler().runTaskAsynchronously(ChatItemDisplay.getInstance(), this::update);

    }


    private void update() {
        if (!m.getConfig().getBoolean("disable-update-checking")) {
            try {
                new UpdateChecker(77177).getLatestVersion(version -> {
                    VersionComparator.Status s = new VersionComparator().isRecent(m.getDescription().getVersion(), version);

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
