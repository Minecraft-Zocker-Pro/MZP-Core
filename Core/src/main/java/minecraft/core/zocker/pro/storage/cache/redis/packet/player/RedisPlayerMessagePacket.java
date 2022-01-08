package minecraft.core.zocker.pro.storage.cache.redis.packet.player;

import minecraft.core.zocker.pro.storage.cache.redis.RedisPacketAbstract;
import minecraft.core.zocker.pro.storage.cache.redis.RedisPacketIdentifyType;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class RedisPlayerMessagePacket extends RedisPacketAbstract {

	private final String message;
	private final UUID uuid;
	private final RedisPacketIdentifyType identifyType;

	// Title
	private String subTitle;
	private int fadeIn = 10;
	private int fadeOut = 10;
	private int stay = 25;

	public RedisPlayerMessagePacket(UUID uuid, String message, RedisPacketIdentifyType identifyType) {
		this.uuid = uuid;
		this.message = message;
		this.identifyType = identifyType;
	}


	public RedisPlayerMessagePacket(UUID uuid, String title, String subTitle) {
		this.uuid = uuid;
		this.message = title;
		this.subTitle = subTitle;
		this.identifyType = RedisPacketIdentifyType.PLAYER_MESSAGE_TITLE;
	}

	public RedisPlayerMessagePacket(UUID uuid, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
		this.uuid = uuid;
		this.message = title;
		this.subTitle = subTitle;
		this.fadeIn = fadeIn;
		this.fadeOut = fadeOut;
		this.stay = stay;
		this.identifyType = RedisPacketIdentifyType.PLAYER_MESSAGE_TITLE;
	}

	@Override
	public String getIdentify() {
		return this.identifyType.name().toUpperCase();
	}

	@Override
	public JSONObject toJSON() {
		try {
			switch (identifyType) {
				case PLAYER_MESSAGE_CHAT: {
					return new JSONObject()
						.put("identify", this.identifyType.name().toUpperCase())
						.put("uuid", this.uuid.toString())
						.put("message", this.message);

				}

				case PLAYER_MESSAGE_ACTION_BAR: {
					return new JSONObject()
						.put("identify", this.identifyType.name().toUpperCase())
						.put("uuid", this.uuid.toString())
						.put("message", this.message);
				}

				case PLAYER_MESSAGE_TITLE: {
					return new JSONObject()
						.put("identify", this.identifyType.name().toUpperCase())
						.put("uuid", this.uuid.toString())
						.put("title", this.message)
						.put("subTitle", this.subTitle)
						.put("fadeIn", this.fadeIn)
						.put("stay", this.stay)
						.put("fadeOut", this.fadeOut);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public String getMessage() {
		return message;
	}
}
