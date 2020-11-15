package minecraft.core.zocker.pro.storage.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLDatabase extends DatabaseHelper implements DatabaseInterface {

	private static Connection connection;

	@Override
	public void connect(String host, int port, String database, String username, String password) {
		try {
			if (connection != null) {
				if (!connection.isClosed()) return;
			}

			connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", username, password);

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
