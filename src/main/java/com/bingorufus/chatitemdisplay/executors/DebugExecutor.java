package com.bingorufus.chatitemdisplay.executors;

import com.bingorufus.chatitemdisplay.ChatItemDisplay;
import com.bingorufus.chatitemdisplay.api.ChatItemDisplayAPI;
import com.bingorufus.chatitemdisplay.util.ChatItemConfig;
import com.bingorufus.chatitemdisplay.util.string.StringFormatter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.SpigotConfig;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DebugExecutor implements CommandExecutor {
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss");

    public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {


        if (!sender.hasPermission("chatitemdisplay.command.debug")) {
            sender.sendMessage(StringFormatter.format(
                    ChatItemConfig.MISSING_PERMISSION_GENERIC.getCachedValue()));
            return true;
        }

        File folder = ChatItemDisplay.getInstance().getDataFolder();
        String logName = "Debug Log " + dateFormat.format(new Date()) + ".txt";
        File log = folder.toPath().resolve("logs").resolve(logName).toFile();

        File logFolder = folder.toPath().resolve("logs").toFile();
        if (!logFolder.exists()) logFolder.mkdirs();

        BufferedWriter writer = null;
        OutputStreamWriter out = null;

        try {
            log.createNewFile();
            out = new OutputStreamWriter(new FileOutputStream(log));

            writer = new BufferedWriter(out);

            if (!writeLogs(writer)) {
                sender.sendMessage(ChatColor.RED + "An unexpected error has occurred");
                writer.close();
                out.close();
                writer.flush();
                out.flush();
                return true;
            }
            writer.flush();
            out.flush();
            writer.close();
            out.close();

            String zipName = "Debug Log " + dateFormat.format(new Date()) + ".zip";
            File zip = folder.toPath().resolve("logs").resolve(zipName).toFile();

            zip.createNewFile();
            Path jar = Paths.get(ChatItemDisplay.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toAbsolutePath();

            if (!zipFiles(zip, log, jar.getParent().getParent().resolve("logs").resolve("latest.log").toFile())) {
                sender.sendMessage(ChatColor.RED + "An unexpected error has occurred");
                return true;
            }
            Files.delete(log.toPath());

            TextComponent tc = new TextComponent(ChatColor.GREEN + "A debug log has been successfully generated and has been saved to ");
            TextComponent extra = new TextComponent(ChatColor.GREEN + "" + ChatColor.BOLD + zip.getAbsolutePath());

            if (!StringFormatter.HEX_AVAILABLE) {
                extra.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to copy the file path").create()));
                extra.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, zip.getAbsolutePath()));

            } else {
                extra.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to copy the file path")));
                extra.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, zip.getAbsolutePath()));
            }

            tc.addExtra(extra);
            if (sender instanceof Player) sender.spigot().sendMessage(tc);
            else
                sender.sendMessage(ChatColor.GREEN + "A debug log has been successfully generated and has been saved to " + ChatColor.BOLD + zip.getAbsolutePath());

        } catch (IOException | URISyntaxException e) {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (Exception ignored) {
                e.printStackTrace();
            }
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "An unexpected error has occurred");
            return true;
        }


        return true;
    }

    private boolean writeLogs(BufferedWriter w) {
        Server s = Bukkit.getServer();
        try {
            writeLine(w, "Server Version - " + s.getVersion());
            writeLine(w, "Bukkit Version - " + s.getBukkitVersion());
            writeLine(w, "Bungee - " + SpigotConfig.bungee);
            writeLine(w, "Offline - " + !s.getOnlineMode());
            writeLine(w, "Java Version - " + System.getProperty("java.version"));
            writeLine(w, "Operating System - " + System.getProperty("os.name"));
            writeLine(w, "OS Version - " + System.getProperty("os.version"));


            w.newLine();

            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                writeLine(w, plugin.getName() + " by " + plugin.getDescription().getAuthors() + " Version: " +
                        plugin.getDescription().getVersion() + " - " + (plugin.isEnabled() ? " Enabled" : "Disabled"));
            }
            w.newLine();
            w.newLine();
            writeLine(w, ChatItemConfig.getConfig().saveToString());
            w.newLine();
            writeLine(w, "Displays:");

            ChatItemDisplayAPI.getDisplayedManager().forEach(display -> {
                try {
                    writeLine(w, StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeJava(display.serialize()))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;


    }

    private void writeLine(BufferedWriter w, String s) throws IOException {
        w.write(s);
        w.newLine();
    }

    private boolean zipFiles(File zip, File... files) {
        try {
            if (!zip.exists()) zip.createNewFile();
            byte[] buffer = new byte[1024];
            FileOutputStream zipFileStream = new FileOutputStream(zip);
            try (ZipOutputStream zipStream = new ZipOutputStream(zipFileStream)) {
                for (File f : files) {
                    try (FileInputStream fileInputStream = new FileInputStream(f)) {
                        zipStream.putNextEntry(new ZipEntry(f.getName()));
                        int length;
                        while ((length = fileInputStream.read(buffer)) > 0) {
                            zipStream.write(buffer, 0, length);
                        }
                        zipStream.closeEntry();
                    }
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

}