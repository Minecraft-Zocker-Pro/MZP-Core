package minecraft.core.zocker.pro.storage.cache.redis;

public enum RedisPacketIdentifyType {

	// remove the player cache to get the newest data from the database
	CORE_CACHE_CLEAN_PLAYER,

	// remove all cached player data
	CORE_CACHE_CLEAN_SERVER,

	// Server
	SERVER_MESSAGE_CHAT,
	SERVER_MESSAGE_TITLE,
	SERVER_MESSAGE_ACTION_BAR,
	SERVER_COMMAND,
	SERVER_COMMAND_PLAYER, // console command but replace the target with the player
	SERVER_SOUND,

	// Player
	PLAYER_MESSAGE_CHAT,
	PLAYER_MESSAGE_TITLE,
	PLAYER_MESSAGE_ACTION_BAR,
	PLAYER_COMMAND,
	PLAYER_SOUND;
}
