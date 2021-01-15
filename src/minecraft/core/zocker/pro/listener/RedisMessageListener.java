package minecraft.core.zocker.pro.listener;

import minecraft.core.zocker.pro.Main;
import minecraft.core.zocker.pro.compatibility.CompatibleMessage;
import minecraft.core.zocker.pro.compatibility.CompatibleSound;
import minecraft.core.zocker.pro.event.RedisMessageEvent;
import minecraft.core.zocker.pro.storage.StorageManager;
import minecraft.core.zocker.pro.storage.cache.memory.MemoryCacheEntry;
import minecraft.core.zocker.pro.storage.cache.memory.MemoryCacheManager;
import minecraft.core.zocker.pro.storage.cache.redis.RedisPacketIdentifyType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONException;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

import java.util.Set;
import java.util.UUID;

public class RedisMessageListener implements Listener {

	@EventHandler
	public void onRedisMessage(RedisMessageEvent e) {
		if (!e.getReceiverName().equalsIgnoreCase("MZP-Core")) return;

		try {
			JSONObject packet = e.getPacket();
			if (packet.isNull("identify")) return;

			String identify = e.getPacket().getString("identify");
			if (identify.length() <= 0) return;

			RedisPacketIdentifyType identifyType = RedisPacketIdentifyType.valueOf(identify);
			switch (identifyType) {

				// region Server

				case SERVER_MESSAGE_CHAT: {
					String message = packet.getString("message");
					if (message.length() <= 0) return;

					for (Player player : Bukkit.getOnlinePlayers()) {
						CompatibleMessage.sendMessage(player, message);
					}

					return;
				}

				case SERVER_MESSAGE_ACTION_BAR: {
					String message = packet.getString("message");
					if (message.length() <= 0) return;

					for (Player player : Bukkit.getOnlinePlayers()) {
						CompatibleMessage.sendActionBar(player, message);
					}

					return;
				}

				case SERVER_MESSAGE_TITLE: {
					String title = packet.getString("title");
					if (title.length() <= 0) return;

					int fadeIn = packet.getInt("fadeIn");
					int stay = packet.getInt("stay");
					int fadeOut = packet.getInt("fadeOut");

					String subTitle = packet.getString("subTitle");

					for (Player player : Bukkit.getOnlinePlayers()) {
						CompatibleMessage.sendTitle(player, title, subTitle, fadeIn, stay, fadeOut);
					}

					return;
				}

				case SERVER_SOUND: {
					String soundName = packet.getString("sound");
					if (soundName.length() <= 0) return;

					CompatibleSound compatibleSound = CompatibleSound.valueOf(soundName);

					for (Player player : Bukkit.getOnlinePlayers()) {
						compatibleSound.play(player);
					}

					return;
				}

				case SERVER_COMMAND: {
					String command = packet.getString("command");
					if (command.length() <= 0) return;

					new BukkitRunnable() {
						@Override
						public void run() {
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
						}
					}.runTask(Main.getPlugin());
					return;
				}

				case SERVER_COMMAND_PLAYER: {
					String command = packet.getString("command");
					if (command.length() <= 0) return;

					new BukkitRunnable() {
						@Override
						public void run() {
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
						}
					}.runTask(Main.getPlugin());
					return;
				}

				// endregion

				// region Player

				case PLAYER_MESSAGE_CHAT: {
					String uuid = packet.getString("uuid");
					if (uuid.length() <= 0) return;

					Player player = Bukkit.getPlayer(UUID.fromString(uuid));
					if (player == null) return;

					String message = packet.getString("message");
					if (message.length() <= 0) return;

					CompatibleMessage.sendMessage(player, message);
					return;
				}

				case PLAYER_MESSAGE_ACTION_BAR: {
					String uuid = packet.getString("uuid");
					if (uuid.length() <= 0) return;

					Player player = Bukkit.getPlayer(UUID.fromString(uuid));
					if (player == null) return;

					String message = packet.getString("message");
					if (message.length() <= 0) return;

					CompatibleMessage.sendActionBar(player, message);
					return;
				}

				case PLAYER_MESSAGE_TITLE: {
					String uuid = packet.getString("uuid");
					if (uuid.length() <= 0) return;

					Player player = Bukkit.getPlayer(UUID.fromString(uuid));
					if (player == null) return;

					String title = packet.getString("title");
					if (title.length() <= 0) return;

					int fadeIn = packet.getInt("fadeIn");
					int stay = packet.getInt("stay");
					int fadeOut = packet.getInt("fadeOut");

					String subTitle = packet.getString("subTitle");

					CompatibleMessage.sendTitle(player, title, subTitle, fadeIn, stay, fadeOut);
					return;
				}

				case PLAYER_SOUND: {
					String uuid = packet.getString("uuid");
					if (uuid.length() <= 0) return;

					Player player = Bukkit.getPlayer(UUID.fromString(uuid));
					if (player == null) return;

					String soundName = packet.getString("sound");
					if (soundName.length() <= 0) return;

					CompatibleSound.valueOf(soundName).play(player);
					return;
				}

				case PLAYER_COMMAND: {
					String command = packet.getString("command");
					if (command.length() <= 0) return;

					String uuid = packet.getString("uuid");
					if (uuid.length() <= 0) return;

					Player player = Bukkit.getPlayer(UUID.fromString(uuid));
					if (player == null) return;

					new BukkitRunnable() {
						@Override
						public void run() {
							if (command.startsWith("/")) {
								player.performCommand(command);
							} else {
								player.performCommand("/" + command);
							}
						}
					}.runTask(Main.getPlugin());
					return;
				}

				// endregion

				// region Core

				case CORE_CACHE_CLEAN_PLAYER: {
					String uuid = packet.getString("uuid");
					if (uuid.length() <= 0) return;

					if (StorageManager.isMemory()) {
						MemoryCacheManager memoryCacheManager = new MemoryCacheManager();
						MemoryCacheEntry memoryCacheEntry = memoryCacheManager.get(uuid);
						if (memoryCacheEntry != null) {
							memoryCacheManager.remove(memoryCacheEntry);
						}
					} else {
						// Redis cache
						Jedis redis = new Jedis(Main.CORE_STORAGE.getString("storage.cache.redis.host"), Main.CORE_STORAGE.getInt("storage.cache.redis.port"));
						redis.auth(Main.CORE_STORAGE.getString("storage.cache.redis.password"));
						redis.clientSetname("Channel-" + StorageManager.getServerName());

						Set<String> jedisData = redis.keys(uuid);
						for (String jedisDataKey : jedisData) {
							redis.del(jedisDataKey);
						}

						redis.disconnect();
						redis.close();
					}

					return;
				}

				case CORE_CACHE_CLEAN_SERVER: {
					MemoryCacheManager.getMemoryCacheEntryList().clear();
					// TODO redis support, but can be dangerous
					return;
				}

				// endregion
			}
		} catch (JSONException jsonException) {
			jsonException.printStackTrace();
		}
	}
}