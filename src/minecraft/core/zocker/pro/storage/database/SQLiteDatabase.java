package minecraft.core.zocker.pro.storage.database;

import minecraft.core.zocker.pro.CorePlugin;
import minecraft.core.zocker.pro.Main;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteDatabase extends DatabaseHelper implements DatabaseInterface {

	private static Connection connection;

	public SQLiteDatabase() {
		try {
			// This is required to put here for Spigot 1.10 and below to force class load
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connect(String host, int port, String database, String username, String password) {
		try {
			if (connection != null) {
				if (!connection.isClosed()) return;
			}

			CorePlugin corePlugin = Main.getPlugin();

			String path = corePlugin.getDataFolder() + File.separator + "storage" + File.separator + "database" + File.separator;
			File databaseDirectory = new File(path);

			if (!databaseDirectory.exists()) {
				databaseDirectory.mkdirs();
			}

			connection = DriverManager.getConnection("jdbc:sqlite:" + path + corePlugin.getPluginName() + ".db");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void disconnect() {
		if (connection == null) return;
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Connection getConnection() {
		return connection;
	}
}
