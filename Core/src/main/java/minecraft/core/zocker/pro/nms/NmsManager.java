package minecraft.core.zocker.pro.nms;

import minecraft.core.zocker.pro.nms.api.anvil.AnvilCore;
import minecraft.core.zocker.pro.nms.api.nbt.NBTCore;
import minecraft.core.zocker.pro.nms.api.world.WorldCore;
import minecraft.core.zocker.pro.nms.v1_14_R1.nbt.NBTCoreImpl;
import minecraft.core.zocker.pro.nms.v1_14_R1.world.WorldCoreImpl;
import org.bukkit.Bukkit;

import java.util.logging.Level;
import java.util.logging.Logger;

/***
 * Original source from https://github.com/songoda/SongodaCore
 */
public class NmsManager {

	private final static String serverPackagePath = Bukkit.getServer().getClass().getPackage().getName();
	private final static String serverPackageVersion = serverPackagePath.substring(serverPackagePath.lastIndexOf('.') + 1);
	private final static AnvilCore anvil;
	private final static NBTCore nbt;
	private final static WorldCore world;

	static {
		switch (serverPackageVersion) {
			case "v1_8_R1":
				anvil = new minecraft.core.zocker.pro.nms.v1_8_R1.anvil.AnvilCore();
				nbt = new minecraft.core.zocker.pro.nms.v1_8_R1.nbt.NBTCoreImpl();
				world = new minecraft.core.zocker.pro.nms.v1_8_R1.world.WorldCoreImpl();
				break;
			case "v1_8_R2":
				anvil = new minecraft.core.zocker.pro.nms.v1_8_R2.anvil.AnvilCore();
				nbt = new minecraft.core.zocker.pro.nms.v1_8_R2.nbt.NBTCoreImpl();
				world = new minecraft.core.zocker.pro.nms.v1_8_R2.world.WorldCoreImpl();
				break;
			case "v1_8_R3":
				anvil = new minecraft.core.zocker.pro.nms.v1_8_R3.anvil.AnvilCore();
				nbt = new minecraft.core.zocker.pro.nms.v1_8_R3.nbt.NBTCoreImpl();
				world = new minecraft.core.zocker.pro.nms.v1_8_R3.world.WorldCoreImpl();
				break;
			case "v1_9_R1":
				anvil = new minecraft.core.zocker.pro.nms.v1_9_R1.anvil.AnvilCore();
				nbt = new minecraft.core.zocker.pro.nms.v1_9_R1.nbt.NBTCoreImpl();
				world = new minecraft.core.zocker.pro.nms.v1_9_R1.world.WorldCoreImpl();
				break;
			case "v1_9_R2":
				anvil = new minecraft.core.zocker.pro.nms.v1_9_R2.anvil.AnvilCore();
				nbt = new minecraft.core.zocker.pro.nms.v1_9_R2.nbt.NBTCoreImpl();
				world = new minecraft.core.zocker.pro.nms.v1_9_R2.world.WorldCoreImpl();
				break;
			case "v1_10_R1":
				anvil = new minecraft.core.zocker.pro.nms.v1_10_R1.anvil.AnvilCore();
				nbt = new minecraft.core.zocker.pro.nms.v1_10_R1.nbt.NBTCoreImpl();
				world = new minecraft.core.zocker.pro.nms.v1_10_R1.world.WorldCoreImpl();
				break;
			case "v1_11_R1":
				anvil = new minecraft.core.zocker.pro.nms.v1_11_R1.anvil.AnvilCore();
				nbt = new minecraft.core.zocker.pro.nms.v1_11_R1.nbt.NBTCoreImpl();
				world = new minecraft.core.zocker.pro.nms.v1_11_R1.world.WorldCoreImpl();
				break;
			case "v1_12_R1":
				anvil = new minecraft.core.zocker.pro.nms.v1_12_R1.anvil.AnvilCore();
				nbt = new minecraft.core.zocker.pro.nms.v1_12_R1.nbt.NBTCoreImpl();
				world = new minecraft.core.zocker.pro.nms.v1_12_R1.world.WorldCoreImpl();
				break;
			case "v1_13_R1":
				anvil = new minecraft.core.zocker.pro.nms.v1_13_R1.anvil.AnvilCore();
				nbt = new minecraft.core.zocker.pro.nms.v1_13_R1.nbt.NBTCoreImpl();
				world = new minecraft.core.zocker.pro.nms.v1_13_R1.world.WorldCoreImpl();
				break;
			case "v1_13_R2":
				anvil = new minecraft.core.zocker.pro.nms.v1_13_R2.anvil.AnvilCore();
				nbt = new minecraft.core.zocker.pro.nms.v1_13_R2.nbt.NBTCoreImpl();
				world = new minecraft.core.zocker.pro.nms.v1_13_R2.world.WorldCoreImpl();
				break;
			case "v1_14_R1":
				anvil = new minecraft.core.zocker.pro.nms.v1_14_R1.anvil.AnvilCore();
				nbt = new minecraft.core.zocker.pro.nms.v1_14_R1.nbt.NBTCoreImpl();
				world = new minecraft.core.zocker.pro.nms.v1_14_R1.world.WorldCoreImpl();
				break;
			case "v1_15_R1":
				anvil = new minecraft.core.zocker.pro.nms.v1_15_R1.anvil.AnvilCore();
				nbt = new minecraft.core.zocker.pro.nms.v1_15_R1.nbt.NBTCoreImpl();
				world = new minecraft.core.zocker.pro.nms.v1_15_R1.world.WorldCoreImpl();
				break;
			case "v1_16_R1":
				anvil = new minecraft.core.zocker.pro.nms.v1_16_R1.anvil.AnvilCore();
				nbt = new minecraft.core.zocker.pro.nms.v1_16_R1.nbt.NBTCoreImpl();
				world = new minecraft.core.zocker.pro.nms.v1_16_R1.world.WorldCoreImpl();
				break;
			case "v1_16_R2":
				anvil = new minecraft.core.zocker.pro.nms.v1_16_R2.anvil.AnvilCore();
				nbt = new minecraft.core.zocker.pro.nms.v1_16_R2.nbt.NBTCoreImpl();
				world = new minecraft.core.zocker.pro.nms.v1_16_R2.world.WorldCoreImpl();
				break;
			case "v1_16_R3":
				anvil = new minecraft.core.zocker.pro.nms.v1_16_R3.anvil.AnvilCore();
				nbt = new minecraft.core.zocker.pro.nms.v1_16_R3.nbt.NBTCoreImpl();
				world = new minecraft.core.zocker.pro.nms.v1_16_R3.world.WorldCoreImpl();
				break;
			case "v1_17_R1":
				anvil = new minecraft.core.zocker.pro.nms.v1_17_R1.anvil.AnvilCore();
				nbt = new minecraft.core.zocker.pro.nms.v1_17_R1.nbt.NBTCoreImpl();
				world = new minecraft.core.zocker.pro.nms.v1_17_R1.world.WorldCoreImpl();
				break;
			case "v1_18_R1":
				anvil = new minecraft.core.zocker.pro.nms.v1_18_R1.anvil.AnvilCore();
				nbt = new minecraft.core.zocker.pro.nms.v1_18_R1.nbt.NBTCoreImpl();
				world = new minecraft.core.zocker.pro.nms.v1_18_R1.world.WorldCoreImpl();
				break;
			default:
				Logger.getLogger(NmsManager.class.getName()).log(Level.SEVERE, "Failed to load NMS for this server version: version {0} not found", serverPackageVersion);
				anvil = null;
				nbt = null;
				world = null;
				break;
		}
	}

	public static AnvilCore getAnvil() {
		return anvil;
	}

	public static boolean hasAnvil() {
		return anvil != null;
	}

	public static NBTCore getNbt() {
		return nbt;
	}

	public static boolean hasNbt() {
		return nbt != null;
	}

	public static WorldCore getWorld() {
		return world;
	}

	public static boolean hasWorld() {
		return world != null;
	}
}