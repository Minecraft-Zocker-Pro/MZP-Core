package minecraft.core.zocker.pro.util;

import minecraft.core.zocker.pro.compatibility.ServerVersion;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Actionbar {

	private static Class<?> CRAFTPLAYERCLASS, PACKET_PLAYER_CHAT_CLASS, ICHATCOMP, CHATMESSAGE, PACKET_CLASS, CHAT_MESSAGE_TYPE_CLASS;

	private static Field PLAYERCONNECTION;
	private static Method GETHANDLE, SENDPACKET;

	private static Constructor<?> PACKET_PLAYER_CHAT_CONSTRUCTOR, CHATMESSAGE_CONSTRUCTOR;

	private static Object CHAT_MESSAGE_TYPE_ENUM_OBJECT;

	private static String SERVER_VERSION;

	static {
		if (ServerVersion.isServerVersionAtOrBelow(ServerVersion.V1_8)) {
			String name = Bukkit.getServer().getClass().getName();
			name = name.substring(name.indexOf("craftbukkit.") + "craftbukkit.".length());
			name = name.substring(0, name.indexOf("."));
			SERVER_VERSION = name;

			try {
				CRAFTPLAYERCLASS = Class.forName("org.bukkit.craftbukkit." + SERVER_VERSION + ".entity.CraftPlayer");
				PACKET_PLAYER_CHAT_CLASS = Class.forName("net.minecraft.server." + SERVER_VERSION + ".PacketPlayOutChat");
				PACKET_CLASS = Class.forName("net.minecraft.server." + SERVER_VERSION + ".Packet");
				ICHATCOMP = Class.forName("net.minecraft.server." + SERVER_VERSION + ".IChatBaseComponent");
				GETHANDLE = CRAFTPLAYERCLASS.getMethod("getHandle");
				PLAYERCONNECTION = GETHANDLE.getReturnType().getField("playerConnection");
				SENDPACKET = PLAYERCONNECTION.getType().getMethod("sendPacket", PACKET_CLASS);
				try {
					PACKET_PLAYER_CHAT_CONSTRUCTOR = PACKET_PLAYER_CHAT_CLASS.getConstructor(ICHATCOMP, byte.class);
				} catch (NoSuchMethodException e) {
					CHAT_MESSAGE_TYPE_CLASS = Class.forName("net.minecraft.server." + SERVER_VERSION + ".ChatMessageType");
					CHAT_MESSAGE_TYPE_ENUM_OBJECT = CHAT_MESSAGE_TYPE_CLASS.getEnumConstants()[2];

					PACKET_PLAYER_CHAT_CONSTRUCTOR = PACKET_PLAYER_CHAT_CLASS.getConstructor(ICHATCOMP,
						CHAT_MESSAGE_TYPE_CLASS);
				}

				CHATMESSAGE = Class.forName("net.minecraft.server." + SERVER_VERSION + ".ChatMessage");

				CHATMESSAGE_CONSTRUCTOR = CHATMESSAGE.getConstructor(String.class, Object[].class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void sendToPlayer(Player player, String message) {
		if (player == null) return;
		if (message == null) return;

		if (ServerVersion.isServerVersionAtOrBelow(ServerVersion.V1_8)) {
			try {
				Object icb = CHATMESSAGE_CONSTRUCTOR.newInstance(message.replaceAll("&", "§"), new Object[0]);
				Object packet;
				try {
					packet = PACKET_PLAYER_CHAT_CONSTRUCTOR.newInstance(icb, (byte) 2);
				} catch (Exception e) {
					packet = PACKET_PLAYER_CHAT_CONSTRUCTOR.newInstance(icb, CHAT_MESSAGE_TYPE_ENUM_OBJECT);
				}
				Object craftplayerInst = CRAFTPLAYERCLASS.cast(player);
				Object methodhHandle = GETHANDLE.invoke(craftplayerInst);
				Object playerConnection = PLAYERCONNECTION.get(methodhHandle);
				SENDPACKET.invoke(playerConnection, packet);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
		}
	}

	public static void sendToPlayers(List<Player> players, String message) {
		if (players == null) return;
		if (message == null) return;

		if (ServerVersion.isServerVersionAtOrBelow(ServerVersion.V1_8)) {
			try {
				Object icb = CHATMESSAGE_CONSTRUCTOR.newInstance(message.replaceAll("&", "§"), new Object[0]);
				Object packet;
				try {
					packet = PACKET_PLAYER_CHAT_CONSTRUCTOR.newInstance(icb, (byte) 2);
				} catch (Exception e) {
					packet = PACKET_PLAYER_CHAT_CONSTRUCTOR.newInstance(icb, CHAT_MESSAGE_TYPE_ENUM_OBJECT);
				}

				for (Player player : players) {
					Object craftplayerInst = CRAFTPLAYERCLASS.cast(player);
					Object methodhHandle = GETHANDLE.invoke(craftplayerInst);
					Object playerConnection = PLAYERCONNECTION.get(methodhHandle);
					SENDPACKET.invoke(playerConnection, packet);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			for (Player player : players) {
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
			}
		}

	}

	public static void sendToAll(String message) {
		if (message == null) return;
		sendToPlayers(new ArrayList<>(Bukkit.getOnlinePlayers()), message);
	}

	public static void clear(Player player) {
		sendToPlayer(player, "");
	}

	public static void clear(List<Player> players) {
		sendToPlayers(players, "");
	}

	/**
	 * Clear all online players the actionbar
	 */
	public static void clearAll() {
		sendToAll("");
	}
}
