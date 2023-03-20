package com.github.hibi_10000.plugins.creativeserverpermissions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Teleport implements CommandExecutor, TabCompleter {

	CreativeServerPermissions plugin;
	Teleport(@NotNull CreativeServerPermissions instance) {this.plugin = instance;}

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
			if (!(x.startsWith("~") || x.startsWith("^")) && !x.contains(".") && Double.parseDouble(x) == Integer.parseInt(x)) {
				x = String.valueOf(Integer.parseInt(x) + 0.5);
			}
			if (!(z.startsWith("~") || z.startsWith("^")) && !z.contains(".") && Double.parseDouble(z) == Integer.parseInt(z)) {
				z = String.valueOf(Integer.parseInt(z) + 0.5);
			}
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
			}
			if (x.startsWith("^") && y.startsWith("^") && z.startsWith("^")) {
				double ix = 0;
				double iy = 0;
				double iz = 0;
				if (!x.equalsIgnoreCase("^")) {ix = Double.parseDouble(x.replaceFirst("\\^", ""));}
				if (!y.equalsIgnoreCase("^")) {iy = Double.parseDouble(y.replaceFirst("\\^", ""));}
				if (!z.equalsIgnoreCase("^")) {iz = Double.parseDouble(z.replaceFirst("\\^", ""));}
				Location loc = p.getLocation();
				Vector vec = loc.getDirection().normalize();
				loc = loc.clone().add(vec.clone().setX(vec.getZ()).setZ(vec.getX()).multiply(ix));
				loc = loc.clone().add(vec.clone().setY(vec.getZ()).setZ(vec.getY()).multiply(iy));
				loc = loc.clone().add(vec.clone().multiply(iz));
				return loc;
			}
			return new Location(p.getWorld(), Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z), yaw, pitch);
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + "Incorrect argument for command");
			return null;
		}
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
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
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
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
