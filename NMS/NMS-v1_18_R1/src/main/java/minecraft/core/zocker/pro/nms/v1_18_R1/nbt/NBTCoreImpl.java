package minecraft.core.zocker.pro.nms.v1_18_R1.nbt;

import minecraft.core.zocker.pro.nms.api.nbt.NBTCore;
import minecraft.core.zocker.pro.nms.api.nbt.NBTEntity;
import minecraft.core.zocker.pro.nms.api.nbt.NBTItem;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class NBTCoreImpl implements NBTCore {
	
	@Override
	public NBTItem of(ItemStack item) {
		return new NBTItemImpl(CraftItemStack.asNMSCopy(item));
	}

	@Override
	public NBTItem newItem() {
		return new NBTItemImpl(null);
	}

	@Override
	public NBTEntity of(Entity entity) {
		net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity) entity).getHandle();
		NBTTagCompound nbt = new NBTTagCompound();
		nmsEntity.f(nbt);

		return new NBTEntityImpl(nbt, nmsEntity);
	}

	@Override
	public NBTEntity newEntity() {
		return new NBTEntityImpl(new NBTTagCompound(), null);
	}
}
