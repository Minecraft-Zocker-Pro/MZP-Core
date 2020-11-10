package minecraft.core.zocker.pro;

import minecraft.core.zocker.pro.command.CoreCommand;
import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
import minecraft.core.zocker.pro.config.Config;
import minecraft.core.zocker.pro.inventory.InventoryActive;
import minecraft.core.zocker.pro.listener.PlayerJoinListener;
import minecraft.core.zocker.pro.listener.PlayerQuitListener;
import minecraft.core.zocker.pro.storage.StorageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class Main extends CorePlugin {

	public static Config CORE_CONFIG;
	public static Config CORE_MESSAGE;
	public static Config CORE_STORAGE;

	private static CorePlugin PLUGIN;

	@Override
	public void onEnable() {
		super.onEnable();
		PLUGIN = this;

		this.setDisplayItem(CompatibleMaterial.OAK_SIGN.getMaterial());
		this.setHelpCommand("core");
		this.setPluginName("MZP-Core");

		this.buildConfig();

		StorageManager.initialize();

		this.registerCommand();
		this.registerListener();

		// Reinitialize
		for (Player player : Bukkit.getOnlinePlayers()) {
			new Zocker(player.getUniqueId());
		}
	}

	@Override
	public void onDisable() {
		if (StorageManager.isMySQL()) {
			StorageManager.getMySQLDatabase().disconnect();
		}

		if (StorageManager.isSQLite()) {
			StorageManager.getSQLiteDatabase().disconnect();
		}

		if (StorageManager.isRedis()) {
//			StorageManager.getRedisDatabase()
		}
	}


	@Override
	public void registerCommand() {
	}

	@Override
	public void registerListener() {
		PluginManager pluginManager = Bukkit.getPluginManager();
		pluginManager.registerEvents(new PlayerJoinListener(), this);
		pluginManager.registerEvents(new PlayerQuitListener(), this);
		pluginManager.registerEvents(new InventoryActive.GUIActiveListener(), this);
	}

	@Override
	public void buildConfig() {
		// Config		
		CORE_CONFIG = new Config("core.yml", this.getPluginName());

		// Storage
		CORE_STORAGE = new Config("storage.yml", this.getPluginName());

		// MySQL
		CORE_STORAGE.set("storage.database.mysql.enabled", false, "0.0.1");
		CORE_STORAGE.set("storage.database.mysql.host", "localhost", "0.0.1");
		CORE_STORAGE.set("storage.database.mysql.port", 3306, "0.0.1");
		CORE_STORAGE.set("storage.database.mysql.database", "mzp_core", "0.0.1");
		CORE_STORAGE.set("storage.database.mysql.username", "mzp_core", "0.0.1");
		CORE_STORAGE.set("storage.database.mysql.password", "!default", "0.0.1");

		// SQLite
		CORE_STORAGE.set("storage.database.sql.enabled", true, "0.0.1");

		// Redis
		CORE_STORAGE.set("storage.cache.redis.enabled", false, "0.0.1");
		CORE_STORAGE.set("storage.cache.redis.host", "localhost", "0.0.1");
		CORE_STORAGE.set("storage.cache.redis.port", 3306, "0.0.1");
		CORE_STORAGE.set("storage.cache.redis.database", "mzp_core", "0.0.1");
		CORE_STORAGE.set("storage.cache.redis.username", "mzp_core", "0.0.1");
		CORE_STORAGE.set("storage.cache.redis.password", "!default", "0.0.1");

		CORE_STORAGE.save();

		// Message
		CORE_MESSAGE = new Config("message.yml", this.getPluginName());

		CORE_MESSAGE.set("message.prefix", "&6&l[MZP] ", "0.0.1");
		CORE_MESSAGE.set("message.permission.deny", "&3You dont have permission!", "0.0.1");
		CORE_MESSAGE.set("message.command.permission.deny", "&3You dont have permission to execute this command!", "0.0.1");

		CORE_MESSAGE.set("message.command.sub.wrong", "&3Wrong sub command for this command!", "0.0.2");
		CORE_MESSAGE.set("message.command.arg.length", "&3Too many or to less arguments for this command!", "0.0.3");

		CORE_MESSAGE.save();
	}

	@Override
	public void reload() {
		Config config = Config.getConfig("core.yml", this.getPluginName());
		config.reload();

		Config message = Config.getConfig("message.yml", this.getPluginName());
		message.reload();

		Zocker.getAllZocker().clear();

		// Reinitialize
		for (Player player : Bukkit.getOnlinePlayers()) {
			new Zocker(player.getUniqueId());
		}

		// Close inventories if possible
		for (InventoryActive inventoryActive : InventoryActive.getActiveInventorys().values()) {
			if (inventoryActive == null) continue;
			inventoryActive.getZocker().getPlayer().closeInventory();
		}
	}

	public static CorePlugin getPlugin() {
		return PLUGIN;
	}

}
