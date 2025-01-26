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

    @Accessor("START_INTERPOLATION")
    static TrackedData<Integer> getSTART_INTERPOLATION() {
        throw new AssertionError();
    }

    @Accessor("INTERPOLATION_DURATION")
    static TrackedData<Integer> getINTERPOLATION_DURATION() {
        throw new AssertionError();
    }

    @Accessor("TELEPORT_DURATION")
    static TrackedData<Integer> getTELEPORT_DURATION() {
        throw new AssertionError();
    }

    @Accessor("TRANSLATION")
    static TrackedData<Vector3f> getTRANSLATION() {
        throw new AssertionError();
    }

    @Accessor("BILLBOARD")
    static TrackedData<AffineTransformation> getBILLBOARD() {
        throw new AssertionError();
    }

    @Accessor("SCALE")
    static TrackedData<Vector3f> getSCALE() {
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

    @Accessor("SHADOW_RADIUS")
    static TrackedData<Float> getSHADOW_RADIUS() {
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

    @Invoker("setInterpolationDuration")
    void setInterpolationDuration(int duration);

    @Invoker("setStartInterpolation")
    void setStartInterpolation(int duration);

    @Invoker("setTeleportDuration")
    void setTeleportDuration(int duration);

    @Invoker("setBillboardMode")
    void setBillboardMode(DisplayEntity.BillboardMode billboardMode);

    @Invoker("setViewRange")
    void setViewRange(float viewRange);

    @Invoker("setShadowRadius")
    void setShadowRadius(float shadowRadius);

    @Invoker("setShadowStrength")
    void setShadowStrength(float shadowStrength);

    @Invoker("setGlowColorOverride")
    void setGlowColorOverride(int glowColorOverride);

    @Invoker("setDisplayWidth")
    void setWidth(float width);

    @Invoker("setDisplayHeight")
    void setHeight(float height);

    @Invoker("setTransformation")
    void doSetTransformation(AffineTransformation transformation);

    @Invoker("setBrightness")
    void setBrightness(@Nullable Brightness brightness);

}