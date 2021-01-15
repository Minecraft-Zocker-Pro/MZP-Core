package minecraft.core.zocker.pro.storage.cache.memory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class MemoryCacheEntryBuilder {

	private String uniqueKey;
	private final ConcurrentMap<String, Object> columns = new ConcurrentHashMap<>();

	private boolean expiringOnQuit = true;
	private long expireDuration;

	public MemoryCacheEntryBuilder setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
		return this;
	}

	public MemoryCacheEntryBuilder addColumn(String column, Object value) {
		columns.put(column, value);
		return this;
	}

	public MemoryCacheEntryBuilder setExpiringOnQuit(boolean expiringOnQuit) {
		this.expiringOnQuit = expiringOnQuit;
		return this;
	}

	public MemoryCacheEntryBuilder setExpireDuration(long duration, TimeUnit timeUnit) {
		if (duration == -1) return this;
		this.expireDuration = System.currentTimeMillis() + (TimeUnit.MILLISECONDS.convert(duration, timeUnit));
		return this;
	}

	public MemoryCacheEntry build() {
		return new MemoryCacheEntry() {
			@Override
			public String getUniqueKey() {
				return uniqueKey;
			}

			@Override
			public ConcurrentMap<String, Object> getColumns() {
				return columns;
			}

			@Override
			public boolean isExpiringOnQuit() {
				return expiringOnQuit;
			}

			@Override
			public void setExpirationDuration(long duration, TimeUnit timeUnit) {
				setExpireDuration(duration, timeUnit);
			}

			@Override
			public long getExpirationDuration() {
				return expireDuration;
			}
		};
	}
}
