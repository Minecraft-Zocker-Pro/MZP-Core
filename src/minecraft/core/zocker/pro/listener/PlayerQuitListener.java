package minecraft.core.zocker.pro.listener;

import minecraft.core.zocker.pro.Zocker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent e) {
		Zocker.getZocker(e.getPlayer().getUniqueId()).set("player", "online", 0);
		Zocker.removeZocker(e.getPlayer().getUniqueId());
	}
}
