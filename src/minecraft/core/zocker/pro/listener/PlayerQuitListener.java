package minecraft.core.zocker.pro.listener;

import minecraft.core.zocker.pro.Zocker;
import minecraft.core.zocker.pro.util.Cooldown;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.UUID;

public class PlayerQuitListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent e) {
		UUID uuid = e.getPlayer().getUniqueId();
		
		Zocker.getZocker(uuid).set("player", "online", 0);
		Zocker.removeZocker(uuid);

		List<Cooldown> cooldowns = Cooldown.getCooldown(uuid);
		if (cooldowns == null) return;

		cooldowns.removeIf(Cooldown::isElapsed);
	}
}
