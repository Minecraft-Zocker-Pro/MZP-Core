package minecraft.core.zocker.pro.inventory;

import minecraft.core.zocker.pro.Main;
import minecraft.core.zocker.pro.Zocker;
import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
import minecraft.core.zocker.pro.inventory.builder.InventoryEntryBuilder;
import minecraft.core.zocker.pro.inventory.page.InventoryPage;
import minecraft.core.zocker.pro.inventory.util.ItemBuilder;
import minecraft.core.zocker.pro.nms.NmsManager;
import minecraft.core.zocker.pro.nms.api.anvil.AnvilCore;
import minecraft.core.zocker.pro.nms.api.anvil.CustomAnvil;
import minecraft.core.zocker.pro.util.Validator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public abstract class InventoryZocker {

	private final HashMap<UUID, InventoryActive> actives = new HashMap<>();
	private final LinkedList<InventoryEntry> entries = new LinkedList<>();
	private final LinkedList<InventoryPage> pages = new LinkedList<>();
	private final Collection<Integer> blacklistedSlots = new ArrayList<>();

	/**
	 * Gets title.
	 *
	 * @return the title
	 */
	public abstract String getTitle();

	/**
	 * Gets inventory type.
	 *
	 * @return the inventory type
	 */
	public abstract InventoryType getInventoryType();

	/**
	 * Gets size.
	 *
	 * @return the size
	 */
	public abstract Integer getSize();

	/**
	 * On open.
	 *
	 * @param inventoryZocker the inventoryZocker
	 * @param inventory       the inventory
	 */
	public void onOpen(InventoryActive inventoryZocker, Inventory inventory, Player player) {
	}

	/**
	 * On open.
	 *
	 * @param inventoryZocker the inventoryZocker
	 * @param event           the event
	 */
	@Deprecated
	public void onOpen(InventoryZocker inventoryZocker, InventoryOpenEvent event) {
	}

	/**
	 * On close.
	 *
	 * @param inventoryZocker the inventoryZocker
	 * @param inventory       the inventory
	 */
	public void onClose(InventoryZocker inventoryZocker, Inventory inventory, Player player) {
	}

	/**
	 * On close.
	 *
	 * @param inventoryZocker the inventoryZocker
	 * @param event           the event
	 */
	@Deprecated
	public void onClose(InventoryZocker inventoryZocker, InventoryCloseEvent event) {
	}

	public void onClick(InventoryZocker inventoryZocker, InventoryClickEvent event) {

	}

	public boolean isMovable() {
		return false;
	}

	/**
	 * Sets inventory.
	 */
	public abstract void setupInventory();

	/**
	 * Gets next arrow.
	 *
	 * @return the next arrow
	 */
	public InventoryEntry getNextArrow() {
		return new InventoryEntryBuilder().setItem(new ItemBuilder(CompatibleMaterial.ARROW.getMaterial()).setName("§6§lNext >>")).setAsync(false).build();
	}

	/**
	 * Gets previous arrow.
	 *
	 * @return the previous arrow
	 */
	public InventoryEntry getPreviousArrow() {
		return new InventoryEntryBuilder().setItem(new ItemBuilder(CompatibleMaterial.ARROW.getMaterial()).setName("§6§l<< Previous")).setAsync(false).build();
	}

	/**
	 * Gets empty arrow.
	 *
	 * @return the empty arrow.
	 */
	public InventoryEntry getEmptyArrow() {
		return new InventoryEntryBuilder().setItem(this.getBorder()).build();
	}

	/**
	 * Gets close button.
	 *
	 * @return the close button
	 */
	public InventoryEntry getCloseButton() {
		return new InventoryEntryBuilder().setItem(new ItemBuilder(CompatibleMaterial.BARRIER.getMaterial()).setName("§6§lClose")).onAllClicks(event -> {
			final Zocker zocker = Zocker.getZocker(event.getWhoClicked().getUniqueId());

			InventoryActive active = this.actives.get(zocker.getUUID());
			Validator.checkNotNull(active, "Can't close the inventory for " + zocker.getPlayer().getName() + ", no active InventoryZocker found.");
			active.stopUpdating();
			active.deleteObject();

			zocker.getPlayer().closeInventory();

			new BukkitRunnable() {
				@Override
				public void run() {
					active.getInventoryZocker().onClose(active.getInventoryZocker(), active.getInventory(), active.getZocker().getPlayer());
				}
			}.runTaskAsynchronously(Main.getPlugin());
		}).setAsync(false).build();
	}

	public InventoryEntry getInfoSign() {
		return null;
	}

	/**
	 * Gets the border
	 *
	 * @return the border
	 */
	public ItemStack getBorder() {
		return new ItemBuilder(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem()).setName(" ").toItemStack();
	}

	/**
	 * Open.
	 *
	 * @param zocker the zocker
	 */
	public void open(Zocker zocker) {
		this.setupInventory();
		InventoryPage page = pages.size() > 0 ? pages.get(0) : null;
		this.playInventory(page, zocker);
	}

	/**
	 * Open.
	 *
	 * @param zocker     the zocker
	 * @param pageNumber the page number
	 */
	public void open(Zocker zocker, int pageNumber) {
		this.setupInventory();
		InventoryPage page = pages.get(pageNumber - 1);
		this.playInventory(page, zocker);
	}

	/**
	 * Play inventory.
	 *
	 * @param page   the page
	 * @param zocker the zocker
	 */
	private void playInventory(InventoryPage page, Zocker zocker) {
		if (this instanceof InventoryAnvilZocker) {
			InventoryAnvilZocker inventoryAnvilZocker = (InventoryAnvilZocker) this;
			AnvilCore anvilCore = NmsManager.getAnvil();
			if (anvilCore == null) return;

			CustomAnvil anvil = anvilCore.createAnvil(zocker.getPlayer());
			inventoryAnvilZocker.setAnvil(anvil);

			InventoryActive active = new InventoryActive(this, page, anvil.getInventory(), zocker);
			actives.put(zocker.getUUID(), active);
			return;
		}

		Inventory inventory = getSize() != null ?
			Bukkit.createInventory(zocker.getPlayer(), getSize(), page == null ? getTitle() : page.getTitle()) :
			Bukkit.createInventory(zocker.getPlayer(), getInventoryType(), page == null ? getTitle() : page.getTitle());

		InventoryActive active = new InventoryActive(this, page, inventory, zocker);
		actives.put(zocker.getUUID(), active);
	}

	/**
	 * Close.
	 *
	 * @param zocker the zocker
	 */
	public void close(Zocker zocker) {
		InventoryActive active = this.actives.get(zocker.getUUID());
		Validator.checkNotNull(active, "Can't close the inventory for " + zocker.getPlayer().getName() + ", no active InventoryZocker found.");
		active.stopUpdating();
		active.deleteObject();

		new BukkitRunnable() {
			@Override
			public void run() {
				active.getInventoryZocker().onClose(active.getInventoryZocker(), active.getInventory(), active.getZocker().getPlayer());
			}
		}.runTaskAsynchronously(Main.getPlugin());
	}

	/**
	 * Remove from actives.
	 *
	 * @param zocker the zocker
	 * @param active the active
	 */
	public void removeFromActives(Zocker zocker, InventoryActive active) {
		actives.remove(zocker.getUUID(), active);
	}

	/**
	 * Add item.
	 *
	 * @param entry the entry
	 */
	public void addItem(InventoryEntry entry) {
		if (entries.contains(entry)) return;
		entries.add(entry);
	}

	/**
	 * Set item.
	 *
	 * @param entry the entry
	 */
	public void setItem(InventoryEntry entry) {
		if (entries.size() <= 0) {
			this.addItem(entry);
			return;
		}
		
		for (InventoryEntry inventoryEntry : entries) {
			if (inventoryEntry.getItem().isSimilar(entry.getItem())) {
				entries.remove(inventoryEntry);
				entries.add(entry);
				return;
			}
		}

		this.addItem(entry);
	}

	/**
	 * Add page.
	 *
	 * @param page the page
	 */
	public void addPage(InventoryPage page) {
		pages.add(page);
	}

	/**
	 * Add blacklisted slot.
	 *
	 * @param slot the slot
	 */
	public void addBlacklistedSlot(Integer slot) {
		blacklistedSlots.add(slot);
	}

	/**
	 * Add blacklisted slots.
	 *
	 * @param slots the slots
	 */
	public void addBlacklistedSlots(Collection<Integer> slots) {
		blacklistedSlots.addAll(slots);
	}

	/**
	 * Add blacklisted slots.
	 *
	 * @param slots the slots
	 */
	public void addBlacklistedSlots(Integer... slots) {
		blacklistedSlots.addAll(Arrays.asList(slots));
	}

	/**
	 * Sets page.
	 *
	 * @param index the index
	 * @param page  the page
	 */
	public void setPage(Integer index, InventoryPage page) {
		pages.add(index - 1, page);
	}

	/**
	 * Gets page number.
	 *
	 * @param page the page
	 * @return the page number
	 */
	public Integer getPageNumber(InventoryPage page) {
		return pages.indexOf(page) + 1;
	}

	/**
	 * Gets entries.
	 *
	 * @return the entries
	 */
	public LinkedList<InventoryEntry> getEntries() {
		return entries;
	}

	/**
	 * Gets pages.
	 *
	 * @return the pages
	 */
	public LinkedList<InventoryPage> getPages() {
		return pages;
	}

	/**
	 * Gets blacklisted slots.
	 *
	 * @return the blacklisted slots
	 */
	public Collection<Integer> getBlacklistedSlots() {
		return blacklistedSlots;
	}

	/**
	 * Gets name.
	 *
	 * @return the name
	 */
	public String getName() {
		return null;
	}

	public int getRows() {
		return this.getSize() / 9;
	}

	public void fillBorders() {
		int[] slots = new int[]{};
		switch (this.getRows()) {
			case 3:
				slots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};
				break;
			case 4:
				slots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35};
				break;
			case 5:
				slots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};
				break;
			case 6:
				slots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};
				break;
		}
		if (slots.length == 0) return;

		for (int slot : slots) {
			if (this.getEntries().stream().map(InventoryEntry::getSlot).anyMatch(integer -> integer == slot)) continue;
			this.addItem(new InventoryEntryBuilder().setSlot(slot).setItem(this.getBorder()).build());
		}
	}

	public void update(Zocker zocker) {
		if (zocker == null) return;

		InventoryActive active = this.actives.get(zocker.getUUID());
		if (active == null) return;

		InventoryPage page = pages.size() > 0 ? pages.get(0) : null;
		active.initInventory(page);
	}

	public void updateContent(Zocker zocker, boolean clear) {
		if (zocker == null) return;

		InventoryActive active = this.actives.get(zocker.getUUID());
		if (active == null) return;

		active.updateContent(clear);
	}

}
