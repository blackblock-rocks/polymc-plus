package rocks.blackblock.polymcplus.generator;

import io.github.theepicblock.polymc.api.PolyRegistry;
import io.github.theepicblock.polymc.impl.Util;
import io.github.theepicblock.polymc.impl.poly.item.Enchantment2LoreTransformer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.Comparator;
import java.util.function.BiConsumer;

public class PolyPlusGenerator {

    /**
     * Automatically generates all polys that are missing in the specified builder
     * @param builder builder to add polys to
     */
    public static void generateMissing(PolyRegistry builder) {
        generateMissingPolys(builder, Registries.ITEM, ItemPolyPlusGenerator::addItemToBuilder, builder::hasItemPoly);
        generateMissingPolys(builder, Registries.BLOCK, BlockPolyPlusGenerator::addBlockToBuilder, builder::hasBlockPoly);
        generateMissingPolys(builder, Registries.SCREEN_HANDLER, GuiPolyPlusGenerator::addGuiToBuilder, builder::hasGuiPoly);
        generateMissingPolys(builder, Registries.ENTITY_TYPE, EntityPolyPlusGenerator::addEntityToBuilder, builder::hasEntityPoly);
    }

    private static <T> void generateMissingPolys(PolyRegistry builder, Registry<T> registry, BiConsumer<T, PolyRegistry> generator, BooleanFunction<T> contains) {
        registry.getEntrySet()
                .stream()
                .filter(entry -> !Util.isVanilla(entry.getKey().getValue()))
                .filter(entry -> !contains.accept(entry.getValue()))
                .sorted(Comparator.comparing(a -> a.getKey().getValue()))  // Compares the identifier
                .forEach(entry -> generator.accept(entry.getValue(), builder));
    }

    /**
     * Registers global {@link io.github.theepicblock.polymc.api.item.ItemTransformer}s that are included with PolyMc by default for vanilla compatibility
     */
    public static void addDefaultGlobalItemPolys(PolyRegistry registry) {
        registry.registerGlobalItemPoly(new Enchantment2LoreTransformer());
    }



    @FunctionalInterface
    public interface BooleanFunction<T> {
        boolean accept(T t);
    }
}
