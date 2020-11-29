package minecraft.core.zocker.pro;

import minecraft.core.zocker.pro.storage.StorageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

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

		return CompletableFuture.supplyAsync(() -> {
			if (StorageManager.isRedis()) {
				return null;
			} else {
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
						return value;
					}
				} catch (SQLException e) {
					e.printStackTrace();
					return null;
				}

				return null;
			}
		});
	}

	// endregion

	// region GetList

	public CompletableFuture<List<String>> getList(String table, String[] primaryKeys) {
		return this.getList(table, primaryKeys, "uuid", this.uuid);
	}

	public CompletableFuture<List<String>> getList(String table, String[] primaryKeys, String uniqueKey, Object uniqueValue) {
		if (table == null || primaryKeys == null) return null;

		return CompletableFuture.supplyAsync(() -> {
			try {
				ResultSet result;
				if (StorageManager.isMySQL()) {
					assert StorageManager.getMySQLDatabase() != null : "Select list command failed";
					result = StorageManager.getMySQLDatabase().select(table, primaryKeys, uniqueKey, uniqueValue.toString());
				} else {
					assert StorageManager.getSQLiteDatabase() != null : "Select list command failed.";
					result = StorageManager.getSQLiteDatabase().select(table, primaryKeys, uniqueKey, uniqueValue.toString());
				}
				if (result == null) return null;

				List<String> data = new ArrayList<>();

				while (result.next()) {
					String primaryKey = result.getString(primaryKeys[1]);
					if (primaryKey == null) continue;

					data.add(primaryKey);
				}

				result.close();

				return data;
			} catch (SQLException e) {
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

		return CompletableFuture.supplyAsync(() -> {
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
					Map<String, String> hmsetData = new HashMap<>();
					for (String column : columns) {
						String value = result.getString(column);
						if (value == null) continue;

						hmsetData.put(column, value);
					}

					result.close();

					return hmsetData;
				}
			} catch (SQLException e) {
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
			if (StorageManager.isMySQL()) {
				assert StorageManager.getMySQLDatabase() != null : "Update command failed.";
				return StorageManager.getMySQLDatabase().update(table, columns, values, uniqueKey, uniqueValue.toString());
			} else {
				assert StorageManager.getSQLiteDatabase() != null : "Update command failed.";
				return StorageManager.getSQLiteDatabase().update(table, columns, values, uniqueKey, uniqueValue.toString());
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
	public boolean isValue(String table, String column) {
		return this.isValue(table, column, "uuid", this.uuid.toString());
	}

	/**
	 * @param table       SQL table
	 * @param column      The unique column for the required value
	 * @param uniqueKey   The unique key e.x the player uuid
	 * @param uniqueValue The unique value of the key e.x the player uuid
	 * @return boolean
	 */
	public boolean isValue(String table, String column, String uniqueKey, Object uniqueValue) {
		CompletableFuture<String> stringCompletableFuture = this.get(table, column, uniqueKey, uniqueValue);
		try {
			String boolString = stringCompletableFuture.get();

			if (boolString == null) return false;
			if (boolString.equals("1")) return true;
			if (boolString.equals("0")) return false;

			return false;
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * @param table       SQL table
	 * @param column      The unique column for the required value
	 * @param uniqueKey   The unique key e.x the player uuid
	 * @param uniqueValue The unique value of the key e.x the player uuid
	 * @return boolean
	 */
	public CompletableFuture<Boolean> isValueAsync(String table, String column, String uniqueKey, Object uniqueValue) {
		return CompletableFuture.supplyAsync(() -> {
			CompletableFuture completableFuture = this.get(table, column, uniqueKey, uniqueValue).thenApply(boolString -> {
				if (boolString == null) return false;
				if (boolString.equals("1")) return true;
				if (boolString.equals("0")) return false;

				return false;
			});

			try {
				return (Boolean) completableFuture.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}

			return null;
		});
	}

	// endregion

	// region Has

	public boolean hasValue(String table, String column) {
		return this.hasValue(table, column, "uuid", this.uuid.toString());
	}

	public boolean hasValue(String table, String column, String uniqueKey, Object uniqueValue) {
		CompletableFuture<String> stringCompletableFuture = this.get(table, column, uniqueKey, uniqueValue);
		try {
			String data = stringCompletableFuture.get();

			if (data == null) return false;
			return true;
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		return false;
	}

	public CompletableFuture<Boolean> hasValueAsync(String table, String column, String uniqueKey, Object uniqueValue) {
		return CompletableFuture.supplyAsync(() -> {
			CompletableFuture completableFuture = this.get(table, column, uniqueKey, uniqueValue).thenApply(data -> {
				if (data == null) return false;
				return true;
			});

			try {
				return (Boolean) completableFuture.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}

			return null;
		});
	}

	// endregion

	// region Insert

	public CompletableFuture<Boolean> insert(String table, String uniqueKey, Object uniqueValue) {
		if (table == null || uniqueKey == null) return null;

		return CompletableFuture.supplyAsync(() -> {
			if (StorageManager.isMySQL()) {
				assert StorageManager.getMySQLDatabase() != null : "Insert command failed.";
				return StorageManager.getMySQLDatabase().insert(table, new String[]{uniqueKey}, new Object[]{uniqueValue});
			}

			assert StorageManager.getSQLiteDatabase() != null : "Insert command failed.";
			return StorageManager.getSQLiteDatabase().insert(table, new String[]{uniqueKey}, new Object[]{uniqueValue});
		});
	}

	public CompletableFuture<Boolean> insert(String table, String[] primaryKeys, Object[] primaryValues) {
		if (table == null) return null;
		if (primaryKeys.length != primaryValues.length) return null;

		return CompletableFuture.supplyAsync(() -> {
			if (StorageManager.isMySQL()) {
				assert StorageManager.getMySQLDatabase() != null : "Insert command failed.";
				return StorageManager.getMySQLDatabase().insert(table, primaryKeys, primaryValues);
			}

			assert StorageManager.getSQLiteDatabase() != null : "Insert command failed.";
			return StorageManager.getSQLiteDatabase().insert(table, primaryKeys, primaryValues);
		});
	}

	// endregion

	// region InsertComplex

	public CompletableFuture<Boolean> insert(String table, String[] columns, Object[] values, String[] primaryKeys, Object[] primaryValues, Object uniqueValue) {
		if (primaryValues == null) return null;
		if (primaryKeys == null) return null;
		if (columns.length != values.length) return null;
		if (primaryKeys.length != primaryValues.length) return null;

		return CompletableFuture.supplyAsync(() -> {
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
		return this.delete(table, primaryKeys, primaryValues, this.uuid.toString());
	}

	public CompletableFuture<Boolean> delete(String table, String[] primaryKeys, Object[] primaryValues, String uniqueValue) {
		if (table == null || primaryValues == null) return null;
		if (primaryKeys.length != primaryValues.length) return null;

		return CompletableFuture.supplyAsync(() -> {
			if (StorageManager.isMySQL()) {
				assert StorageManager.getMySQLDatabase() != null : "Delete command failed.";
				return StorageManager.getMySQLDatabase().deleteConditional(table, primaryKeys, primaryValues);
			}

			assert StorageManager.getSQLiteDatabase() != null : "Delete command failed.";
			return StorageManager.getSQLiteDatabase().deleteConditional(table, primaryKeys, primaryValues);
		});
	}

	// endregion

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
		return uuid.toString();
	}

	// endregion

}
