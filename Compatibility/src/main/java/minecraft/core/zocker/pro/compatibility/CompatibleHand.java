package minecraft.core.zocker.pro.compatibility;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/***
 * Original source from https://github.com/songoda/SongodaCore
 *Get which hand is being used.
 */

public enum CompatibleHand {

	MAIN_HAND, OFF_HAND;

	private static Map<String, Method> methodCache = new HashMap<>();

	public static CompatibleHand getHand(Object event) {
		try {
			Class<?> clazz = event.getClass();
			String className = clazz.getName();
			Method method;
			if (methodCache.containsKey(className)) {
				method = methodCache.get(className);
			} else {
				method = clazz.getDeclaredMethod("getHand");
				methodCache.put(className, method);
			}
			EquipmentSlot slot = (EquipmentSlot) method.invoke(event);
			if (slot == EquipmentSlot.OFF_HAND) return OFF_HAND;
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
		}
		return MAIN_HAND;
	}

	public static CompatibleHand getHand(EquipmentSlot equipmentSlot) {
		return equipmentSlot == EquipmentSlot.HAND ? MAIN_HAND : OFF_HAND;
	}

	/**
	 * Use up whatever item the player is holding in their main hand
	 *
	 * @param player player to grab item from
	 */
	public void takeItem(Player player) {
		takeItem(player, 1);
	}

	/**
	 * Use up whatever item the player is holding in their main hand
	 *
	 * @param player player to grab item from
	 * @param amount number of items to use up
	 */
	public void takeItem(Player player, int amount) {
		ItemStack item = this == CompatibleHand.MAIN_HAND
			? player.getInventory().getItemInHand() : player.getInventory().getItemInOffHand();

		int result = item.getAmount() - amount;
		item.setAmount(result);

		if (this == CompatibleHand.MAIN_HAND)
			player.setItemInHand(result > 0 ? item : null);
		else
			player.getInventory().setItemInOffHand(result > 0 ? item : null);
	}

	/**
	 * Get item in the selected hand
	 *
	 * @param player the player to get the item from
	 * @return the item
	 */
	public ItemStack getItem(Player player) {
		if (this == MAIN_HAND)
			return player.getItemInHand();
		else
			return player.getInventory().getItemInOffHand();
	}

	/**
	 * Set the item in the selected hand
	 *
	 * @param player the player to set the item of
	 * @param item   the item to set
	 */
	public void setItem(Player player, ItemStack item) {
		if (this == MAIN_HAND)
			player.setItemInHand(item);
		else
			player.getInventory().setItemInOffHand(item);
	}

	private static Class<?> cb_CraftPlayer;
	private static Method getHandle, playBreak, asNMSCopy;

	/**
	 * Damage the selected item
	 *
	 * @param player the player who's item you want to damage
	 * @param damage the amount of damage to apply to the item
	 */
	@Deprecated
	public void damageItem(Player player, short damage) {
		throw new UnsupportedOperationException();
	}
	
//	public void damageItem(Player player, short damage) {
//		if (player.getGameMode() == GameMode.CREATIVE) return;
//
//		if (cb_CraftPlayer == null) {
//			try {
//				cb_CraftPlayer = NMS.getCraftClass("entity.CraftPlayer");
//				Class<?> mc_EntityLiving = NMS.getNMSClass("EntityLiving");
//				Class<?> cb_ItemStack = NMS.getCraftClass("inventory.CraftItemStack");
//				Class<?> mc_ItemStack = NMS.getNMSClass("ItemStack");
//				getHandle = cb_CraftPlayer.getMethod("getHandle");
//				if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13))
//					playBreak = mc_EntityLiving.getDeclaredMethod("a", mc_ItemStack, int.class); //Consistent from 1.16-1.13
//				else
//					playBreak = mc_EntityLiving.getDeclaredMethod("b", mc_ItemStack); //Consistent from 1.12-1.8
//				playBreak.setAccessible(true);
//				asNMSCopy = cb_ItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class);
//			} catch (NoSuchMethodException e) {
//				e.printStackTrace();
//			}
//		}
//
//		ItemStack item = getItem(player);
//
//		short newDurability = (short) (item.getDurability() + damage);
//
//		if (newDurability >= item.getType().getMaxDurability()) {
//			PlayerItemBreakEvent breakEvent = new PlayerItemBreakEvent(player, item);
//			Bukkit.getServer().getPluginManager().callEvent(breakEvent);
//			try {
//				if (playBreak.getParameterCount() == 2)
//					playBreak.invoke(getHandle.invoke(cb_CraftPlayer.cast(player)), asNMSCopy.invoke(null, item), 1);
//				else
//					playBreak.invoke(getHandle.invoke(cb_CraftPlayer.cast(player)), asNMSCopy.invoke(item));
//			} catch (IllegalAccessException | InvocationTargetException e) {
//				e.printStackTrace();
//			}
//			setItem(player, null);
//			return;
//		}
//
//		item.setDurability(newDurability);
//	}
}
