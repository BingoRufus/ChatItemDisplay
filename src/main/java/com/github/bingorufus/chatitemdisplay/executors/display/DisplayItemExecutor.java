package com.github.bingorufus.chatitemdisplay.executors.display;

import com.github.bingorufus.chatitemdisplay.ChatItemDisplay;
import com.github.bingorufus.chatitemdisplay.displayables.DisplayItem;
import com.github.bingorufus.chatitemdisplay.util.ChatItemConfig;
import com.github.bingorufus.chatitemdisplay.util.Cooldown;
import com.github.bingorufus.chatitemdisplay.util.bungee.BungeeCordSender;
import com.github.bingorufus.chatitemdisplay.util.display.DisplayPermissionChecker;
import com.github.bingorufus.chatitemdisplay.util.string.StringFormatter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DisplayItemExecutor extends DisplayExecutor {
    final ChatItemDisplay m = ChatItemDisplay.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (isDisabled(sender)) return true;


        Player p = (Player) sender;
        switch (new DisplayPermissionChecker(m, p).displayItem()) {
            case DISPLAY:
                DisplayItem d = new DisplayItem(p.getInventory().getItemInMainHand(), p.getName(), p.getDisplayName(),
                        p.getUniqueId()
                );

                if (d.serialize().length() >= Short.MAX_VALUE - 20) {
                    p.sendMessage(new StringFormatter().format(ChatItemConfig.TOO_LARGE_ITEM));
                    return true;
                }
                if (isEventCancelled(p, d)) return true;
                m.getDisplayedManager().addDisplayable(p.getName().toUpperCase(), d);
                if (ChatItemConfig.BUNGEE)
                    new BungeeCordSender().send(d, true);
                d.getInfo().broadcastCommandMessage();

                if (!p.hasPermission("ChatItemDisplay.cooldownbypass"))
                    ChatItemDisplay.getInstance().getDisplayCooldown().addToCooldown(p);
                break;
            case BLACKLISTED:
                p.sendMessage(new StringFormatter()
                        .format(ChatItemConfig.BLACKLISTED_ITEM));
                break;
            case COOLDOWN:
                Cooldown<Player> cooldown = ChatItemDisplay.getInstance().getDisplayCooldown();
                double secondsRemaining = (double) (Math.round((double) cooldown.getTimeRemaining(p) / 100)) / 10;
                p.sendMessage(new StringFormatter().format(ChatItemConfig.COOLDOWN.replace("%seconds%", "" + secondsRemaining)));
                break;
            case NO_PERMISSON:
                p.sendMessage(new StringFormatter()
                        .format(ChatItemConfig.MISSING_PERMISSION_GENERIC));
                break;
            case NULL_ITEM:
                p.sendMessage(new StringFormatter()
                        .format(ChatItemConfig.EMPTY_HAND));
                break;
        }
        return true;


    }

}
