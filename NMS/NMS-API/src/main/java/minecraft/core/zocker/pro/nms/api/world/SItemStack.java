package minecraft.core.zocker.pro.nms.api.world;

import org.bukkit.entity.Player;

import java.util.Random;

public interface SItemStack {

	Random random = new Random();

	void breakItem(Player player, int amount);

}
