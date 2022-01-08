package minecraft.core.zocker.pro.event;

import minecraft.core.zocker.pro.Zocker;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ZockerDataInitializeEvent extends Event {

	private static final HandlerList HANDLER_LIST = new HandlerList();

	private final Zocker zocker;

	public ZockerDataInitializeEvent(Zocker zocker) {
		super(true);
		this.zocker = zocker;
	}

	public Zocker getZocker() {
		return zocker;
	}

	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}
}
