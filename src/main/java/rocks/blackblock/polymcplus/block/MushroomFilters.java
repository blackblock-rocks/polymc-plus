package rocks.blackblock.polymcplus.block;

import io.github.theepicblock.polymc.api.PolyMap;
import io.github.theepicblock.polymc.api.block.BlockPoly;
import io.github.theepicblock.polymc.api.block.BlockStateProfile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.function.Predicate;

/**
 * Blockstate filters for Mushroom blocks
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.1.0
 */
public class MushroomFilters {

    /**
     * There are 9 Brown Mushroom states that generate in vanilla survival,
     * leave those 9 states alone (+ the all-sides block)
     * (Some other states are obtainable by shearing with other mushroom blocks,
     *  but that will be disabled)
     */
    public static final Predicate<BlockState> BROWN_MUSHROOM_FILTER = (blockState) -> {

        boolean down = hasBooleanDirection(blockState, Direction.DOWN);
        boolean east = hasBooleanDirection(blockState, Direction.EAST);
        boolean north = hasBooleanDirection(blockState, Direction.NORTH);
        boolean south = hasBooleanDirection(blockState, Direction.SOUTH);
        boolean up = hasBooleanDirection(blockState, Direction.UP);
        boolean west = hasBooleanDirection(blockState, Direction.WEST);

        if (!down) {

            if (!east && !north && !south && !west) {
                // Up & no up are in use
                return false;
            }

            if (up) {
                if (west && !east && !north && !south) {
                    return false;
                }

                if (south && !east && !north && !west) {
                    return false;
                }

                if (south && west && !east && !north) {
                    return false;
                }

                if (north && !east && !south && !west) {
                    return false;
                }

                if (north && west && !east && !south) {
                    return false;
                }

                if (east && !north && !south && !west) {
                    return false;
                }

                if (east && south && !north && !west) {
                    return false;
                }

                if (east && north && !south && !west) {
                    return false;
                }
            }
        } else if (east && north && south && up && west) {
            return false;
        }

        return true;
    };

    /**
     * There are 17 Red Mushroom states that generate in vanilla survival,
     * leave those states alone (+ the all-sides block)
     */
    public static final Predicate<BlockState> RED_MUSHROOM_FILTER = (blockState) -> {

        boolean down = hasBooleanDirection(blockState, Direction.DOWN);
        boolean east = hasBooleanDirection(blockState, Direction.EAST);
        boolean north = hasBooleanDirection(blockState, Direction.NORTH);
        boolean south = hasBooleanDirection(blockState, Direction.SOUTH);
        boolean up = hasBooleanDirection(blockState, Direction.UP);
        boolean west = hasBooleanDirection(blockState, Direction.WEST);

        if (!down) {

            if (west && !east && !north && !south) {
                return up;
            }

            if (!east && !north && !up && !west) {
                return south;
            }

            if (up && !east && !north && !south) {
                return false;
            }

            if (south && west && !east && !north && !up) {
                return false;
            }

            if (south && up && west && !east && !north) {
                return false;
            }

            if (north && !east && !south && !up) {
                return false;
            }

            if (north && up && !east && !south) {
                return false;
            }

            if (east && !north && !south && !up && !west) {
                return false;
            }

            if (east && up && !north && !south && !west) {
                return false;
            }

            if (east && south && !north && !up && !west) {
                return false;
            }

            if (east && south && up && !north && !west) {
                return false;
            }

            if (east && north && !south && !up && !west) {
                return false;
            }

            if (east && north && up && !south && !west) {
                return false;
            }
        } else if (east && north && south && up && west) {
            return false;
        }

        return true;
    };

    /**
     * There are 2 Mushroom Stem states that generate in vanilla survival,
     * leave those states alone + the all-sides block and the ones that can be sheared
     */
    public static final Predicate<BlockState> STEM_MUSHROOM_FILTER = (blockState) -> {

        boolean down = hasBooleanDirection(blockState, Direction.DOWN);
        boolean up = hasBooleanDirection(blockState, Direction.UP);

        // Ignore stems
        if (!down && !up) {
            return false;
        }

        boolean east = hasBooleanDirection(blockState, Direction.EAST);
        boolean north = hasBooleanDirection(blockState, Direction.NORTH);
        boolean south = hasBooleanDirection(blockState, Direction.SOUTH);
        boolean west = hasBooleanDirection(blockState, Direction.WEST);

        // Don't use the all-sides block
        if (down && east && north && south && up && west) {
            return false;
        }

        return true;
    };

    /**
     * Specifies if the {@link BlockState} changes done around this block might require a resync.
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     */
    public static boolean mushroomNeedsResync(PolyMap polyMap, BlockState sourceState, BlockState clientState, Direction direction) {

        Block block = clientState.getBlock();

        // See if the client-side blockstate has a non-sheared face on the opposite side of the updated block
        boolean hasNonShearedFace = MushroomFilters.hasBooleanDirection(clientState, direction.getOpposite());

        if (hasNonShearedFace) {

            // Try to get the player's BlockPoly for this opposite side
            BlockPoly poly = polyMap.getBlockPoly(sourceState.getBlock());

            BlockState oppositeClientState = null;

            if (poly == null) {
                // There is no poly map, so the server-side state should be the same as the client-side's one
                oppositeClientState = sourceState;
            } else {
                // There is a polymap, so the client-side state differs from the server-side one
                oppositeClientState = poly.getClientBlock(sourceState);
            }

            // Get the opposite block as used on the client-side
            Block oppositeClientBlock = oppositeClientState.getBlock();

            // If the 2 touching blocks on the client side are NOT the same,
            // no update will occur, and we can safely skip it.
            return block == oppositeClientBlock;
        }

        return false;
    }

    /**
     * Check if the BlockState has the given Direction property enabled.
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     */
    public static boolean hasBooleanDirection(BlockState state, Direction direction) {

        if (state == null || direction == null) {
            return false;
        }

        BooleanProperty booleanProperty = ConnectingBlock.FACING_PROPERTIES.get(direction);

        if (booleanProperty == null) {
            return false;
        }

        return state.contains(booleanProperty) && state.get(booleanProperty);
    }
}
