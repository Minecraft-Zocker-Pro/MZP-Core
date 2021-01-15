package minecraft.core.zocker.pro.storage.cache.redis.packet.server;

import minecraft.core.zocker.pro.storage.cache.redis.RedisPacketAbstract;
import minecraft.core.zocker.pro.storage.cache.redis.RedisPacketIdentifyType;
import org.json.JSONException;
import org.json.JSONObject;

public class RedisServerCommandPacket extends RedisPacketAbstract {

	private final String command;
	private String playerTargetName;
	private final RedisPacketIdentifyType identifyType;

	public RedisServerCommandPacket(String command) {
		this.command = command;
		this.identifyType = RedisPacketIdentifyType.SERVER_COMMAND;
	}

	public RedisServerCommandPacket(String command, String playerTargetName) {
		this.command = command;
		this.playerTargetName = playerTargetName;
		this.identifyType = RedisPacketIdentifyType.SERVER_COMMAND_PLAYER;
	}

	@Override
	public String getIdentify() {
		return this.identifyType.name().toUpperCase();
	}

	@Override
	public JSONObject toJSON() {
		try {
			switch (this.identifyType) {
				case SERVER_COMMAND: {
					return new JSONObject()
						.put("identify", this.identifyType.name().toUpperCase())
						.put("command", this.command);
				}

				case SERVER_COMMAND_PLAYER: {
					return new JSONObject()
						.put("identify", this.identifyType.name().toUpperCase())
						.put("command", this.command.replace("%target%", this.playerTargetName))
						.put("target", this.playerTargetName);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}
}
