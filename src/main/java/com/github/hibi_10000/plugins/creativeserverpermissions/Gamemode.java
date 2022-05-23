package com.github.hibi_10000.plugins.creativeserverpermissions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class Gamemode implements CommandExecutor, TabCompleter {

	CreativeServerPermissions plugin;
	Gamemode(CreativeServerPermissions instance) {
		plugin = instance;
	}

	public static String upperCaseFirst(String val) {
		char[] arr = val.toCharArray();
		arr[0] = Character.toUpperCase(arr[0]);
		return new String(arr);
	}

	private void sendChangeGamemodeMessage(CommandSender sender, Player player, GameMode gamemode) {
		if (player == null) {
			sender.sendMessage("Set own game mode to " + upperCaseFirst(gamemode.name().toLowerCase()) + " Mode");
			Bukkit.getLogger().log(Level.INFO, ChatColor.GRAY + "[" + sender.getName() + ": Set own game mode to " + upperCaseFirst(gamemode.name().toLowerCase()) + " Mode]");
			return;
		}
		sender.sendMessage("Set " + player.getName() + " game mode to " + upperCaseFirst(gamemode.name().toLowerCase()) + " Mode");
		player.sendMessage("Your game mode has been updated to " + upperCaseFirst(gamemode.name().toLowerCase()) + " Mode");
		Bukkit.getLogger().log(Level.INFO, ChatColor.GRAY + "[" + sender.getName() + ": Set " + player.getName() + " game mode to " + upperCaseFirst(gamemode.name().toLowerCase()) + " Mode]");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("gamemode")) {
			if (sender instanceof ConsoleCommandSender || sender instanceof BlockCommandSender || sender.hasPermission("minecraft.command.gamemode")) {
				StringBuilder sb = new StringBuilder("minecraft:gamemode");
				for (String str : args) {
					sb.append(" ").append(str);
				}
				Bukkit.dispatchCommand(sender, sb.toString());
				return true;
			}
			if (args.length == 0 || args.length > 2) {
				sender.sendMessage(ChatColor.RED + "Unknown or incomplete command.");
				return false;
			}
			GameMode gamemode;
			try {
				gamemode = GameMode.valueOf(args[0].toUpperCase());
			} catch (IllegalArgumentException e) {
				Map<String ,GameMode> map = new HashMap<>();
				map.put("0", GameMode.SURVIVAL);
				map.put("s", GameMode.SURVIVAL);
				map.put("1", GameMode.CREATIVE);
				map.put("c", GameMode.CREATIVE);
				map.put("2", GameMode.ADVENTURE);
				map.put("a", GameMode.ADVENTURE);
				map.put("3", GameMode.SPECTATOR);
				map.put("sp", GameMode.SPECTATOR);
				if (map.containsKey(args[0])) {
					gamemode = map.get(args[0]);
				} else {
					sender.sendMessage(ChatColor.RED + "Incorrect argument for command");
					return false;
				}
			}
			if (sender.hasPermission("creativeserverpermissions.gamemode.other") && args.length == 2) {
				if (args[1].equalsIgnoreCase("@e")) {
					sender.sendMessage(ChatColor.RED + "Only players may be affected by this command, but the provided selector includes entities");
					return false;
				} else if (args[1].equalsIgnoreCase("@s")) {
					((Player) sender).setGameMode(gamemode);
					sendChangeGamemodeMessage(sender, null, gamemode);
					return true;
				} else if (args[1].equalsIgnoreCase("@a") || args[1].equalsIgnoreCase("@r")) {
					for (Entity e : Bukkit.selectEntities(sender, args[1])) {
						((Player) e).setGameMode(gamemode);
						sendChangeGamemodeMessage(sender, (Player) e, gamemode);
					}
					return true;
				}
				Player target = Bukkit.getPlayer(args[1]);
				if (target != null) {
					target.setGameMode(gamemode);
					sendChangeGamemodeMessage(sender, target, gamemode);
					return true;
				}
				sender.sendMessage(ChatColor.RED + "No player was found");
				return false;
			}
			((Player) sender).setGameMode(gamemode);
			sendChangeGamemodeMessage(sender, null, gamemode);
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (command.getName().equalsIgnoreCase("gamemode")) {
			if (args.length == 1) {
				List<String> list = new ArrayList<>();
				for (GameMode gm : GameMode.values()) {
					list.add(gm.name().toLowerCase());
				}
				return list;
			} else if (args.length == 2 && sender.hasPermission("creativeserverpermissions.gamemode.other")) {
				List<String> list = new ArrayList<>();
				list.add("@a");
				list.add("@e");
				list.add("@p");
				list.add("@r");
				list.add("@s");
				for (Player p : Bukkit.getOnlinePlayers()) {
					list.add(p.getName());
				}
				return list;
			}
			return null;
		}
		return null;
	}
}
