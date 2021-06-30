package minecraft.core.zocker.pro.event;

import org.bukkit.event.Cancellable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerVoidFallEvent extends Event implements Cancellable {

	private static final HandlerList HANDLER_LIST = new HandlerList();

	private boolean isCancelled;
	private final Player player;

	public PlayerVoidFallEvent(Player player) {
		super(true);
		this.player = player;
	}

	public boolean isCancelled() {
		return isCancelled;
	}

	public void setCancelled(boolean cancelled) {
		isCancelled = cancelled;
	}

	public Player getPlayer() {
		return player;
	}

	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}
}
