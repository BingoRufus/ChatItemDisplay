package com.bingorufus.chatitemdisplay;


import com.bingorufus.chatitemdisplay.api.ChatItemDisplayAPI;
import com.bingorufus.chatitemdisplay.displayables.DisplayEnderChestType;
import com.bingorufus.chatitemdisplay.displayables.DisplayInventoryType;
import com.bingorufus.chatitemdisplay.displayables.DisplayItemType;
import com.bingorufus.chatitemdisplay.displayables.SerializedDisplayType;
import com.bingorufus.chatitemdisplay.executors.ChatItemReloadExecutor;
import com.bingorufus.chatitemdisplay.executors.DebugExecutor;
import com.bingorufus.chatitemdisplay.executors.display.ViewItemExecutor;
import com.bingorufus.chatitemdisplay.listeners.*;
import com.bingorufus.chatitemdisplay.listeners.packet.ChatPacketListener;
import com.bingorufus.chatitemdisplay.listeners.packet.RecipeSelector;
import com.bingorufus.chatitemdisplay.util.ChatItemConfig;
import com.bingorufus.chatitemdisplay.util.CommandRegistry;
import com.bingorufus.chatitemdisplay.util.bungee.BungeeCordReceiver;
import com.bingorufus.chatitemdisplay.util.loaders.DependencyLoader;
import com.bingorufus.chatitemdisplay.util.loaders.LangReader;
import com.bingorufus.chatitemdisplay.util.loaders.Metrics;
import com.bingorufus.chatitemdisplay.util.logger.ConsoleFilter;
import com.bingorufus.chatitemdisplay.util.string.VersionComparator;
import com.bingorufus.common.updater.UpdateChecker;
import com.bingorufus.common.updater.UpdateDownloader;
import com.comphenix.protocol.ProtocolLibrary;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ChatItemDisplay extends JavaPlugin {
    public static final String MINECRAFT_VERSION = Bukkit.getServer().getVersion().substring(Bukkit.getServer().getVersion().indexOf("(MC: ") + 5,
            Bukkit.getServer().getVersion().indexOf(")"));

    private static ChatItemDisplay INSTANCE;
    private final DependencyLoader dependencyLoader = new DependencyLoader();
    private JsonObject lang;
    private boolean deleteOnDisable = false;

    public static ChatItemDisplay getInstance() {
        return INSTANCE;
    }

    /*TODO:
     * Images sent to discordSRV
     * PlayerVaults
     * Create "Module" plugins for above
     * Rewrite DebugLogExecutor
     */

    @Override
    public void onEnable() {
        Bukkit.getScheduler().runTaskAsynchronously(this, this::checkUpdate);

        INSTANCE = this;
        this.saveDefaultConfig();
        this.getCommand("generatedebuglog").setExecutor(new DebugExecutor());
        this.getCommand("viewitem").setExecutor(new ViewItemExecutor());
        this.getCommand("chatitemreload").setExecutor(new ChatItemReloadExecutor());

        Bukkit.getPluginManager().registerEvents(new MessageCommandListener(), this);
        Bukkit.getPluginManager().registerEvents(new LoggerListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClick(), this);
        Bukkit.getPluginManager().registerEvents(new ChatDisplayListener(), this);
        Bukkit.getPluginManager().registerEvents(new ReloadListener(), this);

        new ConsoleFilter().register();

        ChatItemConfig.reloadConfig();


        ChatItemDisplayAPI.registerDisplayable(new DisplayItemType());
        ChatItemDisplayAPI.registerDisplayable(new DisplayInventoryType());
        ChatItemDisplayAPI.registerDisplayable(new DisplayEnderChestType());

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            ProtocolLibrary.getProtocolManager().addPacketListener(new ChatPacketListener());
            ProtocolLibrary.getProtocolManager().addPacketListener(new RecipeSelector());
        }, 1L);


        registerMetrics();

        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this, "chatitemdisplay:in", new BungeeCordReceiver());
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "chatitemdisplay:out");


        ChatItemDisplayAPI.getRegisteredDisplayables().forEach(displayType -> {
            if (displayType instanceof SerializedDisplayType) {
                SerializedDisplayType<?> type = (SerializedDisplayType<?>) displayType;
                if (ChatItemConfig.getConfig().isConfigurationSection(type.dataPath()))
                    type.loadData(Objects.requireNonNull(ChatItemConfig.getConfig().getConfigurationSection(type.dataPath())));
            }
        });
        ChatItemDisplayAPI.getRegisteredDisplayables().forEach(CommandRegistry::registerAlias);


        dependencyLoader.loadDependencies();


    }

    @Override
    public void onDisable() {
        List<HumanEntity> cidInvViewer = new ArrayList<>();
        for (Inventory inventory : ChatItemDisplayAPI.getDisplayedManager().getChatItemDisplayInventories()) {
            cidInvViewer.addAll(inventory.getViewers());
        }
        cidInvViewer.forEach(HumanEntity::closeInventory);
        dependencyLoader.unLoadDependencies();
        if (deleteOnDisable) {
            UpdateDownloader.deletePlugin(this);
        }
    }


    private void registerMetrics() {
        Metrics metrics = new Metrics(this, 7229);

        metrics.addCustomChart(new Metrics.DrilldownPie("player_count", () -> {
            HashMap<String, Map<String, Integer>> mainMap = new HashMap<>();
            HashMap<String, Integer> sizeValue = new HashMap<>();
            int playerCount = Bukkit.getOnlinePlayers().size();
            sizeValue.put(playerCount + "", 1);

            String range;
            if (playerCount < 6) {
                range = "≤ 5";
            } else if (playerCount < 11) {
                range = "6-10";
            } else if (playerCount < 26) {
                range = "11-25";
            } else if (playerCount < 51) {
                range = "26-50";
            } else if (playerCount < 101) {
                range = "51-100";
            } else {
                range = "> 100";
            }
            mainMap.put(range, sizeValue);
            return mainMap;
        }));
    }

    public void loadLang() {
        try {
            lang = new LangReader().readLang(this.getConfig().getString("messages.logger-lang"));
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            lang = new LangReader().readLang("en");
        }
    }

    public JsonObject getLang() {
        if (lang == null) loadLang();
        return lang;
    }

    private void checkUpdate() {
        if (ChatItemConfig.getConfig().getBoolean("disable-update-checking")) return;
        try {
            new UpdateChecker(77177).getLatestVersion(version -> {
                switch (VersionComparator.isRecent(getDescription().getVersion(), version)) {
                    case AHEAD:
                    case SAME:
                        getLogger().info("ChatItemDisplay is up to date");
                        return;
                    case BEHIND:
                        getLogger().warning("ChatItemDisplay is currently running version "
                                + getDescription().getVersion() + " and can be updated to " + version);
                        if (!getConfig().getBoolean("auto-update")) {
                            //Update downloading is not enabled, just printing a warning
                            getLogger().warning(
                                    "Download the newest version at: //https://www.spigotmc.org/resources/chat-item-display.77177/");
                            getLogger().warning("or enable \"auto-update\" in your config.yml");
                            return;
                        }

                        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                            File pluginDownload = new File("plugins/ChatItemDisplay " + version + ".jar");
                            try {
                                UpdateDownloader.download(pluginDownload);
                                //Check if plugin was downloaded properly. If download was incomplete this will throw InvalidDescriptionException
                                ChatItemDisplay.getInstance().getPluginLoader().getPluginDescription(pluginDownload);
                                deleteOnDisable = true;
                                getLogger().info("The newest version of ChatItemDisplay has been downloaded automatically, it will be loaded upon the next startup");

                            } catch (InvalidDescriptionException e) {
                                Bukkit.getLogger().warning("The downloaded version of ChatItemDisplay does not contain a valid plugin description. The download most likely failed. Try downloading the plugin manually");
                                if (pluginDownload.exists() && !pluginDownload.delete()) {
                                    getLogger().warning("The downloaded file could not be deleted at " + pluginDownload.getAbsolutePath());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Bukkit.getLogger().severe("Unable to download the newest version of ChatItemDisplay (" + e.getMessage() + ")");
                            }
                        });
                }
            });
        } catch (IOException e) {
            getLogger().warning(String.format("Unable to retrieve the latest version of ChatItemDisplay ({%s})", e.getMessage()));
        }

    }

    @NotNull
    @Override
    public FileConfiguration getConfig() {
        return ChatItemConfig.getConfig();
    }

    /**
     * This method uses spigot's Built in {@link JavaPlugin#getConfig()}
     * It is recommended to use {@link ChatItemDisplay#getConfig()} when getting cached values.
     *
     * @return Bukkit's config
     */
    public FileConfiguration getConfigSuper() {
        return super.getConfig();
    }
}
