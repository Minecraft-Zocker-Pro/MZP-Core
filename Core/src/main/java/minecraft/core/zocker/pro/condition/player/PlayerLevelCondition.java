package minecraft.core.zocker.pro.condition.player;

import minecraft.core.zocker.pro.Zocker;

public class PlayerLevelCondition extends PlayerCondition {

	@Override
	public String getId() {
		return "f00ff709-c89c-4e7a-9bfd-4b574b407fe6";
	}

	@Override
	public String getName() {
		return "Level";
	}

	@Override
	public String getDisplay() {
		return "Level";
	}

	@Override
	public boolean onCheck(Zocker zocker, Object value) {
		return zocker.getPlayer().getLevel() >= Integer.parseInt(value.toString());
	}

	@Override
	public String getCurrentValue(Zocker zocker, Object value) {
		return String.valueOf(zocker.getPlayer().getLevel());
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
