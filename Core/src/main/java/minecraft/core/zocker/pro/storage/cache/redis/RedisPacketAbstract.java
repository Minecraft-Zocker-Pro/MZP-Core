package minecraft.core.zocker.pro.storage.cache.redis;

import org.json.JSONObject;

public abstract class RedisPacketAbstract {

	public abstract String getIdentify();

	public abstract JSONObject toJSON();
}
