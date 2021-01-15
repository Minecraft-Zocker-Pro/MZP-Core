package minecraft.core.zocker.pro.storage.cache.redis;

import org.json.JSONArray;

public class RedisPacketBuilder {

	private String serverTargetName, receiverName, senderName, pluginName;
	private final JSONArray packets = new JSONArray();

	public RedisPacketBuilder setServerTargetName(String serverTargetName) {
		this.serverTargetName = "MZP-" + serverTargetName;
		return this;
	}

	public RedisPacketBuilder setReceiverName(String receiverName) {
		this.receiverName = receiverName;
		return this;
	}

	public RedisPacketBuilder setSenderName(String senderName) {
		this.senderName = senderName;
		return this;
	}

	public RedisPacketBuilder setPluginName(String pluginName) {
		this.pluginName = pluginName;
		return this;
	}

	public RedisPacketBuilder addPacket(RedisPacketAbstract packet) {
		packets.put(packet.toJSON());
		return this;
	}

	public RedisPacketBuilder addPacket(String packet) {
		packets.put(packet);
		return this;
	}

	public RedisPacket build() {
		return new RedisPacket() {

			@Override
			public String getServerTargetName() {
				return serverTargetName;
			}

			@Override
			public String getSenderName() {
				return senderName;
			}

			@Override
			public String getPluginName() {
				return pluginName;
			}

			@Override
			public String getReceiverName() {
				return receiverName;
			}

			@Override
			public JSONArray getPackets() {
				return packets;
			}
		};
	}
}
