package com.github.hibi_10000.plugins.creativeserverpermissions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.logging.Level;

public class CreativeServerPermissions extends JavaPlugin implements Listener {
	ProtocolLibProvider provider;
	BukkitTask task;

	@Override
	public void onEnable() {
		getCommand("Gamemode").setExecutor(new Gamemode(this));
		getCommand("Gamemode").setTabCompleter(new Gamemode(this));
		getCommand("Teleport").setExecutor(new Teleport(this));
		getCommand("Teleport").setTabCompleter(new Teleport(this));
		getServer().getPluginManager().registerEvents(this, this);
		if (getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
			provider = new ProtocolLibProvider();
			provider.register(this);
			task = getServer().getScheduler().runTaskTimer(this, () -> {
				for (Player p : Bukkit.getOnlinePlayers()) {
					provider.update(p);
				}
			}, 0L,20L);
		} else {
			getLogger().log(Level.SEVERE, "Players cannot use F3 + F4 and F3 + N because ProtocolLib cannot be found!");
		}
		super.onEnable();
	}

	@Override
	public void onDisable() {
		if (getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
			provider.unregister(this);
			task.cancel();
		}
		super.onDisable();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		this.getServer().getScheduler().runTaskLater(this, () -> {
			if (player.isOnline()) {
				provider.update(player);
			}
		}, 10L);
	}
}
