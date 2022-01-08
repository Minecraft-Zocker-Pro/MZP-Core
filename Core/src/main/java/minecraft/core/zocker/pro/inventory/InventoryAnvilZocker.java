package minecraft.core.zocker.pro.inventory;

import minecraft.core.zocker.pro.nms.api.anvil.CustomAnvil;
import org.bukkit.event.inventory.InventoryType;

public abstract class InventoryAnvilZocker extends InventoryZocker {

	private CustomAnvil anvil;

	public abstract InventoryEntry getLeftInventoryEntry();

	public abstract InventoryEntry getRightInventoryEntry();

	public abstract InventoryEntry getResultInventoryEntry();

	public abstract void onResult(String output);

	/**
	 * Gets inventory type.
	 *
	 * @return the inventory type
	 */
	@Override
	public InventoryType getInventoryType() {
		return InventoryType.ANVIL;
	}

	/**
	 * Gets size.
	 *
	 * @return the size
	 */
	@Override
	public Integer getSize() {
		return 0;
	}

	public abstract int getLevelCost();

	public CustomAnvil getAnvil() {
		return anvil;
	}

	protected void setAnvil(CustomAnvil anvil) {
		this.anvil = anvil;
	}
}