package minecraft.core.zocker.pro.inventory.browser;

import minecraft.core.zocker.pro.Zocker;
import minecraft.core.zocker.pro.inventory.InventoryEntry;
import minecraft.core.zocker.pro.inventory.util.ItemBuilder;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class InventoryTab<T extends Zocker> {
    private final List<Integer> availableSlots = Arrays.stream(IntStream.range(0, 53).toArray()).boxed().collect(Collectors.toList());

    private final List<InventoryEntry> entryList = new ArrayList<>();

    private InventoryBrowser<T> browser;

    public InventoryTab() {
        final List<Integer> borderSlots = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53);
        availableSlots.removeAll(borderSlots);
    }

    protected final void addItem(InventoryEntry item) {
        entryList.add(item);
    }

    protected final void reload(T zocker) {
        openTab(zocker, getTabSlot());
    }

    protected final void openTab(T zocker, int key) {
        browser.open(zocker, key);
    }

    public abstract int getTabSlot();

    public abstract ItemBuilder getTabSlotItem();

    public abstract void setupTab(T zocker);

    public InventoryEntry getInfoItem(T zocker) {
        return null;
    }

    protected final String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public InventoryBrowser<T> getBrowser() {
        return browser;
    }

    public void setBrowser(InventoryBrowser<T> browser) {
        this.browser = browser;
    }

    public List<InventoryEntry> getEntryList() {
        return entryList;
    }
}