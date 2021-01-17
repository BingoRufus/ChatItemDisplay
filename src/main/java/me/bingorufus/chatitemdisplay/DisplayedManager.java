package me.bingorufus.chatitemdisplay;

import me.bingorufus.chatitemdisplay.displayables.Displayable;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.Consumer;

public class DisplayedManager {
    private final HashMap<UUID, Display> displayId = new HashMap<>();

    /*
     * PlayerName -> Display
     * Id -> Display
     * PlayerName -> ID
     * Displayable -> Display
     */
    private final HashMap<String, UUID> mostRecent = new HashMap<>();// <Player,Id>

    public DisplayedManager() {

    }

    public void addDisplayable(String player, Displayable display) {
        Display dis = new Display(display, player.toUpperCase(), UUID.randomUUID());
        displayId.put(dis.getId(), dis);
        mostRecent.put(player.toUpperCase(), dis.getId());

    }

    public Display addDisplay(Display d) {
        displayId.put(d.getId(), d);
        mostRecent.put(d.getPlayer().toUpperCase(), d.getId());
        return d;
    }

    public Display getDisplayed(UUID id) {
        return displayId.get(id);
    }

    public Display getMostRecent(String player) {
        if (!mostRecent.containsKey(player.toUpperCase())) {
            try {
                return getMostRecent(mostRecent.keySet().stream().filter(name -> name.toUpperCase().startsWith(player.toUpperCase())).sorted().findFirst().get());
            } catch (NoSuchElementException e) {
                return null;
            }

        }
        UUID recent = mostRecent.get(player.toUpperCase());

        return displayId.get(recent);
    }

    public Display getDisplay(Displayable dis) {

        return displayId.values().stream().filter(display -> display.getDisplayable().equals(dis)).findFirst().get();

    }

    public void forEach(Consumer<Display> displayConsumer) {
        for (Display d : displayId.values()) {
            displayConsumer.accept(d);
        }
    }


}
