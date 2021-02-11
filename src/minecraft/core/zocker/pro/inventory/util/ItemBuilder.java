package minecraft.core.zocker.pro.inventory.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
import minecraft.core.zocker.pro.compatibility.ServerProject;
import minecraft.core.zocker.pro.compatibility.ServerVersion;
import minecraft.core.zocker.pro.util.Reflection;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.material.MaterialData;
import org.bukkit.material.SpawnEgg;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class ItemBuilder {

	private final ItemStack itemStack;
	private final ItemFactory itemFactory;
	private ItemMeta itemMeta;

	/**
	 * Instantiates a new Item builder.
	 *
	 * @param itemStack the item stack
	 */
	public ItemBuilder(ItemStack itemStack) {
		this.itemFactory = Bukkit.getItemFactory();
		this.itemStack = new ItemStack(itemStack);

		if (this.itemStack.getItemMeta() == null) {
			this.itemMeta = this.itemFactory.getItemMeta(this.itemStack.getType());
		} else {
			this.itemMeta = this.itemStack.getItemMeta();
		}
	}

	/**
	 * Instantiates a new Item builder.
	 *
	 * @param type the type
	 */
	public ItemBuilder(Material type) {
		this(new ItemStack(type));
	}

	/**
	 * Instantiates a new Item builder.
	 *
	 * @param type   the type
	 * @param amount the amount
	 */
	public ItemBuilder(Material type, int amount) {
		this(new ItemStack(type, amount));
	}

	/**
	 * Instantiates a new Item builder.
	 *
	 * @param type   the type
	 * @param amount the amount
	 * @param damage the damage
	 */
	public ItemBuilder(Material type, int amount, short damage) {
		this(new ItemStack(type, amount, damage));
	}

	/**
	 * Instantiates a new Item builder.
	 *
	 * @param type   the type
	 * @param amount the amount
	 * @param damage the damage
	 */
	public ItemBuilder(Material type, int amount, int damage) {
		this(new ItemStack(type, amount, (short) damage));
	}

	/**
	 * Build item stack.
	 *
	 * @return the item stack
	 */
	public ItemStack toItemStack() {
		if(this.itemStack.getType() == CompatibleMaterial.AIR.getMaterial()) {
			return itemStack;
		}
		
		itemStack.setItemMeta(itemFactory.asMetaFor(itemMeta, itemStack));
		return itemStack;
	}

	/**
	 * Sets unbreakable.
	 *
	 * @param unbreakable the unbreakable
	 * @return the unbreakable
	 */
	public ItemBuilder setUnbreakable(boolean unbreakable) {
		if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9)) {
			this.itemMeta.setUnbreakable(unbreakable);
			return this;
		}

		if (ServerProject.isServer(ServerProject.CRAFTBUKKIT)) {
			// TODO error message
			return this;
		}

		// TODO reflection for the 1.8 version
//		this.itemMeta.spigot().setUnbreakable(unbreakable);

		return this;
	}

	/**
	 * Add enchantment item builder.
	 *
	 * @param ench  the ench
	 * @param level the level
	 * @return the item builder
	 */
	public ItemBuilder addEnchantment(Enchantment ench, int level) {
		this.itemMeta.addEnchant(ench, level, false);
		return this;
	}

	/**
	 * Add enchantments item builder.
	 *
	 * @param enchantments the enchantments
	 * @return the item builder
	 */
	public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
		enchantments.forEach(this::addEnchantment);
		return this;
	}

	/**
	 * Add unsafe enchantment item builder.
	 *
	 * @param ench  the ench
	 * @param level the level
	 * @return the item builder
	 */
	public ItemBuilder addUnsafeEnchantment(Enchantment ench, int level) {
		this.itemMeta.addEnchant(ench, level, true);
		return this;
	}

	/**
	 * Sets amount.
	 *
	 * @param amount the amount
	 * @return the amount
	 */
	public ItemBuilder setAmount(int amount) {
		this.itemStack.setAmount(amount);
		return this;
	}

	/**
	 * Remove enchantment item builder.
	 *
	 * @param ench the ench
	 * @return the item builder
	 */
	public ItemBuilder removeEnchantment(Enchantment ench) {
		this.itemMeta.removeEnchant(ench);
		return this;
	}

	/**
	 * Sets durability.
	 *
	 * @param durability the durability
	 * @return the durability
	 */
	public ItemBuilder setDurability(int durability) {
		this.itemStack.setDurability((short) durability);
		return this;
	}

	public ItemBuilder addDamage(int damage) {
		if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)) {
			ItemMeta meta = this.itemMeta;
			if (meta instanceof Damageable) {
				((Damageable) meta).setDamage(((Damageable) meta).getDamage() + damage);
				this.setItemMeta(meta);
			}
		} else {
			this.setDurability((short) Math.max(0, this.itemStack.getDurability() + damage));
		}

		return this;
	}

	/**
	 * Sets type.
	 *
	 * @param type the type
	 * @return the type
	 */
	@Deprecated
	public ItemBuilder setType(Material type) {
		this.itemStack.setType(type);
		return this;
	}

	/**
	 * Sets data.
	 *
	 * @param data the data
	 * @return the data
	 */
	@Deprecated
	public ItemBuilder setData(MaterialData data) {
		this.itemStack.setData(data);
		return this;
	}

	/**
	 * Sets item meta.
	 *
	 * @param itemMeta the item meta
	 * @return the item meta
	 */
	@Deprecated
	public ItemBuilder setItemMeta(ItemMeta itemMeta) {
		this.itemStack.setItemMeta(itemMeta);
		this.itemMeta = itemMeta;
		return this;
	}

	/**
	 * Sets display name.
	 *
	 * @param name the name
	 * @return the display name
	 */
	public ItemBuilder setDisplayName(String name) {
		this.itemMeta.setDisplayName(name);
		return this;
	}

	/**
	 * Sets name.
	 *
	 * @param name the name
	 * @return the name
	 */
	public ItemBuilder setName(String name) {
		return this.setDisplayName(name);
	}

	/**
	 * Add white space lore item builder.
	 *
	 * @return the item builder
	 */
	public ItemBuilder addWhiteSpaceLore() {
		addLore(" ");
		return this;
	}

	/**
	 * Sets lore.
	 *
	 * @param lore the lore
	 * @return the lore
	 */
	public ItemBuilder setLore(List<String> lore) {
		this.itemMeta.setLore(lore);
		return this;
	}

	/**
	 * Add lore item builder.
	 *
	 * @param lore the lore
	 * @return the item builder
	 */
	public ItemBuilder addLore(String lore) {
		if (this.itemMeta.hasLore()) {
			List<String> lores = this.itemMeta.getLore();
			lores.add(lore);
			this.itemMeta.setLore(lores);
		} else {
			this.itemMeta.setLore(Collections.singletonList(lore));
		}
		return this;
	}

	/**
	 * Sets lore.
	 *
	 * @param lore the lore
	 * @return the lore
	 */
	public ItemBuilder setLore(String... lore) {
		this.itemMeta.setLore(Arrays.asList(lore));
		return this;
	}

	/**
	 * Add item flags item builder.
	 *
	 * @param itemFlags the item flags
	 * @return the item builder
	 */
	public ItemBuilder addItemFlags(ItemFlag... itemFlags) {
		this.itemMeta.addItemFlags(itemFlags);
		return this;
	}

	/**
	 * Add item flag item builder.
	 *
	 * @param itemFlags the item flags
	 * @return the item builder
	 */
	public ItemBuilder addItemFlag(ItemFlag... itemFlags) {
		this.itemMeta.addItemFlags(itemFlags);
		return this;
	}

	/**
	 * Clear flags item builder.
	 *
	 * @return the item builder
	 */
	public ItemBuilder clearFlags() {
		itemMeta.getItemFlags().forEach(itemMeta::removeItemFlags);
		return this;
	}

	/**
	 * Sets egg type.
	 *
	 * @param type the type
	 * @return the egg type
	 */
	public ItemBuilder setEggType(EntityType type) {
		if (ServerVersion.isServerVersionAtOrBelow(ServerVersion.V1_8)) {
			if (this.itemStack.getType() == CompatibleMaterial.valueOf("MONSTER_EGG").getMaterial()) {
				((SpawnEgg) this.itemMeta).setSpawnedType(type);
				return this;
			}
		}

		if (ServerVersion.isServerVersionAbove(ServerVersion.V1_8)) {
			((SpawnEgg) this.itemMeta).setSpawnedType(type);
			return this;
		}

		return this;
	}

	/**
	 * Sets owning player.
	 *
	 * @param player the player
	 * @return the owning player
	 */
	public ItemBuilder setOwningPlayer(OfflinePlayer player) {
		if (ServerVersion.isServerVersionBelow(ServerVersion.V1_8)) {
			// TODO error
			return this;
		}

		if (this.itemStack.getType() == CompatibleMaterial.PLAYER_HEAD.getMaterial()) {
			SkullMeta meta = (SkullMeta) this.itemMeta;
			if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)) {
				meta.setOwningPlayer(player);
			} else {
				meta.setOwner(player.getName());
			}
		}

		return this;
	}

	private GameProfile createProfileWithTexture(String texture) {
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		PropertyMap propertyMap = profile.getProperties();
		propertyMap.put("textures", new Property("textures", texture));
		return profile;
	}

	public ItemBuilder setSkullURL(String url) {
		if (itemStack.getType() != CompatibleMaterial.PLAYER_HEAD.getMaterial())
			throw new IllegalArgumentException("ItemStack is not SKULL_ITEM");

		if (!Reflection.set(itemMeta.getClass(), itemMeta, "profile", this.createProfileWithTexture(url)))
			throw new IllegalStateException("Unable to inject GameProfile");

		return this;
	}

	/**
	 * Is potion boolean.
	 *
	 * @return the boolean
	 */
	private boolean isPotion() {
		Material material = itemStack.getType();
		return material == CompatibleMaterial.POTION.getMaterial();
	}

	/**
	 * Add custom effect item builder.
	 *
	 * @param effect    the effect
	 * @param overwrite the overwrite
	 * @return the item builder
	 */
	public ItemBuilder addCustomEffect(PotionEffect effect, boolean overwrite) {
		if (this.isPotion()) {
			((PotionMeta) this.itemMeta).addCustomEffect(effect, overwrite);
		}
		return this;
	}

	/**
	 * Remove custom effect item builder.
	 *
	 * @param type the type
	 * @return the item builder
	 */
	public ItemBuilder removeCustomEffect(PotionEffectType type) {
		if (this.isPotion()) {
			((PotionMeta) this.itemMeta).removeCustomEffect(type);
		}
		return this;
	}

	/**
	 * Remove effect item builder.
	 *
	 * @param type the type
	 * @return the item builder
	 */
	public ItemBuilder removeEffect(PotionEffectType type) {
		return this.removeCustomEffect(type);
	}

	/**
	 * Clear custom effects item builder.
	 *
	 * @return the item builder
	 */
	public ItemBuilder clearCustomEffects() {
		if (this.isPotion()) {
			((PotionMeta) this.itemMeta).clearCustomEffects();
		}
		return this;
	}

	/**
	 * Clear effects item builder.
	 *
	 * @return the item builder
	 */
	public ItemBuilder clearEffects() {
		return this.clearCustomEffects();
	}

	/**
	 * Sets map scaling.
	 *
	 * @param value the value
	 * @return the map scaling
	 */
	public ItemBuilder setMapScaling(boolean value) {
		if (this.itemStack.getType() == CompatibleMaterial.MAP.getMaterial()) {
			((MapMeta) this.itemMeta).setScaling(value);
		}
		return this;
	}

	/**
	 * Is leather armor boolean.
	 *
	 * @return the boolean
	 */
	private boolean isLeatherArmor() {
		Material material = itemStack.getType();
		return material == CompatibleMaterial.LEATHER_HELMET.getMaterial() ||
			material == CompatibleMaterial.LEATHER_CHESTPLATE.getMaterial() ||
			material == CompatibleMaterial.LEATHER_LEGGINGS.getMaterial() ||
			material == CompatibleMaterial.LEATHER_BOOTS.getMaterial();
	}

	/**
	 * Is iron armor boolean.
	 *
	 * @return the boolean
	 */
	private boolean isIronArmor() {
		Material material = itemStack.getType();
		return material == CompatibleMaterial.IRON_HELMET.getMaterial() ||
			material == CompatibleMaterial.IRON_CHESTPLATE.getMaterial() ||
			material == CompatibleMaterial.IRON_LEGGINGS.getMaterial() ||
			material == CompatibleMaterial.IRON_BOOTS.getMaterial();
	}

	/**
	 * Is golden armor boolean.
	 *
	 * @return the boolean
	 */
	private boolean isGoldenArmor() {
		Material material = itemStack.getType();
		return material == CompatibleMaterial.GOLDEN_HELMET.getMaterial() ||
			material == CompatibleMaterial.GOLDEN_CHESTPLATE.getMaterial() ||
			material == CompatibleMaterial.GOLDEN_LEGGINGS.getMaterial() ||
			material == CompatibleMaterial.GOLDEN_BOOTS.getMaterial();
	}

	/**
	 * Is iron armor boolean.
	 *
	 * @return the boolean
	 */
	private boolean isChainmailArmor() {
		Material material = itemStack.getType();
		return material == CompatibleMaterial.CHAINMAIL_HELMET.getMaterial() ||
			material == CompatibleMaterial.CHAINMAIL_CHESTPLATE.getMaterial() ||
			material == CompatibleMaterial.CHAINMAIL_LEGGINGS.getMaterial() ||
			material == CompatibleMaterial.CHAINMAIL_BOOTS.getMaterial();
	}

	/**
	 * Is iron armor boolean.
	 *
	 * @return the boolean
	 */
	private boolean isDiamondArmor() {
		Material material = itemStack.getType();
		return material == CompatibleMaterial.DIAMOND_HELMET.getMaterial() ||
			material == CompatibleMaterial.DIAMOND_CHESTPLATE.getMaterial() ||
			material == CompatibleMaterial.DIAMOND_LEGGINGS.getMaterial() ||
			material == CompatibleMaterial.DIAMOND_BOOTS.getMaterial();
	}

	/**
	 * Is iron armor boolean.
	 *
	 * @return the boolean
	 */
	private boolean isNetheriteArmor() {
		if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_16)) {
			Material material = itemStack.getType();
			return material == CompatibleMaterial.NETHERITE_HELMET.getMaterial() ||
				material == CompatibleMaterial.NETHERITE_CHESTPLATE.getMaterial() ||
				material == CompatibleMaterial.NETHERITE_LEGGINGS.getMaterial() ||
				material == CompatibleMaterial.NETHERITE_BOOTS.getMaterial();
		}

		// TODO error;
		return false;
	}

	/**
	 * Sets armor color.
	 *
	 * @param color the color
	 * @return the armor color
	 */
	public ItemBuilder setArmorColor(Color color) {
		if (this.isLeatherArmor()) {
			((LeatherArmorMeta) this.itemMeta).setColor(color);
		}
		return this;
	}

	/**
	 * Reset armor color item builder.
	 *
	 * @return the item builder
	 */
	public ItemBuilder resetArmorColor() {
		if (this.isLeatherArmor()) {
			((LeatherArmorMeta) this.itemMeta)
				.setColor(this.itemFactory.getDefaultLeatherColor());
		}
		return this;
	}

	/**
	 * Sets firework power.
	 *
	 * @param power the power
	 * @return the firework power
	 */
	public ItemBuilder setFireworkPower(int power) {
		if (this.itemStack.getType() == CompatibleMaterial.FIREWORK_ROCKET.getMaterial()) {
			((FireworkMeta) this.itemMeta).setPower(power);
		}
		return this;
	}

	/**
	 * Remove firework effect item builder.
	 *
	 * @param index the index
	 * @return the item builder
	 */
	public ItemBuilder removeFireworkEffect(int index) {
		if (this.itemStack.getType() == CompatibleMaterial.FIREWORK_ROCKET.getMaterial()) {
			((FireworkMeta) this.itemMeta).removeEffect(index);
		}
		return this;
	}

	/**
	 * Add firework effect item builder.
	 *
	 * @param effect the effect
	 * @return the item builder
	 */
	public ItemBuilder addFireworkEffect(FireworkEffect effect) {
		if (this.itemStack.getType() == CompatibleMaterial.FIREWORK_ROCKET.getMaterial()) {
			((FireworkMeta) this.itemMeta).addEffect(effect);
		}
		return this;
	}

	/**
	 * Add firework effects item builder.
	 *
	 * @param effects the effects
	 * @return the item builder
	 */
	public ItemBuilder addFireworkEffects(Iterable<FireworkEffect> effects) {
		if (this.itemStack.getType() == CompatibleMaterial.FIREWORK_ROCKET.getMaterial()) {
			((FireworkMeta) this.itemMeta).addEffects(effects);
		}
		return this;
	}

	/**
	 * Add firework effects item builder.
	 *
	 * @param effects the effects
	 * @return the item builder
	 */
	public ItemBuilder addFireworkEffects(FireworkEffect... effects) {
		if (this.itemStack.getType() == CompatibleMaterial.FIREWORK_ROCKET.getMaterial()) {
			((FireworkMeta) this.itemMeta).addEffects(effects);
		}
		return this;
	}

	/**
	 * Sets charge effect.
	 *
	 * @param effect the effect
	 * @return the charge effect
	 */
	public ItemBuilder setChargeEffect(FireworkEffect effect) {
		if (this.itemStack.getType() == CompatibleMaterial.FIREWORK_STAR.getMaterial()) {
			((FireworkEffectMeta) this.itemMeta).setEffect(effect);
		}
		return this;
	}

	/**
	 * Sets banner pattern.
	 *
	 * @param i       the
	 * @param pattern the pattern
	 * @return the banner pattern
	 */
	public ItemBuilder setBannerPattern(int i, Pattern pattern) {
		if (this.itemStack.getType() == CompatibleMaterial.BLACK_BANNER.getMaterial()) {
			((BannerMeta) this.itemMeta).setPattern(i, pattern);
		}
		return this;
	}

	/**
	 * Sets banner patterns.
	 *
	 * @param patterns the patterns
	 * @return the banner patterns
	 */
	public ItemBuilder setBannerPatterns(List<Pattern> patterns) {
		if (this.itemStack.getType() == CompatibleMaterial.BLACK_BANNER.getMaterial()) {
			((BannerMeta) this.itemMeta).setPatterns(patterns);
		}
		return this;
	}

	/**
	 * Remove banner pattern item builder.
	 *
	 * @param i the
	 * @return the item builder
	 */
	public ItemBuilder removeBannerPattern(int i) {
		if (this.itemStack.getType() == CompatibleMaterial.BLACK_BANNER.getMaterial()) {
			((BannerMeta) this.itemMeta).removePattern(i);
		}
		return this;
	}

	/**
	 * Add banner pattern item builder.
	 *
	 * @param pattern the pattern
	 * @return the item builder
	 */
	public ItemBuilder addBannerPattern(Pattern pattern) {
		if (this.itemStack.getType() == CompatibleMaterial.BLACK_BANNER.getMaterial()) {
			((BannerMeta) this.itemMeta).addPattern(pattern);
		}
		return this;
	}

	/**
	 * Add banner patterns item builder.
	 *
	 * @param patterns the patterns
	 * @return the item builder
	 */
	public ItemBuilder addBannerPatterns(Pattern... patterns) {
		if (this.itemStack.getType() == CompatibleMaterial.BLACK_BANNER.getMaterial()) {
			BannerMeta bannerMeta = (BannerMeta) this.itemMeta;
			for (Pattern pattern : patterns) {
				bannerMeta.addPattern(pattern);
			}
		}
		return this;
	}

	/**
	 * Is book boolean.
	 *
	 * @return the boolean
	 */
	private boolean isBook() {
		Material material = this.itemStack.getType();
		return material == CompatibleMaterial.WRITABLE_BOOK.getMaterial() || material == CompatibleMaterial.WRITTEN_BOOK.getMaterial();
	}

	/**
	 * Add book page item builder.
	 *
	 * @param pages the pages
	 * @return the item builder
	 */
	public ItemBuilder addBookPage(String... pages) {
		if (this.isBook()) {
			((BookMeta) this.itemMeta).addPage(pages);
		}
		return this;
	}

	/**
	 * Sets book author.
	 *
	 * @param author the author
	 * @return the book author
	 */
	public ItemBuilder setBookAuthor(String author) {
		if (this.isBook()) {
			((BookMeta) this.itemMeta).setAuthor(author);
		}
		return this;
	}

	/**
	 * Sets book page.
	 *
	 * @param page the page
	 * @param data the data
	 * @return the book page
	 */
	public ItemBuilder setBookPage(int page, String data) {
		if (this.isBook()) {
			((BookMeta) this.itemMeta).setPage(page, data);
		}
		return this;
	}

	/**
	 * Sets book title.
	 *
	 * @param title the title
	 * @return the book title
	 */
	public ItemBuilder setBookTitle(String title) {
		if (this.isBook()) {
			((BookMeta) this.itemMeta).setTitle(title);
		}
		return this;
	}

	/**
	 * Sets pages.
	 *
	 * @param pages the pages
	 * @return the pages
	 */
	public ItemBuilder setPages(String... pages) {
		if (this.isBook()) {
			((BookMeta) this.itemMeta).setPages(pages);
		}
		return this;
	}

	/**
	 * Sets pages.
	 *
	 * @param pages the pages
	 * @return the pages
	 */
	public ItemBuilder setPages(List<String> pages) {
		if (this.isBook()) {
			((BookMeta) this.itemMeta).setPages(pages);
		}
		return this;
	}
}
