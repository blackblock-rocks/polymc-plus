package rocks.blackblock.polymcplus.generator;

import io.github.theepicblock.polymc.api.PolyRegistry;
import io.github.theepicblock.polymc.api.entity.EntityPoly;
import io.github.theepicblock.polymc.impl.generator.EntityPolyGenerator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * Class to automatically generate {@link EntityPoly}s for {@link EntityType}s
 */
public class EntityPolyPlusGenerator extends EntityPolyGenerator {

    public static <T extends Entity> EntityPoly<T> generatePoly(EntityType<T> entityType, PolyRegistry builder) {

        Identifier entity_id = Registry.ENTITY_TYPE.getId(entityType);

        System.out.println(" -- GENERATING ENTITY POLY FOR '" + entity_id + "'");

        if (entity_id.getNamespace().equals("blackblock")) {
            //return (info, entity) -> null; // Compatibility with Blackblock DisguiseLib npcs
        }

        if (entity_id.getNamespace().equals("redshirt")) {
            //return (info, entity) -> null; // Compatibility with Blackblock Redshirts
        }

        System.out.println("Generating Entity poly for: " + entityType + " - " + Registry.ENTITY_TYPE.getId(entityType));

        return EntityPolyGenerator.generatePoly(entityType, builder);
    }

    public static <T extends Entity> void addEntityToBuilder(EntityType<T> entityType, PolyRegistry builder) {
        builder.registerEntityPoly(entityType, generatePoly(entityType, builder));
    }
}
