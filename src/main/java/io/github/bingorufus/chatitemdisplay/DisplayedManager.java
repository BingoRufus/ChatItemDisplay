package io.github.bingorufus.chatitemdisplay;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.bingorufus.chatitemdisplay.api.display.Displayable;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class DisplayedManager {
    private final Cache<UUID, Display> displayId = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES).build();

    /*
     * PlayerName -> Display
     * Id -> Display
     * PlayerName -> ID
     * Displayable -> Display
     */
    private final Cache<String, UUID> mostRecent = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES).build(); // <Player,Id>

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
        return displayId.asMap().get(id);
    }

    @Nullable
    public Display getMostRecent(@Nullable String player) {
        if (player == null) return null;
        if (!mostRecent.asMap().containsKey(player.toUpperCase())) {
            return getMostRecent(mostRecent.asMap().keySet().stream().filter(name -> name.toUpperCase().startsWith(player.toUpperCase())).sorted().findFirst().orElse(null));

        }
        UUID recent = mostRecent.asMap().get(player.toUpperCase());

        return displayId.asMap().get(recent);
    }

    @Nullable
    public Display getDisplay(Displayable dis) {

        return displayId.asMap().values().stream().filter(display -> display.getDisplayable().equals(dis)).findFirst().orElse(null);

    }

    public void forEach(Consumer<Display> displayConsumer) {
        for (Display d : displayId.asMap().values()) {
            displayConsumer.accept(d);
        }
    }


}
