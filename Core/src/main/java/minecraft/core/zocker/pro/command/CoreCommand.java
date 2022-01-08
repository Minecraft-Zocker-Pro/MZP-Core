package minecraft.core.zocker.pro.command;

import minecraft.core.zocker.pro.CorePlugin;
import minecraft.core.zocker.pro.Main;
import minecraft.core.zocker.pro.Zocker;
import minecraft.core.zocker.pro.event.CorePluginReloadEvent;
import minecraft.core.zocker.pro.inventory.InventoryEntry;
import minecraft.core.zocker.pro.inventory.InventoryZocker;
import minecraft.core.zocker.pro.inventory.builder.InventoryEntryBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredListener;

import java.util.ArrayList;
import java.util.List;

public class CoreCommand extends Command {

	public CoreCommand() {
		super("core", "mzp.core.command.core", new ArrayList<>());
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		return null;
	}

	@Override
	public void onExecute(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Zocker zocker = Zocker.getZocker(((Player) sender).getUniqueId());
			new CoreCommandInventory(zocker).open(zocker);
		}
	}

	private class CoreCommandInventory extends InventoryZocker {

		private final Zocker zocker;

		public CoreCommandInventory(Zocker zocker) {
			this.zocker = zocker;
		}

		@Override
		public String getTitle() {
			return "Core plugins";
		}

		@Override
		public InventoryType getInventoryType() {
			return InventoryType.CHEST;
		}

		@Override
		public Integer getSize() {
			int size = CorePlugin.getPlugins().size();

			if (size <= 9) size = 9;
			if (size >= 10 && size <= 18) size = 18;
			if (size >= 19 && size <= 27) size = 27;
			if (size >= 28 && size <= 36) size = 36;
			if (size >= 37 && size <= 45) size = 45;
			if (size >= 46 && size <= 54) size = 54;

			return size;
		}

		@Override
		public InventoryEntry getNextArrow() {
			return null;
		}

		@Override
		public InventoryEntry getPreviousArrow() {
			return null;
		}

		@Override
		public InventoryEntry getEmptyArrow() {
			return null;
		}

		@Override
		public InventoryEntry getCloseButton() {
			return null;
		}

		@Override
		public ItemStack getBorder() {
			return null;
		}

		@Override
		public void setupInventory() {
			int slot = 0;

			for (CorePlugin corePlugin : CorePlugin.getPlugins()) {
				if (corePlugin == null) continue;
				PluginDescriptionFile pluginDescriptionFile = corePlugin.getDescription();

				ItemStack itemStack = new ItemStack(corePlugin.getDisplayItem());
				ItemMeta itemMeta = itemStack.getItemMeta();
				List<String> lore = new ArrayList<>();

				itemMeta.setDisplayName("§6§l" + corePlugin.getName() + " §3v" + pluginDescriptionFile.getVersion());
				lore.add("§3Description");
				if (getDescription() == null) {
					lore.add("");
				} else {
					lore.add("§7" + getDescription());
				}

				if (pluginDescriptionFile.getAuthors() != null) {
					StringBuilder stringBuilder = new StringBuilder();
					if (pluginDescriptionFile.getAuthors().size() > 1) {
						lore.add("§3Authors");
						for (String author : pluginDescriptionFile.getAuthors()) {
							stringBuilder.append("§7").append(author).append(",");
						}

						lore.add(stringBuilder.toString());
					} else {
						lore.add("§3Author");
						lore.add("§7" + pluginDescriptionFile.getAuthors().get(0));
					}
				}

				lore.add("");

				ArrayList<RegisteredListener> registeredListeners = HandlerList.getRegisteredListeners(corePlugin);
				if (registeredListeners.size() != 0) {
					lore.add("§3" + registeredListeners.size() + " listener loaded");

					for (RegisteredListener registeredListener : registeredListeners) {
						if (registeredListener == null) continue;
						lore.add("§7" + registeredListener.getListener().toString().split("@")[0]);
					}
				}

				lore.add("");
				lore.add("§3Right click for reload");
				lore.add("§3Left click for help");

				itemMeta.setLore(lore);
				itemStack.setItemMeta(itemMeta);

				this.addItem(new InventoryEntryBuilder()
					.setItem(itemStack)
					.onRightClick(event -> {
						long start = System.currentTimeMillis();
						corePlugin.reload();
						Bukkit.getPluginManager().callEvent(new CorePluginReloadEvent(corePlugin));
						event.getWhoClicked().sendMessage(Main.CORE_MESSAGE.getString("message.prefix") + "§6" + corePlugin.getName() + " §3has been reloaded §7[took " + (System.currentTimeMillis() - start) + "ms]");
						zocker.getPlayer().closeInventory();
					})
					.onLeftClick(event -> {
						if (corePlugin.getHelpCommand() == null) return;
						zocker.getPlayer().chat("/" + corePlugin.getHelpCommand());
						zocker.getPlayer().closeInventory();
					})
					.setSlot(slot)
					.build());

				slot++;
			}
		}
	}
}
