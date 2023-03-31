package com.github.hibi_10000.plugins.creativeserverpermissions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;

public class Gamemode implements CommandExecutor, TabCompleter {

	CreativeServerPermissions plugin;
	Map<String ,GameMode> map = new HashMap<>();
	Gamemode(@NotNull CreativeServerPermissions instance) {
		this.plugin = instance;
		map.put("0", GameMode.SURVIVAL);
		map.put("S", GameMode.SURVIVAL);
		map.put("SURVIVAL", GameMode.SURVIVAL);
		map.put("1", GameMode.CREATIVE);
		map.put("C", GameMode.CREATIVE);
		map.put("CREATIVE", GameMode.CREATIVE);
		map.put("2", GameMode.ADVENTURE);
		map.put("A", GameMode.ADVENTURE);
		map.put("ADVENTURE", GameMode.ADVENTURE);
		map.put("3", GameMode.SPECTATOR);
		map.put("SP", GameMode.SPECTATOR);
		map.put("SPECTATOR", GameMode.SPECTATOR);
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
		if (sender.getName().equals(player.getName())) {
			sender.sendMessage("Set own game mode to " + upperCaseFirst(gamemode.name().toLowerCase()) + " Mode");
			Bukkit.getLogger().log(Level.INFO, ChatColor.GRAY + "[" + sender.getName() + ": Set own game mode to " + upperCaseFirst(gamemode.name().toLowerCase()) + " Mode]");
			return;
		}
		sender.sendMessage("Set " + player.getName() + " game mode to " + upperCaseFirst(gamemode.name().toLowerCase()) + " Mode");
		player.sendMessage("Your game mode has been updated to " + upperCaseFirst(gamemode.name().toLowerCase()) + " Mode");
		Bukkit.getLogger().log(Level.INFO, ChatColor.GRAY + "[" + sender.getName() + ": Set " + player.getName() + " game mode to " + upperCaseFirst(gamemode.name().toLowerCase()) + " Mode]");
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
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
			if (!map.containsKey(args[0].toUpperCase())) {
				sender.sendMessage(ChatColor.RED + "Incorrect argument for command");
				return false;
			}
			GameMode gamemode = map.get(args[0].toUpperCase());
			if (sender.hasPermission("creativeserverpermissions.gamemode.other") && args.length == 2) {
				if (args[1].equalsIgnoreCase("@e")) {
					sender.sendMessage(ChatColor.RED + "Only players may be affected by this command, but the provided selector includes entities");
					return false;
				}
				List<Entity> listPlayer = Bukkit.selectEntities(sender, args[1]);
				if (listPlayer.size() == 0) {
					sender.sendMessage(ChatColor.RED + "No player was found");
					return false;
				}
				for (Entity ep : listPlayer) {
					Player p = (Player) ep;
					p.setGameMode(gamemode);
					sendChangeGamemodeMessage(sender, p, gamemode);
				}
			}
			if (args.length == 2) {
				sender.sendMessage(ChatColor.RED + "Unknown or incomplete command.");
				return false;
			}
			((Player) sender).setGameMode(gamemode);
			sendChangeGamemodeMessage(sender, null, gamemode);
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
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
