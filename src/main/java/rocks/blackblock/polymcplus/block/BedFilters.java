package rocks.blackblock.polymcplus.block;

import io.github.theepicblock.polymc.api.PolyRegistry;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class BedFilters {

    public static Block[] BED_BLOCKS = {Blocks.GREEN_BED, Blocks.WHITE_BED, Blocks.ORANGE_BED, Blocks.MAGENTA_BED, Blocks.LIGHT_BLUE_BED, Blocks.YELLOW_BED, Blocks.LIME_BED, Blocks.PINK_BED, Blocks.GRAY_BED, Blocks.LIGHT_GRAY_BED, Blocks.CYAN_BED, Blocks.PURPLE_BED, Blocks.BLUE_BED, Blocks.BLACK_BED};

    // Test available bed states
    protected static final Predicate<BlockState> BED_ANY_FILTER = (blockState) -> {

        Boolean occupied = blockState.get(BedBlock.OCCUPIED);

        return occupied;
    };

    protected static final BiConsumer<Block, PolyRegistry> BED_ON_FIRST_REGISTER = (block, polyRegistry) -> polyRegistry.registerBlockPoly(block, input -> {

        // If the input state is one we use as a poly block, return the default state instead
        if (BED_ANY_FILTER.test(input)) {
            return block.getDefaultState();
        }

        return input;
    });

}
