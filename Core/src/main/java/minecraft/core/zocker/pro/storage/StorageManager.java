package minecraft.core.zocker.pro.storage;

import minecraft.core.zocker.pro.Main;
import minecraft.core.zocker.pro.config.Config;
import minecraft.core.zocker.pro.network.NetworkServerManager;
import minecraft.core.zocker.pro.storage.cache.memory.MemoryCacheManager;
import minecraft.core.zocker.pro.storage.cache.redis.RedisCacheManager;
import minecraft.core.zocker.pro.storage.database.MySQLDatabase;
import minecraft.core.zocker.pro.storage.database.SQLiteDatabase;
import minecraft.core.zocker.pro.storage.disk.JSONDisk;

import javax.annotation.Nullable;

public class StorageManager {

	// persist
	private static SQLiteDatabase sqLiteDatabase;
	private static MySQLDatabase mySQLDatabase;

	// disk
	private static JSONDisk jsonDisk;

	public static void initialize() {
		Config storageConfig = Main.CORE_STORAGE;
		if (storageConfig == null) return;

		// Database
		String playerTable = "CREATE TABLE IF NOT EXISTS player (uuid varchar(36) NOT NULL, name varchar(32) NOT NULL, server varchar(36), online tinyint(1) DEFAULT 0, PRIMARY KEY(uuid));";
		String serverTable =
			"CREATE TABLE IF NOT EXISTS server (server_uuid varchar(36) NOT NULL, host varchar(64) NOT NULL, port int(11) DEFAULT 25565, online int(11) DEFAULT 0, slot int(11) DEFAULT 0, " +
				"motd varchar(64), last_update DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, enabled tinyint(1) DEFAULT 1, PRIMARY KEY(server_uuid));";

		if (storageConfig.getBool("storage.database.mysql.enabled")) {
			mySQLDatabase = new MySQLDatabase();
			mySQLDatabase.connect(
				storageConfig.getString("storage.database.mysql.host"),
				storageConfig.getInt("storage.database.mysql.port"),
				storageConfig.getString("storage.database.mysql.database"),
				storageConfig.getString("storage.database.mysql.username"),
				storageConfig.getString("storage.database.mysql.password"));

			mySQLDatabase.createTable(playerTable);
			mySQLDatabase.createTable(serverTable);
		} else {
			sqLiteDatabase = new SQLiteDatabase();
			sqLiteDatabase.connect(null, 0, null, null, null);
			sqLiteDatabase.createTable(playerTable);
			sqLiteDatabase.createTable(serverTable);
		}

		// Cache
		if (storageConfig.getBool("storage.cache.redis.enabled")) {
			RedisCacheManager.createConnection();
			NetworkServerManager.start();
		}

		if (storageConfig.getBool("storage.cache.memory.enabled")) {
			MemoryCacheManager.start();
		}
	}

	@Nullable
	public static SQLiteDatabase getSQLiteDatabase() {
		return sqLiteDatabase;
	}

	@Nullable
	public static MySQLDatabase getMySQLDatabase() {
		return mySQLDatabase;
	}

	public static boolean isSQLite() {
		return sqLiteDatabase != null;
	}

	public static boolean isMySQL() {
		if (sqLiteDatabase == null) {
			if (mySQLDatabase != null) return true;
		}

		return false;
	}

	public static boolean isRedis() {
		return RedisCacheManager.isRunning();
	}

	public static boolean isMemory() {
		return MemoryCacheManager.isRunning();
	}

	public static String getServerName() {
		return Main.CORE_CONFIG.getString("core.server.name");
	}
}
