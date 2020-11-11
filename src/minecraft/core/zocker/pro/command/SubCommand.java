package minecraft.core.zocker.pro.command;

import minecraft.core.zocker.pro.Main;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public abstract class SubCommand {

	private String name;
	private int expectedArgs;
	private int maximumArgs;

	public SubCommand(String name) {
		this(name, -1, -1);
	}

	public SubCommand(String name, int expectedArgs) {
		this(name, expectedArgs, expectedArgs);
	}

	public SubCommand(String name, int expectedArgs, int maximumArgs) {
		this.name = name;
		this.expectedArgs = expectedArgs;
		this.maximumArgs = maximumArgs;
	}

	public abstract String getUsage();

	public abstract String getPermission();

	public abstract void onExecute(CommandSender sender, String[] args);

	private ConditionResult checkConditions(CommandSender sender, String[] args) {
		if (!args[0].equalsIgnoreCase(name)) {
			return ConditionResult.FAILURE_WRONG_NAME;
		}

		if (expectedArgs != -1 && maximumArgs != -1) {
			if ((args.length - 1) != expectedArgs) {
				if ((args.length - 1) > maximumArgs) {
					return ConditionResult.FAILURE_WRONG_ARGS_LENGTH;
				}
			}
		}

		if (getPermission() == null) {
			return ConditionResult.SUCCESS;
		}

		if (!getPermission().isEmpty() && !sender.hasPermission(getPermission())) {
			return ConditionResult.FAILURE_PERMISSION;
		}

		return ConditionResult.SUCCESS;
	}

	public boolean execute(CommandSender sender, String[] args) {
		ConditionResult result = checkConditions(sender, args);

		switch (result) {
			case SUCCESS: {
				onExecute(sender, Arrays.copyOfRange(args, 1, args.length));
				return true;
			}
			case FAILURE_PERMISSION: {
				if (Main.CORE_MESSAGE == null) throw new NullPointerException("Message.yml file not found.");
				sender.sendMessage(Main.CORE_MESSAGE.getString("message.prefix") + Main.CORE_MESSAGE.getString("message.command.permission.deny"));
				return true;
			}
			case FAILURE_WRONG_NAME: {
				if (Main.CORE_MESSAGE == null) throw new NullPointerException("Message.yml file not found.");
				sender.sendMessage(Main.CORE_MESSAGE.getString("message.prefix") + Main.CORE_MESSAGE.getString("message.command.sub.wrong"));
				return true;
			}
			case FAILURE_WRONG_ARGS_LENGTH: {
				if (Main.CORE_MESSAGE == null) throw new NullPointerException("Message.yml file not found.");
				sender.sendMessage(Main.CORE_MESSAGE.getString("message.prefix") + Main.CORE_MESSAGE.getString("message.command.arg.length"));
				return true;
			}
		}

		return false;
	}

	public abstract List<String> getCompletions(CommandSender sender, String[] args);

	public enum ConditionResult {

		FAILURE_PERMISSION, FAILURE_WRONG_NAME, FAILURE_WRONG_ARGS_LENGTH, SUCCESS

	}

	public String getName() {
		return this.name;
	}
}
