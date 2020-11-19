package minecraft.core.zocker.pro.inventory;

import com.google.common.collect.Maps;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class InventoryEntry {

	private HashMap<ClickType, Consumer<InventoryClickEvent>> clickActions = Maps.newHashMap();


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
		clickActions.put(clickType, consumer);
	}

	/**
	 * Add action.
	 *
	 * @param clickType the click type
	 * @param consumer  the consumer
	 */
	public void addAction(ClickType clickType, Consumer<InventoryClickEvent> consumer) {
		clickActions.put(clickType, consumer);
	}

	/**
	 * On click.
	 *
	 * @param consumer the consumer
	 */
	public void onClick(Consumer<InventoryClickEvent> consumer) {
		clickActions.put(ClickType.LEFT, consumer);
		clickActions.put(ClickType.SHIFT_LEFT, consumer);
		clickActions.put(ClickType.WINDOW_BORDER_LEFT, consumer);
	}

	/**
	 * On right click.
	 *
	 * @param consumer the consumer
	 */
	public void onRightClick(Consumer<InventoryClickEvent> consumer) {
		clickActions.put(ClickType.RIGHT, consumer);
		clickActions.put(ClickType.SHIFT_RIGHT, consumer);
		clickActions.put(ClickType.WINDOW_BORDER_RIGHT, consumer);
	}

	/**
	 * On left click.
	 *
	 * @param consumer the consumer
	 */
	public void onLeftClick(Consumer<InventoryClickEvent> consumer) {
		clickActions.put(ClickType.LEFT, consumer);
		clickActions.put(ClickType.SHIFT_LEFT, consumer);
		clickActions.put(ClickType.WINDOW_BORDER_LEFT, consumer);
	}

	/**
	 * On all clicks.
	 *
	 * @param consumer the consumer
	 */
	public void onAllClicks(Consumer<InventoryClickEvent> consumer) {
		for (ClickType value : ClickType.values()) {
			clickActions.put(value, consumer);
		}
	}

	/**
	 * Gets click action.
	 *
	 * @param clickType the click type
	 * @return the click action
	 */
	public Consumer<InventoryClickEvent> getClickAction(ClickType clickType) {
		return clickActions.get(clickType);
	}

	/**
	 * Sets actions.
	 *
	 * @param clickActions the click actions
	 * @return the actions
	 */
	public InventoryEntry setActions(HashMap<ClickType, Consumer<InventoryClickEvent>> clickActions) {
		this.clickActions = clickActions;
		return this;
	}

	public HashMap<ClickType, Consumer<InventoryClickEvent>> getClickActions() {
		return clickActions;
	}
}

