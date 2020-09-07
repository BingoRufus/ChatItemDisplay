package me.bingorufus.chatitemdisplay;

import java.util.HashMap;
import java.util.NoSuchElementException;

import me.bingorufus.chatitemdisplay.displayables.Displayable;

public class DisplayedManager {
	Long nextId = 0L;

	/*
	 * PlayerName -> Display
	 * Id -> Display
	 * PlayerName -> ID
	 * Displayable -> Display
	 */

	private HashMap<Long, Display> displayId = new HashMap<Long, Display>();

	private HashMap<String, Long> mostRecent = new HashMap<String, Long>();// <Player,Id>


	public DisplayedManager() {
	}

	public Displayable addDisplayable(String player, Displayable display) {
		Display dis = new Display(display, player, nextId);
		displayId.put(nextId, dis);
		mostRecent.put(player.toUpperCase(), nextId);

		nextId++;

		return display;
	}

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
				return getMostRecent(mostRecent.keySet().stream().filter(name -> {
				return name.toUpperCase().startsWith(player.toUpperCase());
				}).sorted().findFirst().get());
			} catch (NoSuchElementException e) {
				return null;
			}

		}
		Long recent = mostRecent.get(player.toUpperCase());

		return displayId.get(recent);
	}

	public Display getDisplay(Displayable dis) {

		return displayId.values().stream().filter(display -> {

			if (display.getDisplayable().equals(dis))
				return true;

			return false;

		}).findFirst().get();


	}

	public Long getNextId() {
		return nextId;
	}



}
