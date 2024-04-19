package rocks.blackblock.polymcplus.generator;

import io.github.theepicblock.polymc.api.PolyRegistry;
import io.github.theepicblock.polymc.api.block.BlockPoly;
import io.github.theepicblock.polymc.api.block.BlockStateManager;
import io.github.theepicblock.polymc.api.block.BlockStateMerger;
import io.github.theepicblock.polymc.api.block.BlockStateProfile;
import io.github.theepicblock.polymc.impl.generator.BlockPolyGenerator;
import io.github.theepicblock.polymc.impl.misc.BooleanContainer;
import io.github.theepicblock.polymc.impl.poly.block.SimpleReplacementPoly;
import net.minecraft.block.*;
import net.minecraft.block.enums.RailShape;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import rocks.blackblock.polymcplus.PolyMcPlus;
import rocks.blackblock.polymcplus.block.FallbackItemBlockPoly;
import rocks.blackblock.polymcplus.block.PolyPlusBlockStateProfile;
import rocks.blackblock.polymcplus.block.WallFilters;
import rocks.blackblock.polymcplus.compatibility.PolyCompatibility;
import rocks.blackblock.polymcplus.compatibility.PolyvalentCompatibility;
import rocks.blackblock.polymcplus.polymc.PolyPlusRegistry;
import rocks.blackblock.polymcplus.tools.MaterialLookup;

import java.util.function.BiFunction;

/**
 * Class to automatically generate {@link BlockPoly}s for {@link Block}s
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.1.0
 */
public class BlockPolyPlusGenerator {

    public static BlockState DEFAULT_STONE = Blocks.STONE.getDefaultState();

    /**
     * Generates the most suitable {@link BlockPoly} and directly adds it to the {@link PolyRegistry}
     * @see #generatePoly(Block, PolyRegistry)
     */
    public static void addBlockToBuilder(Block block, PolyRegistry builder) {
        try {
            builder.registerBlockPoly(block, generatePoly(block, builder));
        } catch (Exception e) {
            PolyMcPlus.LOGGER.error("Failed to generate a poly for block " + block.getTranslationKey());
            e.printStackTrace();
            PolyMcPlus.LOGGER.error("Attempting to recover by using a default poly. Please report this");
            builder.registerBlockPoly(block, new SimpleReplacementPoly(Blocks.RED_STAINED_GLASS));
        }
    }

    /**
     * Generates the most suitable {@link BlockPoly} for a given {@link Block}
     */
    public static BlockPoly generatePoly(Block block, PolyRegistry registry) {
        return new FallbackItemBlockPoly(block, (state, isUniqueCallback) -> registerClientState(state, isUniqueCallback, registry.getSharedValues(BlockStateManager.KEY)), registry);
    }

    /**
     * Generates the most suitable {@link BlockPoly} for a given {@link Block}
     */
    public static BlockPoly generatePoly(Block block, PolyRegistry registry, BlockStateMerger merger) {
        return new FallbackItemBlockPoly(block, (state, isUniqueCallback) -> registerClientState(state, isUniqueCallback, registry.getSharedValues(BlockStateManager.KEY)), registry, merger);
    }

    /**
     * Generate a {@link BlockPoly} for a given {@link Block}, but make sure it's opaque.
     * This prevents it from using one of the more rare transparent poly types.
     */
    public static BlockPoly generateOpaquePoly(Block block, PolyRegistry registry) {
        return new FallbackItemBlockPoly(block, (state, isUniqueCallback) -> registerClientState(state, isUniqueCallback, registry.getSharedValues(BlockStateManager.KEY), true), registry);
    }

    /**
     * Generate a {@link BlockPoly} for a given {@link Block}, but make sure it's opaque.
     * This prevents it from using one of the more rare transparent poly types.
     */
    public static BlockPoly generateOpaquePoly(Block block, PolyRegistry registry, BlockStateMerger merger) {
        return new FallbackItemBlockPoly(block, (state, isUniqueCallback) -> registerClientState(state, isUniqueCallback, registry.getSharedValues(BlockStateManager.KEY), true), registry, merger);
    }

    /**
     * Get the collision shape of a state
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public static VoxelShape getCollisionShape(BlockState modded_state) {

        BlockPolyGenerator.FakedWorld fake_world = new BlockPolyGenerator.FakedWorld(modded_state);
        VoxelShape collision_shape;

        try {
            collision_shape = modded_state.getCollisionShape(fake_world, BlockPos.ORIGIN);
        } catch (Exception e) {
            PolyMcPlus.LOGGER.warn("Failed to get collision shape for " + modded_state);
            e.printStackTrace();
            collision_shape = VoxelShapes.UNBOUNDED;
        }

        return collision_shape;
    }

    /**
     * Get the most suitable client-side BlockState to use for the given modded BlockState.
     * This method uses some custom logic, and will fall back to PolyMC's native implementation when nothing is found.
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     *
     * @param    moddedState        The modded BlockState to get the client-side BlockState for
     * @param    isUniqueCallback   Will be set to true if the return value is a unique block that'll only be used for the inputted moddedState
     * @param    manager            The BlockStateManager to use
     *
     * @return   A client state which best matches the moddedState
     */
    public static BlockState registerClientState(BlockState moddedState, BooleanContainer isUniqueCallback, BlockStateManager manager) {
        return registerClientState(moddedState, isUniqueCallback, manager, moddedState.isOpaque());
    }

    /**
     * Get the most suitable client-side BlockState to use for the given modded BlockState.
     * This method uses some custom logic, and will fall back to PolyMC's native implementation when nothing is found.
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     *
     * @param    moddedState        The modded BlockState to get the client-side BlockState for
     * @param    isUniqueCallback   Will be set to true if the return value is a unique block that'll only be used for the inputted moddedState
     * @param    manager            The BlockStateManager to use
     *
     * @return   A client state which best matches the moddedState
     */
    public static BlockState registerClientState(BlockState moddedState, BooleanContainer isUniqueCallback, BlockStateManager manager, boolean isOpaque) {

        Block moddedBlock = moddedState.getBlock();
        BlockPolyGenerator.FakedWorld fakeWorld = new BlockPolyGenerator.FakedWorld(moddedState);

        //Get the state's collision shape.
        VoxelShape collisionShape = getCollisionShape(moddedState);

        boolean is_full_cube = Block.isShapeFullCube(collisionShape);
        boolean tried_mushrooms = false;
        MaterialLookup.Type material_type = null;

        // == FULL BLOCKS ==
        if (is_full_cube && isOpaque) {

            // Full blocks that have BlockEntities are probably interactive in some way,
            // use vanilla blocks that by default respond to mouse interaction
            if (moddedState.hasBlockEntity()) {
                try {
                    isUniqueCallback.set(true);
                    return manager.requestBlockState(PolyPlusBlockStateProfile.FULL_BLOCK_INTERACTIVE_PROFILE);
                } catch (BlockStateManager.StateLimitReachedException ignored) {}
            }

            material_type = MaterialLookup.getMaterial(moddedState);

            // If the modded block is stone-like, try to use vanilla stone blocks
            if (material_type == MaterialLookup.Type.STONE) {
                try {
                    isUniqueCallback.set(true);
                    return manager.requestBlockState(PolyPlusBlockStateProfile.FULL_BLOCK_STONE_PROFILE);
                } catch (BlockStateManager.StateLimitReachedException ignored) {}
            }

            // If the modded block is metal-like, try to use vanilla metal blocks
            // I actually don't really like this, waxed blocks interact with axes
            /*
            if (moddedMaterial.equals(Material.METAL)) {
                try {
                    isUniqueCallback.set(true);
                    return manager.requestBlockState(PolyPlusBlockStateProfile.FULL_BLOCK_METAL_PROFILE);
                } catch (BlockStateManager.StateLimitReachedException ignored) {}
            }*/

            // If the modded block is grass-like, try to use vanilla grass blocks
            if (moddedBlock instanceof GrassBlock) {
                try {
                    isUniqueCallback.set(true);
                    return manager.requestBlockState(PolyPlusBlockStateProfile.FULL_BLOCK_GRASS_PROFILE);
                } catch (BlockStateManager.StateLimitReachedException ignored) {}
            }

            // Try to use mushroom blocks last
            try {
                isUniqueCallback.set(true);
                return manager.requestBlockState(PolyPlusBlockStateProfile.FULL_BLOCK_MUSHROOM_PROFILE);
            } catch (BlockStateManager.StateLimitReachedException ignored) {
                tried_mushrooms = true;
            }
        }

        boolean has_collision = !collisionShape.isEmpty();

        // == NO COLLISION BLOCKS ==
        if (!has_collision && !(moddedBlock instanceof WallBlock)) {
            var outlineShape = moddedState.getOutlineShape(fakeWorld, BlockPos.ORIGIN);

            if (moddedBlock instanceof AbstractRailBlock) {
                boolean is_waterlogged = false;


                if (moddedState.contains(Properties.WATERLOGGED)) {
                    is_waterlogged = moddedState.get(Properties.WATERLOGGED);
                }

                if (!is_waterlogged && moddedState.contains(Properties.RAIL_SHAPE)) {
                    RailShape rail_shape = moddedState.get(Properties.RAIL_SHAPE);

                    if (rail_shape != RailShape.ASCENDING_EAST && rail_shape != RailShape.ASCENDING_NORTH && rail_shape != RailShape.ASCENDING_SOUTH && rail_shape != RailShape.ASCENDING_WEST) {
                        // Use the plants for this, because using tripwires for straight rails causes annoying
                        // glitches when placing them down
                        try {
                            isUniqueCallback.set(true);
                            return manager.requestBlockState(PolyPlusBlockStateProfile.PLANT_ROOT_PROFILE);
                        } catch (BlockStateManager.StateLimitReachedException ignored) {}
                    }
                }

                try {
                    isUniqueCallback.set(true);
                    return manager.requestBlockState(PolyPlusBlockStateProfile.NO_COLLISION_LOW_PROFILE.and(
                            state -> moddedState.getFluidState().equals(state.getFluidState())
                    ));
                } catch (BlockStateManager.StateLimitReachedException ignored) {}

                try {
                    isUniqueCallback.set(true);
                    return manager.requestBlockState(PolyPlusBlockStateProfile.NO_COLLISION_LOW_TRANSLUCENT_PROFILE.and(
                            state -> moddedState.getFluidState().equals(state.getFluidState())
                    ));
                } catch (BlockStateManager.StateLimitReachedException ignored) {}
            }

            if (moddedBlock instanceof NetherPortalBlock) {
                try {
                    isUniqueCallback.set(true);
                    return manager.requestBlockState(PolyPlusBlockStateProfile.NO_COLLISION_TRANSLUCENT_PROFILE.and(
                            state -> moddedState.getFluidState().equals(state.getFluidState())
                    ));
                } catch (BlockStateManager.StateLimitReachedException ignored) {}
            }
        }

        // See if this block is wall-like
        Direction.Axis wall_axis = WallFilters.getWallAxis(collisionShape, moddedState);

        if (wall_axis == Direction.Axis.X) {
            try {
                isUniqueCallback.set(true);
                return manager.requestBlockState(PolyPlusBlockStateProfile.WALL_X_PROFILE.and(
                        state -> moddedState.getFluidState().equals(state.getFluidState())
                ));
            } catch (BlockStateManager.StateLimitReachedException ignored) {}
        } else if (wall_axis == Direction.Axis.Z) {
            try {
                isUniqueCallback.set(true);
                return manager.requestBlockState(PolyPlusBlockStateProfile.WALL_Z_PROFILE.and(
                        state -> moddedState.getFluidState().equals(state.getFluidState())
                ));
            } catch (BlockStateManager.StateLimitReachedException ignored) {}
        }

        // Fall back to the basic PolyMc implementation
        BlockState result = BlockPolyGenerator.registerClientState(moddedState, isUniqueCallback, manager);

        if (result == DEFAULT_STONE) {
            PolyMcPlus.LOGGER.warn("Failed to find a Poly client state for modded block " + moddedState.getBlock());
            PolyMcPlus.LOGGER.warn(" »» Specific modded BlockState: " + moddedState);
            PolyMcPlus.LOGGER.warn(" »» Is full cube    : " + is_full_cube);
            PolyMcPlus.LOGGER.warn(" »» Material type   : " + material_type);
            PolyMcPlus.LOGGER.warn(" »» Has collisions  : " + has_collision);
            PolyMcPlus.LOGGER.warn(" »» Wall-axis       : " + wall_axis);
            PolyMcPlus.LOGGER.warn(" »» Tried mushrooms : " + tried_mushrooms);
            PolyMcPlus.LOGGER.warn(" »» Is opaque       : " + isOpaque);

            return null;
        }

        return result;
    }

    /**
     * Get a blockstate registration provider
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     */
    public static BiFunction<BlockState, BooleanContainer, BlockState> getBlockStateRegistrationProvider(PolyRegistry registry) {

        BlockStateManager manager = registry.getSharedValues(BlockStateManager.KEY);
        BiFunction<BlockState, BooleanContainer, BlockState> registrationProvider;

        if (PolyCompatibility.hasPolyvalent() && PolyvalentCompatibility.isPolyvalentRegistry(registry)) {
            registrationProvider = (state, isUniqueCallback) -> PolyvalentCompatibility.registerClientState(state, isUniqueCallback, manager);
        } else if (registry instanceof PolyPlusRegistry) {
            registrationProvider = (state, isUniqueCallback) -> BlockPolyPlusGenerator.registerClientState(state, isUniqueCallback, manager);
        } else {
            registrationProvider = (state, isUniqueCallback) -> BlockPolyGenerator.registerClientState(state, isUniqueCallback, manager);
        }

        return registrationProvider;
    }
}
