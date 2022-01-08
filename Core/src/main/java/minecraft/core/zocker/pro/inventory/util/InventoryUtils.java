package minecraft.core.zocker.pro.inventory.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;

public class InventoryUtils {

	/**
	 * Can carry boolean.
	 *
	 * @param player  the player
	 * @param content the content
	 * @return the boolean
	 */
	public static Boolean canCarry(Player player, ItemStack[] content) {
		Inventory fakeInventory = Bukkit.createInventory(player, player.getInventory().getSize());
		fakeInventory.setContents(player.getInventory().getContents().clone());

		return canCarry(fakeInventory, content);
	}

	/**
	 * Can carry boolean.
	 *
	 * @param player  the player
	 * @param content the content
	 * @return the boolean
	 */
	public static Boolean canCarry(Player player, ItemStack content) {
		Inventory fakeInventory = Bukkit.createInventory(player, player.getInventory().getSize());
		fakeInventory.setContents(player.getInventory().getContents().clone());

		return canCarry(fakeInventory, content);
	}

	/**
	 * Can carry boolean.
	 *
	 * @param inventory the inventory
	 * @param content   the content
	 * @return the boolean
	 */
	public static Boolean canCarry(Inventory inventory, ItemStack[] content) {
		Inventory fakeInventory = Bukkit.createInventory(null, inventory.getSize());
		fakeInventory.setContents(inventory.getContents().clone());

		content = Arrays.stream(content).filter((itemStack -> itemStack != null && itemStack.getType() != Material.AIR)).toArray(ItemStack[]::new);

		for (ItemStack itemStack : content) {
			if (canCarry(fakeInventory, itemStack)) {
				fakeInventory.addItem(itemStack);
			} else {
				return false;
			}
		}

		return true;
	}

	/**
	 * Can carry boolean.
	 *
	 * @param inventory the inventory
	 * @param content   the content
	 * @return the boolean
	 */
	public static Boolean canCarry(Inventory inventory, ItemStack content) {
		Inventory fakeInventory = Bukkit.createInventory(null, inventory.getSize());
		fakeInventory.setContents(inventory.getContents().clone());

		if (content == null && content.getType() == Material.AIR) return true;

		HashMap<Integer, ItemStack> toDrop = fakeInventory.addItem(content);
		return toDrop.size() == 0;
	}

	/**
	 * Remove invalid item stack [ ].
	 *
	 * @param content the content
	 * @return the item stack [ ]
	 */
	public static ItemStack[] removeInvalid(ItemStack[] content) {
		return Arrays.stream(content).filter((itemStack -> itemStack != null && itemStack.getType() != Material.AIR)).toArray(ItemStack[]::new);
	}
}
