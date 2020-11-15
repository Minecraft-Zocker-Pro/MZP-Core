package minecraft.core.zocker.pro.nms.v1_8_R3.anvil;

import net.minecraft.server.v1_8_R3.IInventory;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryAnvil;
import org.bukkit.inventory.InventoryHolder;

public class AnvilInventoryCustom extends CraftInventoryAnvil {

    final InventoryHolder holder;

    public AnvilInventoryCustom(InventoryHolder holder, IInventory inventory, IInventory resultInventory) {
        super(inventory, resultInventory);
        this.holder = holder;
    }

    @Override
    public InventoryHolder getHolder() {
        return holder;
    }
}
