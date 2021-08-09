package io.github.bingorufus.chatitemdisplay;


import com.comphenix.protocol.ProtocolLibrary;
import com.google.gson.JsonObject;
import io.github.bingorufus.chatitemdisplay.api.display.DisplayType;
import io.github.bingorufus.chatitemdisplay.displayables.DisplayEnderChestType;
import io.github.bingorufus.chatitemdisplay.displayables.DisplayInventoryType;
import io.github.bingorufus.chatitemdisplay.displayables.DisplayItemType;
import io.github.bingorufus.chatitemdisplay.executors.ChatItemReloadExecutor;
import io.github.bingorufus.chatitemdisplay.executors.DebugExecutor;
import io.github.bingorufus.chatitemdisplay.executors.display.ViewItemExecutor;
import io.github.bingorufus.chatitemdisplay.listeners.ChatDisplayListener;
import io.github.bingorufus.chatitemdisplay.listeners.InventoryClick;
import io.github.bingorufus.chatitemdisplay.listeners.LoggerListener;
import io.github.bingorufus.chatitemdisplay.listeners.MessageCommandListener;
import io.github.bingorufus.chatitemdisplay.listeners.packet.ChatPacketListener;
import io.github.bingorufus.chatitemdisplay.listeners.packet.RecipeSelector;
import io.github.bingorufus.chatitemdisplay.util.ChatItemConfig;
import io.github.bingorufus.chatitemdisplay.util.CommandRegistry;
import io.github.bingorufus.chatitemdisplay.util.Cooldown;
import io.github.bingorufus.chatitemdisplay.util.bungee.BungeeCordReceiver;
import io.github.bingorufus.chatitemdisplay.util.display.ConfigReloader;
import io.github.bingorufus.chatitemdisplay.util.loaders.DiscordSRVRegister;
import io.github.bingorufus.chatitemdisplay.util.loaders.LangReader;
import io.github.bingorufus.chatitemdisplay.util.loaders.Metrics;
import io.github.bingorufus.chatitemdisplay.util.logger.ConsoleFilter;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.Range;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ChatItemDisplay extends JavaPlugin {
    public static final String MINECRAFT_VERSION = Bukkit.getServer().getVersion().substring(Bukkit.getServer().getVersion().indexOf("(MC: ") + 5,
            Bukkit.getServer().getVersion().indexOf(")"));

    private static final LinkedList<DisplayType<?>> registeredDisplayables = new LinkedList<>();
    private static ChatItemDisplay INSTANCE;
    private final Cooldown<Player> displayCooldown = new Cooldown<>(0);
    private DiscordSRVRegister discordReg;
    private DisplayedManager dm;
    private JsonObject lang;

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
        INSTANCE = this;
        this.saveDefaultConfig();
        this.dm = new DisplayedManager();
        this.getCommand("generatedebuglog").setExecutor(new DebugExecutor());
        this.getCommand("viewitem").setExecutor(new ViewItemExecutor());
        this.getCommand("chatitemreload").setExecutor(new ChatItemReloadExecutor());

        Bukkit.getPluginManager().registerEvents(new MessageCommandListener(), this);
        Bukkit.getPluginManager().registerEvents(new LoggerListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClick(), this);
        Bukkit.getPluginManager().registerEvents(new ChatDisplayListener(), this);

        new ConsoleFilter().register();

        reloadListeners();

        new ConfigReloader().reload();
        registerDisplayable(new DisplayItemType());
        registerDisplayable(new DisplayInventoryType());
        registerDisplayable(new DisplayEnderChestType());

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            ProtocolLibrary.getProtocolManager().addPacketListener(new ChatPacketListener());
            ProtocolLibrary.getProtocolManager().addPacketListener(new RecipeSelector());
        }, 1L);


        Metrics metrics = new Metrics(this, 7229);
        HashMap<String, Range<Integer>> playerCountRanges = new HashMap<>();
        playerCountRanges.put("≤ 5", Range.between(0, 5));
        playerCountRanges.put("6-10", Range.between(6, 10));
        playerCountRanges.put("11-25", Range.between(11, 25));
        playerCountRanges.put("26-50", Range.between(26, 50));
        playerCountRanges.put("51-100", Range.between(51, 100));


        metrics.addCustomChart(new Metrics.DrilldownPie("player_count", () -> {
            HashMap<String, Map<String, Integer>> mainMap = new HashMap<>();
            HashMap<String, Integer> playerCount = new HashMap<>();
            playerCount.put(Bukkit.getOnlinePlayers().size() + "", 1);
            mainMap.put(playerCountRanges.keySet().stream().filter(title -> playerCountRanges.get(title).contains(Bukkit.getOnlinePlayers().size())).findFirst().orElse("> 100"), playerCount);
            return mainMap;
        }));
        getRegisteredDisplayables().forEach(CommandRegistry::registerAlias);

        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this, "chatitemdisplay:in", new BungeeCordReceiver());
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "chatitemdisplay:out");
    }

    @Override
    public void onDisable() {
        if (discordReg != null) {
            discordReg.unregister();
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (getDisplayedManager().getChatItemDisplayInventories().containsKey(p.getOpenInventory().getTopInventory())) {
                p.closeInventory();
            }

        }
    }

    public DisplayedManager getDisplayedManager() {
        return dm;
    }


    public void reloadListeners() {
        displayCooldown.setCooldownTime((long) getConfig().getDouble("display-cooldown"));

        if (discordReg != null)
            discordReg.unregister();
        if (Bukkit.getPluginManager().getPlugin("DiscordSRV") != null) {
            if (discordReg == null) {
                discordReg = new DiscordSRVRegister();
            }

            discordReg.register();
        }
        ChatItemConfig.reloadMessages();
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

    public Cooldown<Player> getDisplayCooldown() {
        return displayCooldown;
    }



    public LinkedList<DisplayType<?>> getRegisteredDisplayables() {
        return registeredDisplayables;
    }

    /**
     * Register an instance of a {@link DisplayType} so that it can be displayed.
     *
     * @param displayType an instance of {@link DisplayType}
     * @apiNote This method must be called upon server startup.
     */
    public void registerDisplayable(DisplayType<?> displayType) {
        registeredDisplayables.add(displayType);
        CommandRegistry.registerAlias(displayType);
    }

    /**
     * Gets an instance of a display type from the class path of the display type.
     * An instance has to be registered for this to return an instance.
     * If none has been registered with the specified class path the instance will return null
     *
     * @param displayTypeClass The class of the display type
     * @return An instance of the display type with the given class path.
     * @see #registerDisplayable(DisplayType)
     */
    public DisplayType<?> getDisplayType(Class<? extends DisplayType<?>> displayTypeClass) {
        DisplayType<?> displayType = ChatItemDisplay.getInstance().getRegisteredDisplayables().stream().filter(type -> type.getClass().equals(displayTypeClass)).findFirst().orElse(null);
        if (displayType == null) {
            Bukkit.getLogger().warning("Cannot find a displaytype that has the class path of: " + displayTypeClass.getCanonicalName());
            return null;
        }
        return displayType;
    }

}
