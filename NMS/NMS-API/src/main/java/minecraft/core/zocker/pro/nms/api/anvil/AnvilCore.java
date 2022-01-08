package minecraft.core.zocker.pro.nms.api.anvil;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public interface AnvilCore {
    public CustomAnvil createAnvil(Player player);

    public CustomAnvil createAnvil(Player player, InventoryHolder holder);
}
