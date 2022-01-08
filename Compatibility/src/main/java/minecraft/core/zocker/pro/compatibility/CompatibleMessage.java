package minecraft.core.zocker.pro.compatibility;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class CompatibleMessage {

	private static Class<?> CRAFTPLAYERCLASS;
	private static Field PLAYERCONNECTION;
	private static Method GETHANDLE, SENDPACKET;
	private static Constructor<?> PACKET_PLAYER_CHAT_CONSTRUCTOR, CHATMESSAGE_CONSTRUCTOR;
	private static Object CHAT_MESSAGE_TYPE_ENUM_OBJECT;

	static {
		if (ServerVersion.isServerVersionAtOrBelow(ServerVersion.V1_8)) {
			String name = Bukkit.getServer().getClass().getName();
			name = name.substring(name.indexOf("craftbukkit.") + "craftbukkit.".length());
			name = name.substring(0, name.indexOf("."));
			String SERVER_VERSION = name;

			try {
				CRAFTPLAYERCLASS = Class.forName("org.bukkit.craftbukkit." + SERVER_VERSION + ".entity.CraftPlayer");
				Class<?> PACKET_PLAYER_CHAT_CLASS = Class.forName("net.minecraft.server." + SERVER_VERSION + ".PacketPlayOutChat");
				Class<?> PACKET_CLASS = Class.forName("net.minecraft.server." + SERVER_VERSION + ".Packet");
				Class<?> ICHATCOMP = Class.forName("net.minecraft.server." + SERVER_VERSION + ".IChatBaseComponent");
				GETHANDLE = CRAFTPLAYERCLASS.getMethod("getHandle");
				PLAYERCONNECTION = GETHANDLE.getReturnType().getField("playerConnection");
				SENDPACKET = PLAYERCONNECTION.getType().getMethod("sendPacket", PACKET_CLASS);
				try {
					PACKET_PLAYER_CHAT_CONSTRUCTOR = PACKET_PLAYER_CHAT_CLASS.getConstructor(ICHATCOMP, byte.class);
				} catch (NoSuchMethodException e) {
					Class<?> CHAT_MESSAGE_TYPE_CLASS = Class.forName("net.minecraft.server." + SERVER_VERSION + ".ChatMessageType");
					CHAT_MESSAGE_TYPE_ENUM_OBJECT = CHAT_MESSAGE_TYPE_CLASS.getEnumConstants()[2];

					PACKET_PLAYER_CHAT_CONSTRUCTOR = PACKET_PLAYER_CHAT_CLASS.getConstructor(ICHATCOMP,
						CHAT_MESSAGE_TYPE_CLASS);
				}

				Class<?> CHATMESSAGE = Class.forName("net.minecraft.server." + SERVER_VERSION + ".ChatMessage");

				CHATMESSAGE_CONSTRUCTOR = CHATMESSAGE.getConstructor(String.class, Object[].class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void sendMessage(Player player, String message) {
		player.sendMessage(message);
	}

	public static void sendMessage(CommandSender sender, String message) {
		sender.sendMessage(message);
	}

	public static void sendMessage(Player player, BaseComponent[] baseComponents) {
		if (ServerVersion.isServerVersionAtOrBelow(ServerVersion.V1_8)) {
			player.spigot().sendMessage(baseComponents);
		} else {
			player.sendMessage(baseComponents);
		}
	}

	public static void sendActionBar(Player player, String message) {
		if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9)) {
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
			return;
		}

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
	}

	public static void sendTitle(Player player, String title, String subTitle) {
		if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_11)) {
			player.sendTitle(title, subTitle, 10, 25, 10);
			return;
		}

		player.sendTitle(title, subTitle);
	}

	public static void sendTitle(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
		if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_11)) {
			player.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
			return;
		}

		player.sendTitle(title, subTitle);
	}
}
