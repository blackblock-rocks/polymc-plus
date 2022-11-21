package rocks.blackblock.polymcplus.block;

import io.github.theepicblock.polymc.api.PolyRegistry;
import net.minecraft.block.*;
import net.minecraft.state.property.Properties;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * Use certain rooted-plant age states
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.2.0
 */
public class PlantRootFilters {

    public static Block[] PLANT_BLOCKS = {Blocks.POTATOES, Blocks.CARROTS};

    // Test plant age
    protected static final Predicate<BlockState> PLANT_AGE_FILTER = (blockState) -> {
        int age = blockState.get(Properties.AGE_7);
        return age == 1 || age == 3 || age == 5;
    };

    public static final BiConsumer<Block,PolyRegistry> PLANT_ROOT_ON_FIRST_REGISTER = (block, polyRegistry) -> {
        polyRegistry.registerBlockPoly(block, (input) -> {

            int age = input.get(Properties.AGE_7);

            if (age == 1 || age == 3 || age == 5) {
                age -= 1;
                input = input.with(Properties.AGE_7, age);
            }

            return input;
        });
    };
}
