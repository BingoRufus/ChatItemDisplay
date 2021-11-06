package com.bingorufus.chatitemdisplay.displayables;

import com.bingorufus.chatitemdisplay.api.display.DisplayType;
import com.bingorufus.chatitemdisplay.api.display.Displayable;
import com.bingorufus.chatitemdisplay.util.ChatItemConfig;
import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Objects;

public abstract class SerializedDisplayType<T extends Displayable> extends DisplayType<T> {
    private String command;
    private List<String> triggers;
    private String title;
    private boolean commandEnabled;
    private String tooMuchNBT;
    private String missingPermission;
    private List<String> aliases;

    public SerializedDisplayType() {
        FileConfiguration config = ChatItemConfig.getConfig();
        if (config.isConfigurationSection(dataPath()))
            loadData(Objects.requireNonNull(config.getConfigurationSection(dataPath())));
    }

    public void loadData(ConfigurationSection configData) {
        command = configData.getString("command");
        triggers = configData.getStringList("triggers");
        missingPermission = configData.getString("missing-permission");
        tooMuchNBT = configData.getString("too-much-nbt");
        commandEnabled = configData.getBoolean("command-enabled");
        title = configData.getString("inventory-title");
        aliases = configData.getStringList("aliases");
    }

    public @NonNull
    abstract String dataPath();

    @Override
    public List<String> getTriggers() {
        return triggers;
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public String getInventoryTitle() {
        if (title == null) return "title";
        return title;
    }

    @Override
    public boolean isCommandEnabled() {
        return commandEnabled;
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public String getCommandDescription() {
        return super.getCommandDescription();
    }

    @Override
    public String getTooLargeMessage() {
        if (missingPermission == null) return ChatItemConfig.TOO_LARGE_MESSAGE.getCachedValue();

        return tooMuchNBT;
    }

    @Override
    public String getMissingPermissionMessage() {
        if (missingPermission == null) return ChatItemConfig.MISSING_PERMISSION_GENERIC.getCachedValue();
        return missingPermission;
    }
}
