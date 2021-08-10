package io.github.bingorufus.chatitemdisplay.util;

import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;


public class ReflectionClassRetriever {
    private static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

    private ReflectionClassRetriever() {
    }

    @NonNull
    @SneakyThrows
    public static Class<?> getNMSClassOrThrow(String path) {
        Class<?> out = getNMSClass(path);
        if (out == null) {
            throw new ClassNotFoundException("Cannot find class with the name of " + path);
        }
        return out;
    }

    @NonNull
    @SneakyThrows
    public static Class<?> getCraftBukkitClassOrThrow(String path) {
        Class<?> out = getCraftBukkitClass(path);
        if (out == null) {
            throw new ClassNotFoundException("Cannot find class with the name of " + path);
        }
        return out;
    }

    @Nullable
    public static Class<?> getNMSClass(String path) {
        return getClass("net.minecraft.server", path);
    }

    @Nullable
    public static Class<?> getCraftBukkitClass(String path) {
        return getClass("org.bukkit.craftbukkit", path);
    }

    @Nullable
    public static Class<?> getClass(String prefix, String suffix) {
        return requireNonNullOrElse(getClassWithVersion(prefix, suffix), getClassWithOutVersion(prefix, suffix));
    }

    private static Class<?> getClassWithVersion(String prefix, String suffix) {
        try {
            return Class.forName(prefix + "." + VERSION + "." + suffix);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static Class<?> getClassWithOutVersion(String prefix, String suffix) {
        try {
            return Class.forName(prefix + "." + suffix);
        } catch (ClassNotFoundException e) {
            return null;
        }

    }

    private static <T> T requireNonNullOrElse(T arg0, T arg1) {
        return arg0 == null ? arg1 : arg0;
    }
}
