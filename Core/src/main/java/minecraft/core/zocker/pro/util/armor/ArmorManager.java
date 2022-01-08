package minecraft.core.zocker.pro.util.armor;

import minecraft.core.zocker.pro.compatibility.ServerVersion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ArmorManager {

	public boolean remove(Player player) {
		PlayerInventory playerInventory = player.getInventory();

		playerInventory.setHelmet(null);
		playerInventory.setChestplate(null);
		playerInventory.setLeggings(null);
		playerInventory.setBoots(null);

		if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9)) {
			player.getInventory().setItemInOffHand(null);
		}

		return true;
	}

	public boolean equip(Player player) {
		return this.equip(player, Arrays.asList(player.getInventory().getContents()));
	}

	public boolean equip(Player player, ItemStack itemStack) {
		return this.equip(player, Collections.singletonList(itemStack));
	}

	public boolean equip(Player player, List<ItemStack> itemStackList) {
		if (player == null || !player.isOnline() || itemStackList == null || itemStackList.isEmpty()) return false;

		for (ItemStack itemStack : itemStackList) {
			if (itemStack == null || itemStack.getType() == Material.AIR) continue;

			for (ArmorType armorType : ArmorType.values()) {
				if (itemStack.getType() == armorType.getItemStack().getType()) {
					this.equip(player, itemStack, armorType);
					break;
				}
			}

		}

		return true;
	}

	private void equip(Player player, ItemStack itemStack, ArmorType armorType) {
		player.getInventory().removeItem(itemStack);

		switch (armorType.getCategoryType()) {
			case BOOTS: {
				player.getInventory().setBoots(itemStack);
				return;
			}

			case LEGGINGS: {
				player.getInventory().setLeggings(itemStack);
				return;
			}

			case CHESTPLATE: {
				player.getInventory().setChestplate(itemStack);
				return;
			}

			case HELMET: {
				player.getInventory().setHelmet(itemStack);
				return;
			}

			case OFF_HAND: {
				if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9)) {
					player.getInventory().setItemInOffHand(itemStack);
				}
			}
		}
	}
}
