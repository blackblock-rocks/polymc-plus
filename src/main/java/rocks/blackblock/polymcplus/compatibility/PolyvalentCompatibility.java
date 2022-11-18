package rocks.blackblock.polymcplus.compatibility;

import io.github.theepicblock.polymc.api.PolyRegistry;
import io.github.theepicblock.polymc.api.block.BlockStateManager;
import io.github.theepicblock.polymc.impl.misc.BooleanContainer;
import net.minecraft.block.BlockState;
import rocks.blackblock.polyvalent.polymc.PolyvalentBlockPolyGenerator;
import rocks.blackblock.polyvalent.polymc.PolyvalentRegistry;

public class PolyvalentCompatibility {

    public static boolean isPolyvalentRegistry(PolyRegistry registry) {
        return registry instanceof PolyvalentRegistry;
    }

    public static BlockState registerClientState(BlockState modded_state, BooleanContainer isUniqueCallback, BlockStateManager manager) {
        return PolyvalentBlockPolyGenerator.registerClientState(modded_state, isUniqueCallback, manager);
    }

}
