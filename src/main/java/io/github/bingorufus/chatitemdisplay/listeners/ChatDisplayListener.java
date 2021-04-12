package io.github.bingorufus.chatitemdisplay.listeners;

import io.github.bingorufus.chatitemdisplay.ChatItemDisplay;
import io.github.bingorufus.chatitemdisplay.DisplayParser;
import io.github.bingorufus.chatitemdisplay.DisplayedManager;
import io.github.bingorufus.chatitemdisplay.api.display.DisplayType;
import io.github.bingorufus.chatitemdisplay.api.display.Displayable;
import io.github.bingorufus.chatitemdisplay.displayables.DisplayItemType;
import io.github.bingorufus.chatitemdisplay.util.ChatItemConfig;
import io.github.bingorufus.chatitemdisplay.util.Cooldown;
import io.github.bingorufus.chatitemdisplay.util.bungee.BungeeCordSender;
import io.github.bingorufus.chatitemdisplay.util.logger.DebugLogger;
import io.github.bingorufus.chatitemdisplay.util.string.StringFormatter;
import net.md_5.bungee.chat.ComponentSerializer;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatDisplayListener implements Listener {


    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        DebugLogger.log(p.getName() + " sent a message");


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
        int numberOfDisplays = 0;
        for (Displayable displayable : dp.getDisplayables()) {
            Pattern displayPattern = Pattern.compile(Pattern.quote(dm.getDisplay(displayable).getInsertion()));
            Matcher displayMatcher = displayPattern.matcher(edit);
            while (displayMatcher.find()) {
                numberOfDisplays++;
                edit = displayMatcher.replaceFirst(StringEscapeUtils.unescapeJava(ComponentSerializer.toString(displayable.getDisplayComponent())));
            }
        }
        if (ChatItemConfig.MAXIMUM_DISPLAYS != 0 && numberOfDisplays >= ChatItemConfig.MAXIMUM_DISPLAYS) {
            return true;
        }
        byte[] bytes = edit.getBytes(StandardCharsets.UTF_8);
        return bytes.length >= 25000;
    }


}
