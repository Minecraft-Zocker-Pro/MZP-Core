package minecraft.core.zocker.pro;

import minecraft.core.zocker.pro.config.Config;
import org.bukkit.plugin.Plugin;
import org.bukkit.Material;

public class Main extends CorePlugin {

	private static Plugin PLUGIN;

	@Override
	public void onEnable() {
		super.onEnable();
		PLUGIN = this;

		this.setDisplayItem(Material.SIGN);
		this.setHelpCommand("core");

		this.buildConfig();
		this.registerCommand();
		this.registerListener();
	}

	@Override
	public void onDisable() {
	}


	@Override
	public void registerCommand() {
	}

	@Override
	public void registerListener() {
	}

	@Override
	public void buildConfig() {
		// Config		
		Config config = new Config("core.yml", "MZP-Core");

		if (!config.getBool("core.setup")) {

			config.set("core.setup", true);
			config.set("core.version", "0.0.1");
			config.save();
		}

		// Message
		Config message = new Config("message.yml", "MZP-Core");

		if (!message.getBool("message.setup")) {

			message.set("message.prefix", "&6&l[MZP] ");
			message.set("message.permission.deny", "&3You dont have permission!");
			message.set("message.command.permission.deny", "&3You dont have permission to execute this command!");
			message.set("message.command.sub.wrong", "&3Wrong sub command for this command!");
			message.set("message.command.arg.length", "&3Too many or to less arguments for this command!");

			message.set("message.setup", true);
			message.set("message.version", "0.0.1");
			message.save();
		}

	}

	@Override
	public void reload() {
		Config config = Config.getConfig("config.yml", "MZP-Core");
		config.reload();

		Config message = Config.getConfig("message.yml", "MZP-Core");
		message.reload();
		
	}

	public static Plugin getPlugin() {
		return PLUGIN;
	}

}
