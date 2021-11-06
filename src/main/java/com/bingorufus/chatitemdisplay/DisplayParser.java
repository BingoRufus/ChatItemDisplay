package com.bingorufus.chatitemdisplay;

import com.bingorufus.chatitemdisplay.api.ChatItemDisplayAPI;
import com.bingorufus.chatitemdisplay.api.display.DisplayType;
import com.bingorufus.chatitemdisplay.api.display.Displayable;
import com.bingorufus.chatitemdisplay.api.event.DisplayPreProcessEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class DisplayParser {
    private final String message;
    private final List<DisplayType<?>> displayTypes = new ArrayList<>();
    private final HashMap<DisplayType<?>, Displayable> displayables = new HashMap<>();

    public DisplayParser(String message) {
        this.message = message;
        read();
    }

    private void read() {
        for (DisplayType<?> displayType : ChatItemDisplayAPI.getRegisteredDisplayables()) {
            for (String trigger : displayType.getTriggers()) {
                if (message.toUpperCase().contains(trigger.toUpperCase())) {
                    displayTypes.add(displayType);
                    break;
                }
            }
        }
    }

    public boolean containsDisplay() {
        return displayTypes.size() > 0;
    }

    public List<Displayable> getDisplayables() {
        return new ArrayList<>(displayables.values());
    }

    public List<DisplayType<?>> getDisplayedTypes() {
        return displayTypes;
    }

    public String format(Player p) {
        if (displayables.size() == 0) createDisplayables(p);
        String out = message;
        for (DisplayType<?> displayType : ChatItemDisplayAPI.getRegisteredDisplayables()) {
            out = replaceTrigger(out, p, displayType);
        }

        return out;
    }

    private String replaceTrigger(String message, Player p, DisplayType<?> displayType) {
        String out = message;
        boolean sentEvent = false;


        for (String trigger : displayType.getTriggers()) {
            if (!out.toUpperCase().contains(trigger.toUpperCase())) continue;
            if (!sentEvent) {
                DisplayPreProcessEvent displayEvent = new DisplayPreProcessEvent(p, displayables.get(displayType), true);
                Bukkit.getPluginManager().callEvent(displayEvent);
                if (displayEvent.isCancelled()) {
                    p.sendMessage(displayEvent.getCancellationMessage());
                    break;
                }
                sentEvent = true;
            }

            ChatItemDisplayAPI.getDisplayedManager().addDisplayable(displayables.get(displayType));

            String ins = ChatItemDisplayAPI.getDisplayedManager().getDisplay(displayables.get(displayType)).getInsertion();
            out = out.replaceAll("(?i)" + Pattern.quote(trigger), ins);


        }
        return out;

    }

    public void createDisplayables(Player p) {
        displayTypes.forEach(displayType -> displayables.put(displayType, displayType.initDisplayable(p)));

    }

    public Displayable getDisplayable(DisplayType<?> type) {
        return displayables.get(type);
    }

}
