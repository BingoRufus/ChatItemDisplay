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
                p.sendMessage(StringFormatter.format(ChatItemConfig.COOLDOWN.getCachedValue().replace("%seconds%", "" + secondsRemaining)));
                e.setCancelled(true);
                return; // Is on cooldown
            }
        }
        DebugLogger.log("Display is present");
        for (DisplayType<?> displayType : dp.getDisplayedTypes()) {
            if (!p.hasPermission(displayType.getPermission())) {
                p.sendMessage(StringFormatter.format(displayType.getMissingPermissionMessage()));
                e.setCancelled(true);
                return;
            }
        }

        dp.createDisplayables(p);

        if (dp.getDisplayable(ChatItemDisplay.getInstance().getDisplayType(DisplayItemType.class)) != null) {
            ItemStack item = p.getInventory().getItemInMainHand();
            if (item.getType() == Material.AIR) {
                p.sendMessage(StringFormatter.format(ChatItemConfig.EMPTY_HAND.getCachedValue()));
                return;
            }
        }

        if (!p.hasPermission("chatitemdisplay.blacklistbypass")) {
            for (Displayable displayable : dp.getDisplayables()) {
                if (displayable.hasBlacklistedItem()) {
                    p.sendMessage(StringFormatter.format(ChatItemConfig.CONTAINS_BLACKLIST.getCachedValue()));
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
                p.sendMessage(StringFormatter.format(displayable.getType().getTooLargeMessage()));
                e.setCancelled(true);
                return;
            }
        }

        if (isMessageTooLong(message, dp)) {
            p.sendMessage(StringFormatter.format(ChatItemConfig.TOO_LARGE_MESSAGE.getCachedValue()));
            e.setCancelled(true);
            return;
        }
        e.setMessage(message);

        //Send stuff to bungee
        if (ChatItemConfig.BUNGEE.getCachedValue()) {
            dp.getDisplayables().forEach(display -> BungeeCordSender.send(display, false));
        }
    }

    /**
     * @param display display
     * @return returns true if the length is over the maximum
     */
    private boolean isDisplayTooLong(Displayable display) {
        byte[] bytes = display.serialize().toString().getBytes(StandardCharsets.UTF_8);
        if (ChatItemConfig.BUNGEE.getCachedValue() && bytes.length >= Short.MAX_VALUE) return true; //
        return bytes.length >= 240000;// 11 bit max integer
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
                edit = edit.replace(displayMatcher.group(), ComponentSerializer.toString(displayable.getDisplayComponent()));
                displayMatcher = displayPattern.matcher(edit);
                if (numberOfDisplays >= 512)
                    throw new StackOverflowError("This wasn't supposed to happen... Please do /generatedebuglogs and send the file to the developer of ChatItemDisplay");

            }
        }
        return ChatItemConfig.MAX_DISPLAYS.getCachedValue() != -1 && numberOfDisplays >= ChatItemConfig.MAX_DISPLAYS.getCachedValue();
    }


}
