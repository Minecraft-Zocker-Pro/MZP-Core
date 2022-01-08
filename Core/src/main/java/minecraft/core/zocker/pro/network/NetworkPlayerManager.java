package minecraft.core.zocker.pro.network;

import minecraft.core.zocker.pro.Zocker;
import minecraft.core.zocker.pro.storage.StorageManager;
import minecraft.core.zocker.pro.storage.cache.redis.RedisCacheManager;
import minecraft.core.zocker.pro.storage.cache.redis.RedisPacketBuilder;
import minecraft.core.zocker.pro.storage.cache.redis.RedisPacketIdentifyType;
import minecraft.core.zocker.pro.storage.cache.redis.packet.player.RedisPlayerMessagePacket;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class NetworkPlayerManager {

	public CompletableFuture<String> getServer(Zocker zocker) {
		if (zocker == null) return null;

		return zocker.get("player", "server");
	}

	public CompletableFuture<Boolean> updateServer(Zocker zocker, String serverName) {
		if (zocker == null || serverName == null) return null;

		return zocker.set("player", "server", serverName);
	}

	// region Message

	public CompletableFuture<Boolean> sendMessage(Zocker zocker, String message) {
		if (zocker == null || message == null) return null;
		if (!StorageManager.isRedis()) return null;

		return CompletableFuture.supplyAsync(() -> {
			try {
				RedisPacketBuilder redisPacketBuilder = new RedisPacketBuilder();
				redisPacketBuilder.setPluginName("MZP-Core");
				redisPacketBuilder.setSenderName(StorageManager.getServerName());
				redisPacketBuilder.setReceiverName("MZP-Core");
				redisPacketBuilder.setServerTargetName(getServer(zocker).get());

				redisPacketBuilder.addPacket(new RedisPlayerMessagePacket(zocker.getUUID(), message, RedisPacketIdentifyType.PLAYER_MESSAGE_CHAT));

				RedisCacheManager redisCacheManager = new RedisCacheManager();
				redisCacheManager.publish(redisPacketBuilder.build());

				return true;
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}

			return false;
		});
	}

	// endregion

	// region ActionBar

	public CompletableFuture<Boolean> sendActionBar(Zocker zocker, String message) {
		if (zocker == null || message == null) return null;
		if (!StorageManager.isRedis()) return null;

		return CompletableFuture.supplyAsync(() -> {
			try {
				RedisPacketBuilder redisPacketBuilder = new RedisPacketBuilder();
				redisPacketBuilder.setPluginName("MZP-Core");
				redisPacketBuilder.setSenderName(StorageManager.getServerName());
				redisPacketBuilder.setReceiverName("MZP-Core");
				redisPacketBuilder.setServerTargetName(getServer(zocker).get());

				redisPacketBuilder.addPacket(new RedisPlayerMessagePacket(zocker.getUUID(), message, RedisPacketIdentifyType.PLAYER_MESSAGE_ACTION_BAR));

				RedisCacheManager redisCacheManager = new RedisCacheManager();
				redisCacheManager.publish(redisPacketBuilder.build());

				return true;
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}

			return false;
		});
	}

	// endregion

	// region Title

	public CompletableFuture<Boolean> sendTitle(Zocker zocker, String title, String subTitle) {
		return this.sendTitle(zocker, title, subTitle, 10, 25, 10);
	}

	public CompletableFuture<Boolean> sendTitle(Zocker zocker, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
		if (zocker == null || title == null) return null;
		if (!StorageManager.isRedis()) return null;

		if (fadeIn == 0) fadeIn = 10;
		if (stay == 0) stay = 25;
		if (fadeOut == 0) fadeOut = 10;

		int finalFadeIn = fadeIn;
		int finalStay = stay;
		int finalFadeOut = fadeOut;

		return CompletableFuture.supplyAsync(() -> {
			try {
				RedisPacketBuilder redisPacketBuilder = new RedisPacketBuilder();
				redisPacketBuilder.setPluginName("MZP-Core");
				redisPacketBuilder.setSenderName(StorageManager.getServerName());
				redisPacketBuilder.setReceiverName("MZP-Core");
				redisPacketBuilder.setServerTargetName(getServer(zocker).get());

				redisPacketBuilder.addPacket(new RedisPlayerMessagePacket(zocker.getUUID(), title, subTitle, finalFadeIn, finalStay, finalFadeOut));

				RedisCacheManager redisCacheManager = new RedisCacheManager();
				redisCacheManager.publish(redisPacketBuilder.build());

				return true;
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}

			return false;
		});
	}

	// endregion

	public CompletableFuture<Boolean> isOnline(Zocker zocker) {
		return zocker.isValue("player", "online");
	}
}