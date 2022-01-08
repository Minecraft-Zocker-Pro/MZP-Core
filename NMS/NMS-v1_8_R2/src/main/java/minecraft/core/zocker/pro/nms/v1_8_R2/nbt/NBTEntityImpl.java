package minecraft.core.zocker.pro.nms.v1_8_R2.nbt;

import minecraft.core.zocker.pro.nms.api.nbt.NBTEntity;
import net.minecraft.server.v1_8_R2.Entity;
import net.minecraft.server.v1_8_R2.EntityTypes;
import net.minecraft.server.v1_8_R2.ItemMonsterEgg;
import net.minecraft.server.v1_8_R2.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R2.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.lang.reflect.Field;
import java.util.Map;

public class NBTEntityImpl extends NBTCompoundImpl implements NBTEntity {

	private static Field fieldG;

	private Entity nmsEntity;

	public NBTEntityImpl(NBTTagCompound entityNBT, Entity nmsEntity) {
		super(entityNBT);
		this.nmsEntity = nmsEntity;
	}

	static {
		try {
			fieldG = EntityTypes.class.getDeclaredField("g");
			fieldG.setAccessible(true);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}

	}

	@Override
	public org.bukkit.entity.Entity spawn(Location location) {
		String entityType = getNBTObject("entity_type").asString();

		try {
			Entity spawned = ItemMonsterEgg.spawnCreature( // Changed since 1.14
				((CraftWorld) location.getWorld()).getHandle(),
				((Map<String, Integer>) fieldG.get(null)).get(entityType), // Parameter simplified in 1.10
				location.getBlockX(), location.getBlockY(), location.getBlockZ(),
				CreatureSpawnEvent.SpawnReason.DEFAULT
			);

			if (spawned != null) {
				spawned.f(compound); // This changed from 1.16.1
				org.bukkit.entity.Entity entity = spawned.getBukkitEntity();
				spawned.setLocation(location.getX(), location.getY(), location.getZ(),
					location.getPitch(), location.getYaw());
				nmsEntity = spawned;
				return entity;
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public org.bukkit.entity.Entity reSpawn(Location location) {
		nmsEntity.dead = true;
		return spawn(location);
	}

	@Override
	public void addExtras() {
		String key = EntityTypes.b(nmsEntity); // Changed in 1.12
		if (key != null)
			compound.setString("entity_type", key); // Changed in 1.13
	}
}
