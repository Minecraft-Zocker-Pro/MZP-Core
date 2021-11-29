package minecraft.core.zocker.pro.nms.v1_8_R3.world;

import minecraft.core.zocker.pro.nms.api.world.SItemStack;
import minecraft.core.zocker.pro.nms.api.world.SSpawner;
import minecraft.core.zocker.pro.nms.api.world.SWorld;
import minecraft.core.zocker.pro.nms.api.world.WorldCore;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.inventory.ItemStack;

public class WorldCoreImpl implements WorldCore {

	@Override
	public SSpawner getSpawner(CreatureSpawner spawner) {
		return new SSpawnerImpl(spawner.getLocation());
	}

	@Override
	public SSpawner getSpawner(Location location) {
		return new SSpawnerImpl(location);
	}

	@Override
	public SItemStack getItemStack(ItemStack item) {
		return new SItemStackImpl(item);
	}

	@Override
	public SWorld getWorld(World world) {
		return new SWorldImpl();
	}
}
