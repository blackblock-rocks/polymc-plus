package rocks.blackblock.polymcplus.wizard;

import io.github.theepicblock.polymc.api.wizard.PacketConsumer;
import io.github.theepicblock.polymc.impl.poly.wizard.AbstractVirtualEntity;
import io.github.theepicblock.polymc.impl.poly.wizard.EntityUtil;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import rocks.blackblock.polymcplus.mixin.DisplayEntityAccessor;
import rocks.blackblock.polymcplus.mixin.ItemDisplayEntityAccessor;

/**
 * A Virtual Display entity
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.5.1
 */
public abstract class VDisplayEntity extends AbstractVirtualEntity {

    public static final TrackedData<Vector3f> SCALE_KEY = DisplayEntityAccessor.getSCALE();
    public static final TrackedData<Integer> BRIGHTNESS_KEY = DisplayEntityAccessor.getBRIGHTNESS();
    public static final TrackedData<Float> VIEW_RANGE_KEY = DisplayEntityAccessor.getVIEW_RANGE();
    public static final TrackedData<Quaternionf> LEFT_ROTATION_KEY = DisplayEntityAccessor.getLEFT_ROTATION();
    public static final TrackedData<Quaternionf> RIGHT_ROTATION_KEY = DisplayEntityAccessor.getRIGHT_ROTATION();
    public static final TrackedData<Float> WIDTH_KEY = DisplayEntityAccessor.getWIDTH();
    public static final TrackedData<Float> HEIGHT_KEY = DisplayEntityAccessor.getHEIGHT();
    public static final TrackedData<Float> SHADOW_STRENGTH_KEY = DisplayEntityAccessor.getSHADOW_STRENGTH();
    public static final TrackedData<Integer> GLOW_COLOR_OVERRIDE_KEY = DisplayEntityAccessor.getGLOW_COLOR_OVERRIDE();

    /**
     * Spawn a virtual item display entity for the given consumers
     *
     * @since    0.5.0
     */
    public void spawn(PacketConsumer view, Vec3d pos, float pitch, float yaw) {
        this.spawn(view, pos, pitch, yaw, 0, Vec3d.ZERO);
    }

    /**
     * Send a single width update
     *
     * @since    0.5.0
     */
    public void setWidth(PacketConsumer view, float width) {
        this.sendSingleTrackedData(view, WIDTH_KEY, width);
    }

    /**
     * Send a single height update
     *
     * @since    0.5.0
     */
    public void setHeight(PacketConsumer view, float height) {
        this.sendSingleTrackedData(view, HEIGHT_KEY, height);
    }

    /**
     * Send a single scale update
     *
     * @since    0.5.0
     */
    public void setScale(PacketConsumer view, Vector3f scale) {
        this.sendSingleTrackedData(view, SCALE_KEY, scale);
    }

    /**
     * Send a single brightness update
     *
     * @since    0.5.0
     */
    public void setBrightness(PacketConsumer view, Integer value) {
        this.sendSingleTrackedData(view, BRIGHTNESS_KEY, value);
    }

    /**
     * Send a single view-range update
     *
     * @since    0.5.0
     */
    public void setViewRange(PacketConsumer view, Float range) {
        this.sendSingleTrackedData(view, VIEW_RANGE_KEY, range);
    }

    /**
     * Send a single data tracker update
     *
     * @since 0.5.0
     */
    public <T> void sendSingleTrackedData(PacketConsumer player, TrackedData<T> key, T value) {
        player.sendPacket(EntityUtil.createDataTrackerUpdate(
                this.id,
                key,
                value
        ));
    }
}
