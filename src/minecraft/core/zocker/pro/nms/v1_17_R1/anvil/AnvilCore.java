package minecraft.core.zocker.pro.nms.v1_17_R1.anvil;

import minecraft.core.zocker.pro.nms.api.anvil.CustomAnvil;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public class AnvilCore implements minecraft.core.zocker.pro.nms.api.anvil.AnvilCore {

	@Override
	public CustomAnvil createAnvil(Player player) {
		EntityPlayer p = ((CraftPlayer) player).getHandle();
		return new AnvilView(p.nextContainerCounter(), p, null);
	}

	@Override
	public CustomAnvil createAnvil(Player player, InventoryHolder holder) {
		EntityPlayer p = ((CraftPlayer) player).getHandle();
		return new AnvilView(p.nextContainerCounter(), p, holder);
	}
}
