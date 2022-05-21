package com.github.hibi_10000.plugins.creativeserverpermissions;

import org.bukkit.*;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class CreativeServerPermissions extends JavaPlugin {

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

	private void teleport(CommandSender sender, Player target, Player toplayer, Location to) {
		if (to == null && toplayer != null) {
			target.teleport(toplayer);
			sender.sendMessage("Teleported " + target.getName() + " to " + toplayer.getName());
			Bukkit.getLogger().log(Level.INFO, ChatColor.GRAY + "[" + sender.getName() + ": Teleported " + target.getName() + " to " + toplayer.getName() + "]");
			if (target != sender) {
				target.sendMessage(ChatColor.GRAY + "[" + sender.getName() + ": Teleported " + target.getName() + " to " + toplayer.getName() + "]");
			}
		} else if (toplayer == null && to != null) {
			target.teleport(to);
			sender.sendMessage("Teleported " + target.getName() + " to " + to.getX() + ", " + to.getY() + ", " + to.getZ());
			Bukkit.getLogger().log(Level.INFO, ChatColor.GRAY + "[" + sender.getName() + ": Teleported " + target.getName() + " to " + to.getX() + ", " + to.getY() + ", " + to.getZ() + "]");
			if (target != sender) {
				target.sendMessage(ChatColor.GRAY + "[" + sender.getName() + ": Teleported " + target.getName() + " to " + to.getX() + ", " + to.getY() + ", " + to.getZ() + "]");
			}
		}
	}

	private Location getLocation(CommandSender sender, String x, String y, String z, Float pitch, Float yaw) {
		Player p = (Player) sender;
		try {
			if (x.startsWith("~")) {
				if (x.equalsIgnoreCase("~")) {
					x = String.valueOf(p.getLocation().getX());
				} else {
					x = String.valueOf((p.getLocation().clone().add(Double.parseDouble(x.replaceFirst("~", "")), 0, 0).getX()));
				}
			}
			if (y.startsWith("~")) {
				if (y.equalsIgnoreCase("~")) {
					y = String.valueOf(p.getLocation().getY());
				} else {
					y = String.valueOf(p.getLocation().clone().add(0, Double.parseDouble(y.replaceFirst("~", "")), 0).getY());
				}
			}
			if (z.startsWith("~")) {
				if (z.equalsIgnoreCase("~")) {
					z = String.valueOf(p.getLocation().getZ());
				} else {
					z = String.valueOf(p.getLocation().clone().add(0, 0, Double.parseDouble(z.replaceFirst("~", ""))).getZ());
				}
			}/*
			if (x.startsWith("^")) {
				if (y.startsWith("^")) {
					if (z.startsWith("^")) {
						if (x.equalsIgnoreCase("^")) {x = "0";}
						if (y.equalsIgnoreCase("^")) {y = "0";}
						if (z.equalsIgnoreCase("^")) {z = "0";}
						x = x.replaceFirst("^", "");
						y = y.replaceFirst("^", "");
						z = z.replaceFirst("^", "");
						double npitch = ((pitch + 90) * Math.PI) / 180;
						double nyaw  = ((yaw + 90)  * Math.PI) / 180;
						double nx = Math.sin(pitch) * Math.cos(yaw);
						double ny = Math.sin(pitch) * Math.sin(yaw);
						double nz = Math.cos(pitch);
						Vector v = new Vector(nx, nz, ny);
						v.multiply(new Vector(Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z)));
					}
					sender.sendMessage(ChatColor.RED + "Incorrect argument for command");
					return null;
				}
				sender.sendMessage(ChatColor.RED + "Incorrect argument for command");
				return null;
			}
			if (y.startsWith("^") || z.startsWith("^")) {
				sender.sendMessage(ChatColor.RED + "Incorrect argument for command");
				return null;
			}*/
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + "Incorrect argument for command");
			return null;
		}
		return new Location(p.getWorld(), Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z), yaw, pitch);
	}

	public static String upperCaseFirst(String val) {
		char[] arr = val.toCharArray();
		arr[0] = Character.toUpperCase(arr[0]);
		return new String(arr);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//Player p = (Player) sender;
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

		if (command.getName().equalsIgnoreCase("tp")) {
			if (sender instanceof ConsoleCommandSender || sender instanceof BlockCommandSender || sender.hasPermission("minecraft.command.teleport")) {
				StringBuilder sb = new StringBuilder("minecraft:tp");
				for (String str : args) {
					sb.append(" ").append(str);
				}
				Bukkit.dispatchCommand(sender, sb.toString());
				return true;
			}
			if (args.length == 0 || args.length > 6) {
				sender.sendMessage(ChatColor.RED + "Incorrect argument for command");
				return false;
			}
			if (sender.hasPermission("creativeserverpermissions.teleport.other")) {
				for (Player target : Bukkit.getOnlinePlayers()) {
					if (!target.getName().equalsIgnoreCase(sender.getName()) && target.getName().equalsIgnoreCase(args[0])) {
						if (args.length == 2) {
							try {
								teleport(sender, target, Bukkit.getPlayer(args[1]), null);
								return true;
							} catch (NullPointerException e) {
								sender.sendMessage(ChatColor.RED + "No player was found");
								return false;
							}
						}
						if (args.length == 4) {
							teleport(sender, target, null, getLocation(sender, args[1], args[2], args[3], target.getLocation().getPitch(), target.getLocation().getYaw()));
							return true;
						}
						if (args.length == 6) {
							teleport(sender, target, null, getLocation(sender, args[1], args[2], args[3], Float.parseFloat(args[4]), Float.parseFloat(args[5])));
							return true;
						}
						sender.sendMessage(ChatColor.RED + "Incorrect argument for command");
						return false;
					}
				}
			}
			if (args[0].equalsIgnoreCase("@s") || args[0].equalsIgnoreCase("@p")) {
				if (args.length == 2) {
					try {
						teleport(sender, (Player) sender, Bukkit.getPlayer(args[1]), null);
						return true;
					} catch (NullPointerException e) {
						sender.sendMessage(ChatColor.RED + "No player was found");
						return false;
					}
				}
				if (args.length == 4) {
					teleport(sender, (Player) sender, null, getLocation(sender, args[1], args[2], args[3], ((Player) sender).getLocation().getPitch(), ((Player) sender).getLocation().getYaw()));
					return true;
				}
				if (args.length == 6) {
					teleport(sender, (Player) sender, null, getLocation(sender, args[1], args[2], args[3], Float.parseFloat(args[4]), Float.parseFloat(args[5])));
					return true;
				}
				sender.sendMessage(ChatColor.RED + "Incorrect argument for command");
				return false;
			}
			if (args.length == 1) {
				try {
					teleport(sender, (Player) sender, Bukkit.getPlayer(args[0]), null);
					return true;
				} catch (NullPointerException e) {
					sender.sendMessage(ChatColor.RED + "No player was found");
					return false;
				}
			}
			if (args.length == 3) {
				teleport(sender, (Player) sender, null, getLocation(sender, args[0], args[1], args[2], ((Player) sender).getLocation().getPitch(), ((Player) sender).getLocation().getYaw()));
				return true;
			}
			if (args.length == 5) {
				teleport(sender, (Player) sender, null, getLocation(sender, args[0], args[1], args[2], Float.parseFloat(args[3]), Float.parseFloat(args[4])));
				return true;
			}
			sender.sendMessage(ChatColor.RED + "Incorrect argument for command");
			return false;
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

		if (command.getName().equalsIgnoreCase("tp")) {
			List<String> plist = new ArrayList<>();
			plist.add("@a");
			plist.add("@e");
			plist.add("@p");
			plist.add("@r");
			plist.add("@s");
			for (Player p : Bukkit.getOnlinePlayers()) {
				plist.add(p.getName());
			}
			if (args.length == 1) {
				List<String> list = new ArrayList<>();
				list.add("@a");
				list.add("@e");
				list.add("@p");
				list.add("@r");
				list.add("@s");
				list.add("~");
				list.add("~ ~");
				list.add("~ ~ ~");
				for (Player p : Bukkit.getOnlinePlayers()) {
					list.add(p.getName());
				}
				return list;
			}
			if (args.length == 2 && sender.hasPermission("creativeserverpermissions.teleport.other") && plist.contains(args[0])) {
				List<String> list = new ArrayList<>();
				list.add("@a");
				list.add("@e");
				list.add("@p");
				list.add("@r");
				list.add("@s");
				list.add("~");
				list.add("~ ~");
				list.add("~ ~ ~");
				for (Player p : Bukkit.getOnlinePlayers()) {
					list.add(p.getName());
				}
				return list;
			}
			if (args.length == 2 && args[0].contains("~")) {
				List<String> list = new ArrayList<>();
				list.add("~");
				list.add("~ ~");
				return list;
			}
			if (args.length == 3 && args[0].contains("~")) {
				List<String> list = new ArrayList<>();
				list.add("~");
				return list;
			}
			return null;
		}
		return null;
	}
}
