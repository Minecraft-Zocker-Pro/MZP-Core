package minecraft.core.zocker.pro.nms.api.world;

import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.Set;

public interface SSpawner {

	LivingEntity spawnEntity(EntityType type, Location spawnerLocation);

	LivingEntity spawnEntity(EntityType type, String particleType, SpawnedEntity spawned,
													 Set<CompatibleMaterial> canSpawnOn);

	default String translateName(EntityType type, boolean capital) {
		return capital ? TypeTranslations.getUpperFromType(type) : TypeTranslations.getLowerFromType(type);
	}

	enum TypeTranslations {
		VINDICATOR("vindication_illager", "VindicationIllager"),
		SNOWMAN("snowman", "SnowMan"),
		PIG_ZOMBIE("zombie_pigman", "PigZombie"),
		EVOKER("evocation_illager", "EvocationIllager"),
		ILLUSIONER("illusion_illager", "IllusionIllager"),
		IRON_GOLEM("villager_golem", "VillagerGolem"),
		MUSHROOM_COW("mooshroom", "MushroomCow"),
		MAGMA_CUBE("magma_cube", "LavaSlime"),
		CAVE_SPIDER("cave_spider", "CaveSpider");

		private final String lower;
		private final String upper;

		TypeTranslations(String lower, String upper) {
			this.lower = lower;
			this.upper = upper;
		}

		public static String getLowerFromType(EntityType type) {
			try {
				TypeTranslations typeTranslation = valueOf(type.name());
				return typeTranslation.lower;
			} catch (Exception e) {
				return type.name().toLowerCase();
			}
		}

		public static String getUpperFromType(EntityType type) {
			try {
				TypeTranslations typeTranslation = valueOf(type.name());
				return typeTranslation.upper;
			} catch (Exception e) {
				return WordUtils.capitalize(type.name().toLowerCase()).replace(" ", "");
			}
		}
	}
}
