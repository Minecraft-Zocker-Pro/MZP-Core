package minecraft.core.zocker.pro.inventory.browser;

import minecraft.core.zocker.pro.Zocker;
import minecraft.core.zocker.pro.compatibility.CompatibleMaterial;
import minecraft.core.zocker.pro.inventory.InventoryEntry;
import minecraft.core.zocker.pro.inventory.InventoryZocker;
import minecraft.core.zocker.pro.inventory.builder.InventoryEntryBuilder;
import minecraft.core.zocker.pro.inventory.util.ItemBuilder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class InventoryBrowser<T extends Zocker> {

    private final List<Integer> availableSlots = Arrays.stream(IntStream.range(0, 53).toArray()).boxed().collect(Collectors.toList());

    private final ItemStack border = new ItemBuilder(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem())
            .setDisplayName("§7")
            .toItemStack();

    private final ConcurrentMap<Integer, InventoryTab<T>> inventoryTabs = new ConcurrentHashMap<>();
    private final InventoryTab<T> initialTab;

    private final String title;

    @SafeVarargs
    public InventoryBrowser(String title, InventoryTab<T>... pages) {
        this.title = title;
        this.initialTab = pages[0];

        for (InventoryTab<T> page : pages) {
            page.setBrowser(this);
            inventoryTabs.put(page.getTabSlot(), page);
        }

        final List<Integer> borderSlots = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53);
        availableSlots.removeAll(borderSlots);
    }

    public void open(T zocker) {
        open(zocker, initialTab);
    }

    protected void open(T zocker, int key) {
        open(zocker, inventoryTabs.get(key));
    }

    private void open(T zocker, InventoryTab<T> tab) {
        final BrowserSession<T> session = new BrowserSession<>(zocker, title, this::supplyMenu, border, tab);

        session.getInventory().open(zocker);
    }

    private List<InventoryEntry> supplyMenu(BrowserSession<T> session) {
        return inventoryTabs.values()
                .stream()
                .map(tab -> {
                    final ItemBuilder builder = tab.getTabSlotItem();

                    if (tab == session.actualTab) {
                        builder.addEnchantment(Enchantment.DURABILITY, 1);
                        builder.addItemFlag(ItemFlag.HIDE_ENCHANTS);
                    }

                    return new InventoryEntryBuilder()
                            .setSlot(tab.getTabSlot())
                            .setItem(builder.toItemStack())
                            .onClick(e -> {
                                if (e.getSlot() != session.actualTab.getTabSlot()) {
                                    final InventoryTab<T> otherTab = inventoryTabs.get(e.getSlot());
                                    if (otherTab != null) {
                                        session.setActualTab(otherTab);
                                        supplyMenu(session);

                                        session.show();
                                    }
                                }
                            }).build();
                }).collect(Collectors.toList());
    }

    public List<Integer> getAvailableSlots() {
        return availableSlots;
    }

    private static final class BrowserSession<T extends Zocker> {
        private final T zocker;

        private final String title;
        private final Function<BrowserSession<T>, Collection<InventoryEntry>> itemConsumer;

        private final ItemStack border;

        private InventoryTab<T> actualTab;

        public BrowserSession(T zocker, String title, Function<BrowserSession<T>, Collection<InventoryEntry>> itemConsumer, ItemStack border, InventoryTab<T> actualTab) {
            this.zocker = zocker;
            this.title = title;
            this.itemConsumer = itemConsumer;
            this.border = border;
            this.actualTab = actualTab;
        }

        public void show(){
            getInventory().open(zocker);
        }

        public void setActualTab(InventoryTab<T> actualTab) {
            this.actualTab = actualTab;
        }

        public InventoryZocker getInventory() {
            final BrowserSession<T> session = this;
            return new InventoryZocker() {
                @Override
                public String getTitle() {
                    return title;
                }

                @Override
                public InventoryType getInventoryType() {
                    return InventoryType.CHEST;
                }

                @Override
                public Integer getSize() {
                    return 6 * 9;
                }

                @Override
                public ItemStack getBorder() {
                    return border;
                }

                @Override
                public InventoryEntry getInfoSign() {
                    final InventoryTab<T> tab = session.actualTab;
                    final InventoryEntry item = tab.getInfoItem(session.zocker);

                    return item != null
                            ? item
                            : new InventoryEntryBuilder().setItem(border).build();
                }

                @Override
                public void setupInventory() {
                    fillBorders();

                    actualTab.setupTab(zocker);
                    for (InventoryEntry entry : actualTab.getEntryList()) {
                        addItem(entry);
                    }

                    Collection<InventoryEntry> entries = itemConsumer.apply(session);
                    entries.forEach(this::addItem);
                }
            };
        }
    }
}