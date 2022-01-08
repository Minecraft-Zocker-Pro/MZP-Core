package minecraft.core.zocker.pro.command;

import com.google.common.collect.Lists;
import minecraft.core.zocker.pro.Main;
import minecraft.core.zocker.pro.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public abstract class Command extends org.bukkit.command.Command implements TabExecutor, CommandExecutor {

	public Command(String name) {
		this(name, null, Lists.newArrayList());
	}

	public Command(String name, String... aliases) {
		this(name, null, Lists.newArrayList(aliases));
	}

	public Command(String name, List<String> aliases) {
		this(name, null, aliases);
	}

	public Command(String name, String permission, List<String> aliases) {
		super(name, null, null, aliases);
		super.setPermission(permission);
	}

	public Command(String name, String permission, String[] aliases) {
		super(name, null, null, Arrays.asList(aliases));
		super.setPermission(permission);
	}

	@Override
	public boolean execute(CommandSender commandSender, String s, String[] strings) {
		return true;
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		if (getPermission() != null) {
			if (!sender.hasPermission(getPermission())) {
				Config message = Config.getConfig("message.yml", "MZP-Core");
				if (message == null) throw new NullPointerException("Message.yml file not found.");

				sender.sendMessage(message.getString("message.prefix") + message.getString("message.command.permission.deny"));
				return true;
			}
		}

		try {
			Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
				onExecute(sender, args);
			});

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		if (getPermission() != null) {
			if (!sender.hasPermission(getPermission())) {
				return null;
			}
		}

		List<String> tabCompletion = onTabComplete(sender, args);

		if (tabCompletion == null) {
			if (args.length == 0) {
				List<String> list = Lists.newArrayList();
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					list.add(p.getName());
				}
				return list;
			} else {
				String lastWord = args[args.length - 1];
				List<String> list = Lists.newArrayList();

				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					if (p.getName().toLowerCase().startsWith(lastWord.toLowerCase())) {
						list.add(p.getName());
					}
				}

				return list;
			}
		}
		return tabCompletion;
	}

	public abstract List<String> onTabComplete(CommandSender sender, String[] args);

	public abstract void onExecute(CommandSender sender, String[] args);
}
