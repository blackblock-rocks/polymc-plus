package rocks.blackblock.polymcplus.polymc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.theepicblock.polymc.api.PolyRegistry;
import io.github.theepicblock.polymc.api.item.ItemTransformer;

import java.util.Objects;

/**
 * The PolyPlus registry
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.1.0
 */
public class PolyPlusRegistry extends PolyRegistry {

    /**
     * Generate a new PolyMap based on this registry
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     */
    @Override
    public PolyPlusMap build() {
        return new PolyPlusMap(
                ImmutableMap.copyOf(itemPolys),
                globalItemPolys.toArray(new ItemTransformer[0]),
                ImmutableMap.copyOf(blockPolys),
                ImmutableMap.copyOf(guiPolys),
                ImmutableMap.copyOf(entityPolys),
                ImmutableList.copyOf(sharedValues.entrySet().stream().map((entry) -> entry.getKey().createResources(entry.getValue())).filter(Objects::nonNull).iterator()));
    }

}
