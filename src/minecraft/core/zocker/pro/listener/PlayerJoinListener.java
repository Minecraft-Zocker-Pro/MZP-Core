package minecraft.core.zocker.pro.listener;

import minecraft.core.zocker.pro.Zocker;
import minecraft.core.zocker.pro.event.ZockerDataInitializeEvent;
import minecraft.core.zocker.pro.network.NetworkServerManager;
import minecraft.core.zocker.pro.storage.StorageManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

	private final NetworkServerManager networkServerManager;

	public PlayerJoinListener() {
		this.networkServerManager = new NetworkServerManager();
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Zocker zocker = Zocker.getZocker(e.getPlayer().getUniqueId());
		if (zocker != null) return;

		zocker = new Zocker(e.getPlayer());
		Zocker finalZocker = zocker;

		finalZocker.hasValue("player", "uuid", "uuid", finalZocker.getPlayer().getUniqueId().toString()).thenApplyAsync(aBoolean -> {
			System.out.println("join result : " + aBoolean);
			if (aBoolean) {
				if (networkServerManager.isProxyEnabled()) {
					Bukkit.getPluginManager().callEvent(new ZockerDataInitializeEvent(finalZocker));
					return true;
				}

				finalZocker.set("player",
					new String[]{"name", "server", "online"},
					new Object[]{finalZocker.getPlayer().getName(), StorageManager.getServerName(), 1});
				Bukkit.getPluginManager().callEvent(new ZockerDataInitializeEvent(finalZocker));
				return true;
			}

			if (networkServerManager.isProxyEnabled()) {
				finalZocker.insert(
					"player",
					new String[]{"uuid", "name", "online"},
					new Object[]{finalZocker.getPlayer().getUniqueId().toString(), finalZocker.getPlayer().getName(), 1});

				Bukkit.getPluginManager().callEvent(new ZockerDataInitializeEvent(finalZocker));
				return true;
			}

			finalZocker.insert(
				"player",
				new String[]{"uuid", "name", "server", "online"},
				new Object[]{finalZocker.getPlayer().getUniqueId().toString(), finalZocker.getPlayer().getName(), StorageManager.getServerName(), 1});

			Bukkit.getPluginManager().callEvent(new ZockerDataInitializeEvent(finalZocker));
			return true;
		});
	}
}
