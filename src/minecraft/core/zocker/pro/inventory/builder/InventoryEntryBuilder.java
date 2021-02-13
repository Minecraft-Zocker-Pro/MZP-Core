package minecraft.core.zocker.pro.inventory.builder;

import com.google.common.collect.Maps;
import minecraft.core.zocker.pro.inventory.InventoryEntry;
import minecraft.core.zocker.pro.inventory.util.ItemBuilder;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class InventoryEntryBuilder {

	private final HashMap<ClickType, List<Consumer<InventoryClickEvent>>> clickActions = Maps.newHashMap();
	private ItemStack itemStack;
	private Supplier<ItemStack> itemStackSupplier;
	private Integer slot;
	private TimeUnit updateTimeUnit;
	private long updateOffset;
	private boolean async = true;

	/**
	 * Sets item.
	 *
	 * @param itemStack the item stack
	 * @return the item
	 */
	public InventoryEntryBuilder setItem(ItemStack itemStack) {
		this.itemStack = new ItemStack(itemStack);
		return this;
	}

	/**
	 * Sets item.
	 *
	 * @param itemBuilder the item builder
	 * @return the item
	 */
	public InventoryEntryBuilder setItem(ItemBuilder itemBuilder) {
		this.itemStack = itemBuilder.toItemStack();
		return this;
	}

	/**
	 * Sets item.
	 *
	 * @param itemStackSupplier the item stack supplier
	 * @return the item
	 */
	public InventoryEntryBuilder setItem(Supplier<ItemStack> itemStackSupplier) {
		this.itemStackSupplier = itemStackSupplier;
		return this;
	}

	/**
	 * Gets slot.
	 *
	 * @return the slot
	 */
	public Integer getSlot() {
		return slot;
	}

	/**
	 * Sets slot.
	 *
	 * @param slot the slot
	 * @return the slot
	 */
	public InventoryEntryBuilder setSlot(Integer slot) {
		this.slot = slot;
		return this;
	}

	/**
	 * Update async gui entry builder.
	 *
	 * @param async the update async
	 * @return the gui entry builder
	 */
	public InventoryEntryBuilder setAsync(boolean async) {
		this.async = async;
		return this;
	}

	/**
	 * Gets update time unit.
	 *
	 * @return the update time unit
	 */
	public TimeUnit getUpdateTimeUnit() {
		return updateTimeUnit;
	}

	/**
	 * Sets update time unit.
	 *
	 * @param updateTimeUnit the update time unit
	 * @return the update time unit
	 */
	public InventoryEntryBuilder setUpdateTimeUnit(TimeUnit updateTimeUnit) {
		this.updateTimeUnit = updateTimeUnit;
		return this;
	}

	/**
	 * Gets update offset.
	 *
	 * @return the update offset
	 */
	public long getUpdateOffset() {
		return updateOffset;
	}

	/**
	 * Sets update offset.
	 *
	 * @param updateOffset the update offset
	 * @return the update offset
	 */
	public InventoryEntryBuilder setUpdateOffset(long updateOffset) {
		this.updateOffset = updateOffset;
		return this;
	}

	/**
	 * Is update async boolean.
	 *
	 * @return the boolean
	 */
	public boolean isAsync() {
		return async;
	}

	/**
	 * Add action gui entry builder.
	 *
	 * @param clickType the click type
	 * @param consumer  the consumer
	 * @return the gui entry builder
	 */
	public InventoryEntryBuilder addAction(ClickType clickType, Consumer<InventoryClickEvent> consumer) {
		List<Consumer<InventoryClickEvent>> consumerList = clickActions.get(clickType);
		if (consumerList == null) {
			List<Consumer<InventoryClickEvent>> consumers = new ArrayList<>();
			consumers.add(consumer);
			clickActions.put(clickType, consumers);
			return this;
		}

		consumerList.add(consumer);
		clickActions.put(clickType, consumerList);
		return this;
	}

	/**
	 * On click gui entry builder.
	 *
	 * @param consumer the consumer
	 * @return the gui entry builder
	 */
	public InventoryEntryBuilder onClick(Consumer<InventoryClickEvent> consumer) {
		this.onLeftClick(consumer);
		return this;
	}

	/**
	 * On right click gui entry builder.
	 *
	 * @param consumer the consumer
	 * @return the gui entry builder
	 */
	public InventoryEntryBuilder onRightClick(Consumer<InventoryClickEvent> consumer) {
		List<Consumer<InventoryClickEvent>> rightClickConsumers = clickActions.get(ClickType.RIGHT);
		List<Consumer<InventoryClickEvent>> shiftRightClickConsumers = clickActions.get(ClickType.SHIFT_RIGHT);
		List<Consumer<InventoryClickEvent>> windowBorderRightClickConsumers = clickActions.get(ClickType.WINDOW_BORDER_RIGHT);

		// RIGHT
		if (rightClickConsumers == null) {
			List<Consumer<InventoryClickEvent>> consumers = new ArrayList<>();
			consumers.add(consumer);
			clickActions.put(ClickType.RIGHT, consumers);
		} else {
			rightClickConsumers.add(consumer);
			clickActions.put(ClickType.RIGHT, rightClickConsumers);
		}

		// SHIFT_RIGHT
		if (shiftRightClickConsumers == null) {
			List<Consumer<InventoryClickEvent>> consumers = new ArrayList<>();
			consumers.add(consumer);
			clickActions.put(ClickType.SHIFT_RIGHT, consumers);
		} else {
			shiftRightClickConsumers.add(consumer);
			clickActions.put(ClickType.SHIFT_RIGHT, shiftRightClickConsumers);
		}

		// WINDOW_BORDER_RIGHT
		if (windowBorderRightClickConsumers == null) {
			List<Consumer<InventoryClickEvent>> consumers = new ArrayList<>();
			consumers.add(consumer);
			clickActions.put(ClickType.WINDOW_BORDER_RIGHT, consumers);
		} else {
			windowBorderRightClickConsumers.add(consumer);
			clickActions.put(ClickType.WINDOW_BORDER_RIGHT, windowBorderRightClickConsumers);
		}

		return this;
	}

	/**
	 * On left click gui entry builder.
	 *
	 * @param consumer the consumer
	 * @return the gui entry builder
	 */
	public InventoryEntryBuilder onLeftClick(Consumer<InventoryClickEvent> consumer) {
		List<Consumer<InventoryClickEvent>> leftClickConsumers = clickActions.get(ClickType.LEFT);
		List<Consumer<InventoryClickEvent>> shiftLeftClickConsumers = clickActions.get(ClickType.SHIFT_LEFT);
		List<Consumer<InventoryClickEvent>> windowBorderLeftClickConsumers = clickActions.get(ClickType.WINDOW_BORDER_LEFT);

		// LEFT
		if (leftClickConsumers == null) {
			List<Consumer<InventoryClickEvent>> consumers = new ArrayList<>();
			consumers.add(consumer);
			clickActions.put(ClickType.LEFT, consumers);
		} else {
			leftClickConsumers.add(consumer);
			clickActions.put(ClickType.LEFT, leftClickConsumers);
		}

		// SHIFT_LEFT
		if (shiftLeftClickConsumers == null) {
			List<Consumer<InventoryClickEvent>> consumers = new ArrayList<>();
			consumers.add(consumer);
			clickActions.put(ClickType.SHIFT_LEFT, consumers);
		} else {
			shiftLeftClickConsumers.add(consumer);
			clickActions.put(ClickType.SHIFT_LEFT, shiftLeftClickConsumers);
		}

		// WINDOW_BORDER_LEFT
		if (windowBorderLeftClickConsumers == null) {
			List<Consumer<InventoryClickEvent>> consumers = new ArrayList<>();
			consumers.add(consumer);
			clickActions.put(ClickType.WINDOW_BORDER_LEFT, consumers);
		} else {
			windowBorderLeftClickConsumers.add(consumer);
			clickActions.put(ClickType.WINDOW_BORDER_LEFT, windowBorderLeftClickConsumers);
		}

		return this;
	}

	/**
	 * On all clicks gui entry builder.
	 *
	 * @param consumer the consumer
	 * @return the gui entry builder
	 */
	public InventoryEntryBuilder onAllClicks(Consumer<InventoryClickEvent> consumer) {
		for (ClickType clickType : ClickType.values()) {
			List<Consumer<InventoryClickEvent>> clickTypeConsumer = clickActions.get(clickType);
			if (clickTypeConsumer == null) {
				List<Consumer<InventoryClickEvent>> consumers = new ArrayList<>();
				consumers.add(consumer);
				clickActions.put(clickType, consumers);
			} else {
				clickTypeConsumer.add(consumer);
				clickActions.put(clickType, clickTypeConsumer);
			}
		}

		return this;
	}

	/**
	 * Build gui entry.
	 *
	 * @return the gui entry
	 */
	public InventoryEntry build() {
		return new InventoryEntry() {
			@Override
			public TimeUnit getUpdateTimeUnit() {
				return updateTimeUnit;
			}

			@Override
			public long getUpdateOffset() {
				return updateOffset;
			}

			@Override
			public Integer getSlot() {
				return slot;
			}

			@Override
			public ItemStack getItem() {
				return itemStack;
			}

			@Override
			public boolean isAsync() {
				return async;
			}

			@Override
			public Supplier<ItemStack> getItemStackSupplier() {
				return itemStackSupplier;
			}
		}.setActions(clickActions);
	}
}
