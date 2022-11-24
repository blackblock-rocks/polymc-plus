package rocks.blackblock.polymcplus.wizard;

import io.github.theepicblock.polymc.impl.poly.wizard.AbstractVirtualEntity;
import net.minecraft.entity.EntityType;

import java.util.UUID;

public class TestVirtualEntity extends AbstractVirtualEntity {

    protected final EntityType<?> entity_type;

    public TestVirtualEntity(EntityType<?> entity_type) {
        super();
        this.entity_type = entity_type;
    }


    @Override
    public EntityType<?> getEntityType() {
        return this.entity_type;
    }
}
