package rocks.blackblock.polymcplus.mixin;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.decoration.Brightness;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.util.math.AffineTransformation;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DisplayEntity.class)
public interface DisplayEntityAccessor {

    @Accessor("SCALE")
    public static TrackedData<Vector3f> getSCALE() {
        throw new AssertionError();
    }

    @Accessor("BRIGHTNESS")
    static TrackedData<Integer> getBRIGHTNESS() {
        throw new AssertionError();
    }

    @Accessor("VIEW_RANGE")
    static TrackedData<Float> getVIEW_RANGE() {
        throw new AssertionError();
    }

    @Accessor("LEFT_ROTATION")
    static TrackedData<Quaternionf> getLEFT_ROTATION() {
        throw new AssertionError();
    }

    @Accessor("RIGHT_ROTATION")
    static TrackedData<Quaternionf> getRIGHT_ROTATION() {
        throw new AssertionError();
    }

    @Accessor("WIDTH")
    static TrackedData<Float> getWIDTH() {
        throw new AssertionError();
    }

    @Accessor("HEIGHT")
    static TrackedData<Float> getHEIGHT() {
        throw new AssertionError();
    }

    @Accessor("SHADOW_STRENGTH")
    static TrackedData<Float> getSHADOW_STRENGTH() {
        throw new AssertionError();
    }

    @Accessor("GLOW_COLOR_OVERRIDE")
    static TrackedData<Integer> getGLOW_COLOR_OVERRIDE() {
        throw new AssertionError();
    }

    @Invoker("setDisplayWidth")
    void setWidth(float width);

    @Invoker("setDisplayHeight")
    void setHeight(float height);

    @Invoker("setTransformation")
    void doSetTransformation(AffineTransformation transformation);

    @Invoker("setBrightness")
    void setBrightness(@Nullable Brightness brightness);

}