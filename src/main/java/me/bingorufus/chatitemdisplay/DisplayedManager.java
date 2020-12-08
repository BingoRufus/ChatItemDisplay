package me.bingorufus.chatitemdisplay;

import me.bingorufus.chatitemdisplay.displayables.Displayable;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class DisplayedManager {
    private final HashMap<Long, Display> displayId = new HashMap<>();

    /*
     * PlayerName -> Display
     * Id -> Display
     * PlayerName -> ID
     * Displayable -> Display
     */
    private final HashMap<String, Long> mostRecent = new HashMap<>();// <Player,Id>
    Long nextId = 0L;


    public DisplayedManager() {

    }

    public void addDisplayable(String player, Displayable display) {
        Display dis = new Display(display, player.toUpperCase(), nextId);
        displayId.put(nextId, dis);
        mostRecent.put(player.toUpperCase(), nextId);
        nextId++;

    }

    @SuppressWarnings("UnusedReturnValue")
    public Display addDisplay(Display d) {
        displayId.put(d.getId(), d);
        mostRecent.put(d.getPlayer().toUpperCase(), d.getId());
        nextId = d.getId() + 1;
        return d;
    }

    public Display getDisplayed(Long id) {
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
        Long recent = mostRecent.get(player.toUpperCase());

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

    public Long getNextId() {
        return nextId;
    }


}
