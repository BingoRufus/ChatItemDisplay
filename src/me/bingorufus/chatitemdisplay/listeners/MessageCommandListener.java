package me.bingorufus.chatitemdisplay.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import me.bingorufus.chatitemdisplay.ChatItemDisplay;
import me.bingorufus.chatitemdisplay.DisplayParser;

public class MessageCommandListener implements Listener {
	List<String> msgCmds = new ArrayList<String>();
	ChatItemDisplay m;

	public MessageCommandListener(ChatItemDisplay m) {
		this.m = m;
		msgCmds = m.getConfig().getStringList("message-command");
		msgCmds.replaceAll(cmd -> { // Makes sure the command ends with a space
			return cmd.trim() + " ";
		});

	}
	@EventHandler
	public void onCmd(PlayerCommandPreprocessEvent e) {
		if (m.useOldFormat || e.getMessage() == null || !e.getMessage().startsWith("/")
				|| !msgCmds.stream().anyMatch(e.getMessage()::startsWith))
			return;
		DisplayParser dp = new DisplayParser(m, e.getMessage(), e.getPlayer(), true);
		String out = dp.parse();
		if (!dp.containsDisplay() || dp.cancelMessage())
			return;
		e.setMessage(out);
	}

}
