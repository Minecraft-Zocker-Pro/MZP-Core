package minecraft.core.zocker.pro.storage.cache.redis.packet;

import minecraft.core.zocker.pro.storage.cache.redis.RedisPacketAbstract;
import minecraft.core.zocker.pro.storage.cache.redis.RedisPacketIdentifyType;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class RedisCorePacket extends RedisPacketAbstract {

	private final RedisPacketIdentifyType identify;
	private UUID uuid;

	public RedisCorePacket(RedisPacketIdentifyType identify) {
		this.identify = identify;
	}

	public RedisCorePacket(UUID uuid, RedisPacketIdentifyType identify) {
		this.uuid = uuid;
		this.identify = identify;
	}

	@Override
	public String getIdentify() {
		return identify.toString().toUpperCase();
	}

	@Override
	public JSONObject toJSON() {
		try {
			switch (identify) {
				case CORE_CACHE_CLEAN_PLAYER: {
					return new JSONObject()
						.put("identify", this.identify)
						.put("uuid", this.uuid.toString());
				}

				case CORE_CACHE_CLEAN_SERVER: {
					return new JSONObject()
						.put("identify", this.identify);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}
}
