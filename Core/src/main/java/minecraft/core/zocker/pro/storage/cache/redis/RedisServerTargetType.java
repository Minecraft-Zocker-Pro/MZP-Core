package minecraft.core.zocker.pro.storage.cache.redis;

public enum RedisServerTargetType {

	PROXY_ALL("ProxyCore"),
	SERVER_ALL("Core");

	private final String name;

	RedisServerTargetType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
