package rocks.blackblock.polymcplus.block;

import io.github.theepicblock.polymc.api.block.BlockPoly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;

import java.util.function.Predicate;

/**
 * A BlockPoly that will only replace the server-side state with a property-retaining
 * BlockState of the given clientBlock if it passes the predicate test.
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.2.0
 */
public class ConditionalPropertyReplacementPoly implements BlockPoly {

    protected final Block client_block;
    protected final Predicate<BlockState> use_replacement_state_test;

    public ConditionalPropertyReplacementPoly(Block client_block, Predicate<BlockState> use_replacement_state_test) {
        this.client_block = client_block;
        this.use_replacement_state_test = use_replacement_state_test;
    }

    @Override
    public BlockState getClientBlock(BlockState input) {

        // See if this state needs to use the replacement state
        if (!this.use_replacement_state_test.test(input)) {
            return input;
        }

        BlockState output = client_block.getDefaultState();
        for (Property<?> p : input.getProperties()) {
            output = copyProperty(output, input, p);
        }

        return output;
    }

    /**
     * Copies Property p from BlockState b into BlockState a
     */
    private <T extends Comparable<T>> BlockState copyProperty(BlockState a, BlockState b, Property<T> p) {
        return a.with(p, b.get(p));
    }

    @Override
    public String getDebugInfo(Block obj) {
        return client_block.getTranslationKey();
    }
}
