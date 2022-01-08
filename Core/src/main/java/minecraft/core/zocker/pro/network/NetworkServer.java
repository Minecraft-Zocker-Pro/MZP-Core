package minecraft.core.zocker.pro.network;

public class NetworkServer {

	private final String name;
	private final String host;
	private final int port;
	private final int online;
	private final int slot;
	private final String motd;
	private final long lastUpdate;
	private final boolean enabled;

	public NetworkServer(String name, String host, int port, int online, int slot, String motd, long lastUpdate, boolean enabled) {
		this.name = name;
		this.host = host;
		this.port = port;
		this.online = online;
		this.slot = slot;
		this.motd = motd;
		this.lastUpdate = lastUpdate;
		this.enabled = enabled;
	}

	public String getName() {
		return name;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public int getOnline() {
		return online;
	}

	public int getSlot() {
		return slot;
	}

	public String getMotd() {
		return motd;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public boolean isEnabled() {
		return enabled;
	}
}
