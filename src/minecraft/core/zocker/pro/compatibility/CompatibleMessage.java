package minecraft.core.zocker.pro.compatibility;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public enum CompatibleMessage {

	MESSAGE;

	public void send(Player player, String message) {
		switch (this) {
			case MESSAGE: {
				if (ServerVersion.isServerVersionAtOrBelow(ServerVersion.V1_8)) {
					player.sendMessage(message);
				} else {
					player.sendMessage(TextComponent.fromLegacyText(message));
				}
			}
		}
	}

	public static void sendMessage(Player player, String message) {
		if (ServerVersion.isServerVersionAtOrBelow(ServerVersion.V1_8)) {
			player.sendMessage(message);
		} else {
			player.sendMessage(TextComponent.fromLegacyText(message));
		}
	}

	public static void sendMessage(Player player, BaseComponent[] baseComponents) {
		if (ServerVersion.isServerVersionAtOrBelow(ServerVersion.V1_8)) {
			player.spigot().sendMessage(baseComponents);
		} else {
			player.sendMessage(baseComponents);
		}
	}
}
