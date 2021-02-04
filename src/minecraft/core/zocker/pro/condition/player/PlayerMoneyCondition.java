package minecraft.core.zocker.pro.condition.player;

import minecraft.core.zocker.pro.Zocker;
import minecraft.core.zocker.pro.hook.HookManager;

public class PlayerMoneyCondition extends PlayerCondition {

	@Override
	public String getId() {
		return "f4aed3c4-a0ff-4e0b-846a-5f53855b9e6b";
	}

	@Override
	public String getName() {
		return "Money";
	}

	@Override
	public String getDisplay() {
		return "Money";
	}

	@Override
	public boolean onCheck(Zocker zocker, Object value) {
		return new HookManager().getEconomy().has(zocker.getPlayer(), Double.parseDouble(value.toString()));
	}

	@Override
	public String getCurrentValue(Zocker zocker, Object value) {
		return String.valueOf(new HookManager().getEconomy().getBalance(zocker.getPlayer()));
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
