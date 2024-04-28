package rocks.blackblock.polymcplus.block;

import io.github.theepicblock.polymc.api.PolyRegistry;
import net.minecraft.block.*;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * Filters to use turtle eggs
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.5.0
 */
public class TurtleEggFilters {

    public static Block[] TURTLE_EGG_BLOCKS = {Blocks.TURTLE_EGG};

    // Claim the `hatch=1` state
    public static final Predicate<BlockState> TURTLE_EGG_FILTER = (blockState) -> blockState.get(TurtleEggBlock.HATCH) == 1;

    public static final Predicate<BlockState> ONE_TURTLE_EGG_FILTER = TURTLE_EGG_FILTER.and(blockState -> blockState.get(TurtleEggBlock.EGGS) == 1);

    public static final Predicate<BlockState> TWO_TURTLE_EGG_FILTER = TURTLE_EGG_FILTER.and(blockState -> blockState.get(TurtleEggBlock.EGGS) == 2);

    // Make it use the `hatch=2` state instead
    public static final Predicate<BlockState> TURTLE_EGG_REPLACEMENT_FILTER = (blockState) -> {
        if (blockState.get(TurtleEggBlock.EGGS) > 2) return false;
        return blockState.get(TurtleEggBlock.HATCH) == 2;
    };

    public static final BiConsumer<Block, PolyRegistry> TURTLE_EGG_ON_FIRST_REGISTER = (block, polyRegistry) -> {
        polyRegistry.registerBlockPoly(block, new ConditionalPropertyReplacementPoly(Blocks.TURTLE_EGG, TURTLE_EGG_REPLACEMENT_FILTER));
    };

}
