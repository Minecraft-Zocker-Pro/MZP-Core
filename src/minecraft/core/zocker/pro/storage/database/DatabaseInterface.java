package minecraft.core.zocker.pro.storage.database;

import java.sql.Connection;

public interface DatabaseInterface {
	
	public void connect(String host, int port, String database, String username, String password);
	
	public void disconnect();
	
	public Connection getConnection();
	
}
