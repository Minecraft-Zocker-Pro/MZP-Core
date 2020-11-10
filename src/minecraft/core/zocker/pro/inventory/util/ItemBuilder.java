package minecraft.core.zocker.pro.inventory.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
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
		this.itemStack = itemStack;
		this.itemMeta = this.itemFactory.getItemMeta(itemStack.getType());
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
	 * Add ench item builder.
	 *
	 * @param ench  the ench
	 * @param level the level
	 * @return the item builder
	 */
	public ItemBuilder addEnch(Enchantment ench, int level) {
		return this.addEnchantment(ench, level);
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
	 * Add unsafe ench item builder.
	 *
	 * @param ench  the ench
	 * @param level the level
	 * @return the item builder
	 */
	public ItemBuilder addUnsafeEnch(Enchantment ench, int level) {
		return this.addUnsafeEnchantment(ench, level);
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
	 * Amount item builder.
	 *
	 * @param amount the amount
	 * @return the item builder
	 */
	public ItemBuilder amount(int amount) {
		return this.setAmount(amount);
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
	 * Remove ench item builder.
	 *
	 * @param ench the ench
	 * @return the item builder
	 */
	public ItemBuilder removeEnch(Enchantment ench) {
		return this.removeEnchantment(ench);
	}

	/**
	 * Sets durability.
	 *
	 * @param durability the durability
	 * @return the durability
	 */
	public ItemBuilder setDurability(short durability) {
		this.itemStack.setDurability(durability);
		return this;
	}

	/**
	 * Durability item builder.
	 *
	 * @param durability the durability
	 * @return the item builder
	 */
	public ItemBuilder durability(short durability) {
		return this.setDurability(durability);
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

	/**
	 * Durability item builder.
	 *
	 * @param durability the durability
	 * @return the item builder
	 */
	public ItemBuilder durability(int durability) {
		return this.setDurability(durability);
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
	 * Type item builder.
	 *
	 * @param type the type
	 * @return the item builder
	 */
	@Deprecated
	public ItemBuilder type(Material type) {
		return this.setType(type);
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
	 * Data item builder.
	 *
	 * @param data the data
	 * @return the item builder
	 */
	@Deprecated
	public ItemBuilder data(MaterialData data) {
		return this.setData(data);
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
	 * Meta item builder.
	 *
	 * @param itemMeta the item meta
	 * @return the item builder
	 */
	@Deprecated
	public ItemBuilder meta(ItemMeta itemMeta) {
		return this.setItemMeta(itemMeta);
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
	 * Name item builder.
	 *
	 * @param name the name
	 * @return the item builder
	 */
	public ItemBuilder name(String name) {
		return this.setDisplayName(name);
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
//        if (this.itemStack.getType() == CompatibleMaterial.getSpawnEgg().MONSTER_EGG) {
		((SpawnEgg) this.itemMeta).setSpawnedType(type);
//        }
		return this;
	}

	/**
	 * Sets owning player.
	 *
	 * @param player the player
	 * @return the owning player
	 */
	public ItemBuilder setOwningPlayer(OfflinePlayer player) {
		if (this.itemStack.getType() == CompatibleMaterial.PLAYER_HEAD.getMaterial()) {
			((SkullMeta) this.itemMeta).setOwner(player.getName());
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

	public ItemBuilder skullURL(String url) {
		return this;
	}

	/**
	 * Skull item builder.
	 *
	 * @param owner the owner
	 * @return the item builder
	 */
	public ItemBuilder skull(OfflinePlayer owner) {
		return this.setOwningPlayer(owner);
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
	 * Effect item builder.
	 *
	 * @param effect    the effect
	 * @param overwrite the overwrite
	 * @return the item builder
	 */
	public ItemBuilder effect(PotionEffect effect, boolean overwrite) {
		return this.addCustomEffect(effect, overwrite);
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
	 * Map scaling item builder.
	 *
	 * @param value the value
	 * @return the item builder
	 */
	public ItemBuilder mapScaling(boolean value) {
		return this.setMapScaling(value);
	}

	/**
	 * Is leather armor boolean.
	 *
	 * @return the boolean
	 */
	private boolean isLeatherArmor() {
		Material material = itemStack.getType();
		return material == Material.LEATHER_HELMET ||
			material == Material.LEATHER_CHESTPLATE ||
			material == Material.LEATHER_LEGGINGS ||
			material == Material.LEATHER_BOOTS;
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
	 * Author item builder.
	 *
	 * @param author the author
	 * @return the item builder
	 */
	public ItemBuilder author(String author) {
		return this.setBookAuthor(author);
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
	 * Sets page.
	 *
	 * @param page the page
	 * @param data the data
	 * @return the page
	 */
	public ItemBuilder setPage(int page, String data) {
		return this.setBookPage(page, data);
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
	 * Title item builder.
	 *
	 * @param title the title
	 * @return the item builder
	 */
	public ItemBuilder title(String title) {
		return this.setBookTitle(title);
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
