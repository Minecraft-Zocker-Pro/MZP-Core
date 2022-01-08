package minecraft.core.zocker.pro.storage.cache.redis.packet.player;

import minecraft.core.zocker.pro.storage.cache.redis.RedisPacketAbstract;
import minecraft.core.zocker.pro.storage.cache.redis.RedisPacketIdentifyType;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class RedisPlayerCommandPacket extends RedisPacketAbstract {

	private final UUID uuid;
	private final String command;

	public RedisPlayerCommandPacket(UUID uuid, String command) {
		this.uuid = uuid;
		this.command = command;
	}

	@Override
	public String getIdentify() {
		return RedisPacketIdentifyType.PLAYER_COMMAND.name().toUpperCase();
	}

	@Override
	public JSONObject toJSON() {
		try {
			return new JSONObject()
				.put("identify", getIdentify())
				.put("uuid", this.uuid.toString())
				.put("command", this.command);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}
}
