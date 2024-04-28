package rocks.blackblock.polymcplus.block;

import io.github.theepicblock.polymc.api.PolyRegistry;
import net.minecraft.block.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class TripwireHookFilters {

    public static Block[] TRIPWIRE_HOOK_BLOCKS = {Blocks.TRIPWIRE_HOOK};

    // A tripwire hook that is unattached and powered is almost indistinguishable from one that is attached -- especially for how short it's out for.
    public static final Predicate<BlockState> TRIPWIRE_HOOK_BASE_FILTER = (blockState) -> {
        boolean powered = blockState.get(TripwireHookBlock.POWERED);
        boolean attached = blockState.get(TripwireHookBlock.ATTACHED);
        return powered && !attached;
    };
    public static final Predicate<BlockState> TRIPWIRE_HOOK_NORTH_FILTER = TRIPWIRE_HOOK_BASE_FILTER.and(blockState -> {
        return blockState.get(TripwireHookBlock.FACING) == Direction.NORTH;
    });
    public static final Predicate<BlockState> TRIPWIRE_HOOK_EAST_FILTER = TRIPWIRE_HOOK_BASE_FILTER.and(blockState -> {
        return blockState.get(TripwireHookBlock.FACING) == Direction.EAST;
    });
    public static final Predicate<BlockState> TRIPWIRE_HOOK_SOUTH_FILTER = TRIPWIRE_HOOK_BASE_FILTER.and(blockState -> {
        return blockState.get(TripwireHookBlock.FACING) == Direction.SOUTH;
    });
    public static final Predicate<BlockState> TRIPWIRE_HOOK_WEST_FILTER = TRIPWIRE_HOOK_BASE_FILTER.and(blockState -> {
        return blockState.get(TripwireHookBlock.FACING) == Direction.WEST;
    });

    public static final BiConsumer<Block, PolyRegistry> TRIPWIRE_HOOK_ON_FIRST_REGISTER = (block, polyRegistry) -> {
        polyRegistry.registerBlockPoly(block, (input) -> {

            boolean powered = input.get(Properties.POWERED);
            boolean attached = input.get(Properties.ATTACHED);

            if (powered && !attached) {
                input = input.with(Properties.ATTACHED, true);
            }

            return input;
        });
    };
}
