package io.github.bingorufus.chatitemdisplay.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import io.github.bingorufus.chatitemdisplay.api.display.DisplayType;
import io.github.bingorufus.chatitemdisplay.util.iteminfo.ItemStackReflection;
import org.bukkit.Bukkit;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

public class BrigadierRegistry {
    private static CommandDispatcher<?> commandDispatcher;

    static {
        try {            //This whole block will set:  commandDispatcher to ((org.bukkit.craftbukkit.v1_16_R3.CraftServer) Bukkit.getServer()).getHandle().getServer().getCommandDispatcher().a()

            Class<?> craftServerClass = Class.forName("org.bukkit.craftbukkit.{v}.CraftServer".replace("{v}", ItemStackReflection.VERSION));
            Object craftBukkitServer = craftServerClass.cast(Bukkit.getServer());

            Method getPlayerList = craftBukkitServer.getClass().getMethod("getHandle");
            Object playerList = getPlayerList.invoke(craftBukkitServer);

            Method getMinecraftServer = playerList.getClass().getMethod("getServer");
            Object minecraftServer = getMinecraftServer.invoke(playerList);

            Method getDispatcher = minecraftServer.getClass().getMethod("getCommandDispatcher");
            Object NMSDispatcher = getDispatcher.invoke(minecraftServer);

            Method getMojangDispatcher = Arrays.stream(NMSDispatcher.getClass().getMethods()).filter(method -> method.getParameterCount() == 0).filter(method -> method.getReturnType().equals(CommandDispatcher.class)).findFirst().orElseThrow(() -> new NoSuchMethodException("Could not find a method to obtain the command dispatcher"));
            commandDispatcher = (CommandDispatcher<?>) getMojangDispatcher.invoke(NMSDispatcher);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

    }

    public static void registerCommand(CommandNode commandNode) {
        RootCommandNode<?> rootCommandNode = commandDispatcher.getRoot();
        rootCommandNode.addChild(commandNode);
    }

    public static CommandDispatcher<?> getCommandDispatcher() {
        return commandDispatcher;
    }

    public static void addAliases(String mainCommand, String[] aliases) {
        RootCommandNode<?> rootCommandNode = commandDispatcher.getRoot();

        CommandNode original;
        try {
            original = rootCommandNode.getChild(mainCommand);
            if (original == null) throw new NullPointerException();
        } catch (NullPointerException e) {
            new NoSuchMethodException("There is no command named \"" + mainCommand + "\"").printStackTrace();
            return;
        }
        if (aliases == null) return;
        for (String alias : aliases) {
            registerCommand(LiteralArgumentBuilder.literal(alias).redirect(original).build());
        }
    }

    public static void registerDisplayableAliases(ArrayList<DisplayType<?>> displayTypes) {
        RootCommandNode<?> rootCommandNode = commandDispatcher.getRoot();
        displayTypes.forEach(displayType -> addAliases(displayType.getCommand(), displayType.getAliases()));
    }

}
