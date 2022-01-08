package minecraft.core.zocker.pro.network;

import minecraft.core.zocker.pro.Main;
import minecraft.core.zocker.pro.Zocker;
import minecraft.core.zocker.pro.storage.StorageManager;
import minecraft.core.zocker.pro.storage.cache.redis.RedisCacheManager;
import minecraft.core.zocker.pro.storage.cache.redis.RedisPacketBuilder;
import minecraft.core.zocker.pro.storage.cache.redis.RedisPacketIdentifyType;
import minecraft.core.zocker.pro.storage.cache.redis.packet.server.RedisServerCommandPacket;
import minecraft.core.zocker.pro.storage.cache.redis.packet.server.RedisServerMessagePacket;
import minecraft.core.zocker.pro.workers.JobRunnable;
import minecraft.core.zocker.pro.workers.instances.WorkerPriority;
import minecraft.core.zocker.pro.workers.instances.Workers;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.spigotmc.SpigotConfig;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class NetworkServerManager {

	private static JobRunnable jobRunnable;

	public static void start() {
		if (isRunning()) return;

		try {
			Zocker zocker = new Zocker("dummy");
			zocker.hasValueAsync("server", "server_uuid", "server_uuid", StorageManager.getServerName()).thenAcceptAsync(aBoolean -> {
				if (aBoolean) {
					return;
				}

				Server server = Bukkit.getServer();

				zocker.insert("server",
					new String[]{"server_uuid", "host", "port", "online", "slot", "motd"},
					new Object[]{StorageManager.getServerName(), server.getIp(), server.getPort(), server.getOnlinePlayers().size(), server.getMaxPlayers(), server.getMotd()},
					new String[]{"server_uuid"},
					new Object[]{StorageManager.getServerName()});
			});

			jobRunnable = new JobRunnable() {
				final Server server = Bukkit.getServer();

				@Override
				public void run() {
					zocker.set("server",
						new String[]{"host", "port", "online", "slot", "motd", "last_update"},
						new Object[]{server.getIp(), server.getPort(), server.getOnlinePlayers().size(), server.getMaxPlayers(), server.getMotd(),
							new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.sql.Date(System.currentTimeMillis()))},
						new String[]{"server_uuid"},
						new Object[]{StorageManager.getServerName()});
				}
			};

			Workers.BACKEND_WORKER.addJob(jobRunnable, 0, 5, TimeUnit.SECONDS, WorkerPriority.EXTREME);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void stop() {
		if (jobRunnable == null) return;

		new Zocker("dummy").set("server",
			new String[]{"online", "last_update"},
			new Object[]{"-1", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(System.currentTimeMillis()))},
			new String[]{"server_uuid"},
			new Object[]{StorageManager.getServerName()});

		jobRunnable.cancel();
	}

	public static boolean isRunning() {
		if (jobRunnable == null) return false;
		return !jobRunnable.isCancelled();
	}

	public CompletableFuture<NetworkServer> getServer(String serverUUID) {
		if (serverUUID == null) return null;

		return CompletableFuture.supplyAsync(() -> {
			Zocker zocker = new Zocker("dummy");

			try {
				Map<String, String> data = zocker.get("server", new String[]{"host", "port", "online", "slot", "motd", "last_update", "enabled"}, "server_uuid", serverUUID).get();
				if (data.isEmpty()) return null;

				String isEnabledString = data.get("enabled");
				boolean isEnabled;

				isEnabled = !isEnabledString.equalsIgnoreCase("0");

				return new NetworkServer(
					serverUUID,
					data.get("host"),
					Integer.parseInt(data.get("port")),
					Integer.parseInt(data.get("online")),
					Integer.parseInt(data.get("slot")),
					data.get("motd"),
					new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(data.get("last_update")).getTime(),
					isEnabled
				);
			} catch (InterruptedException | ExecutionException | ParseException e) {
				e.printStackTrace();
			}

			return null;
		});
	}

	public CompletableFuture<List<NetworkServer>> getServers() {
		return CompletableFuture.supplyAsync(() -> {
			Zocker zocker = new Zocker("dummy");

			try {
				List<String> serverUUIDs = zocker.getList(
					"server",
					new String[]{"server_uuid"},
					"server_uuid",
					StorageManager.getServerName())
					.get();

				if (serverUUIDs == null || serverUUIDs.isEmpty()) return null;

				List<NetworkServer> networkServers = new ArrayList<>();

				for (String serverUUID : serverUUIDs) {
					if (serverUUID == null) continue;

					Map<String, String> data = zocker.get("server", new String[]{"host", "port", "online", "slot", "motd", "last_update", "enabled"}, "server_uuid", serverUUID).get();
					if (data.isEmpty()) continue;

					String isEnabledString = data.get("enabled");
					boolean isEnabled;

					isEnabled = !isEnabledString.equalsIgnoreCase("0");

					networkServers.add(new NetworkServer(
						serverUUID,
						data.get("host"),
						Integer.parseInt(data.get("port")),
						Integer.parseInt(data.get("online")),
						Integer.parseInt(data.get("slot")),
						data.get("motd"),
						new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(data.get("last_update")).getTime(),
						isEnabled
					));
				}

				return networkServers;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		});
	}

	public CompletableFuture<Integer> getGlobalOnline() {
		return getServers().thenApply(networkServers -> {
			int amount = 0;

			for (NetworkServer networkServer : networkServers) {
				if (!networkServer.isEnabled()) continue;
				if (networkServer.getName().startsWith("Proxy")) continue;

				amount += networkServer.getOnline();
			}

			if (amount <= 0) {
				amount = Bukkit.getServer().getOnlinePlayers().size();
			}

			return amount;
		});
	}

	// region Message

	public CompletableFuture<Boolean> sendMessage(NetworkServer networkServer, String message) {
		if (networkServer == null || message == null) return null;
		if (!StorageManager.isRedis()) return null;

		return this.sendMessage(networkServer.getName(), message);
	}

	public CompletableFuture<Boolean> sendMessage(String serverName, String message) {
		if (serverName == null || message == null) return null;
		if (!StorageManager.isRedis()) return null;

		return CompletableFuture.supplyAsync(() -> {
			RedisPacketBuilder redisPacketBuilder = new RedisPacketBuilder();
			redisPacketBuilder.setPluginName("MZP-Core");
			redisPacketBuilder.setSenderName(StorageManager.getServerName());
			redisPacketBuilder.setReceiverName("MZP-Core");
			redisPacketBuilder.setServerTargetName(serverName);

			redisPacketBuilder.addPacket(new RedisServerMessagePacket(message, RedisPacketIdentifyType.SERVER_MESSAGE_CHAT));

			RedisCacheManager redisCacheManager = new RedisCacheManager();
			redisCacheManager.publish(redisPacketBuilder.build());

			return true;
		});
	}

	// endregion

	// region ActionBar

	public CompletableFuture<Boolean> sendActionBar(NetworkServer networkServer, String message) {
		if (networkServer == null || message == null) return null;
		if (!StorageManager.isRedis()) return null;

		return this.sendActionBar(networkServer.getName(), message);
	}

	public CompletableFuture<Boolean> sendActionBar(String serverName, String message) {
		if (serverName == null || message == null) return null;
		if (!StorageManager.isRedis()) return null;

		return CompletableFuture.supplyAsync(() -> {
			RedisPacketBuilder redisPacketBuilder = new RedisPacketBuilder();
			redisPacketBuilder.setPluginName("MZP-Core");
			redisPacketBuilder.setSenderName(StorageManager.getServerName());
			redisPacketBuilder.setReceiverName("MZP-Core");
			redisPacketBuilder.setServerTargetName(serverName);

			redisPacketBuilder.addPacket(new RedisServerMessagePacket(message, RedisPacketIdentifyType.SERVER_MESSAGE_ACTION_BAR));

			RedisCacheManager redisCacheManager = new RedisCacheManager();
			redisCacheManager.publish(redisPacketBuilder.build());

			return true;
		});
	}

	// endregion

	// region Title

	public CompletableFuture<Boolean> sendTitle(NetworkServer networkServer, String title, String subTitle) {
		if (networkServer == null || title == null) return null;
		if (!StorageManager.isRedis()) return null;

		return this.sendTitle(networkServer.getName(), title, subTitle, 10, 25, 10);
	}

	public CompletableFuture<Boolean> sendTitle(NetworkServer networkServer, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
		if (networkServer == null || title == null) return null;
		if (!StorageManager.isRedis()) return null;

		return this.sendTitle(networkServer.getName(), title, subTitle, fadeIn, stay, fadeOut);
	}

	public CompletableFuture<Boolean> sendTitle(String serverName, String title, String subTitle) {
		if (serverName == null || title == null) return null;
		if (!StorageManager.isRedis()) return null;

		return this.sendTitle(serverName, title, subTitle, 10, 25, 10);
	}

	public CompletableFuture<Boolean> sendTitle(String serverName, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
		if (serverName == null || title == null) return null;
		if (!StorageManager.isRedis()) return null;

		return CompletableFuture.supplyAsync(() -> {
			RedisPacketBuilder redisPacketBuilder = new RedisPacketBuilder();
			redisPacketBuilder.setPluginName("MZP-Core");
			redisPacketBuilder.setSenderName(StorageManager.getServerName());
			redisPacketBuilder.setReceiverName("MZP-Core");
			redisPacketBuilder.setServerTargetName(serverName);

			redisPacketBuilder.addPacket(new RedisServerMessagePacket(title, subTitle, fadeIn, stay, fadeOut, RedisPacketIdentifyType.SERVER_MESSAGE_TITLE));

			RedisCacheManager redisCacheManager = new RedisCacheManager();
			redisCacheManager.publish(redisPacketBuilder.build());

			return true;
		});
	}

	// endregion

	// region Command

	public CompletableFuture<Boolean> sendCommand(NetworkServer networkServer, String command) {
		if (networkServer == null || command == null) return null;
		if (!StorageManager.isRedis()) return null;

		return this.sendCommand(networkServer.getName(), command);
	}

	public CompletableFuture<Boolean> sendCommand(String serverName, String command) {
		if (serverName == null || command == null) return null;
		if (!StorageManager.isRedis()) return null;

		return CompletableFuture.supplyAsync(() -> {
			RedisPacketBuilder redisPacketBuilder = new RedisPacketBuilder();
			redisPacketBuilder.setPluginName("MZP-Core");
			redisPacketBuilder.setSenderName(StorageManager.getServerName());
			redisPacketBuilder.setReceiverName("MZP-Core");
			redisPacketBuilder.setServerTargetName(serverName);

			redisPacketBuilder.addPacket(new RedisServerCommandPacket(command));

			RedisCacheManager redisCacheManager = new RedisCacheManager();
			redisCacheManager.publish(redisPacketBuilder.build());

			return true;
		});
	}

	// endregion

	public boolean isProxyEnabled() {
		return SpigotConfig.bungee && Main.CORE_CONFIG.getBool("core.proxy.enabled");
	}
}