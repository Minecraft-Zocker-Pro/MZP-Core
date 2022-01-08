package minecraft.core.zocker.pro.util.invisibility;

import minecraft.core.zocker.pro.Main;
import minecraft.core.zocker.pro.compatibility.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class InvisibilityManager {

	public void updateEntityList(List<Player> players, boolean visible) {
		// Hide every player
		for (Player observer : players) {
			for (Player player : players) {
				if (observer.getEntityId() != player.getEntityId()) {
					if (visible) {
						if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_12)) {
							observer.showPlayer(Main.getPlugin(), player);
						} else {
							observer.showPlayer(player);
						}
					} else if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_12)) {
						observer.hidePlayer(Main.getPlugin(), player);
					} else {
						observer.hidePlayer(player);
					}
				}
			}
		}
	}

	public List<Player> getPlayersWithin(Player player, int distance) {
		List<Player> res = new ArrayList<>();
		int d2 = distance * distance;

		for (Player playerOnline : Bukkit.getServer().getOnlinePlayers()) {
			if (playerOnline == null) continue;
			if (playerOnline.getWorld() == player.getWorld() && playerOnline.getLocation().distanceSquared(player.getLocation()) <= d2) {
				res.add(playerOnline);
			}
		}

		return res;
	}
}
