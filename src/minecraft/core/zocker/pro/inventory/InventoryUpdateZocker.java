package minecraft.core.zocker.pro.inventory;

import java.util.concurrent.TimeUnit;

public abstract class InventoryUpdateZocker extends InventoryZocker {

	private TimeUnit updateTimeUnit = TimeUnit.SECONDS;
	private int updateOffset = 1;
	private boolean update = true;
	private boolean clearBefore = false;

	public abstract void onUpdate();

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}

	public TimeUnit getUpdateTimeUnit() {
		return updateTimeUnit;
	}

	public void setUpdateTimeUnit(TimeUnit timeUnit) {
		this.updateTimeUnit = timeUnit;
	}

	public int getUpdateOffset() {
		return updateOffset;
	}

	public boolean isClearBefore() {
		return clearBefore;
	}

	public void setClearBefore(boolean clearBefore) {
		this.clearBefore = clearBefore;
	}

	public void setUpdateOffset(int offset) {
		this.updateOffset = offset;
	}
}
