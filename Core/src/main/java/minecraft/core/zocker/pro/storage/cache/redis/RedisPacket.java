package minecraft.core.zocker.pro.storage.cache.redis;

import minecraft.core.zocker.pro.storage.StorageManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class RedisPacket {

	public abstract String getServerTargetName();

	public abstract String getSenderName();

	public abstract String getPluginName();

	public abstract String getReceiverName();

	public abstract JSONArray getPackets();

	public String toJSON() {
		String senderName;
		if (getSenderName() == null) {
			senderName = StorageManager.getServerName();
		} else {
			senderName = getSenderName();
		}

		try {
			JSONObject object = new JSONObject();
			object.put("sender", senderName); // Which server send the packet
			object.put("plugin", getPluginName()); // Which plugin send the packet
			object.put("target", getServerTargetName()); // Which server should receive the packet
			object.put("receiver", getReceiverName()); // Which plugin should take the packet
			object.put("packets", getPackets());

			return object.toString();
		} catch (
			JSONException e) {
			e.printStackTrace();
		}

		return null;
	}
}
