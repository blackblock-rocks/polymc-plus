package rocks.blackblock.polymcplus.block;

import io.github.theepicblock.polymc.api.PolyRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * Filters to use unlit soul campfires
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.2.0
 */
public class SoulCampfireFilters {

    public static Block[] SOUL_CAMPFIRE_BLOCKS = {Blocks.SOUL_CAMPFIRE};

    // Unlit campfires look identical
    public static final Predicate<BlockState> SOUL_CAMPFIRE_FILTER = (blockState) -> {
        Boolean is_lit = blockState.get(CampfireBlock.LIT);
        return !is_lit;
    };

    public static final BiConsumer<Block,PolyRegistry> SOUL_CAMPFIRE_ON_FIRST_REGISTER = (block, polyRegistry) -> {
        polyRegistry.registerBlockPoly(block, new ConditionalPropertyReplacementPoly(Blocks.CAMPFIRE, SOUL_CAMPFIRE_FILTER));
    };
}
