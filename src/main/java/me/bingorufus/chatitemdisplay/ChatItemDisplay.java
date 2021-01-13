package me.bingorufus.chatitemdisplay;


import com.google.gson.JsonObject;
import me.bingorufus.chatitemdisplay.executors.ChatItemReloadExecutor;
import me.bingorufus.chatitemdisplay.executors.DebugExecutor;
import me.bingorufus.chatitemdisplay.executors.display.DisplayEnderChestExecutor;
import me.bingorufus.chatitemdisplay.executors.display.DisplayInventoryExecutor;
import me.bingorufus.chatitemdisplay.executors.display.DisplayItemExecutor;
import me.bingorufus.chatitemdisplay.executors.display.ViewItemExecutor;
import me.bingorufus.chatitemdisplay.listeners.*;
import me.bingorufus.chatitemdisplay.util.ChatItemConfig;
import me.bingorufus.chatitemdisplay.util.Cooldown;
import me.bingorufus.chatitemdisplay.util.display.ConfigReloader;
import me.bingorufus.chatitemdisplay.util.loaders.DiscordSRVRegister;
import me.bingorufus.chatitemdisplay.util.loaders.LangReader;
import me.bingorufus.chatitemdisplay.util.loaders.Metrics;
import me.bingorufus.chatitemdisplay.util.loaders.ProtocolLibRegister;
import me.bingorufus.chatitemdisplay.util.logger.ConsoleFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class ChatItemDisplay extends JavaPlugin {
    public static final String MINECRAFT_VERSION = Bukkit.getServer().getVersion().substring(Bukkit.getServer().getVersion().indexOf("(MC: ") + 5,
            Bukkit.getServer().getVersion().indexOf(")"));
    private static ChatItemDisplay main;
    public final HashMap<Player, ItemStack> viewingMap = new HashMap<>();//Contains players who are looking at maps, the value is the item that was replaced
    public final HashMap<Inventory, UUID> invs = new HashMap<>(); //Inventories and the UUIDs of the owners
    private final Cooldown<Player> displayCooldown = new Cooldown<>(0);
    private ProtocolLibRegister pl;
    private DiscordSRVRegister discordReg;
    private DisplayedManager dm;
    private JsonObject lang;

    public static ChatItemDisplay getInstance() {
        return main;
    }

    /*TODO:
     * Auto config updating
     * Images sent to discordSRV
     * PlayerVaults
     * Party chat
     */

    @Override
    public void onEnable() {
        main = this;
        this.saveDefaultConfig();
        this.dm = new DisplayedManager();
        this.getCommand("generatedebuglog").setExecutor(new DebugExecutor());
        this.getCommand("viewitem").setExecutor(new ViewItemExecutor());
        this.getCommand("chatitemreload").setExecutor(new ChatItemReloadExecutor());
        this.getCommand("displayitem").setExecutor(new DisplayItemExecutor());
        this.getCommand("displayinv").setExecutor(new DisplayInventoryExecutor());
        this.getCommand("displayenderchest").setExecutor(new DisplayEnderChestExecutor());

        Bukkit.getPluginManager().registerEvents(new MessageCommandListener(), this);
        Bukkit.getPluginManager().registerEvents(new MapViewerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new LoggerListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClick(), this);
        Bukkit.getPluginManager().registerEvents(new ChatDisplayListener(), this);

        registerFilter();
        reloadListeners();

        new ConfigReloader().reload();

        Metrics metrics = new Metrics(this, 7229);
    }

    @Override
    public void onDisable() {
        if (discordReg != null) {
            discordReg.unregister();
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (viewingMap.containsKey(p)) {
                p.getInventory().setItemInMainHand(viewingMap.get(p));
            }
            if (invs.containsKey(p.getOpenInventory().getTopInventory())) {
                p.closeInventory();
            }

        }
    }

    public DisplayedManager getDisplayedManager() {
        return dm;
    }

    private void registerFilter() {
        Logger logger = (Logger) LogManager.getRootLogger();
        Iterator<Filter> filters = logger.getFilters();
        while (filters.hasNext()) { // Prevents duplicate loggers
            Filter f = filters.next();

            if (f.getClass().getName().equals(ConsoleFilter.class.getName())) {// Check if the filter is one of mine
                f.stop();
            }
        }
        logger.addFilter(new ConsoleFilter());
    }

    public void reloadListeners() {
        displayCooldown.setCooldownTime((long) getConfig().getDouble("display-cooldown"));
        if (pl == null)
            pl = new ProtocolLibRegister(this);
        pl.registerPacketListener();

        if (discordReg != null)
            discordReg.unregister();
        if (Bukkit.getPluginManager().getPlugin("DiscordSRV") != null) {
            if (discordReg == null) {
                discordReg = new DiscordSRVRegister(this);
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
}
