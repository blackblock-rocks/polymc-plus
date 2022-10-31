package rocks.blackblock.polymcplus.generator;

import io.github.theepicblock.polymc.api.PolyRegistry;
import io.github.theepicblock.polymc.api.block.BlockPoly;
import io.github.theepicblock.polymc.api.block.BlockStateManager;
import io.github.theepicblock.polymc.impl.generator.BlockPolyGenerator;
import io.github.theepicblock.polymc.impl.misc.BooleanContainer;
import io.github.theepicblock.polymc.impl.poly.block.FunctionBlockStatePoly;
import io.github.theepicblock.polymc.impl.poly.block.SimpleReplacementPoly;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import rocks.blackblock.polymcplus.PolyMcPlus;
import rocks.blackblock.polymcplus.block.PolyPlusBlockStateProfile;
import rocks.blackblock.polymcplus.block.WallFilters;
import rocks.blackblock.polymcplus.polymc.PolyPlusRegistry;
import rocks.blackblock.polyvalent.polymc.PolyvalentBlockPolyGenerator;
import rocks.blackblock.polyvalent.polymc.PolyvalentRegistry;

import java.util.function.BiFunction;

/**
 * Class to automatically generate {@link BlockPoly}s for {@link Block}s
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.1.0
 */
public class BlockPolyPlusGenerator {

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
        return new FunctionBlockStatePoly(block, (state, isUniqueCallback) -> registerClientState(state, isUniqueCallback, registry.getSharedValues(BlockStateManager.KEY)));
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

        Block moddedBlock = moddedState.getBlock();
        BlockPolyGenerator.FakedWorld fakeWorld = new BlockPolyGenerator.FakedWorld(moddedState);

        //Get the state's collision shape.
        VoxelShape collisionShape;
        try {
            collisionShape = moddedState.getCollisionShape(fakeWorld, BlockPos.ORIGIN);
        } catch (Exception e) {
            PolyMcPlus.LOGGER.warn("Failed to get collision shape for " + moddedState);
            e.printStackTrace();
            collisionShape = VoxelShapes.UNBOUNDED;
        }

        boolean isOpaque = moddedState.isOpaque();

        // == FULL BLOCKS ==
        if (Block.isShapeFullCube(collisionShape) && isOpaque) {

            // Full blocks that have BlockEntities are probably interactive in some way,
            // use vanilla blocks that by default respond to mouse interaction
            if (moddedState.hasBlockEntity()) {
                try {
                    isUniqueCallback.set(true);
                    return manager.requestBlockState(PolyPlusBlockStateProfile.FULL_BLOCK_INTERACTIVE_PROFILE);
                } catch (BlockStateManager.StateLimitReachedException ignored) {}
            }

            Material moddedMaterial = moddedState.getMaterial();

            // If the modded block is stone-like, try to use vanilla stone blocks
            if (moddedMaterial.equals(Material.STONE)) {
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
            } catch (BlockStateManager.StateLimitReachedException ignored) {}
        }

        // == NO COLLISION BLOCKS ==
        if (collisionShape.isEmpty() && !(moddedBlock instanceof WallBlock)) {
            var outlineShape = moddedState.getOutlineShape(fakeWorld, BlockPos.ORIGIN);

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
        return BlockPolyGenerator.registerClientState(moddedState, isUniqueCallback, manager);
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

        if (registry instanceof PolyvalentRegistry) {
            registrationProvider = (state, isUniqueCallback) -> PolyvalentBlockPolyGenerator.registerClientState(state, isUniqueCallback, manager);
        } else if (registry instanceof PolyPlusRegistry) {
            registrationProvider = (state, isUniqueCallback) -> BlockPolyPlusGenerator.registerClientState(state, isUniqueCallback, manager);
        } else {
            registrationProvider = (state, isUniqueCallback) -> BlockPolyGenerator.registerClientState(state, isUniqueCallback, manager);
        }

        return registrationProvider;
    }
}
