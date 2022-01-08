package minecraft.core.zocker.pro.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ItemSerializer {

	/**
	 * A method to serialize an {@link ItemStack} list to Base64 String.
	 *
	 * @param items to turn into a Base64 String.
	 * @return Base64 string of the items.
	 */
	public static String serialize(List<ItemStack> items) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

			// Write the size of the inventory
			dataOutput.writeInt(items.size());

			// Save every element in the list
			for (ItemStack item : items)
				dataOutput.writeObject(item);

			// Serialize that array
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String serialize(ConcurrentMap<Integer, ItemStack> items) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

		dataOutput.writeInt(items.size());

		for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
			dataOutput.writeInt(entry.getKey());
			dataOutput.writeObject(entry.getValue());
		}

		dataOutput.close();
		return Base64.getEncoder().encodeToString(outputStream.toByteArray());
	}

	/**
	 * Gets a list of ItemStacks from Base64 string.
	 *
	 * @param data Base64 string to convert to ItemStack list.
	 * @return ItemStack array created from the Base64 string.
	 */
	public static List<ItemStack> deserialize(String data) {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			int length = dataInput.readInt();
			List<ItemStack> items = new ArrayList<>();

			// Read the serialized itemstack list
			for (int i = 0; i < length; i++)
				items.add((ItemStack) dataInput.readObject());

			dataInput.close();
			return items;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static ConcurrentMap<Integer, ItemStack> deserialize(Object data) throws IOException {
		ConcurrentMap<Integer, ItemStack> items = new ConcurrentHashMap<>();

		byte[] bytes = Base64.getMimeDecoder().decode(data.toString());
		ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
		BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

		int mapSize = dataInput.readInt();
		int pos;
		ItemStack item;

		for (int i = 0; i < mapSize; i++) {
			pos = dataInput.readInt();
			try {
				item = (ItemStack) dataInput.readObject();
			} catch (ClassNotFoundException e) {
				throw new IOException("cannot found ItemStack class during deserialization", e);
			}

			items.put(pos, item);
		}

		dataInput.close();

		return items;
	}
}