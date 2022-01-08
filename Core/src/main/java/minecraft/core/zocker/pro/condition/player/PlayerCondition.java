package minecraft.core.zocker.pro.condition.player;

import minecraft.core.zocker.pro.Zocker;
import minecraft.core.zocker.pro.condition.Condition;

public abstract class PlayerCondition extends Condition {

	public abstract boolean onCheck(Zocker zocker, Object value);

	public abstract String getCurrentValue(Zocker zocker, Object value);

}
