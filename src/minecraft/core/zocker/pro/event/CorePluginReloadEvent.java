package minecraft.core.zocker.pro.event;

import minecraft.core.zocker.pro.CorePlugin;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CorePluginReloadEvent extends Event {

	private static final HandlerList HANDLER_LIST = new HandlerList();

	private CorePlugin plugin;

	public CorePluginReloadEvent(CorePlugin plugin) {
		super(false);
		this.plugin = plugin;
	}

	public CorePlugin getPlugin() {
		return plugin;
	}

	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}
}
