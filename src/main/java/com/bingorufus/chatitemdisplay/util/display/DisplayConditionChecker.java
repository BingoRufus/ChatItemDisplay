package com.bingorufus.chatitemdisplay.util.display;

import com.bingorufus.chatitemdisplay.DisplayParser;
import com.bingorufus.chatitemdisplay.DisplayedManager;
import com.bingorufus.chatitemdisplay.api.ChatItemDisplayAPI;
import com.bingorufus.chatitemdisplay.api.display.DisplayType;
import com.bingorufus.chatitemdisplay.api.display.Displayable;
import com.bingorufus.chatitemdisplay.displayables.DisplayItemType;
import com.bingorufus.chatitemdisplay.util.ChatItemConfig;
import com.bingorufus.chatitemdisplay.util.Cooldown;
import com.bingorufus.chatitemdisplay.util.logger.DebugLogger;
import com.bingorufus.chatitemdisplay.util.string.StringFormatter;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DisplayConditionChecker {

    private DisplayConditionChecker() {
    }

    public static Result doCancelEvent(Player p, DisplayParser dp) {
        if (!dp.containsDisplay()) return Result.IGNORE; //Not trying to display anything
        if (!p.hasPermission("chatitemdisplay.cooldownbypass")) {
            Cooldown<Player> cooldown = ChatItemDisplayAPI.getDisplayCooldown();
            if (cooldown.isOnCooldown(p)) {
                double secondsRemaining = (double) (Math.round((double) cooldown.getTimeRemaining(p) / 100)) / 10;
                p.sendMessage(StringFormatter.format(ChatItemConfig.COOLDOWN.getCachedValue().replace("%seconds%", "" + secondsRemaining)));
                return Result.CANCEL; // Is on cooldown
            }

            cooldown.ensureRemoved(p);
        }
        DebugLogger.log("Display is present");
        for (DisplayType<?> displayType : dp.getDisplayedTypes()) {
            if (!p.hasPermission(displayType.getPermission())) {
                p.sendMessage(StringFormatter.format(displayType.getMissingPermissionMessage()));
                return Result.CANCEL;
            }
        }

        dp.createDisplayables(p);

        if (dp.getDisplayable(ChatItemDisplayAPI.getDisplayType(DisplayItemType.class)) != null) {
            ItemStack item = p.getInventory().getItemInMainHand();
            if (item.getType() == Material.AIR) {
                p.sendMessage(StringFormatter.format(ChatItemConfig.EMPTY_HAND.getCachedValue()));
                return Result.IGNORE;
            }
        }

        if (!p.hasPermission("chatitemdisplay.blacklistbypass")) {
            for (Displayable displayable : dp.getDisplayables()) {
                if (displayable.hasBlacklistedItem()) {
                    p.sendMessage(StringFormatter.format(ChatItemConfig.CONTAINS_BLACKLIST.getCachedValue()));
                    return Result.CANCEL; //Inventory, Item, or Enderchest contains a blacklisted item
                }
            }
        }


        // At this point, all checks should be passed, and the user should be able to display their item/inventory
        ChatItemDisplayAPI.getDisplayCooldown().addToCooldown(p);

        String message = dp.format(p);

        for (Displayable displayable : dp.getDisplayables()) {
            if (isDisplayTooLong(displayable)) {
                p.sendMessage(StringFormatter.format(displayable.getType().getTooLargeMessage()));
                return Result.CANCEL;
            }
        }

        if (isMessageTooLong(message, dp)) {
            p.sendMessage(StringFormatter.format(ChatItemConfig.TOO_LARGE_MESSAGE.getCachedValue()));
            return Result.CANCEL;
        }
        return Result.ACCEPT;
    }

    /**
     * @param display display
     * @return returns true if the length is over the maximum
     */
    private static boolean isDisplayTooLong(Displayable display) {
        byte[] bytes = display.serialize().toString().getBytes(StandardCharsets.UTF_8);
        return bytes.length >= 240000;
    }

    /**
     * @param message chat message
     * @return returns true if the length is over mojang's max chat length
     */
    private static boolean isMessageTooLong(String message, DisplayParser dp) {
        DisplayedManager dm = ChatItemDisplayAPI.getDisplayedManager();
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

    public enum Result {
        CANCEL,
        ACCEPT,
        IGNORE,

    }
}
