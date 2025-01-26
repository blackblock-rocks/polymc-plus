package rocks.blackblock.polymcplus.wizard;

import io.github.theepicblock.polymc.api.wizard.PacketConsumer;
import io.github.theepicblock.polymc.impl.poly.wizard.AbstractVirtualEntity;
import io.github.theepicblock.polymc.impl.poly.wizard.EntityUtil;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import rocks.blackblock.polymcplus.mixin.DisplayEntityAccessor;

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
    public static final TrackedData<Integer> START_INTERPOLATION_KEY = DisplayEntityAccessor.getSTART_INTERPOLATION();
    public static final TrackedData<Integer> INTERPOLATION_DURATION_KEY = DisplayEntityAccessor.getINTERPOLATION_DURATION();
    public static final TrackedData<Integer> TELEPORT_DURATION_KEY = DisplayEntityAccessor.getTELEPORT_DURATION();
    public static final TrackedData<Vector3f> TRANSLATION_KEY = DisplayEntityAccessor.getTRANSLATION();
    public static final TrackedData<AffineTransformation> BILLBOARD_KEY = DisplayEntityAccessor.getBILLBOARD();
    public static final TrackedData<Float> SHADOW_RADIUS_KEY = DisplayEntityAccessor.getSHADOW_RADIUS();

    /**
     * Move the entity
     *
     * @since    0.5.0
     */
    public void move(PacketConsumer view, Position pos) {
        this.move(view, pos.getX(), pos.getY(), pos.getZ(), (byte) 0, (byte) 0, false);
    }

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
     * Set the left rotation
     *
     * @since    0.6.0
     */
    public void setLeftRotation(PacketConsumer view, Quaternionf rotation) {
        this.sendSingleTrackedData(view, LEFT_ROTATION_KEY, rotation);
    }

    /**
     * Set the right rotation
     *
     * @since    0.6.0
     */
    public void setRightRotation(PacketConsumer view, Quaternionf rotation) {
        this.sendSingleTrackedData(view, RIGHT_ROTATION_KEY, rotation);
    }

    /**
     * Set the interpolation duration
     *
     * @since    0.6.0
     */
    public void setInterpolationStart(PacketConsumer view, Integer start) {
        this.sendSingleTrackedData(view, START_INTERPOLATION_KEY, start);
    }

    /**
     * Set the interpolation duration
     *
     * @since    0.6.0
     */
    public void setInterpolationDuration(PacketConsumer view, Integer duration) {
        this.sendSingleTrackedData(view, INTERPOLATION_DURATION_KEY, duration);
    }

    /**
     * Set the teleport duration
     *
     * @since    0.6.0
     */
    public void setTeleportDuration(PacketConsumer view, Integer duration) {
        this.sendSingleTrackedData(view, TELEPORT_DURATION_KEY, duration);
    }

    /**
     * Set the translation
     *
     * @since    0.6.0
     */
    public void setTranslation(PacketConsumer view, Vector3f translation) {
        this.sendSingleTrackedData(view, TRANSLATION_KEY, translation);
    }

    /**
     * Set the billboard mode
     *
     * @since    0.6.0
     */
    public void setBillboard(PacketConsumer view, AffineTransformation billboard) {
        this.sendSingleTrackedData(view, BILLBOARD_KEY, billboard);
    }

    /**
     * Set the shadow radius
     *
     * @since    0.6.0
     */
    public void setShadowRadius(PacketConsumer view, Float radius) {
        this.sendSingleTrackedData(view, SHADOW_RADIUS_KEY, radius);
    }

    /**
     * Send a single shadow-strength update
     *
     * @since    0.6.0
     */
    public void setShadowStrength(PacketConsumer view, Float strength) {
        this.sendSingleTrackedData(view, SHADOW_STRENGTH_KEY, strength);
    }

    /**
     * Send a single glow-color-override update
     *
     * @since    0.6.0
     */
    public void setGlowColorOverride(PacketConsumer view, Integer color) {
        this.sendSingleTrackedData(view, GLOW_COLOR_OVERRIDE_KEY, color);
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
