package com.github.bingorufus.chatitemdisplay.listeners;

import com.github.bingorufus.chatitemdisplay.ChatItemDisplay;
import com.github.bingorufus.chatitemdisplay.DisplayParser;
import com.github.bingorufus.chatitemdisplay.DisplayedManager;
import com.github.bingorufus.chatitemdisplay.api.display.DisplayType;
import com.github.bingorufus.chatitemdisplay.api.display.Displayable;
import com.github.bingorufus.chatitemdisplay.displayables.DisplayItemType;
import com.github.bingorufus.chatitemdisplay.util.ChatItemConfig;
import com.github.bingorufus.chatitemdisplay.util.Cooldown;
import com.github.bingorufus.chatitemdisplay.util.bungee.BungeeCordSender;
import com.github.bingorufus.chatitemdisplay.util.string.StringFormatter;
import net.md_5.bungee.chat.ComponentSerializer;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.nio.charset.StandardCharsets;

public class ChatDisplayListener implements Listener {


    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (ChatItemConfig.DEBUG_MODE)
            Bukkit.getLogger().info(p.getName() + " sent a message");


        DisplayParser dp = new DisplayParser(e.getMessage());
        if (!dp.containsDisplay()) return; //Not trying to display anything
        if (!p.hasPermission("chatitemdisplay.cooldownbypass")) {
            Cooldown<Player> cooldown = ChatItemDisplay.getInstance().getDisplayCooldown();
            if (cooldown.isOnCooldown(p)) {
                double secondsRemaining = (double) (Math.round((double) cooldown.getTimeRemaining(p) / 100)) / 10;
                p.sendMessage(new StringFormatter().format(ChatItemConfig.COOLDOWN.replace("%seconds%", "" + secondsRemaining)));
                e.setCancelled(true);
                return; // Is on cooldown
            }
        }
        for (DisplayType displayType : dp.getDisplayedTypes()) {
            if (!p.hasPermission(displayType.getPermission())) {
                p.sendMessage(new StringFormatter().format(displayType.getMissingPermissionMessage()));
                e.setCancelled(true);
                return;
            }
        }

        dp.createDisplayables(p);
        if (dp.getDisplayable(ChatItemDisplay.getInstance().getDisplayType(DisplayItemType.class)) != null) {
            ItemStack item = p.getInventory().getItemInMainHand();
            if (item.getType() == Material.AIR) {
                p.sendMessage(new StringFormatter().format(ChatItemConfig.EMPTY_HAND));
                return;
            }
        }
        if (!p.hasPermission("chatitemdisplay.blacklistbypass")) {
            for (Displayable displayable : dp.getDisplayables()) {
                if (displayable.hasBlacklistedItem()) {
                    p.sendMessage(new StringFormatter().format(ChatItemConfig.CONTAINS_BLACKLIST));
                    e.setCancelled(true);
                    return; //Inventory, Item, or Enderchest contains a blacklisted item
                }
            }
        }


        // At this point, all checks should be passed, and the user should be able to display their item/inventory
        ChatItemDisplay.getInstance().getDisplayCooldown().addToCooldown(p);
        String message = dp.format(p);
        for (Displayable displayable : dp.getDisplayables()) {
            if (isDisplayTooLong(displayable)) {
                p.sendMessage(displayable.getType().getTooLargeMessage());
                e.setCancelled(true);
                return;
            }
        }

        if (isMessageTooLong(message, dp)) {
            p.sendMessage(new StringFormatter().format(ChatItemConfig.TOO_LARGE_MESSAGE));
            e.setCancelled(true);
            return;
        }

        e.setMessage(message);

        //Send stuff to bungee
        if (ChatItemConfig.BUNGEE) {
            BungeeCordSender sender = new BungeeCordSender();
            dp.getDisplayables().forEach(display -> sender.send(display, false));

        }
    }

    /**
     * @param display display
     * @return returns true if the length is over the maximum
     */
    private boolean isDisplayTooLong(Displayable display) {
        byte[] bytes = display.serialize().toString().getBytes(StandardCharsets.UTF_8);
        if (ChatItemConfig.BUNGEE && bytes.length >= 30000) return true; //
        return bytes.length >= 240000;
    }

    /**
     * @param message chat message
     * @return returns true if the length is over mojang's max chat length
     */
    private boolean isMessageTooLong(String message, DisplayParser dp) {
        DisplayedManager dm = ChatItemDisplay.getInstance().getDisplayedManager();
        String edit = message;
        for (Displayable displayable : dp.getDisplayables()) {
            edit = edit.replace(dm.getDisplay(displayable).getInsertion(), StringEscapeUtils.unescapeJava(ComponentSerializer.toString(displayable.getInsertion())));

        }
        byte[] bytes = edit.getBytes(StandardCharsets.UTF_8);
        return bytes.length >= 240000;
    }


}
