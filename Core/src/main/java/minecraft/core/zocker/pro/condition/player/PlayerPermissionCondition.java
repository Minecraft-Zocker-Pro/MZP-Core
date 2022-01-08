package minecraft.core.zocker.pro.condition.player;

import minecraft.core.zocker.pro.Zocker;

public class PlayerPermissionCondition extends PlayerCondition {

	@Override
	public String getId() {
		return "1c75980a-027e-415d-9d6e-275cb7594c21";
	}

	@Override
	public String getName() {
		return "Permission";
	}

	@Override
	public String getDisplay() {
		return "Permission";
	}

	@Override
	public boolean onCheck(Zocker zocker, Object value) {
		return zocker.getPlayer().hasPermission(value.toString());
	}

	@Override
	public String getCurrentValue(Zocker zocker, Object value) {
		if (zocker.getPlayer().hasPermission(value.toString())) return "Yes";
		return "No";
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
