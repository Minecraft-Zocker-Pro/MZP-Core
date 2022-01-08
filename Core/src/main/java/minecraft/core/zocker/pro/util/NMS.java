package minecraft.core.zocker.pro.util;

import minecraft.core.zocker.pro.compatibility.ServerVersion;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/***
 * Original source from https://github.com/songoda/SongodaCore
 */
public class NMS {

	public static Class<?> getNMSClass(String className) {
		try {
			String fullName = "net.minecraft.server." + ServerVersion.getServerVersionString() + "." + className;
			Class<?> clazz = Class.forName(fullName);
			return clazz;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Class<?> getCraftClass(String className) {
		try {
			String fullName = "org.bukkit.craftbukkit." + ServerVersion.getServerVersionString() + "." + className;
			Class<?> clazz = Class.forName(fullName);
			return clazz;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Method getPrivateMethod(Class<?> c, String methodName, Class<?>... parameters) throws Exception {
		Method m = c.getDeclaredMethod(methodName, parameters);
		m.setAccessible(true);
		return m;
	}

	public static Field getField(Class<?> clazz, String name, boolean declared) {
		try {
			Field field;

			if (declared) {
				field = clazz.getDeclaredField(name);
			} else {
				field = clazz.getField(name);
			}

			field.setAccessible(true);
			return field;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object getFieldObject(Object object, Field field) {
		try {
			return field.get(object);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void setField(Object object, String fieldName, Object fieldValue, boolean declared) {
		try {
			Field field;

			if (declared) {
				field = object.getClass().getDeclaredField(fieldName);
			} else {
				field = object.getClass().getField(fieldName);
			}

			field.setAccessible(true);
			field.set(object, fieldValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendPacket(Player player, Object packet) {
		try {
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
