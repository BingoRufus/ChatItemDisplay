package me.BingoRufus.RickROP;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin implements Listener {
	private RickRoll play;
	private Info info;
	public List<String> BlockedMessages = new ArrayList<String>();
	String[][] Replacements;

	@Override
	public void onEnable() {
		this.saveDefaultConfig();

		BlockedMessages = getConfig().getStringList("blocked-messages");
		this.getServer().getPluginManager().registerEvents(this, this);

		info = new Info();
		play = new RickRoll(info);
		info.setThisPlugin(this);
		List<String> syns = getConfig().getStringList("synonyms");
		getLogger().info(syns.toString());

		Replacements = new String[syns.size()][2];
		for (int length = 0; length < syns.size(); length++) {
			getLogger().info(String.valueOf(length));

			getLogger().info(syns.get(length));

			Replacements[length] = syns.get(length).split(":");

		}
	}

	@Override
	public void onDisable() {

	}

	@EventHandler()
	public void AskForOp(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		String Message = e.getMessage().toLowerCase();

		if (p.hasPermission("rickrop.exclude"))
			return;

		try {
			for (String[] ReplaceSet : Replacements) {
				if (Message.contains(ReplaceSet[0].toLowerCase())) {
					Message = Message.replaceAll(ReplaceSet[0].toLowerCase(), ReplaceSet[1].toLowerCase());
					continue;
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		Message = Message.replaceAll("[^a-zA-Z0-9]", "");
		for (String Blocked : BlockedMessages) {
			Message = Message.replaceAll(" ", "");
			Blocked = Blocked.toLowerCase().replaceAll(" ", "");
			if (Message.contains(Blocked) || Message.toLowerCase().equalsIgnoreCase(Blocked)) {
				p.sendMessage("It does");
				p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "[Server: Made " + p.getName()
						+ " a server operator]");
				e.setCancelled(true);
				Bukkit.getScheduler().scheduleSyncDelayedTask(info.getThisPlugin(), new Runnable() {
					public void run() {
						play.Play(p);
						return;
					}
				}, 50L);
				return;
			}
		}

	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("rickroll")) {
			if (!(sender.hasPermission("rickrop.command"))) {
				sender.sendMessage(ChatColor.RED + "You do not have permission to do that!");
				return true;
			}
			if (args.length == 0) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(
							ChatColor.RED + "You can not do this to a non player, please do /rickroll <player>");
					return true;
				}
				Player p = (Player) sender;
				if (p.hasPermission("rickrop.command")) {
					play.Play(p);
					return true;
				}

			}
			if (args.length == 1) {
				if (sender.hasPermission("rickrop.command")) {
					if (Bukkit.getPlayer(args[0]) != null) {
						Player p = Bukkit.getPlayer(args[0]);
						play.Play(p);
						return true;
					}
					sender.sendMessage(ChatColor.RED + "That player is not online.");
					return true;
				}
				return false;
			}
		}
		return false;
	}

}
