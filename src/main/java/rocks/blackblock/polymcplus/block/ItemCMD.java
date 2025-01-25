package rocks.blackblock.polymcplus.block;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import rocks.blackblock.bib.util.BibLog;

/**
 * Item & CMD info
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.2.0
 */
public class ItemCMD implements BibLog.Argable {

    // The client-side replacement item
    protected Item client_item;

    // The CMD value for the item
    protected int cmd_value;

    // The unique name of the block state
    protected String block_state_id;

    // The name of the items asset entry
    protected String items_asset_name;

    // The item model identifier
    protected Identifier item_model_identifier;

    /**
     * Set the item & CMD value to use
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public ItemCMD(Item client_item, String block_state_id) {
        this.setClientItem(client_item, block_state_id);
    }

    /**
     * Set the item & CMD value to use
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public void setClientItem(Item client_item, String block_state_id) {
        this.client_item = client_item;
        this.block_state_id = block_state_id;
        this.items_asset_name = block_state_id;
        this.item_model_identifier = Identifier.of("polymcplus", this.items_asset_name);
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
     * Get the Items Asset file name
     * This is where the json will be stored in the `items/` folder
     *
     * @since    0.7.0
     */
    public String getItemsAssetFileName() {
        return this.items_asset_name + ".json";
    }

    /**
     * Get the item model identifier:
     * This is used in the ItemStack's `ITEM_MODEL` component
     *
     * @since    0.7.0
     */
    public Identifier getItemModelIdentifier() {
        return this.item_model_identifier;
    }

    /**
     * BibLog.Arg representation of this instance
     *
     * @since    0.7.0
     */
    @Override
    public BibLog.Arg toBBLogArg() {
        return BibLog.createArg(this)
            .add("cmd_value", this.cmd_value)
            .add("client_item", this.client_item);
    }

    /**
     * String representation of this instance
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public String toString() {
        return this.toBBLogArg().toString();
    }
}
