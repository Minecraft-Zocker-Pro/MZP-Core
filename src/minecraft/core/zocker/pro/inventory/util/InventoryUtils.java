package minecraft.core.zocker.pro.inventory.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

	/**
	 * Player inventory to base 64 string [ ].
	 *
	 * @param playerInventory the player inventory
	 * @return the string [ ]
	 * @throws IllegalStateException the illegal state exception
	 */
	public static String[] playerInventoryToBase64(PlayerInventory playerInventory) throws IllegalStateException {
		//getTaskByID the main content part, this doesn't return the armor
		String content = toBase64(playerInventory);
		String armor = itemStackArrayToBase64(playerInventory.getArmorContents());

		return new String[]{content, armor};
	}

	/**
	 * Item stack array to base 64 string.
	 *
	 * @param items the items
	 * @return the string
	 * @throws IllegalStateException the illegal state exception
	 */
	public static String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

			// Write the size of the inventory
			dataOutput.writeInt(items.length);

			// Save every element in the list
			for (ItemStack item : items) {
				dataOutput.writeObject(item);
			}

			// Serialize that array
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e) {
			throw new IllegalStateException("Unable to save item stacks.", e);
		}
	}

	/**
	 * Item stack to base 64 string.
	 *
	 * @param item the item
	 * @return the string
	 * @throws IllegalStateException the illegal state exception
	 */
	public static String itemStackToBase64(ItemStack item) throws IllegalStateException {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

			// Save every element in the list
			dataOutput.writeObject(item);

			// Serialize that array
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e) {
			throw new IllegalStateException("Unable to save item stacks.", e);
		}
	}

	/**
	 * To base 64 string.
	 *
	 * @param inventory the inventory
	 * @return the string
	 * @throws IllegalStateException the illegal state exception
	 */
	public static String toBase64(Inventory inventory) throws IllegalStateException {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

			// Write the size of the inventory
			dataOutput.writeInt(inventory.getSize());

			// Save every element in the list
			for (int i = 0; i < inventory.getSize(); i++) {
				dataOutput.writeObject(inventory.getItem(i));
			}

			// Serialize that array
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e) {
			throw new IllegalStateException("Unable to save item stacks.", e);
		}
	}

	/**
	 * From base 64 inventory.
	 *
	 * @param data the data
	 * @return the inventory
	 * @throws IOException the io exception
	 */
	public static Inventory fromBase64(String data) throws IOException {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			Inventory inventory = Bukkit.getServer().createInventory(null, dataInput.readInt());

			// Read the serialized inventory
			for (int i = 0; i < inventory.getSize(); i++) {
				inventory.setItem(i, (ItemStack) dataInput.readObject());
			}

			dataInput.close();
			return inventory;
		} catch (ClassNotFoundException e) {
			throw new IOException("Kan deze code niet omzetten; ", e);
		}
	}

	/**
	 * Item stack array from base 64 item stack [ ].
	 *
	 * @param data the data
	 * @return the item stack [ ]
	 */
	public static ItemStack[] itemStackArrayFromBase64(String data) {
		if (data.equals("") || data.isEmpty()) {
			return new ItemStack[0];
		}

		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			ItemStack[] items = new ItemStack[dataInput.readInt()];

			// Read the serialized inventory
			for (int i = 0; i < items.length; i++) {
				items[i] = (ItemStack) dataInput.readObject();
			}

			dataInput.close();
			return items;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return new ItemStack[0];
		}
	}

	/**
	 * Item stack from base 64 item stack.
	 *
	 * @param data the data
	 * @return the item stack
	 */
	public static ItemStack itemStackFromBase64(String data) {
		if (data.equals("") || data.isEmpty()) {
			return new ItemStack(Material.AIR);
		}

		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			ItemStack items;

			// Read the serialized inventory
			items = (ItemStack) dataInput.readObject();

			dataInput.close();
			return items;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return new ItemStack(Material.AIR);
		}
	}
}
