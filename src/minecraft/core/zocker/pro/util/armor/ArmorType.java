package minecraft.core.zocker.pro.util.armor;

import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
import org.bukkit.inventory.ItemStack;

public enum ArmorType {

	// region Boots
	LEATHER_BOOTS(ArmorCategoryType.BOOTS, CompatibleMaterial.LEATHER_BOOTS.getItem()),
	CHAINMAIL_BOOTS(ArmorCategoryType.BOOTS, CompatibleMaterial.CHAINMAIL_BOOTS.getItem()),
	IRON_BOOTS(ArmorCategoryType.BOOTS, CompatibleMaterial.IRON_BOOTS.getItem()),
	GOLDEN_BOOTS(ArmorCategoryType.BOOTS, CompatibleMaterial.GOLDEN_BOOTS.getItem()),
	DIAMOND_BOOTS(ArmorCategoryType.BOOTS, CompatibleMaterial.DIAMOND_BOOTS.getItem()),
	NETHERITE_BOOTS(ArmorCategoryType.BOOTS, CompatibleMaterial.NETHERITE_BOOTS.getItem()),
	// endregion

	// region Leggings
	LEATHER_LEGGINGS(ArmorCategoryType.LEGGINGS, CompatibleMaterial.LEATHER_LEGGINGS.getItem()),
	CHAINMAIL_LEGGINGS(ArmorCategoryType.LEGGINGS, CompatibleMaterial.CHAINMAIL_LEGGINGS.getItem()),
	IRON_LEGGINGS(ArmorCategoryType.LEGGINGS, CompatibleMaterial.IRON_LEGGINGS.getItem()),
	GOLDEN_LEGGINGS(ArmorCategoryType.LEGGINGS, CompatibleMaterial.GOLDEN_LEGGINGS.getItem()),
	DIAMOND_LEGGINGS(ArmorCategoryType.LEGGINGS, CompatibleMaterial.DIAMOND_LEGGINGS.getItem()),
	NETHERITE_LEGGINGS(ArmorCategoryType.LEGGINGS, CompatibleMaterial.NETHERITE_LEGGINGS.getItem()),
	// endregion

	// region Chestplate
	LEATHER_CHESTPLATE(ArmorCategoryType.CHESTPLATE, CompatibleMaterial.LEATHER_CHESTPLATE.getItem()),
	CHAINMAIL_CHESTPLATE(ArmorCategoryType.CHESTPLATE, CompatibleMaterial.CHAINMAIL_CHESTPLATE.getItem()),
	IRON_CHESTPLATE(ArmorCategoryType.CHESTPLATE, CompatibleMaterial.IRON_CHESTPLATE.getItem()),
	GOLDEN_CHESTPLATE(ArmorCategoryType.CHESTPLATE, CompatibleMaterial.GOLDEN_CHESTPLATE.getItem()),
	DIAMOND_CHESTPLATE(ArmorCategoryType.CHESTPLATE, CompatibleMaterial.DIAMOND_CHESTPLATE.getItem()),
	NETHERITE_CHESTPLATE(ArmorCategoryType.CHESTPLATE, CompatibleMaterial.NETHERITE_CHESTPLATE.getItem()),

	ELYTRA(ArmorCategoryType.CHESTPLATE, CompatibleMaterial.ELYTRA.getItem()),
	// endregion

	// region Helmet
	LEATHER_HELMET(ArmorCategoryType.HELMET, CompatibleMaterial.LEATHER_HELMET.getItem()),
	CHAINMAIL_HELMET(ArmorCategoryType.HELMET, CompatibleMaterial.CHAINMAIL_HELMET.getItem()),
	IRON_HELMET(ArmorCategoryType.HELMET, CompatibleMaterial.IRON_HELMET.getItem()),
	GOLDEN_HELMET(ArmorCategoryType.HELMET, CompatibleMaterial.GOLDEN_HELMET.getItem()),
	DIAMOND_HELMET(ArmorCategoryType.HELMET, CompatibleMaterial.DIAMOND_HELMET.getItem()),
	NETHERITE_HELMET(ArmorCategoryType.HELMET, CompatibleMaterial.NETHERITE_HELMET.getItem()),

	TURTLE_HELMET(ArmorCategoryType.HELMET, CompatibleMaterial.TURTLE_HELMET.getItem()),
	PLAYER_HEAD(ArmorCategoryType.HELMET, CompatibleMaterial.PLAYER_HEAD.getItem()),
	ZOMBIE_HEAD(ArmorCategoryType.HELMET, CompatibleMaterial.ZOMBIE_HEAD.getItem()),
	CREEPER_HEAD(ArmorCategoryType.HELMET, CompatibleMaterial.CREEPER_HEAD.getItem()),
	DRAGON_HEAD(ArmorCategoryType.HELMET, CompatibleMaterial.DRAGON_HEAD.getItem()),
	SKELETON_SKULL(ArmorCategoryType.HELMET, CompatibleMaterial.SKELETON_SKULL.getItem()),
	WITHER_SKELETON_SKULL(ArmorCategoryType.HELMET, CompatibleMaterial.WITHER_SKELETON_SKULL.getItem()),
	// endregion

	// region OffHand
	SHIELD(ArmorCategoryType.OFF_HAND, CompatibleMaterial.SHIELD.getItem());
	// endregion


	private final ArmorCategoryType categoryType;
	private final ItemStack itemStack;

	ArmorType(ArmorCategoryType categoryType, ItemStack itemStack) {
		this.categoryType = categoryType;
		this.itemStack = itemStack;
	}

	public ArmorCategoryType getCategoryType() {
		return categoryType;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}
	
}
