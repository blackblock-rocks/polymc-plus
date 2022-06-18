package rocks.blackblock.polymcplus.block;

import io.github.theepicblock.polymc.api.PolyRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.WallShape;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * Blockstate filters for Wall blocks
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.1.0
 */
public class WallFilters {

    // All wall blocks
    protected static final Block[] WALL_BLOCKS = {Blocks.COBBLESTONE_WALL, Blocks.MOSSY_COBBLESTONE_WALL, Blocks.BRICK_WALL, Blocks.PRISMARINE_WALL, Blocks.RED_SANDSTONE_WALL, Blocks.MOSSY_STONE_BRICK_WALL, Blocks.GRANITE_WALL, Blocks.STONE_BRICK_WALL, Blocks.NETHER_BRICK_WALL, Blocks.ANDESITE_WALL, Blocks.RED_NETHER_BRICK_WALL, Blocks.SANDSTONE_WALL, Blocks.END_STONE_BRICK_WALL, Blocks.DIORITE_WALL, Blocks.BLACKSTONE_WALL, Blocks.POLISHED_BLACKSTONE_BRICK_WALL, Blocks.POLISHED_BLACKSTONE_WALL, Blocks.COBBLED_DEEPSLATE_WALL, Blocks.POLISHED_DEEPSLATE_WALL, Blocks.DEEPSLATE_TILE_WALL, Blocks.DEEPSLATE_BRICK_WALL, Blocks.MUD_BRICK_WALL};

    // Test available wall states
    protected static final Predicate<BlockState> WALL_ANY_FILTER = (blockState) -> {

        WallInfo info = getWallInfo(blockState);

        return info.useable;
    };

    protected static final BiConsumer<Block, PolyRegistry> WALL_ON_FIRST_REGISTER = (block, polyRegistry) -> polyRegistry.registerBlockPoly(block, input -> {

        // If the input state is one we use as a poly block, return the default state instead
        if (WALL_ANY_FILTER.test(input)) {
            return block.getDefaultState();
        }

        return input;
    });

    // Get available X axis states
    protected static final Predicate<BlockState> WALL_X_FILTER = (blockState) -> {

        WallInfo info = getWallInfo(blockState);

        if (!info.useable) {
            return false;
        }

        Direction.Axis axis = info.getAxis();

        if (axis == null) {
            return false;
        }

        if (axis == Direction.Axis.X) {
            // At least 2 sides are preferred, those are wider.
            // @TODO: use less wide ones if they're the last options
            return info.none == 2;
        }

        return false;
    };

    // Get available Z axis states
    protected static final Predicate<BlockState> WALL_Z_FILTER = (blockState) -> {

        WallInfo info = getWallInfo(blockState);

        if (!info.useable) {
            return false;
        }

        Direction.Axis axis = info.getAxis();

        if (axis == null) {
            return false;
        }

        if (axis == Direction.Axis.Z) {
            // At least 2 sides are preferred, those are wider
            // @TODO: use less wide ones if they're the last options
            return info.none == 2;
        }

        return false;
    };

    /**
     * Try getting the wall axis to use, if it's a wall-like block
     *
     * @param    shape   The collision shape
     *
     * @return   The collision axis, or null if it's not a wall-like block
     */
    public static Direction.Axis getWallAxis(VoxelShape shape, BlockState state) {

        double max_y = shape.getMax(Direction.Axis.Y) * 16;

        if (max_y < 14) {
            return null;
        }

        double min_x = shape.getMin(Direction.Axis.X) * 16;
        double max_x = shape.getMax(Direction.Axis.X) * 16;

        double min_z = shape.getMin(Direction.Axis.Z) * 16;
        double max_z = shape.getMax(Direction.Axis.Z) * 16;

        double min_y = shape.getMin(Direction.Axis.Y) * 16;

        double x_width = max_x - min_x;
        double z_width = max_z - min_z;
        double height = max_y - min_y;

        Direction.Axis axis = null;
        double width;
        double depth;

        if (x_width > z_width) {
            width = x_width;
            depth = z_width;
            axis = Direction.Axis.X;
        } else {
            width = z_width;
            depth = x_width;
            axis = Direction.Axis.Z;
        }

        if (depth > 0 && width > 0 && depth < 6 && width > 6) {
            return axis;
        }

        return null;
    }

    /**
     * Get a {@link WallInfo} instance for the given {@link BlockState}
     *
     *  @author   Jelle De Loecker   <jelle@elevenways.be>
     *  @since    0.1.0
     *
     * @param     blockState   The blockstate to get the wallinfo for
     */
    @NotNull
    public static WallInfo getWallInfo(@NotNull BlockState blockState) {

        boolean up = blockState.get(Properties.UP);

        // All the "up" states are in use
        if (up) {
            return new WallInfo(0, 0, 0, false, false, false, false);
        }

        WallShape east_shape = blockState.get(Properties.EAST_WALL_SHAPE);
        WallShape north_shape = blockState.get(Properties.NORTH_WALL_SHAPE);
        WallShape south_shape = blockState.get(Properties.SOUTH_WALL_SHAPE);
        WallShape west_shape = blockState.get(Properties.WEST_WALL_SHAPE);

        boolean east_low = east_shape == WallShape.LOW;
        boolean north_low = north_shape == WallShape.LOW;
        boolean south_low = south_shape == WallShape.LOW;
        boolean west_low = west_shape == WallShape.LOW;

        boolean east_tall = east_shape == WallShape.TALL;
        boolean north_tall = north_shape == WallShape.TALL;
        boolean south_tall = south_shape == WallShape.TALL;
        boolean west_tall = west_shape == WallShape.TALL;

        boolean east_none = east_shape == WallShape.NONE;
        boolean north_none = north_shape == WallShape.NONE;
        boolean south_none = south_shape == WallShape.NONE;
        boolean west_none = west_shape == WallShape.NONE;

        boolean east = east_low || east_tall;
        boolean north = north_low || north_tall;
        boolean south = south_low || south_tall;
        boolean west = west_low || west_tall;

        int lows = east_low ? 1 : 0;
        lows += north_low ? 1 : 0;
        lows += south_low ? 1 : 0;
        lows += west_low ? 1 : 0;

        int talls = east_tall ? 1 : 0;
        talls += north_tall ? 1 : 0;
        talls += south_tall ? 1 : 0;
        talls += west_tall ? 1 : 0;

        int none = east_none ? 1 : 0;
        none += north_none ? 1 : 0;
        none += south_none ? 1 : 0;
        none += west_none ? 1 : 0;

        return new WallInfo(none, lows, talls, east, north, south, west);
    }

    /**
     * Little helper class to work with Wall states
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     */
    public static class WallInfo {

        public final int none;
        public final int lows;
        public final int talls;
        public final boolean east;
        public final boolean north;
        public final boolean south;
        public final boolean west;

        // If this state can be used as a polyblock
        public final boolean useable;

        /**
         * Initialize the instance
         *
         * @author   Jelle De Loecker   <jelle@elevenways.be>
         * @since    0.1.0
         *
         * @param    none   How many "none" states it has
         * @param    lows   How many "low" states it has
         * @param    talls  How many "tall" states it has
         * @param    east   If the "east" state is enabled
         * @param    north  If the "north" state is enabled
         * @param    south  If the "south" state is enabled
         * @param    west   If the "west" state is enabled
         */
        public WallInfo(int none, int lows, int talls, boolean east, boolean north, boolean south, boolean west) {
            this.none = none;
            this.lows = lows;
            this.talls = talls;
            this.east = east;
            this.north = north;
            this.south = south;
            this.west = west;

            if (none == 3 && lows == 1) {
                useable = true;
            } else if (none == 2 && lows == 1 && talls == 1) {
                useable = true;
            } else if (none == 2 && lows == 0 && talls == 2) {
                useable = false;
            } else if (none == 1 && lows == 1 && talls == 2) {
                useable = true;
            } else if (none == 0 && lows == 2 && talls == 2) {
                useable = true;
            } else {
                useable = false;
            }
        }

        /**
         * Are there any walls on the X-axis?
         *
         * @author   Jelle De Loecker   <jelle@elevenways.be>
         * @since    0.1.0
         */
        public boolean hasXWalls() {
            return this.east || this.west;
        }

        /**
         * Are there any walls on the Z-axis?
         *
         * @author   Jelle De Loecker   <jelle@elevenways.be>
         * @since    0.1.0
         */
        public boolean hasZWalls() {
            return this.north || this.south;
        }

        /**
         * Get the axis of this wall.
         * This will only return an axis if all the walls are on the same axis.
         * If not, it will return null.
         *
         * @author   Jelle De Loecker   <jelle@elevenways.be>
         * @since    0.1.0
         */
        @Nullable
        public Direction.Axis getAxis() {

            boolean x_walls = this.hasXWalls();
            boolean z_walls = this.hasZWalls();

            if (!x_walls && !z_walls) {
                return null;
            }

            if (this.none == 3) {
                if (x_walls) {
                    return Direction.Axis.X;
                } else {
                    return Direction.Axis.Z;
                }
            }

            if (this.none == 2) {
                if (x_walls && !z_walls) {
                    return Direction.Axis.X;
                }

                if (!x_walls) {
                    return Direction.Axis.Z;
                }
            }

            return null;
        }

    }
}
