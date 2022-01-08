package minecraft.core.zocker.pro.hook;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;

public class HookManager {

	private final static ArrayList<String> REGISTERED_HOOKS = new ArrayList<>();

	public boolean load(String pluginName) {
		if (isLoaded(pluginName)) return true;

		if (Bukkit.getPluginManager().isPluginEnabled(pluginName)) {
			REGISTERED_HOOKS.add(pluginName.toLowerCase());
			return true;
		}

		return false;
	}

	public boolean unload(String pluginName) {
		if (!isLoaded(pluginName)) return true;

		REGISTERED_HOOKS.remove(pluginName);
		return true;
	}

	public boolean isLoaded(String pluginName) {
		return REGISTERED_HOOKS.contains(pluginName.toLowerCase());
	}

	public static ArrayList<String> getRegisteredHooks() {
		return REGISTERED_HOOKS;
	}

	// region Economy
	
	public Economy getEconomy() {
		if (isLoaded("Vault")) {
			RegisteredServiceProvider<Economy> registration = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
			if (registration == null) return null;

			return registration.getProvider();
		}

		if (load("Vault")) {
			RegisteredServiceProvider<Economy> registration = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
			if (registration == null) return null;

			return registration.getProvider();
		}

		return null;
	}

	public boolean hasEconomy() {
		return this.isLoaded("Vault");
	}
	
	// endregion
}