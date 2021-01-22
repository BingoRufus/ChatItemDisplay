package me.bingorufus.chatitemdisplay.listeners;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.DisplayParser;
import me.bingorufus.chatitemdisplay.DisplayedManager;
import me.bingorufus.chatitemdisplay.displayables.Displayable;
import me.bingorufus.chatitemdisplay.util.ChatItemConfig;
import me.bingorufus.chatitemdisplay.util.Cooldown;
import me.bingorufus.chatitemdisplay.util.bungee.BungeeCordSender;
import me.bingorufus.chatitemdisplay.util.string.StringFormatter;
import net.md_5.bungee.chat.ComponentSerializer;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.nio.charset.StandardCharsets;

public class ChatDisplayListener implements Listener {


    public ChatDisplayListener() {
    }

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


        if (dp.containsItem() && !p.hasPermission("chatitemdisplay.display.item")) {
            p.sendMessage(new StringFormatter().format(ChatItemConfig.MISSING_PERMISSION_ITEM));
            e.setCancelled(true);
            return; //Does not have permission to display items
        }

        if (dp.containsInventory() && !p.hasPermission("chatitemdisplay.display.inventory")) {
            p.sendMessage(new StringFormatter().format(ChatItemConfig.MISSING_PERMISSION_INVENTORY));
            e.setCancelled(true);
            return; // Does not have permission to display inventories
        }

        if (dp.containsEnderChest() && !p.hasPermission("chatitemdisplay.display.enderchest")) {
            p.sendMessage(new StringFormatter().format(ChatItemConfig.MISSING_PERMISSION_ENDERCHEST));
            e.setCancelled(true);
            return; // Does not have permission to display enderchests
        }

        if (dp.containsItem()) {
            ItemStack item = p.getInventory().getItemInMainHand();
            if (item.getType() == Material.AIR) {
                p.sendMessage(new StringFormatter().format(ChatItemConfig.EMPTY_HAND));
                return; // Player's item is nothing
            }

            if (!p.hasPermission("chatitemdisplay.blacklistbypass")) {
                if (isBlackListed(item)) {
                    p.sendMessage(new StringFormatter().format(ChatItemConfig.BLACKLISTED_ITEM));
                    e.setCancelled(true);
                    return; // Item is blacklisted
                }

            }

        }

        boolean containsBlacklisted = false;

        if (dp.containsItem()) { // Item is an inventory with blacklisted item
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();
            if (meta instanceof BlockStateMeta) {
                BlockStateMeta bsm = (BlockStateMeta) meta;
                if (bsm.getBlockState() instanceof Container) {
                    Container c = (Container) bsm.getBlockState();
                    containsBlacklisted = containsBlacklist(c.getInventory());
                }
            }
        }


        if (dp.containsInventory() && !containsBlacklisted) { //Inventory contains a blacklisted item
            containsBlacklisted = containsBlacklist(e.getPlayer().getInventory());
        }
        if (dp.containsEnderChest() && !containsBlacklisted) {//Enderchest contains a blacklisted item
            containsBlacklisted = containsBlacklist(e.getPlayer().getEnderChest());
        }

        if (!p.hasPermission("chatitemdisplay.blacklistbypass")) {
            if (containsBlacklisted) {
                p.sendMessage(new StringFormatter().format(ChatItemConfig.CONTAINS_BLACKLIST));
                e.setCancelled(true);
                return; //Inventory, Item, or Enderchest contains a blacklisted item
            }
        }


        // At this point, all checks should be passed, and the user should be able to display their item/inventory
        ChatItemDisplay.getInstance().getDisplayCooldown().addToCooldown(p);
        String message = dp.format(p);
        if (dp.containsItem() && isDisplayTooLong(dp.getItem())) {
            p.sendMessage(new StringFormatter().format(ChatItemConfig.TOO_LARGE_ITEM));
            e.setCancelled(true);
            return;
        }
        if (dp.containsEnderChest() && isDisplayTooLong(dp.getEnderChest())) {
            p.sendMessage(new StringFormatter().format(ChatItemConfig.TOO_LARGE_ENDERCHEST));
            e.setCancelled(true);
            return;
        }
        if (dp.containsInventory() && isDisplayTooLong(dp.getInventory())) {
            p.sendMessage(new StringFormatter().format(ChatItemConfig.TOO_LARGE_INVENTORY));
            e.setCancelled(true);
            return;
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
            if (dp.containsEnderChest()) {
                sender.send(dp.getEnderChest(), false);
            }
            if (dp.containsItem()) {
                sender.send(dp.getItem(), false);
            }
            if (dp.containsInventory()) {
                sender.send(dp.getInventory(), false);
            }
        }
    }

    /**
     * @param display display
     * @return returns true if the length is over the maximum
     */
    private boolean isDisplayTooLong(Displayable display) {
        byte[] bytes = display.serialize().getBytes(StandardCharsets.UTF_8);
        if (ChatItemConfig.BUNGEE && bytes.length >= 30000) return true; //
        return bytes.length >= 1097152;
    }

    /**
     * @param message chat message
     * @return returns true if the length is over mojang's max chat length
     */
    private boolean isMessageTooLong(String message, DisplayParser dp) {
        DisplayedManager dm = ChatItemDisplay.getInstance().getDisplayedManager();
        String edit = message;
        if (dp.containsInventory()) {
            edit = edit.replace(dm.getDisplay(dp.getInventory()).getInsertion(), StringEscapeUtils.unescapeJava(ComponentSerializer.toString(dp.getInventory().getInfo().getHover())));
        }
        if (dp.containsEnderChest()) {
            edit = edit.replace(dm.getDisplay(dp.getEnderChest()).getInsertion(), StringEscapeUtils.unescapeJava(ComponentSerializer.toString(dp.getEnderChest().getInfo().getHover())));

        }
        if (dp.containsItem()) {
            edit = edit.replace(dm.getDisplay(dp.getItem()).getInsertion(), ComponentSerializer.toString(dp.getItem().getInfo().getHover()));

        }
        byte[] bytes = edit.getBytes(StandardCharsets.UTF_8);
        return bytes.length >= 240000;
    }


    private boolean containsBlacklist(Inventory inv) {
        for (ItemStack item : inv.getStorageContents()) {
            if (item == null) continue;
            if (isBlackListed(item)) return true;

            ItemMeta meta = item.getItemMeta();
            if (meta instanceof BlockStateMeta) {

                BlockStateMeta bsm = (BlockStateMeta) meta;
                if (bsm.getBlockState() instanceof Container) {

                    Container c = (Container) bsm.getBlockState();

                    if (containsBlacklist(c.getInventory())) return true;
                }
            }

        }
        return false;
    }

    private boolean isBlackListed(ItemStack item) {
        return ChatItemConfig.BLACKLISTED_ITEMS.contains(item.getType());
    }


}
