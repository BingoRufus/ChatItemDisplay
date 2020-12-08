package me.bingorufus.chatitemdisplay.listeners;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.DisplayParser;
import me.bingorufus.chatitemdisplay.util.ChatItemConfig;
import me.bingorufus.chatitemdisplay.util.Cooldown;
import me.bingorufus.chatitemdisplay.util.string.StringFormatter;
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

public class ChatDisplayListener implements Listener {

    final char bell = '\u0007';

    final ChatItemDisplay m;
    boolean displayed;

    public ChatDisplayListener() {
        this.m = ChatItemDisplay.getInstance();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        displayed = false;
        if (ChatItemConfig.DEBUG_MODE)
            Bukkit.getLogger().info(p.getName() + " sent a message");

        // Check for permission/cooldown
        //Send permission message
        //Do stuff regarding format
        DisplayParser dp = new DisplayParser(e.getMessage());
        if (!dp.containsDisplay()) return; //Trying to display something

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
        e.setMessage(dp.format(p));

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
