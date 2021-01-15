package minecraft.core.zocker.pro.storage.cache.redis.packet.server;

import minecraft.core.zocker.pro.storage.cache.redis.RedisPacketAbstract;
import minecraft.core.zocker.pro.storage.cache.redis.RedisPacketIdentifyType;
import org.json.JSONException;
import org.json.JSONObject;

public class RedisServerMessagePacket extends RedisPacketAbstract {

	private final String message;
	private final String identify;

	// Title
	private String subTitle;
	private int fadeIn = 10;
	private int fadeOut = 10;
	private int stay = 25;

	public RedisServerMessagePacket(String message, RedisPacketIdentifyType identifyType) {
		this.message = message;
		this.identify = identifyType.name().toUpperCase();
	}

	public RedisServerMessagePacket(String title, String subTitle, RedisPacketIdentifyType identifyType) {
		this.message = title;
		this.subTitle = subTitle;
		this.identify = identifyType.name().toUpperCase();
	}

	public RedisServerMessagePacket(String title, String subTitle, int fadeIn, int fadeOut, int stay, RedisPacketIdentifyType identifyType) {
		this.message = title;
		this.subTitle = subTitle;
		this.fadeIn = fadeIn;
		this.fadeOut = fadeOut;
		this.stay = stay;
		this.identify = identifyType.name().toUpperCase();
	}

	@Override
	public String getIdentify() {
		return identify;
	}

	@Override
	public JSONObject toJSON() {
		try {
			RedisPacketIdentifyType identifyType = RedisPacketIdentifyType.valueOf(this.identify);

			switch (identifyType) {
				case SERVER_MESSAGE_CHAT: {
					return new JSONObject()
						.put("identify", this.identify)
						.put("message", this.message);
				}

				case SERVER_MESSAGE_ACTION_BAR: {
					return new JSONObject()
						.put("identify", this.identify)
						.put("message", this.message);
				}

				case SERVER_MESSAGE_TITLE: {
					return new JSONObject()
						.put("identify", this.identify)
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


