package me.bingorufus.chatitemdisplay;


import com.google.gson.JsonObject;
import me.bingorufus.chatitemdisplay.executors.ChatItemReloadExecutor;
import me.bingorufus.chatitemdisplay.executors.DebugExecutor;
import me.bingorufus.chatitemdisplay.executors.display.DisplayEnderChestExecutor;
import me.bingorufus.chatitemdisplay.executors.display.DisplayInventoryExecutor;
import me.bingorufus.chatitemdisplay.executors.display.DisplayItemExecutor;
import me.bingorufus.chatitemdisplay.executors.display.ViewItemExecutor;
import me.bingorufus.chatitemdisplay.listeners.*;
import me.bingorufus.chatitemdisplay.util.bungee.BungeeCordReceiver;
import me.bingorufus.chatitemdisplay.util.display.ConfigReloader;
import me.bingorufus.chatitemdisplay.util.loaders.DiscordSRVRegister;
import me.bingorufus.chatitemdisplay.util.loaders.LangReader;
import me.bingorufus.chatitemdisplay.util.loaders.Metrics;
import me.bingorufus.chatitemdisplay.util.loaders.ProtocolLibRegister;
import me.bingorufus.chatitemdisplay.util.logger.LoggerFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class ChatItemDisplay extends JavaPlugin {
    private static ChatItemDisplay main;
    public final Map<UUID, Long> displayCooldowns = new HashMap<>();
    public final HashMap<Player, ItemStack> viewingMap = new HashMap<>();
    public final HashMap<Inventory, UUID> invs = new HashMap<>();
    public NewVersionDisplayer NewVer;
    public BungeeCordReceiver in;
    public boolean hasProtocollib = false;
    public Boolean useOldFormat = false;
    public Long pingTime;
    ChatDisplayListener DisplayListener;
    ProtocolLibRegister pl;
    DiscordSRVRegister discordReg;
    private DisplayedManager dm;
    private JsonObject lang;
    private InventoryClick ic;
    private Boolean isBungee;

    public static ChatItemDisplay getInstance() {
        return main;
    }

    /*
     * TODO: a /version command that shows java version server cversion etc - auto
     * update config - Command Block GUI
     */

    @Override
    public void onEnable() {

        main = this;
        this.saveDefaultConfig();
        this.dm = new DisplayedManager();
        this.getCommand("generatedebuglog").setExecutor(new DebugExecutor());
        this.getCommand("viewitem").setExecutor(new ViewItemExecutor());
        this.getCommand("chatitemreload").setExecutor(new ChatItemReloadExecutor());
        Metrics metrics = new Metrics(this, 7229);
        this.getCommand("displayitem").setExecutor(new DisplayItemExecutor());
        this.getCommand("displayinv").setExecutor(new DisplayInventoryExecutor());
        this.getCommand("displayenderchest").setExecutor(new DisplayEnderChestExecutor());
        Bukkit.getPluginManager().registerEvents(new MessageCommandListener(), this);
        reloadFilters();
        reloadListeners();
        new ConfigReloader(this).reload();
        metrics.addCustomChart(new Metrics.SimplePie("old_display_messages", () -> getConfig().getString("use-old-format")));
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

    private void reloadFilters() {
        Logger logger = (Logger) LogManager.getRootLogger();
        Iterator<Filter> filters = logger.getFilters();
        while (filters.hasNext()) { // Prevents duplicate loggers
            Filter f = filters.next();

            if (f.getClass().getName().equals(LoggerFilter.class.getName())) {// Check if the filter is one of mine
                if (!f.isStopped())
                    f.stop();
            }

        }
        logger.addFilter(new LoggerFilter(this));
    }

    public void reloadListeners() {
        Bukkit.getPluginManager().registerEvents(new MapViewerListener(this), this);

        useOldFormat = this.getConfig().getBoolean("use-old-format")
                || Bukkit.getServer().getPluginManager().getPlugin("ProtocolLib") == null;
        if (!useOldFormat) {
            if (pl == null)
                pl = new ProtocolLibRegister(this);
            pl.registerPacketListener();
            hasProtocollib = true;
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "" + ChatColor.BOLD
                    + "[ChatItemDisplay] In Chat Item Displaying has Been Disabled Because This Server Does Not Have ProtocolLib Or Has Been Disabled in The config.yml");
            hasProtocollib = false;
        }
        if (ic != null) HandlerList.unregisterAll(ic);
        if (DisplayListener != null)
            HandlerList.unregisterAll(DisplayListener);
        if (NewVer != null)
            HandlerList.unregisterAll(NewVer);

        if (discordReg != null)
            discordReg.unregister();
        if (Bukkit.getPluginManager().getPlugin("DiscordSRV") != null) {
            if (discordReg == null) {
                discordReg = new DiscordSRVRegister(this);
            }

            discordReg.register();
        }
        DisplayListener = new ChatDisplayListener(this);
        Bukkit.getPluginManager().registerEvents(DisplayListener, this);
        ic = new InventoryClick();
        Bukkit.getPluginManager().registerEvents(ic, this);


    }

    public boolean isBungee() {
        if (isBungee == null) checkBungee();
        return isBungee;

    }

    public void checkBungee() {
        this.isBungee = getConfig().getBoolean("send-to-bungee");
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
}
