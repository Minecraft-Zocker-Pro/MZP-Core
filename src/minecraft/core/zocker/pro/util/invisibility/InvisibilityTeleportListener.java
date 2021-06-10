package minecraft.core.zocker.pro.util.invisibility;

import minecraft.core.zocker.pro.Main;
import minecraft.core.zocker.pro.compatibility.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class InvisibilityTeleportListener implements Listener {

	private final int visibleDistance = Bukkit.getServer().getViewDistance() * 16;
	private final InvisibilityManager invisibilityManager = new InvisibilityManager();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (event.getCause() == PlayerTeleportEvent.TeleportCause.UNKNOWN) return;
		if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9)) {
			if (event.getCause() == PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT) return;
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				final List<Player> playersWithinList = invisibilityManager.getPlayersWithin(event.getPlayer(), visibleDistance);
				invisibilityManager.updateEntityList(playersWithinList, false);

				new BukkitRunnable() {
					@Override
					public void run() {
						invisibilityManager.updateEntityList(playersWithinList, true);
					}
				}.runTaskLater(Main.getPlugin(), 1L);
			}
		}.runTaskLater(Main.getPlugin(), Main.CORE_CONFIG.getInt("core.invisibility.fix.delay"));
	}
}
