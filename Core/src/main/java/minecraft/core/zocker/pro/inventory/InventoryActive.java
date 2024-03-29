package minecraft.core.zocker.pro.inventory;

import com.google.common.collect.Maps;
import minecraft.core.zocker.pro.Main;
import minecraft.core.zocker.pro.Zocker;
import minecraft.core.zocker.pro.compatibility.CompatibleSound;
import minecraft.core.zocker.pro.inventory.page.InventoryPage;
import minecraft.core.zocker.pro.inventory.util.ItemBuilder;
import minecraft.core.zocker.pro.nms.NmsManager;
import minecraft.core.zocker.pro.nms.api.anvil.AnvilCore;
import minecraft.core.zocker.pro.nms.api.anvil.CustomAnvil;
import minecraft.core.zocker.pro.util.Validator;
import minecraft.core.zocker.pro.workers.JobRunnable;
import minecraft.core.zocker.pro.workers.instances.WorkerPriority;
import minecraft.core.zocker.pro.workers.instances.Workers;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class InventoryActive {

	private static final ItemStack blacklistedItem = new ItemBuilder(Material.BARRIER)
		.setDisplayName("§b§lBLACKLISTED SLOT §k" + UUID.randomUUID())
		.toItemStack();

	private static final HashMap<Inventory, InventoryActive> activeGUIs = Maps.newHashMap();
	private final HashMap<Integer, GUIActiveListener.GUIActiveEntry> activeEntries = new HashMap<>();
	private final InventoryZocker inventoryZocker;
	private final Inventory inventory;
	private final Zocker zocker;

	/**
	 * Instantiates a new Gui active.
	 *
	 * @param inventoryZocker the inventoryZocker
	 * @param page            the page
	 * @param inventory       the inventory
	 * @param zocker          the zocker
	 */
	public InventoryActive(InventoryZocker inventoryZocker, InventoryPage page, Inventory inventory, Zocker zocker) {
		this.inventoryZocker = inventoryZocker;
		this.inventory = inventory;
		this.zocker = zocker;

		Validator.checkNotNull("Template or Inventory cannot be null!", inventoryZocker, inventory, zocker);

		this.initInventory(page);
		this.openInventory();
		this.startUpdating();
	}

	/**
	 * Value of inventoryZocker active.
	 *
	 * @param inventory the inventory
	 * @return the inventoryZocker active
	 */
	private static InventoryActive valueOf(Inventory inventory) {
		return activeGUIs.get(inventory);
	}

	/**
	 * Init inventory.
	 *
	 * @param page the page
	 */
	public void initInventory(InventoryPage page) {
		if (this.inventoryZocker instanceof InventoryUpdateZocker) {
			InventoryUpdateZocker inventoryUpdateZocker = (InventoryUpdateZocker) this.inventoryZocker;

			if (inventoryUpdateZocker.isClearBefore()) {
				this.clearContent(this.inventoryZocker.getEntries()
					.stream()
					.filter(Objects::nonNull)
					.collect(Collectors.toCollection(ArrayList::new)));
			}
		}

		if (this.inventoryZocker instanceof InventoryAnvilZocker) {
			InventoryAnvilZocker inventoryAnvilZocker = (InventoryAnvilZocker) this.inventoryZocker;
			AnvilCore anvilCore = NmsManager.getAnvil();
			if (anvilCore == null) return;

			CustomAnvil anvil = inventoryAnvilZocker.getAnvil();
			anvil.setCustomTitle(inventoryAnvilZocker.getTitle());
			anvil.setLevelCost(inventoryAnvilZocker.getLevelCost());

			anvil.setOnChange(() -> {
				if (anvil.getRenameText() != null) {
					ItemStack itemStack = inventoryAnvilZocker.getResultInventoryEntry().getItem();
					ItemMeta itemMeta = itemStack.getItemMeta();

					// For 1.8.x players on Java 13
					String renameText = anvil.getRenameText();
					if (renameText.startsWith("ff")) {
						renameText = renameText.substring(2);
					}

					itemMeta.setDisplayName(renameText);
					itemStack.setItemMeta(itemMeta);

					anvil.setOutput(itemStack);
				}
			});

			anvil.setLeftInput(inventoryAnvilZocker.getLeftInventoryEntry().getItem());
			anvil.setRightInput(inventoryAnvilZocker.getRightInventoryEntry().getItem());
			anvil.setOutput(inventoryAnvilZocker.getResultInventoryEntry().getItem());

			anvil.setRenameText(inventoryAnvilZocker.getResultInventoryEntry().getItem().getItemMeta().getDisplayName());

			this.inventoryZocker.addItem(inventoryAnvilZocker.getLeftInventoryEntry());
			this.inventoryZocker.addItem(inventoryAnvilZocker.getRightInventoryEntry());
			this.inventoryZocker.addItem(inventoryAnvilZocker.getResultInventoryEntry());
		}

		for (Integer blacklistedSlot : this.inventoryZocker.getBlacklistedSlots()) {
			inventory.setItem(blacklistedSlot, blacklistedItem);
		}

		for (InventoryEntry entry : this.inventoryZocker.getEntries().stream().filter(Objects::nonNull).collect(Collectors.toCollection(ArrayList::new))) {
			if (entry.getSlot() != null) {
				inventory.setItem(entry.getSlot(), entry.getItem());
				activeEntries.put(entry.getSlot(), new GUIActiveListener.GUIActiveEntry(this, entry));
			} else {
				inventory.addItem(entry.getItem());
				int slot = inventory.first(entry.getItem());
				activeEntries.put(slot, new GUIActiveListener.GUIActiveEntry(this, entry));
			}
		}

		if (page != null) {
			int rows = inventoryZocker.getSize() / 9;
			for (Integer blacklistedSlot : page.getBlacklistedSlots()) {
				inventory.setItem(blacklistedSlot, blacklistedItem);
			}

			int currentPage = inventoryZocker.getPageNumber(page);

			for (InventoryEntry entry : page.getEntries().stream().filter(Objects::nonNull).collect(Collectors.toCollection(ArrayList::new))) {
				if (entry.getSlot() != null) {
					inventory.setItem(entry.getSlot(), entry.getItem());
					activeEntries.put(entry.getSlot(), new GUIActiveListener.GUIActiveEntry(this, entry));
				} else {
					inventory.addItem(entry.getItem());
					int slot = inventory.first(entry.getItem());
					activeEntries.put(slot, new GUIActiveListener.GUIActiveEntry(this, entry));
				}
			}

			InventoryEntry previousArrow = inventoryZocker.getPreviousArrow();
			if (currentPage != 1) previousArrow.onAllClicks((event) -> {
				InventoryActive before = InventoryActive.valueOf(inventory);
				before.stopUpdating();
				before.deleteObject();
				inventoryZocker.open(Zocker.getZocker(event.getWhoClicked().getUniqueId()), currentPage - 1);
			});

			InventoryEntry nextArrow = inventoryZocker.getNextArrow();
			if (currentPage != inventoryZocker.getPages().size()) nextArrow.onAllClicks((event) -> {
				InventoryActive before = InventoryActive.valueOf(inventory);
				before.stopUpdating();
				before.deleteObject();
				inventoryZocker.open(Zocker.getZocker(event.getWhoClicked().getUniqueId()), currentPage + 1);
			});

			// PREVIOUS ARROW
			InventoryEntry emptyArrow = inventoryZocker.getEmptyArrow();
			if (currentPage != 1) {
				if (previousArrow.getSlot() != null) {
					inventory.setItem(previousArrow.getSlot(), previousArrow.getItem());
				} else {
					if (rows <= 2) inventory.addItem(previousArrow.getItem());
					else inventory.setItem((rows * 9) - 6, previousArrow.getItem());
				}

				activeEntries.put(previousArrow.getSlot() == null ? inventory.first(previousArrow.getItem()) : previousArrow.getSlot(), new GUIActiveListener.GUIActiveEntry(this, previousArrow));
			} else {
				if (emptyArrow != null) {
					if (previousArrow.getSlot() != null) {
						inventory.setItem(previousArrow.getSlot(), emptyArrow.getItem());
					} else {
						if (rows <= 2) inventory.addItem(emptyArrow.getItem());
						else inventory.setItem((rows * 9) - 6, emptyArrow.getItem());
					}
				}
			}

			//NEXT ARROW
			if (currentPage != inventoryZocker.getPages().size()) {
				if (nextArrow.getSlot() != null) {
					inventory.setItem(nextArrow.getSlot(), nextArrow.getItem());
				} else {
					if (rows <= 2) inventory.addItem(nextArrow.getItem());
					else inventory.setItem((rows * 9) - 4, nextArrow.getItem());
				}

				activeEntries.put(nextArrow.getSlot() == null ? inventory.first(nextArrow.getItem()) : nextArrow.getSlot(), new GUIActiveListener.GUIActiveEntry(this, nextArrow));

			} else {
				if (emptyArrow != null) {
					if (nextArrow.getSlot() != null) {
						inventory.setItem(nextArrow.getSlot(), emptyArrow.getItem());
					} else {
						if (rows <= 2) inventory.addItem(emptyArrow.getItem());
						else inventory.setItem((rows * 9) - 4, emptyArrow.getItem());
					}
				}
			}

			InventoryEntry closeButton = inventoryZocker.getCloseButton();
			if (closeButton != null) {
				if (closeButton.getSlot() != null) {
					inventory.setItem(closeButton.getSlot(), closeButton.getItem());
				} else {
					if (rows <= 2) inventory.addItem(closeButton.getItem());
					else inventory.setItem(9 * (rows - 1), closeButton.getItem());
				}

				activeEntries.put(closeButton.getSlot() == null ? inventory.first(closeButton.getItem()) : closeButton.getSlot(), new GUIActiveListener.GUIActiveEntry(this, closeButton));
			}
		}

		InventoryEntry infoSign = inventoryZocker.getInfoSign();
		if (infoSign != null) {
			if (infoSign.getSlot() != null) inventory.setItem(infoSign.getSlot(), infoSign.getItem());
			else inventory.setItem(8, infoSign.getItem());
		}

		if (inventoryZocker.getPages().size() == 0) {
			InventoryEntry closeButton = inventoryZocker.getCloseButton();
			if (closeButton != null) {
				if (closeButton.getSlot() != null) {
					inventory.setItem(closeButton.getSlot(), closeButton.getItem());
				} else {
					int rows = inventoryZocker.getSize() / 9;
					if (rows <= 2) inventory.addItem(closeButton.getItem());
					else inventory.setItem(9 * (rows - 1), closeButton.getItem());
				}
				activeEntries.put(closeButton.getSlot() == null ? inventory.first(closeButton.getItem()) : closeButton.getSlot(), new GUIActiveListener.GUIActiveEntry(this, closeButton));
			}
		}

		this.inventory.remove(blacklistedItem);
		inventoryZocker.getPages().clear();
	}

	public void updateContent(boolean clear) {
		final List<InventoryEntry> inventoryEntries = this.inventoryZocker.getEntries()
			.stream()
			.filter(Objects::nonNull)
			.filter(inventoryEntry -> !inventoryEntry.getItem().isSimilar(this.inventoryZocker.getBorder()))
			.filter(inventoryEntry -> !inventoryEntry.getItem().isSimilar(this.inventoryZocker.getCloseButton().getItem()))
			.filter(inventoryEntry -> !inventoryEntry.getItem().isSimilar(this.inventoryZocker.getPreviousArrow().getItem()))
			.filter(inventoryEntry -> !inventoryEntry.getItem().isSimilar(this.inventoryZocker.getNextArrow().getItem()))
			.filter(inventoryEntry -> (this.inventoryZocker.getPreviousArrow() != null && !inventoryEntry.getItem().isSimilar(this.inventoryZocker.getPreviousArrow().getItem())))
			.collect(Collectors.toList());

		if (clear) {
			this.clearContent(inventoryEntries);
		}

		for (InventoryEntry entry : inventoryEntries) {
			if (entry.getSlot() != null) {
				inventory.setItem(entry.getSlot(), entry.getItem());
				activeEntries.put(entry.getSlot(), new GUIActiveListener.GUIActiveEntry(this, entry));
			} else {
				inventory.addItem(entry.getItem());
				int slot = inventory.first(entry.getItem());
				activeEntries.put(slot, new GUIActiveListener.GUIActiveEntry(this, entry));
			}
		}
	}

	private void clearContent(List<InventoryEntry> inventoryEntries) {
		Inventory inventory = this.getInventory();
		if (inventory == null) return;

		for (InventoryEntry entry : inventoryEntries) {
			ItemStack itemStack = entry.getItem();

			inventory.removeItem(itemStack);

			if (entry.getSlot() != null) {
				activeEntries.remove(entry.getSlot());
			}
		}
	}

	/**
	 * Open inventory.
	 */
	private void openInventory() {
		new BukkitRunnable() {
			@Override
			public void run() {
				Player player = zocker.getPlayer();

				if (inventoryZocker instanceof InventoryAnvilZocker) {
					((InventoryAnvilZocker) inventoryZocker).getAnvil().open();
				} else {
					player.openInventory(inventory);
				}
			}
		}.runTask(Main.getPlugin());

		activeGUIs.put(inventory, this);
		inventoryZocker.onOpen(this, this.getInventory(), getZocker().getPlayer());

		if (this.inventoryZocker instanceof InventoryUpdateZocker) {
			InventoryUpdateZocker inventoryUpdateZocker = (InventoryUpdateZocker) this.inventoryZocker;

			Workers.FRONTEND_WORKER.addJob(new JobRunnable("openInventory", "InventoryActive") {
				@Override
				public void run() {
					try {
						if (!activeGUIs.containsValue(InventoryActive.this)) {
							this.cancel();
						} else {
							inventoryUpdateZocker.onUpdate();
							inventoryUpdateZocker.update(zocker);
							inventoryUpdateZocker.getEntries().clear();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, 0, inventoryUpdateZocker.getUpdateOffset(), inventoryUpdateZocker.getUpdateTimeUnit(), WorkerPriority.HIGH);
		}
	}

	/**
	 * Start updating.
	 */
	private void startUpdating() {
		for (GUIActiveListener.GUIActiveEntry activeEntry : this.activeEntries.values())
			activeEntry.start();
	}

	/**
	 * Stop updating.
	 */
	public void stopUpdating() {
		for (GUIActiveListener.GUIActiveEntry activeEntry : this.activeEntries.values())
			activeEntry.cancel();
	}

	/**
	 * Delete object.
	 */
	public void deleteObject() {
		activeGUIs.remove(this.inventory, this);
	}

	/**
	 * Update.
	 *
	 * @param entry the entry
	 */
	private void update(InventoryEntry entry) {
		inventory.setItem(entry.getSlot(), entry.getItemStackSupplier().get());
	}

	/**
	 * Update async.
	 *
	 * @param entry the entry
	 */
	protected void updateAsync(InventoryEntry entry) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () ->
			inventory.setItem(entry.getSlot(), entry.getItemStackSupplier().get()));
	}

	/**
	 * Gets active entry.
	 *
	 * @param slot the slot
	 * @return the active entry
	 */
	private GUIActiveListener.GUIActiveEntry getActiveEntry(int slot) {
		return activeEntries.get(slot);
	}

	public HashMap<Integer, GUIActiveListener.GUIActiveEntry> getActiveEntries() {
		return activeEntries;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public InventoryZocker getInventoryZocker() {
		return inventoryZocker;
	}

	public Zocker getZocker() {
		return zocker;
	}

	public static HashMap<Inventory, InventoryActive> getActiveInventorys() {
		return activeGUIs;
	}

	@Deprecated
	public static HashMap<Inventory, InventoryActive> getActiveGUIs() {
		return activeGUIs;
	}

	public static class GUIActiveListener implements Listener {

		/**
		 * On inventory close event.
		 *
		 * @param event the event
		 */
		@EventHandler(priority = EventPriority.LOW)
		private void onInventoryCloseEvent(InventoryCloseEvent event) {
			try {
				InventoryActive active = InventoryActive.valueOf(event.getInventory());
				if (active == null) return;
				Zocker zocker = Zocker.getZocker(event.getPlayer().getUniqueId());
				if (zocker != null) {

					if (active.inventoryZocker instanceof InventoryAnvilZocker) {
						active.inventory.clear();
					}

					active.inventoryZocker.close(zocker);

//					@Deprecated
					new BukkitRunnable() {
						@Override
						public void run() {
							active.inventoryZocker.onClose(active.inventoryZocker, event);
						}
					}.runTaskAsynchronously(Main.getPlugin());

					active.inventoryZocker.removeFromActives(zocker, active);
					return;
				}

				throw new NullPointerException("InventoryCloseEvent zocker is null");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * On click.
		 *
		 * @param event the event
		 */
		@EventHandler(priority = EventPriority.LOW)
		private void onClick(InventoryClickEvent event) {
			Player player = (Player) event.getWhoClicked();
			Inventory inventory = player.getOpenInventory().getTopInventory();

			InventoryActive active = InventoryActive.valueOf(inventory);
			if (active == null) return;

			if (player.getOpenInventory().getTopInventory().equals(inventory)) {
				if (!active.inventoryZocker.isMovable()) {
					event.setCancelled(true);
				}
			}

			if (active.getInventoryZocker() instanceof InventoryAnvilZocker) {
				InventoryAnvilZocker inventoryAnvilZocker = (InventoryAnvilZocker) active.getInventoryZocker();
				CustomAnvil anvil = inventoryAnvilZocker.getAnvil();
				if (anvil == null) return;

				if (event.getSlotType() == InventoryType.SlotType.RESULT) {
					CompletableFuture.runAsync(() -> {
						String renameText = anvil.getRenameText();
						if (renameText != null) {
							if (renameText.startsWith("ff")) {
								renameText = renameText.substring(2);
								inventoryAnvilZocker.onResult(renameText);
								return;
							}
						}

						inventoryAnvilZocker.onResult(anvil.getRenameText());
					});
				}
			}

			// Trigger onClick event
			active.getInventoryZocker().onClick(active.getInventoryZocker(), event);

			GUIActiveEntry activeEntry = active.getActiveEntry(event.getSlot());
			if (activeEntry == null) return;

			if (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.WINDOW_BORDER_LEFT) {
				CompatibleSound.playChangedSound(player);
			} else if (event.getClick() == ClickType.RIGHT || event.getClick() == ClickType.SHIFT_RIGHT || event.getClick() == ClickType.WINDOW_BORDER_RIGHT) {
				CompatibleSound.playChangedSound(player);
			}

			if (!event.getClickedInventory().equals(inventory)) return;

			if (activeEntry.getEntry().getClickAction(event.getClick()) == null) return;
			activeEntry.getEntry().getClickAction(event.getClick()).stream().filter(Objects::nonNull).forEach(consumer -> {
				if (activeEntry.getEntry().isAsync()) {
					CompletableFuture.runAsync(() -> consumer.accept(event));
					return;
				}

				consumer.accept(event);
			});
		}

		public static class GUIActiveEntry {

			private boolean alive;

			private InventoryActive inventoryActive;
			private InventoryEntry entry;
			private TimeUnit timeUnit;
			private Integer offset;


			/**
			 * Instantiates a new Gui active entry.
			 *
			 * @param inventoryActive the inventoryZocker active
			 * @param entry           the entry
			 */
			public GUIActiveEntry(InventoryActive inventoryActive, InventoryEntry entry) {
				this.inventoryActive = inventoryActive;
				this.entry = entry;
				this.timeUnit = entry.getUpdateTimeUnit();
				this.offset = Math.toIntExact(entry.getUpdateOffset());
				this.alive = entry.getUpdateTimeUnit() != null;
			}

			/**
			 * Start.
			 */
			private void start() {
				if (!alive) return;

				Workers.FRONTEND_WORKER.addJob(new JobRunnable("setupWorker", "GUIActiveEntry") {
					@Override
					public void run() {
						try {
							if (!alive) {
								this.cancel();
								return;
							}

							if (entry.isAsync()) inventoryActive.updateAsync(entry);
							else inventoryActive.update(entry);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, 0, offset, timeUnit, WorkerPriority.HIGH);

			}

			/**
			 * Cancel.
			 */
			public void cancel() {
				this.alive = false;
			}

			/**
			 * Is alive boolean.
			 *
			 * @return the boolean
			 */
			public boolean isAlive() {
				return alive;
			}

			/**
			 * Sets alive.
			 *
			 * @param alive the alive
			 */
			public void setAlive(boolean alive) {
				this.alive = alive;
			}

			/**
			 * Gets inventoryZocker active.
			 *
			 * @return the inventoryZocker active
			 */
			public InventoryActive getInventoryActive() {
				return inventoryActive;
			}

			/**
			 * Sets inventoryZocker active.
			 *
			 * @param inventoryActive the inventoryZocker active
			 */
			public void setInventoryActive(InventoryActive inventoryActive) {
				this.inventoryActive = inventoryActive;
			}

			/**
			 * Gets entry.
			 *
			 * @return the entry
			 */
			public InventoryEntry getEntry() {
				return entry;
			}

			/**
			 * Sets entry.
			 *
			 * @param entry the entry
			 */
			public void setEntry(InventoryEntry entry) {
				this.entry = entry;
			}

			/**
			 * Gets time unit.
			 *
			 * @return the time unit
			 */
			public TimeUnit getTimeUnit() {
				return timeUnit;
			}

			/**
			 * Sets time unit.
			 *
			 * @param timeUnit the time unit
			 */
			public void setTimeUnit(TimeUnit timeUnit) {
				this.timeUnit = timeUnit;
			}

			/**
			 * Gets offset.
			 *
			 * @return the offset
			 */
			public Integer getOffset() {
				return offset;
			}

			/**
			 * Sets offset.
			 *
			 * @param offset the offset
			 */
			public void setOffset(Integer offset) {
				this.offset = offset;
			}
		}
	}
}