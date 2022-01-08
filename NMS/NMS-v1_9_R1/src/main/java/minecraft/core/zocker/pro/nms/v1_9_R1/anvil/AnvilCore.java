package minecraft.core.zocker.pro.nms.v1_9_R1.anvil;

import minecraft.core.zocker.pro.nms.api.anvil.CustomAnvil;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public class AnvilCore implements minecraft.core.zocker.pro.nms.api.anvil.AnvilCore {

	@Override
	public CustomAnvil createAnvil(Player player) {
		return new AnvilView(((CraftPlayer) player).getHandle(), null);
	}

	@Override
	public CustomAnvil createAnvil(Player player, InventoryHolder holder) {
		return new AnvilView(((CraftPlayer) player).getHandle(), holder);
	}

}
