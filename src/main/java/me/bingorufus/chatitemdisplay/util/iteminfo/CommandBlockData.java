package me.bingorufus.chatitemdisplay.util.iteminfo;


import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.Objects;

public class CommandBlockData {
    private boolean isConditional = false;
    private CommandBlockType type = CommandBlockType.IMPULSE;
    private String command = "";
    private String name = "@";
    private boolean auto = false;

    public CommandBlockData() {
    }

    public CommandBlockData(Block b) {
        this((CommandBlock) b.getState());
    }

    public CommandBlockData(CommandBlock cb) {
        if (cb == null) return;
        this.type = CommandBlockType.fromMaterial(cb.getType());
        if (this.type == null) return;
        this.command = cb.getCommand();
        this.isConditional = ((org.bukkit.block.data.type.CommandBlock) cb.getBlockData()).isConditional();
    }

    public CommandBlockData(ItemStack item) {
        this((CommandBlock) ((BlockStateMeta) Objects.requireNonNull(item.getItemMeta())).getBlockState());
        this.auto = new ItemStackReflection().getNBT(item).contains(",auto:1b");
    }

    //Getters
    public boolean isAlwaysActive() {
        return auto;
    }

    //Setters
    public void setAlwaysActive(boolean auto) {
        this.auto = auto;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isConditional() {
        return isConditional;
    }

    public void setConditional(boolean conditional) {
        this.isConditional = conditional;
    }

    public String getCommand() {
        return this.command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public CommandBlockType getType() {
        return this.type;
    }

    // Util
    public void openCommandBlock(Player p) {
        if (!ChatItemDisplay.getInstance().hasProtocollib) return;
        // new SignOpener().open(p);

    }

    public enum CommandBlockType {
        IMPULSE(Material.COMMAND_BLOCK), CHAIN(Material.CHAIN_COMMAND_BLOCK), REPEATING(Material.REPEATING_COMMAND_BLOCK), MINECART(Material.COMMAND_BLOCK_MINECART);
        private final Material mat;

        CommandBlockType(Material mat) {
            this.mat = mat;
        }

        public static CommandBlockType fromMaterial(Material m) {
            for (CommandBlockType type : CommandBlockType.values()) if (type.getMat().equals(m)) return type;
            return null;
        }

        public Material getMat() {
            return mat;
        }
    }
}
