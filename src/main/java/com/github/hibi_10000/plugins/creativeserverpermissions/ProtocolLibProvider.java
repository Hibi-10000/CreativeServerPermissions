package com.github.hibi_10000.plugins.creativeserverpermissions;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

public class ProtocolLibProvider {
	static ProtocolManager libmgr;

	public void register(CreativeServerPermissions plugin) {

		libmgr = ProtocolLibrary.getProtocolManager();

		libmgr.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.ENTITY_STATUS) {

			@Override
			public void onPacketSending(PacketEvent event) {

				if (event.getPacketType() != PacketType.Play.Server.ENTITY_STATUS) {return;}

				if (event.isPlayerTemporary()) {return;}

				PacketContainer packet = event.getPacket();

				if (packet.getIntegers().read(0) != event.getPlayer().getEntityId()) {return;}

				if (packet.getBytes().read(0) > (byte)(1 + 24)) {
					packet.getBytes().write(0, packet.getBytes().read(0));

				} else if (event.getPlayer().hasPermission("creativeserverpermissions.gamemode.use-f3f4n")) {
					packet.getBytes().write(0, (byte)(2 + 24));

				} else if (event.getPlayer().isOp()) {
					packet.getBytes().write(0, (byte) (1 + 24));

				} else {
					packet.getBytes().write(0, (byte)(24));

				}
			}
		});

		plugin.getLogger().log(Level.INFO, "You have successfully connected to ProtocolLib!");
	}

	public void unregister(CreativeServerPermissions plugin) {
		libmgr.removePacketListeners(plugin);
	}

	public void update(Player player) {

		PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_STATUS);

		packet.getIntegers().write(0, player.getEntityId());
		if (player.hasPermission("minecraft.command.stop")) {
			packet.getBytes().write(0, (byte) (4 + 24));

		} else if (player.hasPermission("minecraft.command.op")) {
			packet.getBytes().write(0, (byte) (3 + 24));

		} else if (player.hasPermission("minecraft.command.gamemode")) {
			packet.getBytes().write(0, (byte) (2+24));

		} else if (player.hasPermission("creativeserverpermissions.gamemode.use-f3f4n")) {
			packet.getBytes().write(0, (byte) (2 + 24));

		} else if (player.isOp()) {
			packet.getBytes().write(0, (byte) (1 + 24));

		} else {
			packet.getBytes().write(0, (byte)(24));

		}

		try {
			libmgr.sendServerPacket(player, packet, false);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Could not send status packet!", e);
		}
	}
}
