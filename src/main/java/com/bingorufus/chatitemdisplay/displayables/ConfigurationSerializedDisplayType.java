package com.bingorufus.chatitemdisplay.displayables;

import com.bingorufus.chatitemdisplay.api.display.Displayable;
import com.bingorufus.chatitemdisplay.util.ChatItemConfig;
import org.bukkit.configuration.ConfigurationSection;

public abstract class ConfigurationSerializedDisplayType<T extends Displayable> extends SerializedDisplayType<T> {
    private ChatItemConfig.ConfigOption<String> title;
    private ChatItemConfig.ConfigOption<Boolean> commandEnabled;
    private ChatItemConfig.ConfigOption<String> tooMuchNBT;
    private ChatItemConfig.ConfigOption<String> missingPermission;

    public void loadData(ConfigurationSection configData) {
        super.loadData(configData);
        missingPermission = new ChatItemConfig.ConfigOption<>(dataPath() + "." + "missing-permission", String.class);
        tooMuchNBT = new ChatItemConfig.ConfigOption<>(dataPath() + "." + "too-much-nbt", String.class);
        commandEnabled = new ChatItemConfig.ConfigOption<>(dataPath() + "." + "command-enabled", Boolean.class);
        title = new ChatItemConfig.ConfigOption<>(dataPath() + "." + "inventory-title", String.class);
    }


    @Override
    public String getInventoryTitle() {
        if (title.getCachedValue() == null) return "title";
        return title.getCachedValue();
    }

    @Override
    public boolean isCommandEnabled() {
        return commandEnabled.getCachedValue();
    }

    @Override
    public String getTooLargeMessage() {
        if (missingPermission == null) return ChatItemConfig.TOO_LARGE_MESSAGE.getCachedValue();

        return tooMuchNBT.getCachedValue();
    }

    @Override
    public String getMissingPermissionMessage() {
        if (missingPermission == null) return ChatItemConfig.MISSING_PERMISSION_GENERIC.getCachedValue();
        return missingPermission.getCachedValue();
    }
}
