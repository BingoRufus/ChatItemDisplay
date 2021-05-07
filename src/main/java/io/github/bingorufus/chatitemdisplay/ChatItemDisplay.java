package io.github.bingorufus.chatitemdisplay;


import com.comphenix.protocol.ProtocolLibrary;
import com.google.gson.JsonObject;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.bingorufus.chatitemdisplay.api.display.DisplayType;
import io.github.bingorufus.chatitemdisplay.displayables.DisplayEnderChestType;
import io.github.bingorufus.chatitemdisplay.displayables.DisplayInventoryType;
import io.github.bingorufus.chatitemdisplay.displayables.DisplayItemType;
import io.github.bingorufus.chatitemdisplay.executors.ChatItemReloadExecutor;
import io.github.bingorufus.chatitemdisplay.executors.DebugExecutor;
import io.github.bingorufus.chatitemdisplay.executors.display.DisplayExecutor;
import io.github.bingorufus.chatitemdisplay.executors.display.ViewItemExecutor;
import io.github.bingorufus.chatitemdisplay.listeners.ChatDisplayListener;
import io.github.bingorufus.chatitemdisplay.listeners.InventoryClick;
import io.github.bingorufus.chatitemdisplay.listeners.LoggerListener;
import io.github.bingorufus.chatitemdisplay.listeners.MessageCommandListener;
import io.github.bingorufus.chatitemdisplay.listeners.packet.ChatPacketListener;
import io.github.bingorufus.chatitemdisplay.listeners.packet.RecipeSelector;
import io.github.bingorufus.chatitemdisplay.util.BrigadierRegistry;
import io.github.bingorufus.chatitemdisplay.util.ChatItemConfig;
import io.github.bingorufus.chatitemdisplay.util.Cooldown;
import io.github.bingorufus.chatitemdisplay.util.display.ConfigReloader;
import io.github.bingorufus.chatitemdisplay.util.loaders.DiscordSRVRegister;
import io.github.bingorufus.chatitemdisplay.util.loaders.LangReader;
import io.github.bingorufus.chatitemdisplay.util.loaders.Metrics;
import io.github.bingorufus.chatitemdisplay.util.logger.ConsoleFilter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ChatItemDisplay extends JavaPlugin {
    public static final String MINECRAFT_VERSION = Bukkit.getServer().getVersion().substring(Bukkit.getServer().getVersion().indexOf("(MC: ") + 5,
            Bukkit.getServer().getVersion().indexOf(")"));

    private static final ArrayList<DisplayType<?>> registeredDisplayables = new ArrayList<>();
    private static ChatItemDisplay INSTANCE;
    private final HashMap<Inventory, UUID> chatItemDisplayInventories = new HashMap<>(); //Inventories and the UUIDs of the owners
    private final Cooldown<Player> displayCooldown = new Cooldown<>(0);
    private DiscordSRVRegister discordReg;
    private DisplayedManager dm;
    private JsonObject lang;

    public static ChatItemDisplay getInstance() {
        return INSTANCE;
    }

    /*TODO:
     * Auto config updating
     * Images sent to discordSRV
     * PlayerVaults
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
        ProtocolLibrary.getProtocolManager().addPacketListener(new ChatPacketListener());
        ProtocolLibrary.getProtocolManager().addPacketListener(new RecipeSelector());

        Metrics metrics = new Metrics(this, 7229);

        Bukkit.getScheduler().runTask(this, () -> BrigadierRegistry.registerDisplayableAliases(getRegisteredDisplayables()));
    }

    @Override
    public void onDisable() {
        if (discordReg != null) {
            discordReg.unregister();
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (getChatItemDisplayInventories().containsKey(p.getOpenInventory().getTopInventory())) {
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


    public HashMap<Inventory, UUID> getChatItemDisplayInventories() {
        return chatItemDisplayInventories;
    }


    public ArrayList<DisplayType<?>> getRegisteredDisplayables() {
        return (ArrayList<DisplayType<?>>) registeredDisplayables.clone();
    }

    public void registerDisplayable(DisplayType<?> displayType) {
        registeredDisplayables.add(displayType);

        try {
            PluginCommand displayCommand = getCommand(displayType.getCommand());
            if (displayCommand == null) {
                BrigadierRegistry.registerCommand(LiteralArgumentBuilder.literal(displayType.getCommand()).build());
            }
            displayCommand = getCommand(displayType.getCommand());
            displayCommand.setExecutor(new DisplayExecutor(displayType));
        } catch (NullPointerException e) {
            throw new NullPointerException("No command named \"" + displayType.getCommand() + "\" has been registered in the plugin.yml");
        }
    }

    public DisplayType<?> getDisplayType(Class<? extends DisplayType<?>> displayTypeClass) {
        DisplayType<?> displayType = ChatItemDisplay.getInstance().getRegisteredDisplayables().stream().filter(type -> type.getClass().equals(displayTypeClass)).findFirst().orElse(null);
        if (displayType == null) {
            Bukkit.getLogger().warning("Cannot find a displaytype that has the class path of: " + displayTypeClass.getCanonicalName());
            return null;
        }
        return displayType;
    }

}
