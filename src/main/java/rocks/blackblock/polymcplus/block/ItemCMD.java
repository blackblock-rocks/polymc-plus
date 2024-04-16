package rocks.blackblock.polymcplus.block;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

/**
 * Item & CMD info
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.2.0
 */
public class ItemCMD {

    // The client-side replacement item
    protected Item client_item;

    // The CMD value for the item
    protected int cmd_value;

    /**
     * Set the item & CMD value to use
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public ItemCMD(Item client_item, int cmd_value) {
        this.setClientItem(client_item, cmd_value);
    }

    /**
     * Set the item & CMD value to use
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public void setClientItem(Item client_item, int cmd_value) {
        this.client_item = client_item;
        this.cmd_value = cmd_value;
    }

    /**
     * Get the client item's identifier
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public Identifier getIdentifier() {
        return Registries.ITEM.getId(this.client_item);
    }

    /**
     * String representation of this instance
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public String toString() {
        return "ItemCMD{" + this.cmd_value + "," + this.client_item + "}";
    }
}
