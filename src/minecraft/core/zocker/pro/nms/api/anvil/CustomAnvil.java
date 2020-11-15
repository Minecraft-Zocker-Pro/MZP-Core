package minecraft.core.zocker.pro.nms.api.anvil;

import minecraft.core.zocker.pro.nms.api.anvil.methods.AnvilTextChange;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * NMS interface for creating an anvil view for a single player
 */
public interface CustomAnvil {

	public void setLevelCost(int cost);

	public int getLevelCost();

	public void setCanUse(boolean bool);

	public String getCustomTitle();

	public void setCustomTitle(String title);

	public String getRenameText();

	public void setRenameText(String text);

	public void setOnChange(AnvilTextChange handler);

	public ItemStack getLeftInput();

	public ItemStack getRightInput();

	public ItemStack getOutput();

	public void setLeftInput(ItemStack item);

	public void setRightInput(ItemStack item);

	public void setOutput(ItemStack item);

	public Inventory getInventory();

	/**
	 * Open this anvil for the provided player
	 */
	public void open();

	/**
	 * Force a redraw of the output
	 */
	public void update();
}
