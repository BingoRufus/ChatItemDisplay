package io.github.bingorufus.chatitemdisplay;

import io.github.bingorufus.chatitemdisplay.api.display.Displayable;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
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

    public void addDisplayable(Displayable display) {
        Display dis = new Display(display, UUID.randomUUID());
        displayId.put(dis.getId(), dis);
        mostRecent.put(display.getDisplayer().getRegularName().toUpperCase(), dis.getId());
    }

    public void addDisplay(Display d) {
        displayId.put(d.getId(), d);
        mostRecent.put(d.getPlayer().getRegularName().toUpperCase(), d.getId());
    }

    public Display getDisplayed(UUID id) {
        return displayId.get(id);
    }

    @Nullable
    public Display getMostRecent(@Nullable String player) {
        if (player == null) return null;
        if (!mostRecent.containsKey(player.toUpperCase())) {
            return getMostRecent(mostRecent.keySet().stream().filter(name -> name.toUpperCase().startsWith(player.toUpperCase())).sorted().findFirst().orElse(null));

        }
        UUID recent = mostRecent.get(player.toUpperCase());

        return displayId.get(recent);
    }

    @Nullable
    public Display getDisplay(Displayable dis) {

        return displayId.values().stream().filter(display -> display.getDisplayable().equals(dis)).findFirst().orElse(null);

    }

    public void forEach(Consumer<Display> displayConsumer) {
        for (Display d : displayId.values()) {
            displayConsumer.accept(d);
        }
    }


}
