package minecraft.core.zocker.pro.nms.api.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.inventory.ItemStack;

public interface WorldCore {

	SSpawner getSpawner(CreatureSpawner spawner);

	SSpawner getSpawner(Location location);

	SItemStack getItemStack(ItemStack item);

	SWorld getWorld(World world);
}
