package minecraft.core.zocker.pro.inventory.page;

import minecraft.core.zocker.pro.inventory.InventoryEntry;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public abstract class InventoryPage {

    private final ArrayList<InventoryEntry> entries = new ArrayList<>();
    private final Collection<Integer> blacklistedSlots = new ArrayList<>();

    /**
     * Gets title.
     *
     * @return the title
     */
    public abstract String getTitle();

    /**
     * Gets size.
     *
     * @return the size
     */
    public abstract Integer getSize();

    /**
     * On open.
     *
     * @param page  the page
     * @param event the event
     */
    public abstract void onOpen(InventoryPage page, InventoryOpenEvent event);

    /**
     * On close.
     *
     * @param page  the page
     * @param event the event
     */
    public abstract void onClose(InventoryPage page, InventoryCloseEvent event);

    /**
     * Add item.
     *
     * @param entry the entry
     */
    public void addItem(InventoryEntry entry) {
        entries.add(entry);
    }

    /**
     * Add blacklisted slot.
     *
     * @param slot the slot
     */
    public void addBlacklistedSlot(Integer slot) {
        blacklistedSlots.add(slot);
    }

    /**
     * Add blacklisted slots.
     *
     * @param slots the slots
     */
    public void addBlacklistedSlots(Collection<Integer> slots) {
        blacklistedSlots.addAll(slots);
    }

    /**
     * Add blacklisted slots.
     *
     * @param slots the slots
     */
    public void addBlacklistedSlots(Integer... slots) {
        blacklistedSlots.addAll(Arrays.asList(slots));
    }

    /**
     * Gets entries.
     *
     * @return the entries
     */
    public ArrayList<InventoryEntry> getEntries() {
        return entries;
    }

    /**
     * Gets blacklisted slots.
     *
     * @return the blacklisted slots
     */
    public Collection<Integer> getBlacklistedSlots() {
        return blacklistedSlots;
    }
}
