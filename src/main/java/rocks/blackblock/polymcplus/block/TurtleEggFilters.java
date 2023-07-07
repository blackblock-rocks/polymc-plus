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
    public static final Predicate<BlockState> TURTLE_EGG_FILTER = (blockState) -> {

        int amount_of_eggs = blockState.get(TurtleEggBlock.EGGS);

        if (amount_of_eggs != 1) {
            return false;
        }

        int age = blockState.get(TurtleEggBlock.HATCH);

        return age == 1;
    };

    // Make it use the `hatch=2` state instead
    public static final Predicate<BlockState> TURTLE_EGG_REPLACEMENT_FILTER = (blockState) -> {

        int amount_of_eggs = blockState.get(TurtleEggBlock.EGGS);

        if (amount_of_eggs != 1) {
            return false;
        }

        int age = blockState.get(TurtleEggBlock.HATCH);

        return age == 2;
    };

    public static final BiConsumer<Block, PolyRegistry> TURTLE_EGG_ON_FIRST_REGISTER = (block, polyRegistry) -> {
        polyRegistry.registerBlockPoly(block, new ConditionalPropertyReplacementPoly(Blocks.TURTLE_EGG, TURTLE_EGG_REPLACEMENT_FILTER));
    };

}
