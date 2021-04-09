package minecraft.core.zocker.pro;

import minecraft.core.zocker.pro.command.CoreCommand;
import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
import minecraft.core.zocker.pro.compatibility.ServerProject;
import minecraft.core.zocker.pro.condition.ConditionManager;
import minecraft.core.zocker.pro.config.Config;
import minecraft.core.zocker.pro.event.PlayerVoidFallEvent;
import minecraft.core.zocker.pro.hook.HookManager;
import minecraft.core.zocker.pro.inventory.InventoryActive;
import minecraft.core.zocker.pro.listener.PlayerJoinListener;
import minecraft.core.zocker.pro.listener.PlayerQuitListener;
import minecraft.core.zocker.pro.listener.RedisMessageListener;
import minecraft.core.zocker.pro.network.NetworkServerManager;
import minecraft.core.zocker.pro.storage.StorageManager;
import minecraft.core.zocker.pro.storage.cache.memory.MemoryCacheManager;
import minecraft.core.zocker.pro.storage.cache.redis.RedisCacheManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends CorePlugin {

	public static boolean isProxy;

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

		if (ServerProject.isServer(ServerProject.CRAFTBUKKIT)) {
			System.out.println("MZP-Core it's not compatible with CraftBukkit! Use Spigot or similar server software.");
			onDisable();
			return;
		}

		this.buildConfig();

		new HookManager().load("Vault");

		StorageManager.initialize();

		this.registerCommand();
		this.registerListener();
		this.handleVoidFall();

		ConditionManager.loadAll();

		// Reinitialize
		for (Player player : Bukkit.getOnlinePlayers()) {
			new Zocker(player.getUniqueId());
		}
	}

	public void onDisable() {
		if (StorageManager.isMySQL()) {
			assert StorageManager.getMySQLDatabase() != null;
			StorageManager.getMySQLDatabase().disconnect();
		}

		if (StorageManager.isSQLite()) {
			assert StorageManager.getSQLiteDatabase() != null;
			StorageManager.getSQLiteDatabase().disconnect();
		}

		if (StorageManager.isRedis()) {
			RedisCacheManager.closeConnections();
			NetworkServerManager.stop();
		}

		if (StorageManager.isMemory()) {
			MemoryCacheManager.stop();
		}

		InventoryActive.getActiveInventorys().forEach((inventory, inventoryActive) -> {
			inventoryActive.stopUpdating();
			Zocker zocker = inventoryActive.getZocker();
			if (zocker == null) return;

			zocker.getPlayer().closeInventory();
		});

		InventoryActive.getActiveGUIs().clear();
	}

	@Override
	public void registerCommand() {
		getCommand("core").setExecutor(new CoreCommand());
	}

	@Override
	public void registerListener() {
		PluginManager pluginManager = Bukkit.getPluginManager();
		pluginManager.registerEvents(new PlayerJoinListener(), this);
		pluginManager.registerEvents(new PlayerQuitListener(), this);
		pluginManager.registerEvents(new InventoryActive.GUIActiveListener(), this);

		if (CORE_STORAGE.getBool("storage.cache.redis.enabled")) {
			pluginManager.registerEvents(new RedisMessageListener(), this);
		}
	}

	@Override
	public void buildConfig() {
		// Config		
		CORE_CONFIG = new Config("core.yml", this.getPluginName());

		CORE_CONFIG.set("core.server.name", "my-server", "0.0.2");
		CORE_CONFIG.set("core.event.void.enabled", false, "0.0.9");
		CORE_CONFIG.set("core.event.void.high", 0, "0.0.9");
		CORE_CONFIG.set("core.event.void.delay", 20, "0.0.9");

		CORE_CONFIG.setVersion("0.0.9", true);

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

		// Memory
		CORE_STORAGE.set("storage.cache.memory.enabled", true, "0.0.11");
		CORE_STORAGE.set("storage.cache.memory.delay", 1, "0.0.11");
		CORE_STORAGE.set("storage.cache.memory.limit", 100000, "0.0.17");
		CORE_STORAGE.set("storage.cache.memory.expiration.duration", 60, "0.0.11");
		CORE_STORAGE.set("storage.cache.memory.expiration.renew", true, "0.0.11");

		// Redis
		CORE_STORAGE.set("storage.cache.redis.enabled", false, "0.0.1");
		CORE_STORAGE.set("storage.cache.redis.host", "localhost", "0.0.1");
		CORE_STORAGE.set("storage.cache.redis.port", 6379, "0.0.1");
		CORE_STORAGE.set("storage.cache.redis.password", "!default", "0.0.1");

		CORE_STORAGE.setVersion("0.0.17", true);

		// Message
		CORE_MESSAGE = new Config("message.yml", this.getPluginName());

		CORE_MESSAGE.set("message.prefix", "&6&l[MZP] ", "0.0.1");
		CORE_MESSAGE.set("message.permission.deny", "&3You dont have permission!", "0.0.1");
		CORE_MESSAGE.set("message.command.permission.deny", "&3You dont have permission to execute this command!", "0.0.1");

		CORE_MESSAGE.set("message.command.sub.wrong", "&3Wrong sub command for this command!", "0.0.2");
		CORE_MESSAGE.set("message.command.arg.length", "&3Too many or to less arguments for this command!", "0.0.3");

		CORE_MESSAGE.setVersion("0.0.9", true);
	}

	@Override
	public void reload() {
		CORE_CONFIG.reload();
		CORE_MESSAGE.reload();
		CORE_STORAGE.reload();

		Zocker.getAllZocker().clear();

		// Reinitialize
		for (Player player : Bukkit.getOnlinePlayers()) {
			new Zocker(player.getUniqueId());
		}

		InventoryActive.getActiveInventorys().forEach((inventory, inventoryActive) -> {
			inventoryActive.stopUpdating();
			Zocker zocker = inventoryActive.getZocker();
			if (zocker == null) return;

			zocker.getPlayer().closeInventory();
		});

		InventoryActive.getActiveInventorys().clear();

		if (StorageManager.isMemory()) {
			MemoryCacheManager.stop();
			MemoryCacheManager.start();
		}

		if (StorageManager.isRedis()) {
			NetworkServerManager.stop();
			NetworkServerManager.start();
		}

		this.handleVoidFall();
	}

	private void handleVoidFall() {
		if (!CORE_CONFIG.getBool("core.event.void.enabled")) return;

		float yPosition = CORE_CONFIG.getInt("core.event.void.high");

		new BukkitRunnable() {
			@Override
			public void run() {
				if (Bukkit.getOnlinePlayers().size() == 0) return;

				Bukkit.getOnlinePlayers().forEach(player -> {
					if (player.getLocation().getY() <= yPosition) {
						Bukkit.getPluginManager().callEvent(new PlayerVoidFallEvent(player));
					}
				});
			}
		}.runTaskTimerAsynchronously(this, 0, CORE_CONFIG.getInt("core.event.void.delay"));
	}

	public static CorePlugin getPlugin() {
		return PLUGIN;
	}
}
