package com.github.hibi_10000.plugin.creativeserverpermissions;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class CreativeServerPermissions extends JavaPlugin {

	@Override
	public void onEnable() {

	}

	@Override
	public void onDisable() {

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player p = (Player) sender;
		if (command.getName().equalsIgnoreCase("gamemode")) {
			if (args.length == 0 || args.length > 2) {
				sender.sendMessage("§cUnknown or incomplete command.");
				return false;
			}
			GameMode gamemode;
			try {
				gamemode = GameMode.valueOf(args[0]);
			} catch (IllegalArgumentException e) {
				sender.sendMessage("§cIncorrect argument for command");
				return false;
			}
			if (sender.hasPermission("creativeserverpermissions.gamemode.other") && args.length == 2) {
				if (args[1].equalsIgnoreCase("@e")) {
					sender.sendMessage("§cOnly players may be affected by this command, but the provided selector includes entities");
					return false;
				} else if (args[1].equalsIgnoreCase("@s")) {
					p.setGameMode(gamemode);
					return true;
				} else if (args[1].equalsIgnoreCase("@p") || args[1].equalsIgnoreCase("@a") || args[1].equalsIgnoreCase("@r")) {
					for (Entity e : Bukkit.selectEntities(sender, args[1])) {
						((Player) e).setGameMode(gamemode);
					}
					return true;
				}
				try {
					Bukkit.getPlayer(args[1]).setGameMode(gamemode);
				} catch (NullPointerException e) {
					sender.sendMessage("§cNo player was found");
					return false;
				}
				return true;
			}
			p.setGameMode(gamemode);
			return true;
		}
		if (command.getName().equalsIgnoreCase("tp")) {
			if (args.length == 0 || args.length > 6) {
				sender.sendMessage("§cIncorrect argument for command");
				return false;
			}
			if (sender.hasPermission("creativeserverpermissions.teleport.other") || sender instanceof BlockCommandSender) {
				StringBuilder sb = new StringBuilder("minecraft:tp");
				for (String str : args) {
					sb.append(" ").append(str);
				}
				p.performCommand(sb.toString());
				return true;
			}
			if (args[0].equalsIgnoreCase("@s") || args[0].equalsIgnoreCase("@p")) {
				if (args.length == 2) {
					try {
						p.teleport(Bukkit.getPlayer(args[1]));
						return true;
					} catch (NullPointerException e) {
						sender.sendMessage("§cNo player was found");
						return false;
					}
				}
				if (args.length == 4) {
					//String args1 = args[1];
					//String args2 = args[2];
					//String args3 = args[3];
					//if (args[1].contains("~")) {
					//	args1 = String.valueOf(p.getLocation().clone().add(Integer.parseInt(args1.replace("~","")), 0, 0);
					//}
					//Location targetloc = new Location(p.getWorld(), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]);
					StringBuilder sb = new StringBuilder("minecraft:tp");
					for (String str : args) {
						sb.append(" ").append(str);
					}
					p.performCommand(sb.toString());
					return true;
				}
				if (args.length == 6) {
					StringBuilder sb = new StringBuilder("minecraft:tp");
					for (String str : args) {
						sb.append(" ").append(str);
					}
					p.performCommand(sb.toString());
					return true;
				}
				sender.sendMessage("Incorrect argument for command");
				return false;
			}

		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

		return null;
	}
}
