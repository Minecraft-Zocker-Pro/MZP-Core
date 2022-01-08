package minecraft.core.zocker.pro.inventory;

import com.google.common.collect.Maps;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class InventoryEntry {

	private HashMap<ClickType, List<Consumer<InventoryClickEvent>>> clickActions = Maps.newHashMap();

	/**
	 * Gets update time unit.
	 *
	 * @return the update time unit
	 */
	public abstract TimeUnit getUpdateTimeUnit();

	/**
	 * Gets update offset.
	 *
	 * @return the update offset
	 */
	public abstract long getUpdateOffset();

	/**
	 * Gets slot.
	 *
	 * @return the slot
	 */
	public abstract Integer getSlot();

	/**
	 * Gets item.
	 *
	 * @return the item
	 */
	public abstract ItemStack getItem();

	/**
	 * Is Async boolean.
	 *
	 * @return the async boolean
	 */
	public abstract boolean isAsync();

	/**
	 * Gets item stack supplier.
	 *
	 * @return the item stack supplier
	 */
	public abstract Supplier<ItemStack> getItemStackSupplier();

	/**
	 * Sets action.
	 *
	 * @param clickType the click type
	 * @param consumer  the consumer
	 */
	public void setAction(ClickType clickType, Consumer<InventoryClickEvent> consumer) {
		List<Consumer<InventoryClickEvent>> consumers = new ArrayList<>();
		consumers.add(consumer);
		clickActions.put(clickType, consumers);
	}

	/**
	 * Add action.
	 *
	 * @param clickType the click type
	 * @param consumer  the consumer
	 */
	public void addAction(ClickType clickType, Consumer<InventoryClickEvent> consumer) {
		List<Consumer<InventoryClickEvent>> consumerList = clickActions.get(clickType);
		if (consumerList == null) {
			List<Consumer<InventoryClickEvent>> consumers = new ArrayList<>();
			consumers.add(consumer);
			clickActions.put(clickType, consumers);
			return;
		}

		consumerList.add(consumer);
		clickActions.put(clickType, consumerList);
	}

	/**
	 * On click.
	 *
	 * @param consumer the consumer
	 */
	public void onClick(Consumer<InventoryClickEvent> consumer) {
		this.onLeftClick(consumer);
	}

	/**
	 * On right click.
	 *
	 * @param consumer the consumer
	 */
	public void onRightClick(Consumer<InventoryClickEvent> consumer) {
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
	}

	/**
	 * On left click.
	 *
	 * @param consumer the consumer
	 */
	public void onLeftClick(Consumer<InventoryClickEvent> consumer) {
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
	}

	/**
	 * On all clicks.
	 *
	 * @param consumer the consumer
	 */
	public void onAllClicks(Consumer<InventoryClickEvent> consumer) {
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
	}

	/**
	 * Gets click action.
	 *
	 * @param clickType the click type
	 * @return the click action
	 */
	public List<Consumer<InventoryClickEvent>> getClickAction(ClickType clickType) {
		return clickActions.get(clickType);
	}

	/**
	 * Sets actions.
	 *
	 * @param clickActions the click actions
	 * @return the actions
	 */
	public InventoryEntry setActions(HashMap<ClickType, List<Consumer<InventoryClickEvent>>> clickActions) {
		this.clickActions = clickActions;
		return this;
	}

	public HashMap<ClickType, List<Consumer<InventoryClickEvent>>> getClickActions() {
		return clickActions;
	}
}

