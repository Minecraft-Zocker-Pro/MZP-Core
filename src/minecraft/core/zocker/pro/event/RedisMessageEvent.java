package minecraft.core.zocker.pro.event;

import minecraft.core.zocker.pro.storage.cache.redis.RedisPacket;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.json.JSONObject;

public class RedisMessageEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final String serverTargetName;
	private final String receiverName;
	private final String senderName;
	private final String pluginName;

	private final JSONObject packet;

	public RedisMessageEvent(RedisPacket redisPacket, JSONObject packet, boolean isAsync) {
		super(isAsync);

		this.serverTargetName = redisPacket.getServerTargetName();
		this.receiverName = redisPacket.getReceiverName();
		this.senderName = redisPacket.getSenderName();
		this.pluginName = redisPacket.getPluginName();

		this.packet = packet;
	}

	public String getServerTargetName() {
		return serverTargetName;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public String getSenderName() {
		return senderName;
	}

	public String getPluginName() {
		return pluginName;
	}

	public JSONObject getPacket() {
		return packet;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
