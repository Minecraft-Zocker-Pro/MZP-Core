package minecraft.core.zocker.pro.nms.v1_11_R1.nbt;

import minecraft.core.zocker.pro.nms.api.nbt.NBTCompound;
import minecraft.core.zocker.pro.nms.api.nbt.NBTItem;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NBTItemImpl extends NBTCompoundImpl implements NBTItem {

	private final net.minecraft.server.v1_11_R1.ItemStack nmsItem;

	public NBTItemImpl(net.minecraft.server.v1_11_R1.ItemStack nmsItem) {
		super(nmsItem != null && nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound());
		this.nmsItem = nmsItem;
	}

	public ItemStack finish() {
		if (nmsItem == null) {
			return CraftItemStack.asBukkitCopy(new net.minecraft.server.v1_11_R1.ItemStack(compound));
		} else {
			return CraftItemStack.asBukkitCopy(nmsItem);
		}
	}

	@Override
	public NBTCompound set(String tag, byte[] b) {
		compound.setByteArray(tag, b);
		return this;
	}

	@Override
	public byte[] getByteArray(String tag) {
		return new byte[0];
	}

	@Override
	public void addExtras() {
	}
}
