package minecraft.core.zocker.pro;

import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
import org.bukkit.Material;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashMap;

public abstract class CorePlugin extends JavaPlugin {

	private static final HashMap<String, CorePlugin> PLUGINS = new HashMap<>();

	private Material displayItem;
	private PluginDescriptionFile pluginDescriptionFile;
	private String helpCommand;
	private String pluginName;

	@Override
	public void onEnable() {
		super.onEnable();

		if (PLUGINS.containsKey(this.getName())) return;

		this.pluginDescriptionFile = getDescription();
		this.setDisplayItem(CompatibleMaterial.OAK_SIGN.getMaterial());
		this.setPluginName("MZP-Core");
		PLUGINS.put(this.getName(), this);
	}

	public void setDisplayItem(Material material) {
		this.displayItem = material;
	}

	public Material getDisplayItem() {
		return displayItem;
	}

	public void setHelpCommand(String helpCommand) {
		this.helpCommand = helpCommand;
	}

	public String getHelpCommand() {
		return helpCommand;
	}

	public String getPluginName() {
		return pluginName;
	}

	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	public void registerCommand() {
	}

	public void registerListener() {
	}

	public void buildConfig() {
	}

	public void reload() {
	}

	public PluginDescriptionFile getPluginDescriptionFile() {
		return pluginDescriptionFile;
	}

	public static Collection<CorePlugin> getPlugins() {
		return PLUGINS.values();
	}

	public static CorePlugin getPlugin(String name) {
		if (PLUGINS.containsKey(name)) {
			return PLUGINS.get(name);
		}

		return null;
	}

}
