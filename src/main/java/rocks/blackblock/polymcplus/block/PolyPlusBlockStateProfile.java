package rocks.blackblock.polymcplus.block;

import io.github.theepicblock.polymc.api.PolyRegistry;
import io.github.theepicblock.polymc.api.block.BlockStateProfile;
import io.github.theepicblock.polymc.impl.poly.block.ConditionalSimpleBlockPoly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TripwireBlock;
import net.minecraft.state.property.Properties;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class PolyPlusBlockStateProfile {

    // Filters and consumers
    private static final Predicate<BlockState> TRIPWIRE_THIN_FILTER = PolyPlusBlockStateProfile::isThinTripwireUsable;
    private static final Predicate<BlockState> TRIPWIRE_THICK_FILTER = PolyPlusBlockStateProfile::isThickTripwireUsable;

    private static final BiConsumer<Block, PolyRegistry> BROWN_MUSHROOM_ON_FIRST_REGISTER = (block, polyRegistry) -> polyRegistry.registerBlockPoly(block, new ConditionalSimpleBlockPoly(Blocks.BROWN_MUSHROOM_BLOCK.getDefaultState(), MushroomFilters.BROWN_MUSHROOM_FILTER));
    private static final BiConsumer<Block, PolyRegistry> RED_MUSHROOM_ON_FIRST_REGISTER = (block, polyRegistry) -> polyRegistry.registerBlockPoly(block, new ConditionalSimpleBlockPoly(Blocks.RED_MUSHROOM_BLOCK.getDefaultState(), MushroomFilters.RED_MUSHROOM_FILTER));
    private static final BiConsumer<Block, PolyRegistry> STEM_MUSHROOM_ON_FIRST_REGISTER = (block, polyRegistry) -> polyRegistry.registerBlockPoly(block, new ConditionalSimpleBlockPoly(Blocks.MUSHROOM_STEM.getDefaultState(), MushroomFilters.STEM_MUSHROOM_FILTER));

    private static final BiConsumer<Block,PolyRegistry> TRIPWIRE_ON_FIRST_REGISTER = (block, polyRegistry) -> {
        polyRegistry.registerBlockPoly(block, (input) ->
                input.with(Properties.POWERED, false).with(Properties.DISARMED,false)
        );
    };

    // Actual profiles
    public static final BlockStateProfile BROWN_MUSHROOM_BLOCK_PROFILE = new BlockStateProfile("brown mushroom block", Blocks.BROWN_MUSHROOM_BLOCK, MushroomFilters.BROWN_MUSHROOM_FILTER, BROWN_MUSHROOM_ON_FIRST_REGISTER);
    public static final BlockStateProfile RED_MUSHROOM_BLOCK_PROFILE = new BlockStateProfile("red mushroom block", Blocks.RED_MUSHROOM_BLOCK, MushroomFilters.RED_MUSHROOM_FILTER, RED_MUSHROOM_ON_FIRST_REGISTER);
    public static final BlockStateProfile STEM_MUSHROOM_BLOCK_PROFILE = new BlockStateProfile("stem mushroom block", Blocks.MUSHROOM_STEM, MushroomFilters.STEM_MUSHROOM_FILTER, STEM_MUSHROOM_ON_FIRST_REGISTER);

    public static final BlockStateProfile TRIPWIRE_THIN_PROFILE = new BlockStateProfile("thin tripwire", Blocks.TRIPWIRE, TRIPWIRE_THIN_FILTER, TRIPWIRE_ON_FIRST_REGISTER);
    public static final BlockStateProfile TRIPWIRE_THICK_PROFILE = new BlockStateProfile("thick tripwire", Blocks.TRIPWIRE, TRIPWIRE_THICK_FILTER, TRIPWIRE_ON_FIRST_REGISTER);

    // Full block profiles
    public static final BlockStateProfile FULL_BLOCK_MUSHROOM_PROFILE = BlockStateProfile.combine("full mushroom blocks", BROWN_MUSHROOM_BLOCK_PROFILE, RED_MUSHROOM_BLOCK_PROFILE, STEM_MUSHROOM_BLOCK_PROFILE);
    public static final BlockStateProfile FULL_BLOCK_STONE_PROFILE = BlockStateProfile.combine("full stone blocks", BlockStateProfile.INFESTED_STONE_SUB_PROFILE);
    public static final BlockStateProfile FULL_BLOCK_GRASS_PROFILE = BlockStateProfile.combine("full grass blocks", BlockStateProfile.SNOWY_GRASS_SUB_PROFILE);
    public static final BlockStateProfile FULL_BLOCK_INTERACTIVE_PROFILE = BlockStateProfile.combine("full interactive blocks", BlockStateProfile.DISPENSER_SUB_PROFILE, BlockStateProfile.JUKEBOX_SUB_PROFILE, BlockStateProfile.NOTE_BLOCK_SUB_PROFILE);
    public static final BlockStateProfile FULL_BLOCK_METAL_PROFILE = BlockStateProfile.combine("full metal blocks", BlockStateProfile.WAXED_COPPER_FULLBLOCK_SUB_PROFILE);

    // "Opaque" meaning the texture cannot be see-through
    // @TODO: Dripleaf blocks have a visually random positioning
    public static final BlockStateProfile NO_COLLISION_OPAQUE_PROFILE = BlockStateProfile.combine("opaque blocks without collisions", BlockStateProfile.KELP_SUB_PROFILE, BlockStateProfile.SAPLING_SUB_PROFILE, BlockStateProfile.OPEN_FENCE_GATE_PROFILE, BlockStateProfile.SMALL_DRIPLEAF_SUB_PROFILE);
    public static final BlockStateProfile NO_COLLISION_TRANSLUCENT_PROFILE = BlockStateProfile.combine("translucent blocks without collisions", TRIPWIRE_THICK_PROFILE, TRIPWIRE_THIN_PROFILE);
    public static final BlockStateProfile NO_COLLISION_LOW_TRANSLUCENT_PROFILE = BlockStateProfile.combine("translucent blocks without collisions with a low profile", TRIPWIRE_THIN_PROFILE, TRIPWIRE_THICK_PROFILE);
    public static final BlockStateProfile NO_COLLISION_LOW_PROFILE = BlockStateProfile.combine("blocks with a low profile", TRIPWIRE_THIN_PROFILE, TRIPWIRE_THICK_PROFILE);

    // Wall states
    public static final BlockStateProfile WALL_X_PROFILE = new BlockStateProfile("wall_x", WallFilters.WALL_BLOCKS, WallFilters.WALL_X_FILTER, WallFilters.WALL_ON_FIRST_REGISTER);
    public static final BlockStateProfile WALL_Z_PROFILE = new BlockStateProfile("wall_z", WallFilters.WALL_BLOCKS, WallFilters.WALL_Z_FILTER, WallFilters.WALL_ON_FIRST_REGISTER);

    // Bed states
    public static final BlockStateProfile BED_PROFILE = new BlockStateProfile("bed profiles", BedFilters.BED_BLOCKS, BedFilters.BED_ANY_FILTER, BedFilters.BED_ON_FIRST_REGISTER);

    // Soul Campfire states
    public static final BlockStateProfile SOUL_CAMPFIRE_PROFILE = BlockStateProfile.newProfile("soul campfire profiles", SoulCampfireFilters.SOUL_CAMPFIRE_BLOCKS, SoulCampfireFilters.SOUL_CAMPFIRE_FILTER, SoulCampfireFilters.SOUL_CAMPFIRE_ON_FIRST_REGISTER);

    // Tripwire hook states
    public static final BlockStateProfile TRIPWIRE_HOOK_NORTH_PRFILE = BlockStateProfile.newProfile("tripwire hook north profiles", TripwireHookFilters.TRIPWIRE_HOOK_BLOCKS, TripwireHookFilters.TRIPWIRE_HOOK_NORTH_FILTER, TripwireHookFilters.TRIPWIRE_HOOK_ON_FIRST_REGISTER);
    public static final BlockStateProfile TRIPWIRE_HOOK_EAST_PRFILE = BlockStateProfile.newProfile("tripwire hook east profiles", TripwireHookFilters.TRIPWIRE_HOOK_BLOCKS, TripwireHookFilters.TRIPWIRE_HOOK_EAST_FILTER, TripwireHookFilters.TRIPWIRE_HOOK_ON_FIRST_REGISTER);
    public static final BlockStateProfile TRIPWIRE_HOOK_SOUTH_PRFILE = BlockStateProfile.newProfile("tripwire hook south profiles", TripwireHookFilters.TRIPWIRE_HOOK_BLOCKS, TripwireHookFilters.TRIPWIRE_HOOK_SOUTH_FILTER, TripwireHookFilters.TRIPWIRE_HOOK_ON_FIRST_REGISTER);
    public static final BlockStateProfile TRIPWIRE_HOOK_WEST_PRFILE = BlockStateProfile.newProfile("tripwire hook west profiles", TripwireHookFilters.TRIPWIRE_HOOK_BLOCKS, TripwireHookFilters.TRIPWIRE_HOOK_WEST_FILTER, TripwireHookFilters.TRIPWIRE_HOOK_ON_FIRST_REGISTER);

    // Turtle egg states
    public static final BlockStateProfile ONE_TURTLE_EGG_PROFILE = BlockStateProfile.newProfile("turtle egg profiles", TurtleEggFilters.TURTLE_EGG_BLOCKS, TurtleEggFilters.ONE_TURTLE_EGG_FILTER, TurtleEggFilters.TURTLE_EGG_ON_FIRST_REGISTER);
    public static final BlockStateProfile TWO_TURTLE_EGG_PROFILE = BlockStateProfile.newProfile("double turtle egg profiles", TurtleEggFilters.TURTLE_EGG_BLOCKS, TurtleEggFilters.TWO_TURTLE_EGG_FILTER, TurtleEggFilters.TURTLE_EGG_ON_FIRST_REGISTER);

    // Potted plant states
    public static final BlockStateProfile POTTED_PLANT_PROFILE = BlockStateProfile.newProfile("potted plant profiles", PottedPlantFilters.POTTED_PLANT_BLOCKS, PottedPlantFilters.POTTED_PLANT_FILTER, PottedPlantFilters.POTTED_PLANT_ON_FIRST_REGISTER);

    // Plant root states
    public static final BlockStateProfile PLANT_ROOT_PROFILE = BlockStateProfile.newProfile("plant root profiles", PlantRootFilters.PLANT_BLOCKS, PlantRootFilters.PLANT_AGE_FILTER, PlantRootFilters.PLANT_ROOT_ON_FIRST_REGISTER);

    /**
     * Is the given piece of string usable?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     *
     * @param    state   The blockstate to check
     */
    private static boolean isStringUseable(BlockState state) {
        return  state.get(Properties.POWERED) ||
                state.get(TripwireBlock.DISARMED);
    }

    /**
     * "Thin" tripwires have the same height as a carpet
     * (though hover 1 pixel above the ground)
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     *
     * @param    state   The blockstate to check
     */
    private static boolean isThinTripwireUsable(BlockState state) {
        if (isStringUseable(state)) {
            return state.get(Properties.ATTACHED);
        }

        return false;
    }

    /**
     * "Thick" tripwires have the same height as a bottom slab
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     *
     * @param    state   The blockstate to check
     */
    private static boolean isThickTripwireUsable(BlockState state) {
        if (isStringUseable(state)) {
            return !state.get(Properties.ATTACHED);
        }

        return false;
    }
}
