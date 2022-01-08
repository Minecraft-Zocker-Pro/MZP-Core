package minecraft.core.zocker.pro.storage.cache.memory;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public abstract class MemoryCacheEntry {

	public abstract long getExpirationDuration();

	public abstract void setExpirationDuration(long duration, TimeUnit timeUnit);

	public abstract String getUniqueKey();

	public abstract ConcurrentMap<String, Object> getColumns();

	public abstract boolean isExpiringOnQuit();

	public boolean addColumn(String column, Object value) {
		if (column == null || value == null) return false;

		getColumns().put(column, value);
		return true;
	}

	public boolean updateColumn(String column, Object value) {
		if (column == null || value == null) return false;
//		if (!getColumns().containsKey(column)) return false;

		getColumns().put(column, value);

		return true;
	}

	public boolean removeColumn(String column) {
		if (column == null) return false;

		if (getColumns().containsKey(column)) {
			getColumns().remove(column);
			return true;
		}

		return false;
	}

}
