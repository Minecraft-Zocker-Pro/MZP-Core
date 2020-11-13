package minecraft.core.zocker.pro.storage;

import minecraft.core.zocker.pro.Main;
import minecraft.core.zocker.pro.config.Config;
import minecraft.core.zocker.pro.storage.cache.MemoryCache;
import minecraft.core.zocker.pro.storage.database.MySQLDatabase;
import minecraft.core.zocker.pro.storage.cache.RedisCache;
import minecraft.core.zocker.pro.storage.database.SQLiteDatabase;
import minecraft.core.zocker.pro.storage.disk.JSONDisk;

import javax.annotation.Nullable;

public class StorageManager {

	private static final String playerTable = "CREATE TABLE IF NOT EXISTS player (uuid varchar(36) NOT NULL, name varchar(32) NOT NULL, PRIMARY KEY(uuid));";

	// persist
	private static SQLiteDatabase sqLiteDatabase;
	private static MySQLDatabase mySQLDatabase;

	// disk
	private static JSONDisk jsonDisk;

	// cache
	private static MemoryCache memoryCache;
	private static RedisCache redisDatabase;

	public static void initialize() {
		Config storageConfig = Main.CORE_STORAGE;
		if (storageConfig == null) return;

		// Database
		if (storageConfig.getBool("storage.database.mysql.enabled")) {
			mySQLDatabase = new MySQLDatabase();
			mySQLDatabase.connect(
				storageConfig.getString("storage.database.mysql.host"),
				storageConfig.getInt("storage.database.mysql.port"),
				storageConfig.getString("storage.database.mysql.database"),
				storageConfig.getString("storage.database.mysql.username"),
				storageConfig.getString("storage.database.mysql.password"));

			mySQLDatabase.createTable(playerTable);
		} else {
			sqLiteDatabase = new SQLiteDatabase();
			sqLiteDatabase.connect(null, 0, null, null, null);
			sqLiteDatabase.createTable(playerTable);
		}

		// Cache
		if (storageConfig.getBool("storage.cache.redis.enabled")) {

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

	@Nullable
	public static RedisCache getRedisDatabase() {
		return redisDatabase;
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
		return redisDatabase != null;
	}

	public static String getServerName() {
		return Main.CORE_CONFIG.getString("core.server.name");
	}
}
