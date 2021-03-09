package minecraft.core.zocker.pro.util;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Cooldown {

	private static final HashMap<UUID, List<Cooldown>> COOLDOWN = new HashMap<>();

	private long endTime;

	private String identify;
	private ItemStack itemStack;

	public Cooldown(UUID uuid, TimeUnit timeUnit, long cooldown) {
		this.endTime = System.currentTimeMillis() + timeUnit.toMillis(cooldown);

		List<Cooldown> cooldowns = COOLDOWN.get(uuid);
		if (cooldowns == null) {
			cooldowns = new ArrayList<>();
		}

		cooldowns.add(this);

		COOLDOWN.put(uuid, cooldowns);
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public boolean isElapsed() {
		return this.endTime - System.currentTimeMillis() <= 0;
	}

	public double getLeftTime() {
		return (this.endTime - System.currentTimeMillis()) / 1000.0;
	}

	public static List<Cooldown> getCooldown(UUID uuid) {
		return COOLDOWN.get(uuid);
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	public String getIdentify() {
		return identify;
	}

	public void setIdentify(String identify) {
		this.identify = identify;
	}
}
