package minecraft.core.zocker.pro;

import minecraft.core.zocker.pro.storage.StorageManager;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class OfflineZocker extends Zocker {

	public OfflineZocker(Player player) {
		super(player);
	}

	public OfflineZocker(UUID uuid) {
		super(uuid);
	}

	public static UUID fetchUUID(String playerName) {
		ResultSet resultSet;

		if (StorageManager.isMySQL()) {
			assert StorageManager.getMySQLDatabase() != null : "Select command failed.";
			resultSet = StorageManager.getMySQLDatabase().select("player", "uuid", "name", playerName);
		} else {
			assert StorageManager.getSQLiteDatabase() != null : "Select command failed.";
			resultSet = StorageManager.getSQLiteDatabase().select("player", "uuid", "name", playerName);
		}

		try {
			if (resultSet.next()) {
				String uuid = resultSet.getString("uuid");
				resultSet.close();

				if (uuid == null) throw new NullPointerException();

				return UUID.fromString(uuid);
			}

			resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static CompletableFuture<UUID> fetchUUIDAsync(String playerName) {
		return CompletableFuture.supplyAsync(() -> OfflineZocker.fetchUUID(playerName));
	}

	public String getName() {
		return OfflineZocker.getName(this.getUUID());
	}

	public CompletableFuture<String> getNameAsync() {
		return CompletableFuture.supplyAsync(this::getName);
	}

	public static String getName(UUID uuid) {
		ResultSet resultSet;

		if (StorageManager.isMySQL()) {
			assert StorageManager.getMySQLDatabase() != null : "Select command failed.";
			resultSet = StorageManager.getMySQLDatabase().select("player", "name", "uuid", uuid.toString());
		} else {
			assert StorageManager.getSQLiteDatabase() != null : "Select command failed.";
			resultSet = StorageManager.getSQLiteDatabase().select("player", "name", "name", uuid.toString());
		}

		try {
			if (resultSet.next()) {
				String name = resultSet.getString("name");
				resultSet.close();

				if (name == null) throw new NullPointerException();

				return name;
			}

			resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static CompletableFuture<String> getNameAsync(UUID uuid) {
		return CompletableFuture.supplyAsync(() -> OfflineZocker.getName(uuid));
	}
}
