package minecraft.core.zocker.pro.nms.v1_18_R1.nbt;

import minecraft.core.zocker.pro.nms.api.nbt.NBTItem;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NBTItemImpl extends NBTCompoundImpl implements NBTItem {
	private final net.minecraft.world.item.ItemStack nmsItem;

	public NBTItemImpl(net.minecraft.world.item.ItemStack nmsItem) {
		super(nmsItem != null && nmsItem.r() ? nmsItem.s() : new NBTTagCompound());

		this.nmsItem = nmsItem;
	}

	public ItemStack finish() {
		if (nmsItem == null) {
			return CraftItemStack.asBukkitCopy(net.minecraft.world.item.ItemStack.a(compound));
		}

		return CraftItemStack.asBukkitCopy(nmsItem);
	}
}
