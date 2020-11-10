package minecraft.core.zocker.pro.inventory.builder;

import minecraft.core.zocker.pro.inventory.InventoryEntry;
import minecraft.core.zocker.pro.inventory.page.InventoryPage;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class InventoryPageBuilder {

    private final ArrayList<InventoryEntry> entries = new ArrayList<>();
    private final Collection<Integer> blacklistedSlots = new ArrayList<>();
    private String title;
    private Integer size;

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title.
     *
     * @param title the title
     * @return the title
     */
    public InventoryPageBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Gets size.
     *
     * @return the size
     */
    public Integer getSize() {
        return size;
    }

    /**
     * Sets size.
     *
     * @param size the size
     * @return the size
     */
    public InventoryPageBuilder setSize(Integer size) {
        this.size = size;
        return this;
    }

    /**
     * Add entry gui page builder.
     *
     * @param entry the entry
     * @return the gui page builder
     */
    public InventoryPageBuilder addEntry(InventoryEntry entry) {
        entries.add(entry);
        return this;
    }

    /**
     * Add entry gui page builder.
     *
     * @param itemStack the item stack
     * @return the gui page builder
     */
    public InventoryPageBuilder addEntry(ItemStack itemStack) {
        entries.add(new InventoryEntryBuilder().setItem(itemStack).build());
        return this;
    }

    /**
     * Add blacklisted slot gui page builder.
     *
     * @param slot the slot
     * @return the gui page builder
     */
    public InventoryPageBuilder addBlacklistedSlot(Integer slot) {
        blacklistedSlots.add(slot);
        return this;
    }

    /**
     * Add blacklisted slots gui page builder.
     *
     * @param slots the slots
     * @return the gui page builder
     */
    public InventoryPageBuilder addBlacklistedSlots(Integer... slots) {
        blacklistedSlots.addAll(Arrays.asList(slots));
        return this;
    }

    /**
     * Add blacklisted slots gui page builder.
     *
     * @param slots the slots
     * @return the gui page builder
     */
    public InventoryPageBuilder addBlacklistedSlots(Collection<Integer> slots) {
        blacklistedSlots.addAll(slots);
        return this;
    }

    /**
     * Build gui page.
     *
     * @return the gui page
     */
    public InventoryPage build() {
        InventoryPage page = new InventoryPage() {
            @Override
            public String getTitle() {
                return title;
            }

            @Override
            public Integer getSize() {
                return size;
            }

            @Override
            public void onOpen(InventoryPage page, InventoryOpenEvent event) {

            }

            @Override
            public void onClose(InventoryPage page, InventoryCloseEvent event) {

            }
        };

        for (InventoryEntry entry : entries) {
            page.addItem(entry);
        }

        page.addBlacklistedSlots(blacklistedSlots);

        return page;
    }
}
