package minecraft.core.zocker.pro;

import minecraft.core.zocker.pro.storage.StorageManager;
import minecraft.core.zocker.pro.storage.cache.memory.MemoryCacheEntry;
import minecraft.core.zocker.pro.storage.cache.memory.MemoryCacheEntryBuilder;
import minecraft.core.zocker.pro.storage.cache.memory.MemoryCacheManager;
import minecraft.core.zocker.pro.storage.cache.redis.RedisCacheManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.ShardedJedis;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Zocker {

	private static final ConcurrentHashMap<UUID, Zocker> ZOCKERS = new ConcurrentHashMap<>();

	private Player player;
	private UUID uuid;

	// region Constructors

	public Zocker(Player player) {
		ZOCKERS.putIfAbsent(player.getUniqueId(), this);

		this.player = player;
		this.uuid = player.getUniqueId();
	}

	public Zocker(UUID uuid) {
		ZOCKERS.putIfAbsent(uuid, this);

		Player player = Bukkit.getPlayer(uuid);
		if (player != null) {
			this.player = player;
		}

		this.uuid = uuid;
	}

	public Zocker(String dummy) {
	}

	// endregion Constructors

	// region Statics methods

	public static Zocker getZocker(UUID uuid) {
		if (uuid == null) return null;

		Zocker zocker = ZOCKERS.get(uuid);
		if (zocker == null) return null;

		Player player = Bukkit.getPlayer(uuid);
		if (player == null) return null;

		zocker.setPlayer(player);
		return zocker;
	}

	public static Zocker getZocker(Player player) {
		if (player == null) return null;

		Zocker zocker = ZOCKERS.get(player.getUniqueId());
		if (zocker == null) return null;

		zocker.setPlayer(player);
		return zocker;
	}

	public static Zocker getZocker(String name) {
		if (name == null) return null;

		Player player = Bukkit.getPlayer(name);
		if (player == null) return null;

		Zocker zocker = ZOCKERS.get(player.getUniqueId());
		if (zocker == null) return null;

		zocker.setPlayer(player);
		return zocker;
	}

	public static void removeZocker(UUID uuid) {
		if (ZOCKERS.get(uuid) != null) {
			ZOCKERS.remove(uuid);
		}
	}

	public static void removeZocker(Zocker zocker) {
		if (zocker != null) {
			ZOCKERS.entrySet().removeIf(item -> item.getValue().equals(zocker));
		}
	}

	public static void removeZocker(String name) {
		Player player = Bukkit.getPlayer(name);
		if (player != null) {
			ZOCKERS.remove(player.getUniqueId());
		}
	}

	public static Collection<Zocker> getAllZocker() {
		return ZOCKERS.values();
	}

	// endregion

	// region Get

	public CompletableFuture<String> get(String table, String column) {
		return get(table, column, "uuid", this.uuid.toString());
	}

	public CompletableFuture<String> get(String table, String column, String uniqueKey, Object uniqueValue) {
		if (table == null || column == null || uniqueKey == null) return null;

		// Cache
		if (StorageManager.isMemory()) {
			MemoryCacheManager memoryCacheManager = new MemoryCacheManager();
			MemoryCacheEntry cacheEntry = memoryCacheManager.get(uniqueValue.toString() + "-" + table);

			if (cacheEntry != null) {
				if (!cacheEntry.getColumns().isEmpty()) {
					for (String key : cacheEntry.getColumns().keySet()) {
						if (key.equalsIgnoreCase(column)) {
							Object value = cacheEntry.getColumns().get(column);
							if (value != null) return CompletableFuture.completedFuture(value.toString());
							break;
						}
					}
				}
			}
		}

		return CompletableFuture.supplyAsync(() -> {
			if (StorageManager.isRedis()) {
				RedisCacheManager redisCacheManager = new RedisCacheManager();
				try (ShardedJedis redis = redisCacheManager.getResource()) {
					String redisData = redis.hget(uniqueValue.toString() + "-" + table, column);

					if (redisData != null) {
						HashMap<String, String> data = new HashMap<>();
						data.put(column, redisData);

						createMemoryCacheEntry(uniqueValue.toString() + "-" + table, data);
						return redisData;
					}
				}
			}

			// Database
			try {
				ResultSet result;
				if (StorageManager.isMySQL()) {
					assert StorageManager.getMySQLDatabase() != null : "Select command failed";
					result = StorageManager.getMySQLDatabase().select(table, column, uniqueKey, uniqueValue.toString());
				} else {
					assert StorageManager.getSQLiteDatabase() != null : "Select command failed.";
					result = StorageManager.getSQLiteDatabase().select(table, column, uniqueKey, uniqueValue.toString());
				}
				if (result == null) return null;

				if (result.next()) {
					String value = result.getString(column);
					result.close();

					if (value == null) return null;

					if (StorageManager.isRedis()) {
						RedisCacheManager redisCacheManager = new RedisCacheManager();
						try (ShardedJedis redis = redisCacheManager.getResource()) {
							redis.hset(uniqueValue.toString() + "-" + table, column, value);
						}
					}

					if (StorageManager.isMemory()) {
						MemoryCacheManager memoryCacheManager = new MemoryCacheManager();
						MemoryCacheEntry cacheEntry = memoryCacheManager.get(uniqueValue.toString() + "-" + table);

						if (cacheEntry == null) {
							cacheEntry = new MemoryCacheEntryBuilder()
								.setUniqueKey(uniqueValue.toString() + "-" + table)
								.setExpireDuration(Main.CORE_STORAGE.getInt("storage.cache.memory.expiration.duration"), TimeUnit.SECONDS)
								.addColumn(column, value).build();
						} else {
							cacheEntry.addColumn(column, value);
						}

						memoryCacheManager.add(cacheEntry);
					}

					return value;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}

			return null;
		});
	}

	// endregion

	// region GetList

	public CompletableFuture<List<String>> getList(String table, String[] primaryKeys) {
		return this.getList(table, primaryKeys, "uuid", this.uuid);
	}

	public CompletableFuture<List<String>> getList(String table, String[] columns, String uniqueKey, Object uniqueValue) {
		if (table == null || columns == null) return null;

		String uniqueKeyDatabase = uniqueValue.toString() + "-list-" + table;

		if (StorageManager.isMemory()) {
			MemoryCacheManager memoryCacheManager = new MemoryCacheManager();
			MemoryCacheEntry cacheEntry = memoryCacheManager.get(uniqueKeyDatabase);

			if (cacheEntry != null) {
				if (!cacheEntry.getColumns().isEmpty()) {
					List<String> data = new ArrayList<>(cacheEntry.getColumns().keySet());

					if (!data.isEmpty()) {
						return CompletableFuture.completedFuture(data);
					}
				}
			}
		}

		return CompletableFuture.supplyAsync(() -> {
			try {
				if (StorageManager.isRedis()) {
					RedisCacheManager redisCacheManager = new RedisCacheManager();
					try (ShardedJedis redis = redisCacheManager.getResource()) {
						Map<String, String> redisData = redis.hgetAll(uniqueKeyDatabase);

						if (redisData.size() > 0) {
							createMemoryCacheEntry(uniqueKeyDatabase, redisData);
							return new ArrayList<>(redisData.keySet());
						}
					}
				}

				ResultSet result;
				if (StorageManager.isMySQL()) {
					assert StorageManager.getMySQLDatabase() != null : "Select list command failed";
					if (uniqueKey != null && uniqueKey.length() >= 2) {
						result = StorageManager.getMySQLDatabase().select(table, columns, uniqueKey, uniqueValue.toString());
					} else {
						result = StorageManager.getMySQLDatabase().select(table, columns);
					}
				} else {
					assert StorageManager.getSQLiteDatabase() != null : "Select list command failed.";
					if (uniqueKey != null && uniqueKey.length() >= 2) {
						result = StorageManager.getSQLiteDatabase().select(table, columns, uniqueKey, uniqueValue.toString());
					} else {
						result = StorageManager.getSQLiteDatabase().select(table, columns);
					}
				}

				if (result == null) return null;

				HashMap<String, String> data = new HashMap<>();
				List<String> returnData = new ArrayList<>();

				int row = 0;

				if (columns.length > 1) {
					row = 1;
				}

				while (result.next()) {
					String value = result.getString(columns[row]);
					if (value.equalsIgnoreCase(this.getUUIDString())) continue;

					data.put(value, columns[row]);
					returnData.add(value);
				}

				result.close();

				if (data.isEmpty()) {
					return returnData;
				}

				if (StorageManager.isRedis()) {
					RedisCacheManager redisCacheManager = new RedisCacheManager();
					try (ShardedJedis redis = redisCacheManager.getResource()) {
						redis.hmset(uniqueKeyDatabase, data);
						redis.expire(uniqueKeyDatabase, 5);
					}
				}

				if (StorageManager.isMemory()) {
					MemoryCacheEntryBuilder memoryCacheEntryBuilder = new MemoryCacheEntryBuilder();
					memoryCacheEntryBuilder.setUniqueKey(uniqueKeyDatabase);

					for (String value : data.keySet()) {
						memoryCacheEntryBuilder.addColumn(value, "");
					}

					new MemoryCacheManager().add(memoryCacheEntryBuilder.build());
				}

				return returnData;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
	}

	// endregion

	// region GetComplex

	public CompletableFuture<Map<String, String>> get(String table, String[] columns) {
		return this.get(table, columns, "uuid", this.uuid);
	}

	public CompletableFuture<Map<String, String>> get(String table, String[] columns, String uniqueKey, Object uniqueValue) {
		if (table == null || columns == null) return null;

		if (StorageManager.isMemory()) {
			MemoryCacheManager memoryCacheManager = new MemoryCacheManager();
			MemoryCacheEntry cacheEntry = memoryCacheManager.get(uniqueValue.toString() + "-" + table);

			if (cacheEntry != null) {
				if (!cacheEntry.getColumns().isEmpty()) {

					Map<String, String> data = new HashMap<>();

					for (String column : columns) {
						for (String key : cacheEntry.getColumns().keySet()) {
							if (column.equalsIgnoreCase(key)) {
								data.put(column, cacheEntry.getColumns().get(column).toString());
							}
						}
					}

					if (data.size() == columns.length) {
						return CompletableFuture.completedFuture(data);
					}
				}
			}
		}

		return CompletableFuture.supplyAsync(() -> {
			if (StorageManager.isRedis()) {
				RedisCacheManager redisCacheManager = new RedisCacheManager();
				try (ShardedJedis redis = redisCacheManager.getResource()) {
					List<String> redisData = redis.hmget(uniqueValue.toString() + "-" + table, columns);
					if (redisData != null) {
						Map<String, String> data = new HashMap<>();

						for (int i = 0; i < redisData.size(); i++) {
							String value = redisData.get(i);
							if (value == null) continue;

							data.put(columns[i], redisData.get(i));
						}

						if (data.size() == columns.length) {
							createMemoryCacheEntry(uniqueKey, data);
							return data;
						}
					}
				}
			}

			try {
				ResultSet result;
				if (StorageManager.isMySQL()) {
					assert StorageManager.getMySQLDatabase() != null : "Select command failed";
					result = StorageManager.getMySQLDatabase().select(table, columns, uniqueKey, uniqueValue.toString());
				} else {
					assert StorageManager.getSQLiteDatabase() != null : "Select command failed.";
					result = StorageManager.getSQLiteDatabase().select(table, columns, uniqueKey, uniqueValue.toString());
				}
				if (result == null) return null;

				if (result.next()) {
					Map<String, String> data = new HashMap<>();
					for (String column : columns) {
						String value = result.getString(column);
						if (value == null) continue;

						data.put(column, value);
					}

					if (StorageManager.isRedis()) {
						RedisCacheManager redisCacheManager = new RedisCacheManager();
						try (ShardedJedis redis = redisCacheManager.getResource()) {
							redis.hmset(uniqueValue.toString() + "-" + table, data);
						}
					}

					if (StorageManager.isMemory()) {
						MemoryCacheEntryBuilder memoryCacheEntryBuilder = new MemoryCacheEntryBuilder();
						memoryCacheEntryBuilder.setUniqueKey(uniqueValue.toString() + "-" + table);

						for (String column : columns) {
							String value = result.getString(column);
							if (value == null) continue;

							memoryCacheEntryBuilder.addColumn(column, value);
						}
					}

					result.close();

					return data;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}

			return null;
		});
	}

	public CompletableFuture<Map<String, String>> get(String table, String[] columns, String[] uniqueKeys, Object[] uniqueValues) {
		if (table == null || columns == null || uniqueKeys.length != uniqueValues.length) return null;

		String uniqueKey = generateKey(table, uniqueValues);

		if (StorageManager.isMemory()) {
			MemoryCacheManager memoryCacheManager = new MemoryCacheManager();
			MemoryCacheEntry cacheEntry = memoryCacheManager.get(uniqueKey);

			if (cacheEntry != null) {
				if (!cacheEntry.getColumns().isEmpty()) {

					Map<String, String> data = new HashMap<>();

					for (String column : columns) {
						for (String key : cacheEntry.getColumns().keySet()) {
							if (column.equalsIgnoreCase(key)) {
								data.put(column, cacheEntry.getColumns().get(column).toString());
							}
						}
					}

					if (data.size() == columns.length) {
						return CompletableFuture.completedFuture(data);
					}
				}
			}
		}

		return CompletableFuture.supplyAsync(() -> {
			if (StorageManager.isRedis()) {
				RedisCacheManager redisCacheManager = new RedisCacheManager();

				try (ShardedJedis redis = redisCacheManager.getResource()) {
					List<String> redisData = redis.hmget(uniqueKey, columns);

					if (redisData != null) {
						Map<String, String> data = new HashMap<>();

						for (int i = 0; i < redisData.size(); i++) {
							String value = redisData.get(i);
							if (value == null) continue;

							data.put(columns[i], redisData.get(i));
						}

						if (data.size() == columns.length) {
							createMemoryCacheEntry(uniqueKey, data);

							return data;
						}
					}
				}
			}

			try {
				ResultSet result;
				if (StorageManager.isMySQL()) {
					assert StorageManager.getMySQLDatabase() != null : "Select command failed";
					result = StorageManager.getMySQLDatabase().select(table, columns, uniqueKeys, uniqueValues);
				} else {
					assert StorageManager.getSQLiteDatabase() != null : "Select command failed.";
					result = StorageManager.getSQLiteDatabase().select(table, columns, uniqueKeys, uniqueValues);
				}

				if (result == null) return null;

				if (result.next()) {
					Map<String, String> data = new HashMap<>();
					for (String column : columns) {
						String value = result.getString(column);
						if (value == null) continue;

						data.put(column, value);
					}

					result.close();

					if (data.isEmpty()) return null;

					if (StorageManager.isRedis()) {
						RedisCacheManager redisCacheManager = new RedisCacheManager();
						try (ShardedJedis redis = redisCacheManager.getResource()) {
							redis.hmset(uniqueKey, data);
						}
					}

					if (StorageManager.isMemory()) {
						MemoryCacheEntryBuilder memoryCacheEntryBuilder = new MemoryCacheEntryBuilder();
						memoryCacheEntryBuilder.setUniqueKey(uniqueKey);
						memoryCacheEntryBuilder.setExpireDuration(Main.CORE_STORAGE.getInt("storage.cache.memory.expiration.duration"), TimeUnit.SECONDS);

						for (String column : data.keySet()) {
							String value = data.get(column);
							if (value == null) continue;

							memoryCacheEntryBuilder.addColumn(column, value);
						}

						new MemoryCacheManager().add(memoryCacheEntryBuilder.build());
					}

					return data;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

			return null;
		});
	}

	// endregion

	// region Set

	public CompletableFuture<Boolean> set(String table, String column, Object value) {
		return this.set(table, column, value, "uuid", this.uuid.toString());
	}

	public CompletableFuture<Boolean> set(String table, String column, Object value, String uniqueKey, Object uniqueValue) {
		if (table == null || column == null || uniqueKey == null || uniqueValue == null || value == null) return null;

		return CompletableFuture.supplyAsync(() -> {
			if (StorageManager.isMemory()) {
				MemoryCacheManager memoryCacheManager = new MemoryCacheManager();
				MemoryCacheEntry cacheEntry = memoryCacheManager.get(uniqueValue.toString() + "-" + table);
				if (cacheEntry != null) {
					cacheEntry.updateColumn(column, value.toString());
				}
			}

			if (StorageManager.isRedis()) {
				RedisCacheManager redisCacheManager = new RedisCacheManager();
				try (ShardedJedis redis = redisCacheManager.getResource()) {
					redis.hset(uniqueValue.toString() + "-" + table, column, value.toString());
				}
			}

			if (StorageManager.isMySQL()) {
				assert StorageManager.getMySQLDatabase() != null : "Update command failed.";
				return StorageManager.getMySQLDatabase().update(table, column, value.toString(), uniqueKey, uniqueValue.toString());
			} else {
				assert StorageManager.getSQLiteDatabase() != null : "Update command failed.";
				return StorageManager.getSQLiteDatabase().update(table, column, value.toString(), uniqueKey, uniqueValue.toString());
			}
		});
	}

	// endregion

	// region SetComplex

	public CompletableFuture<Boolean> set(String table, String[] columns, Object[] values) {
		return this.set(table, columns, values, "uuid", this.uuid);
	}

	public CompletableFuture<Boolean> set(String table, String[] columns, Object[] values, String uniqueKey, Object uniqueValue) {
		if (table == null || columns.length != values.length || uniqueKey == null || uniqueValue == null) return null;

		return CompletableFuture.supplyAsync(() -> {
			if (StorageManager.isMemory()) {
				MemoryCacheManager memoryCacheManager = new MemoryCacheManager();
				MemoryCacheEntry cacheEntry = memoryCacheManager.get(uniqueValue.toString() + "-" + table);
				if (cacheEntry != null) {
					for (int i = 0; i < columns.length; i++) {
						cacheEntry.updateColumn(columns[i], values[i].toString());
					}
				}
			}

			if (StorageManager.isRedis()) {
				RedisCacheManager redisCacheManager = new RedisCacheManager();
				Map<String, String> keyValues = new HashMap<>();

				for (int i = 0; i < columns.length; i++) {
					keyValues.put(columns[i], values[i].toString());
				}

				try (ShardedJedis redis = redisCacheManager.getResource()) {
					redis.hmset(uniqueValue.toString() + "-" + table, keyValues);
				}
			}

			if (StorageManager.isMySQL()) {
				assert StorageManager.getMySQLDatabase() != null : "Update command failed.";
				return StorageManager.getMySQLDatabase().update(table, columns, values, uniqueKey, uniqueValue.toString());
			} else {
				assert StorageManager.getSQLiteDatabase() != null : "Update command failed.";
				return StorageManager.getSQLiteDatabase().update(table, columns, values, uniqueKey, uniqueValue.toString());
			}
		});
	}

	public CompletableFuture<Boolean> set(String table, String column, Object value, String[] uniqueKeys, Object[] uniqueValues) {
		if (table == null || column == null || value == null || uniqueKeys.length != uniqueValues.length) return null;

		return CompletableFuture.supplyAsync(() -> {
			String uniqueKey = generateKey(table, uniqueValues);

			if (StorageManager.isMemory()) {
				MemoryCacheManager memoryCacheManager = new MemoryCacheManager();
				MemoryCacheEntry cacheEntry = memoryCacheManager.get(uniqueKey);
				if (cacheEntry != null) {
					cacheEntry.updateColumn(column, value.toString());
				}
			}

			if (StorageManager.isRedis()) {
				RedisCacheManager redisCacheManager = new RedisCacheManager();

				try (ShardedJedis redis = redisCacheManager.getResource()) {
					redis.hset(uniqueKey, column, value.toString());
				}
			}

			if (StorageManager.isMySQL()) {
				assert StorageManager.getMySQLDatabase() != null : "Update command failed.";
				return StorageManager.getMySQLDatabase().update(table, column, value, uniqueKeys, uniqueValues);
			} else {
				assert StorageManager.getSQLiteDatabase() != null : "Update command failed.";
				return StorageManager.getSQLiteDatabase().update(table, column, value, uniqueKeys, uniqueValues);
			}
		});
	}

	public CompletableFuture<Boolean> set(String table, String[] columns, Object[] values, String[] uniqueKeys, Object[] uniqueValues) {
		if (table == null || columns.length != values.length || uniqueKeys.length != uniqueValues.length) return null;

		return CompletableFuture.supplyAsync(() -> {
			String uniqueKey = generateKey(table, uniqueValues);

			if (StorageManager.isMemory()) {
				MemoryCacheManager memoryCacheManager = new MemoryCacheManager();
				MemoryCacheEntry cacheEntry = memoryCacheManager.get(uniqueKey);
				if (cacheEntry != null) {
					for (int i = 0; i < columns.length; i++) {
						cacheEntry.updateColumn(columns[i], values[i].toString());
					}
				}
			}

			if (StorageManager.isRedis()) {
				RedisCacheManager redisCacheManager = new RedisCacheManager();
				Map<String, String> keyValues = new HashMap<>();

				for (int i = 0; i < columns.length; i++) {
					keyValues.put(columns[i], values[i].toString());
				}

				try (ShardedJedis redis = redisCacheManager.getResource()) {
					redis.hmset(uniqueKey, keyValues);
				}
			}

			if (StorageManager.isMySQL()) {
				assert StorageManager.getMySQLDatabase() != null : "Update command failed.";
				return StorageManager.getMySQLDatabase().update(table, columns, values, uniqueKeys, uniqueValues);
			} else {
				assert StorageManager.getSQLiteDatabase() != null : "Update command failed.";
				return StorageManager.getSQLiteDatabase().update(table, columns, values, uniqueKeys, uniqueValues);
			}
		});
	}

	// endregion

	// region Is

	/**
	 * @param table  SQL table
	 * @param column The unique column for the required value
	 * @return boolean
	 */
	public CompletableFuture<Boolean> isValue(String table, String column) {
		return this.isValue(table, column, "uuid", this.uuid.toString());
	}

	/**
	 * @param table       SQL table
	 * @param column      The unique column for the required value
	 * @param uniqueKey   The unique key e.x the player uuid
	 * @param uniqueValue The unique value of the key e.x the player uuid
	 * @return boolean
	 */
	public CompletableFuture<Boolean> isValue(String table, String column, String uniqueKey, Object uniqueValue) {
		return this.get(table, column, uniqueKey, uniqueValue).thenApply(boolString -> {
			if (boolString == null) return false;
			if (boolString.equals("1")) return true;
			if (boolString.equals("0")) return false;

			return false;
		});
	}

	// endregion

	// region Has

	public CompletableFuture<Boolean> hasValue(String table, String column) {
		return this.hasValue(table, column, "uuid", this.uuid.toString());
	}

	public CompletableFuture<Boolean> hasValue(String table, String column, String uniqueKey, Object uniqueValue) {
		return this.get(table, column, uniqueKey, uniqueValue).thenApply(Objects::nonNull);
	}

	@Deprecated
	public CompletableFuture<Boolean> hasValueAsync(String table, String column, String uniqueKey, Object uniqueValue) {
		return this.get(table, column, uniqueKey, uniqueValue).thenApply(Objects::nonNull);
	}

	// endregion

	// region Insert

	public CompletableFuture<Boolean> insert(String table, String uniqueKey, Object uniqueValue) {
		return this.insert(table, new String[]{uniqueKey}, new Object[]{uniqueValue});
	}

	public CompletableFuture<Boolean> insert(String table, String[] columns, Object[] values) {
		if (table == null) return null;
		if (columns.length != values.length) return null;

		return CompletableFuture.supplyAsync(() -> {
			if (StorageManager.isMemory()) {
				MemoryCacheManager memoryCacheManager = new MemoryCacheManager();
				MemoryCacheEntry cacheEntry = memoryCacheManager.get(this.getUUIDString() + "-" + table);
				if (cacheEntry != null) {
					for (int i = 0; i < columns.length; i++) {
						cacheEntry.updateColumn(columns[i], values[i].toString());
					}
				}
			}

			if (StorageManager.isRedis()) {
				RedisCacheManager redisCacheManager = new RedisCacheManager();

				Map<String, String> keyValues = new HashMap<>();

				for (int i = 0; i < columns.length; i++) {
					keyValues.put(columns[i], values[i].toString());
				}

				try (ShardedJedis redis = redisCacheManager.getResource()) {
					redis.hmset(this.getUUIDString() + "-" + table, keyValues);
				}
			}

			if (StorageManager.isMySQL()) {
				assert StorageManager.getMySQLDatabase() != null : "Insert command failed.";
				return StorageManager.getMySQLDatabase().insert(table, columns, values);
			}

			assert StorageManager.getSQLiteDatabase() != null : "Insert command failed.";
			return StorageManager.getSQLiteDatabase().insert(table, columns, values);
		});
	}

	// endregion

	// region InsertComplex

	public CompletableFuture<Boolean> insert(String table, String[] columns, Object[] values, String[] uniqueKeys, Object[] uniqueValues) {
		if (uniqueValues == null) return null;
		if (uniqueKeys == null) return null;
		if (columns.length != values.length) return null;
		if (uniqueKeys.length != uniqueValues.length) return null;

		return CompletableFuture.supplyAsync(() -> {
			String uniqueKey = generateKey(table, uniqueValues);

			if (StorageManager.isMemory()) {
				MemoryCacheManager memoryCacheManager = new MemoryCacheManager();
				MemoryCacheEntry cacheEntry = memoryCacheManager.get(uniqueKey);
				if (cacheEntry != null) {
					for (int i = 0; i < columns.length; i++) {
						cacheEntry.updateColumn(columns[i], values[i].toString());
					}
				}
			}

			if (StorageManager.isRedis()) {
				RedisCacheManager redisCacheManager = new RedisCacheManager();
				try (ShardedJedis redis = redisCacheManager.getResource()) {
					Map<String, String> keyValues = new HashMap<>();

					for (int i = 0; i < columns.length; i++) {
						keyValues.put(columns[i], values[i].toString());
					}

					redis.hmset(uniqueKey, keyValues);
				}
			}

			if (StorageManager.isMySQL()) {
				assert StorageManager.getMySQLDatabase() != null : "Insert command failed.";
				return StorageManager.getMySQLDatabase().insert(table, columns, values);
			}

			assert StorageManager.getSQLiteDatabase() != null : "Insert command failed.";
			return StorageManager.getSQLiteDatabase().insert(table, columns, values);
		});
	}

	// endregion

	// region Delete

	public CompletableFuture<Boolean> delete(String table, String[] primaryKeys, Object[] primaryValues) {
		if (table == null || primaryValues == null) return null;
		if (primaryKeys.length != primaryValues.length) return null;

		String uniqueKey = generateKey(table, primaryValues);

		if (StorageManager.isMemory()) {
			MemoryCacheManager memoryCacheManager = new MemoryCacheManager();
			MemoryCacheEntry cacheEntry = memoryCacheManager.get(uniqueKey);
			if (cacheEntry != null) {
				memoryCacheManager.remove(cacheEntry);
			}
		}

		if (StorageManager.isRedis()) {
			RedisCacheManager redisCacheManager = new RedisCacheManager();
			try (ShardedJedis redis = redisCacheManager.getResource()) {
				redis.del(uniqueKey);
			}
		}

		return CompletableFuture.supplyAsync(() -> {
			if (StorageManager.isMySQL()) {
				assert StorageManager.getMySQLDatabase() != null : "Delete command failed.";
				return StorageManager.getMySQLDatabase().deleteConditional(table, primaryKeys, primaryValues);
			}

			assert StorageManager.getSQLiteDatabase() != null : "Delete command failed.";
			return StorageManager.getSQLiteDatabase().deleteConditional(table, primaryKeys, primaryValues);
		});
	}

	public CompletableFuture<Boolean> deleteList(String table, String[] columns, String uniqueKey, Object uniqueValue) {
		if (table == null || columns == null || uniqueKey == null || uniqueValue == null) return null;

		return CompletableFuture.supplyAsync(() -> {
			String uniqueKeyDatabase = uniqueValue.toString() + "-list-" + table;

			if (StorageManager.isMemory()) {
				final MemoryCacheManager memoryCacheManager = new MemoryCacheManager();
				MemoryCacheEntry memoryCacheEntry = memoryCacheManager.get(uniqueKeyDatabase);

				if (memoryCacheEntry != null) {
					memoryCacheManager.remove(memoryCacheEntry);
				}
			}

			if (StorageManager.isRedis()) {
				RedisCacheManager redisCacheManager = new RedisCacheManager();
				try (ShardedJedis jedis = redisCacheManager.getResource()) {
					jedis.del(uniqueKeyDatabase);
				}
			}

			return true;
		});
	}

	// endregion

	// region Placement

	public CompletableFuture<Integer> getPlacement(String table, String column) {
		return getPlacement(table, column, "uuid", this.getUUIDString());
	}

	public CompletableFuture<Integer> getPlacement(String table, String column, String uniqueKey, Object uniqueValue) {
		if (table == null || column == null || uniqueKey == null || uniqueValue == null) return null;

		return CompletableFuture.supplyAsync(() -> {
			if (StorageManager.isMySQL()) {
				assert StorageManager.getMySQLDatabase() != null : "Placement player command failed.";
				return StorageManager.getMySQLDatabase().placement(table, column, uniqueKey, uniqueValue.toString());
			} else {
				assert StorageManager.getSQLiteDatabase() != null : "Placement player command failed.";
				return StorageManager.getSQLiteDatabase().placement(table, column, uniqueKey, uniqueValue.toString());
			}
		});
	}

	public CompletableFuture<Integer> getPlacement(String table, String column, String uniqueKey, Object uniqueValue, String whereKey, Object whereValue) {
		return CompletableFuture.supplyAsync(() -> {
			if (StorageManager.isMySQL()) {
				assert StorageManager.getMySQLDatabase() != null : "Placement player command failed.";
				return StorageManager.getMySQLDatabase().placement(table, column, uniqueKey, uniqueValue.toString(), whereKey, whereValue.toString());
			} else {
				assert StorageManager.getSQLiteDatabase() != null : "Placement player command failed.";
				return StorageManager.getSQLiteDatabase().placement(table, column, uniqueKey, uniqueValue.toString(), whereKey, whereValue.toString());
			}
		});
	}

	public CompletableFuture<Map<String, Integer>> getPlacement(String table, String column, int topCount) {
		return getPlacement(table, column, "name", topCount);
	}

	public CompletableFuture<Map<String, Integer>> getPlacement(String table, String column, String uniqueKey, int topCount) {
		return getPlacement(table, column, uniqueKey, column, topCount);
	}

	public CompletableFuture<Map<String, Integer>> getPlacement(String table, String column, String uniqueKey, String orderBy, int topCount) {
		return getPlacement(table, column, uniqueKey, orderBy, "DESC", topCount);
	}

	public CompletableFuture<Map<String, Integer>> getPlacement(String table, String column, String uniqueKey, String orderBy, String orderByType, int topCount) {
		if (table == null || column == null || uniqueKey == null || orderBy == null || topCount == 0) return null;

		return CompletableFuture.supplyAsync(() -> {
			ResultSet resultSet;

			if (StorageManager.isMySQL()) {
				assert StorageManager.getMySQLDatabase() != null : "Placement command failed.";
				resultSet = StorageManager.getMySQLDatabase().placement(table, column, uniqueKey, orderBy, orderByType, topCount);
			} else {
				assert StorageManager.getSQLiteDatabase() != null : "Placement command failed.";
				resultSet = StorageManager.getSQLiteDatabase().placement(table, column, uniqueKey, orderBy, orderByType, topCount);
			}

			try {
				HashMap<String, Integer> placements = new HashMap<>();
				while (resultSet.next()) {
					String playerName = resultSet.getString(uniqueKey);
					int rank = resultSet.getInt(column);
					placements.put(playerName, rank);
				}

				return placements.entrySet().stream()
					.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
					.limit(topCount)
					.collect(Collectors.toMap(
						Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
			} catch (SQLException e) {
				e.printStackTrace();
			}

			return null;
		});
	}

	public CompletableFuture<Map<String, Integer>> getPlacement(String table, String column, String uniqueKey, String orderBy, String orderByType, String whereKey, String whereValue, int topCount) {
		if (table == null || column == null || uniqueKey == null || orderBy == null || orderByType == null || whereKey == null || whereValue == null || topCount == 0) return null;

		return CompletableFuture.supplyAsync(() -> {
			ResultSet resultSet;

			if (StorageManager.isMySQL()) {
				assert StorageManager.getMySQLDatabase() != null : "Placement command failed.";
				resultSet = StorageManager.getMySQLDatabase().placement(table, column, uniqueKey, orderBy, orderByType, whereKey, whereValue, topCount);
			} else {
				assert StorageManager.getSQLiteDatabase() != null : "Placement command failed.";
				resultSet = StorageManager.getSQLiteDatabase().placement(table, column, uniqueKey, orderBy, orderByType, whereKey, whereValue, topCount);
			}

			try {
				HashMap<String, Integer> placements = new HashMap<>();
				while (resultSet.next()) {
					String playerName = resultSet.getString(uniqueKey);
					int rank = resultSet.getInt(column);
					placements.put(playerName, rank);
				}

				return placements.entrySet().stream()
					.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
					.limit(topCount)
					.collect(Collectors.toMap(
						Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
			} catch (SQLException e) {
				e.printStackTrace();
			}

			return null;
		});
	}

	// endregion

	private String generateKey(String table, Object[] keyValues) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < keyValues.length; i++) {
			stringBuilder.append(keyValues[i].toString());
			if (i != (keyValues.length - 1)) {
				stringBuilder.append("-");
			}
		}

		stringBuilder.append("-").append(table);
		return stringBuilder.toString();
	}

	private void createMemoryCacheEntry(String uniqueKeyDatabase, Map<String, String> data) {
		if (!StorageManager.isMemory()) return;

		MemoryCacheManager memoryCacheManager = new MemoryCacheManager();

		if (memoryCacheManager.getSize() >= Main.CORE_STORAGE.getInt("storage.cache.memory.limit")) return;

		MemoryCacheEntryBuilder memoryCacheEntryBuilder = new MemoryCacheEntryBuilder();
		memoryCacheEntryBuilder.setUniqueKey(uniqueKeyDatabase);
		memoryCacheEntryBuilder.setExpireDuration(Main.CORE_STORAGE.getInt("storage.cache.memory.expiration.duration"), TimeUnit.SECONDS);

		data.forEach(memoryCacheEntryBuilder::addColumn);

		new MemoryCacheManager().add(memoryCacheEntryBuilder.build());
	}

	// region Getter and Setter

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public UUID getUUID() {
		return uuid;
	}

	public String getUUIDString() {
		if (uuid == null) {
			return "00000000-0000-0000-0000-000000000000";
		}

		return uuid.toString();
	}

	// endregion

}