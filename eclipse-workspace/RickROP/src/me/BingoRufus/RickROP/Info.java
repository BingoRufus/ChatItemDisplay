package me.BingoRufus.RickROP;

import org.bukkit.plugin.Plugin;

public class Info {
	private Plugin plugin;

	public void setThisPlugin(Plugin p) {
		this.plugin = p;
	}

	public Plugin getThisPlugin() {
		return this.plugin;
	}
}