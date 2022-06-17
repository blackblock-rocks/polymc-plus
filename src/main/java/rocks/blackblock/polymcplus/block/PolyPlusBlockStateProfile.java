package rocks.blackblock.polymcplus.block;

import io.github.theepicblock.polymc.api.PolyRegistry;
import io.github.theepicblock.polymc.api.block.BlockStateProfile;
import io.github.theepicblock.polymc.impl.poly.block.ConditionalSimpleBlockPoly;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.function.BiConsumer;

public class PolyPlusBlockStateProfile {

    private static final BiConsumer<Block, PolyRegistry> BROWN_MUSHROOM_ON_FIRST_REGISTER = (block, polyRegistry) -> polyRegistry.registerBlockPoly(block, new ConditionalSimpleBlockPoly(Blocks.BROWN_MUSHROOM_BLOCK.getDefaultState(), MushroomFilters.BROWN_MUSHROOM_FILTER));
    private static final BiConsumer<Block,PolyRegistry> RED_MUSHROOM_ON_FIRST_REGISTER = (block, polyRegistry) -> polyRegistry.registerBlockPoly(block, new ConditionalSimpleBlockPoly(Blocks.RED_MUSHROOM_BLOCK.getDefaultState(), MushroomFilters.RED_MUSHROOM_FILTER));
    private static final BiConsumer<Block,PolyRegistry> STEM_MUSHROOM_ON_FIRST_REGISTER = (block, polyRegistry) -> polyRegistry.registerBlockPoly(block, new ConditionalSimpleBlockPoly(Blocks.MUSHROOM_STEM.getDefaultState(), MushroomFilters.STEM_MUSHROOM_FILTER));

    // Actual profiles
    public static final BlockStateProfile BROWN_MUSHROOM_BLOCK_PROFILE = new BlockStateProfile("brown mushroom block", Blocks.BROWN_MUSHROOM_BLOCK, MushroomFilters.BROWN_MUSHROOM_FILTER, BROWN_MUSHROOM_ON_FIRST_REGISTER);
    public static final BlockStateProfile RED_MUSHROOM_BLOCK_PROFILE = new BlockStateProfile("red mushroom block", Blocks.RED_MUSHROOM_BLOCK, MushroomFilters.RED_MUSHROOM_FILTER, RED_MUSHROOM_ON_FIRST_REGISTER);
    public static final BlockStateProfile STEM_MUSHROOM_BLOCK_PROFILE = new BlockStateProfile("stem mushroom block", Blocks.MUSHROOM_STEM, MushroomFilters.STEM_MUSHROOM_FILTER, STEM_MUSHROOM_ON_FIRST_REGISTER);

    public static final BlockStateProfile FULL_BLOCK_MUSHROOM_PROFILE = BlockStateProfile.combine("full mushroom blocks", BROWN_MUSHROOM_BLOCK_PROFILE, RED_MUSHROOM_BLOCK_PROFILE, STEM_MUSHROOM_BLOCK_PROFILE);
    public static final BlockStateProfile FULL_BLOCK_STONE_PROFILE = BlockStateProfile.combine("full stone blocks", BlockStateProfile.INFESTED_STONE_SUB_PROFILE);
    public static final BlockStateProfile FULL_BLOCK_GRASS_PROFILE = BlockStateProfile.combine("full grass blocks", BlockStateProfile.SNOWY_GRASS_SUB_PROFILE);
    public static final BlockStateProfile FULL_BLOCK_INTERACTIVE_PROFILE = BlockStateProfile.combine("full interactive blocks", BlockStateProfile.DISPENSER_SUB_PROFILE, BlockStateProfile.JUKEBOX_SUB_PROFILE, BlockStateProfile.NOTE_BLOCK_SUB_PROFILE);
    public static final BlockStateProfile FULL_BLOCK_METAL_PROFILE = BlockStateProfile.combine("full metal blocks", BlockStateProfile.WAXED_COPPER_FULLBLOCK_SUB_PROFILE);




}
