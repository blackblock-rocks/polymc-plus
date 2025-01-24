package rocks.blackblock.polymcplus.generator;

import io.github.theepicblock.polymc.api.PolyRegistry;
import io.github.theepicblock.polymc.api.item.ItemPoly;
import io.github.theepicblock.polymc.impl.generator.ItemPolyGenerator;
import io.github.theepicblock.polymc.impl.poly.item.SimpleItemPoly;
import net.minecraft.item.*;
import rocks.blackblock.polymcplus.PolyMcPlus;

/**
 * Class to automatically generate {@link ItemPoly}s for {@link Item}s
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.1.0
 */
public class ItemPolyPlusGenerator {

    /**
     * Generates the most suitable {@link ItemPoly} for a given {@link Item}
     */
    public static ItemPoly generatePoly(Item item, PolyRegistry builder) {

        if (item instanceof PotionItem){
            return new SimpleItemPoly(Items.MILK_BUCKET);
        } else if (item instanceof WrittenBookItem) {
            return new SimpleItemPoly(Items.WRITTEN_BOOK);
        } else if (item instanceof WritableBookItem) {
            return new SimpleItemPoly(Items.WRITABLE_BOOK);
        }

        // Fall back to the basic PolyMc implementation
        return ItemPolyGenerator.generatePoly(item, builder);
    }

    /**
     * Generates the most suitable {@link ItemPoly} and directly adds it to the {@link PolyRegistry}
     * @see #generatePoly(Item, PolyRegistry)
     */
    public static void addItemToBuilder(Item item, PolyRegistry builder) {
        try {
            builder.registerItemPoly(item, generatePoly(item, builder));
        } catch (Exception e) {
            PolyMcPlus.LOGGER.error("Failed to generate a poly for item " + item.getTranslationKey());
            e.printStackTrace();
            PolyMcPlus.LOGGER.error("Attempting to recover by using a default poly. Please report this");
            builder.registerItemPoly(item, (input, player, location) -> new ItemStack(Items.BARRIER));
        }
    }
}
