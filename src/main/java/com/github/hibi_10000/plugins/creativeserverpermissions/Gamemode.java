package com.github.hibi_10000.plugins.creativeserverpermissions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
		return Character.toUpperCase(val.charAt(0)) + val.substring(1);
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (command.getName().equalsIgnoreCase("gamemode")) {
			if (!(sender instanceof Player)
					|| sender.hasPermission("minecraft.command.gamemode")) {
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
			Player senderP = (Player) sender;
			GameMode gamemode = map.get(args[0].toUpperCase());
			if (args.length == 2) {
				try {
					List<Entity> listE = plugin.getServer().selectEntities(sender, args[1]);
					if (listE.size() == 0) {
						// 当てはまるエンティティがいなかった
						sender.sendMessage(ChatColor.RED + "No entity was found");
						return false;
					}
					if (listE.size() > 1 || !listE.get(0).getUniqueId().equals(senderP.getUniqueId())) {
						// 自分以外を(も)指定した
						sender.sendMessage(ChatColor.RED + "You are not authorized to specify a player");
						return false;
					}
				} catch (IllegalArgumentException e) {
					// '/gamemode [gamemode] @[!(a|e|p|r|s)]'
					sender.sendMessage(ChatColor.RED + "Unknown selector type '" + args[1] + "'");
					return false;
				}
			}
			senderP.setGameMode(gamemode);
			sender.sendMessage("Set own game mode to " + upperCaseFirst(gamemode.name().toLowerCase()) + " Mode");
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + sender.getName() + ": Set own game mode to " + upperCaseFirst(gamemode.name().toLowerCase()) + " Mode]");
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
		if (!command.getName().equalsIgnoreCase("gamemode")) return null;
		List<String> list = new ArrayList<>();
		if (args.length == 1) {
			for (GameMode gm : GameMode.values()) {
				list.add(gm.name().toLowerCase());
			}
			return list;
		}
		if (args.length == 2) {
			if (!(sender instanceof  Player)
					|| sender.hasPermission("minecraft.command.gamemode")) {
				list.add("@a");
				list.add("@e");
				list.add("@p");
				list.add("@r");
				list.add("@s");
				for (Player p : plugin.getServer().getOnlinePlayers()) {
					list.add(p.getName());
				}
			} else {
				list.add("@s");
				list.add(sender.getName());
			}
			return list;
		}
		return null;
	}
}
