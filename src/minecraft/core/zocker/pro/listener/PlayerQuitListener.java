package minecraft.core.zocker.pro.listener;

import minecraft.core.zocker.pro.Zocker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

	// TODO remove zocker after x time to prevent storage read transaction
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Zocker.removeZocker(e.getPlayer().getUniqueId());
	}

}
