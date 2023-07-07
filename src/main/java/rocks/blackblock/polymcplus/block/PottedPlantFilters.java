package rocks.blackblock.polymcplus.block;

import io.github.theepicblock.polymc.api.PolyRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * Filters to use a potted plant state
 * (We'll use the oak sapling for now)
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.5.0
 */
public class PottedPlantFilters {

    public static Block[] POTTED_PLANT_BLOCKS = {Blocks.POTTED_OAK_SAPLING};

    public static final Predicate<BlockState> POTTED_PLANT_FILTER = (blockState) -> {
        return true;
    };

    public static final BiConsumer<Block, PolyRegistry> POTTED_PLANT_ON_FIRST_REGISTER = (block, polyRegistry) -> {
        // @TODO: replace with a wizard block poly!
        polyRegistry.registerBlockPoly(block, new ConditionalPropertyReplacementPoly(Blocks.POTTED_DARK_OAK_SAPLING, POTTED_PLANT_FILTER));
    };

}
