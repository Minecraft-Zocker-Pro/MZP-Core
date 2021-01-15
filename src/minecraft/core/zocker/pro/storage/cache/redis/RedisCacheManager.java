package minecraft.core.zocker.pro.storage.cache.redis;

import minecraft.core.zocker.pro.Main;
import minecraft.core.zocker.pro.event.RedisMessageEvent;
import minecraft.core.zocker.pro.storage.StorageManager;
import org.bukkit.Bukkit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import redis.clients.jedis.*;

import java.util.Collections;

public class RedisCacheManager {

	private static ShardedJedisPool REDIS_POOL;
	private static Jedis REDIS_CHANNEL;
	private static Jedis REDIS_PUBLISH;

	public ShardedJedis getResource() {
		return REDIS_POOL.getResource();
	}

	public void publish(RedisPacket redisPacket) {
		if (redisPacket == null) return;
		if (REDIS_PUBLISH == null) return;

		System.out.println("Send packets to: " + redisPacket.getServerTargetName());
		REDIS_PUBLISH.publish(redisPacket.getServerTargetName().toUpperCase(), redisPacket.toJSON());
	}

	public static void createConnection() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(128);
		jedisPoolConfig.setMaxIdle(10);

		JedisShardInfo jedisShardInfo = new JedisShardInfo(
			Main.CORE_STORAGE.getString("storage.cache.redis.host"),
			Main.CORE_STORAGE.getInt("storage.cache.redis.port"),
			Protocol.DEFAULT_TIMEOUT,
			StorageManager.getServerName()
		);

		jedisShardInfo.setPassword(Main.CORE_STORAGE.getString("storage.cache.redis.password"));

		REDIS_POOL = new ShardedJedisPool(jedisPoolConfig, Collections.singletonList(jedisShardInfo));
		try (ShardedJedis jedis = REDIS_POOL.getResource()) {
			if (jedis == null) {
				System.err.println("Redis connection failed!");
				return;
			} else {
				System.out.println("Redis connection created");
			}
		}

		createChannelSubscription();
	}

	private static void createChannelSubscription() {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
			REDIS_CHANNEL = new Jedis(Main.CORE_STORAGE.getString("storage.cache.redis.host"), Main.CORE_STORAGE.getInt("storage.cache.redis.port"));
			REDIS_CHANNEL.auth(Main.CORE_STORAGE.getString("storage.cache.redis.password"));
			REDIS_CHANNEL.clientSetname("Channel-" + StorageManager.getServerName());

			REDIS_PUBLISH = new Jedis(Main.CORE_STORAGE.getString("storage.cache.redis.host"), Main.CORE_STORAGE.getInt("storage.cache.redis.port"));
			REDIS_PUBLISH.auth(Main.CORE_STORAGE.getString("storage.cache.redis.password"));
			REDIS_PUBLISH.clientSetname("Publish-" + StorageManager.getServerName());

			REDIS_CHANNEL.subscribe(new JedisPubSub() {
				@Override
				public void onMessage(String channel, String message) {
					if (channel == null) return;
					if (message == null) return;
					System.out.println("redis channel: " + channel + " msg:" + message);

					try {
						JSONObject json = new JSONObject(message);
						if (json.length() == 0) return;

						String target = json.getString("target");

						// check for server packet
						if (!target.equalsIgnoreCase("MZP-" + StorageManager.getServerName())) {
							// check for global packet
							if (!target.equalsIgnoreCase("MZP-Core")) {
								if (!target.equalsIgnoreCase("MZP-ProxyCore")) {
									System.out.println("Invalid packet received");
									return;
								}
							}
						}

						System.out.println("Valid packet received");

						String sender = json.getString("sender");
						String receiver = json.getString("receiver");

						if (json.isNull("packets")) return;

						JSONArray packets = json.getJSONArray("packets");
						if (packets.length() <= 0) return;
						
						RedisPacketBuilder redisPacketBuilder = new RedisPacketBuilder()
							.setServerTargetName(target)
							.setReceiverName(receiver)
							.setSenderName(sender);

						for (int i = 0; i < packets.length(); i++) {
							JSONObject jsonObject = packets.getJSONObject(i);
							if (jsonObject == null) continue;

							Bukkit.getPluginManager().callEvent(new RedisMessageEvent(redisPacketBuilder.build(), jsonObject, true));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}, "MZP-CORE", "MZP-" + StorageManager.getServerName().toUpperCase());
		});
	}

	public static void closeConnections() {
		if (REDIS_POOL != null) {
			REDIS_POOL.close();
		}

		if (REDIS_CHANNEL != null) {
			REDIS_CHANNEL.close();
		}
	}

	public static boolean isRunning() {
		return REDIS_POOL != null && REDIS_CHANNEL != null;
	}
}
