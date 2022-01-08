package minecraft.core.zocker.pro.condition;

import minecraft.core.zocker.pro.condition.player.PlayerLevelCondition;
import minecraft.core.zocker.pro.condition.player.PlayerMoneyCondition;
import minecraft.core.zocker.pro.condition.player.PlayerPermissionCondition;
import minecraft.core.zocker.pro.hook.HookManager;

import java.util.ArrayList;
import java.util.List;

public class ConditionManager {

	private static final List<Condition> conditionList = new ArrayList<>();

	public static void loadAll() {
		if (new HookManager().isLoaded("Vault")) {
			register(new PlayerMoneyCondition());
		}

		register(new PlayerPermissionCondition());
		register(new PlayerLevelCondition());
	}

	public static void register(Condition condition) {
		if (conditionList.contains(condition)) return;
		conditionList.add(condition);

		System.out.println("Condition: " + condition.getName() +" loaded.");
	}

	public static void unregister(Condition condition) {
		conditionList.remove(condition);
	}

	public static List<Condition> getConditionList() {
		return conditionList;
	}
}
