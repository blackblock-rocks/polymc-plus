package rocks.blackblock.polymcplus.mixin;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DisplayEntity.TextDisplayEntity.class)
public interface TextDisplayEntityAccessor {

    @Accessor("TEXT")
    static TrackedData<Text> getTEXT() {
        throw new AssertionError();
    }

    @Accessor("LINE_WIDTH")
    static TrackedData<Integer> getLINE_WIDTH() {
        throw new AssertionError();
    }

    @Accessor("BACKGROUND")
    static TrackedData<Integer> getBACKGROUND() {
        throw new AssertionError();
    }

    @Accessor("TEXT_OPACITY")
    static TrackedData<Byte> getTEXT_OPACITY() {
        throw new AssertionError();
    }

    @Accessor("TEXT_DISPLAY_FLAGS")
    static TrackedData<Byte> getTEXT_DISPLAY_FLAGS() {
        throw new AssertionError();
    }
}
