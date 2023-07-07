package rocks.blackblock.polymcplus.wizard;

import io.github.theepicblock.polymc.api.wizard.PacketConsumer;
import io.github.theepicblock.polymc.impl.poly.wizard.AbstractVirtualEntity;
import io.github.theepicblock.polymc.impl.poly.wizard.EntityUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import rocks.blackblock.polymcplus.mixin.DisplayEntityAccessor;
import rocks.blackblock.polymcplus.mixin.ItemDisplayEntityAccessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Virtual Item Display entity
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.5.0
 */
public class VItemDisplay extends AbstractVirtualEntity {

    public static final TrackedData<Vector3f> SCALE_KEY = DisplayEntityAccessor.getSCALE();
    public static final TrackedData<Integer> BRIGHTNESS_KEY = DisplayEntityAccessor.getBRIGHTNESS();
    public static final TrackedData<Float> VIEW_RANGE_KEY = DisplayEntityAccessor.getVIEW_RANGE();
    public static final TrackedData<Quaternionf> LEFT_ROTATION_KEY = DisplayEntityAccessor.getLEFT_ROTATION();
    public static final TrackedData<Quaternionf> RIGHT_ROTATION_KEY = DisplayEntityAccessor.getRIGHT_ROTATION();
    public static final TrackedData<ItemStack> ITEM_KEY = ItemDisplayEntityAccessor.getITEM();
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
     * Send a single item update
     *
     * @since    0.5.0
     */
    public void setItem(PacketConsumer view, ItemStack stack) {
        this.sendSingleTrackedData(view, ITEM_KEY, stack);
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

    /**
     * The type of entity this uses
     *
     * @since    0.5.0
     */
    @Override
    public EntityType<?> getEntityType() {
        return EntityType.ITEM_DISPLAY;
    }

    /**
     * A class to batch the updates
     *
     * @since    0.5.0
     */
    public static class BatchedData {
        private final VItemDisplay display;
        private final List<DataTracker.Entry<?>> entries = new ArrayList<>();
        private final Map<TrackedData<?>, DataTracker.Entry<?>> map = new HashMap<>();
        private boolean is_dirty = false;

        private Vec3d pos = Vec3d.ZERO;
        private float pitch = 0f;
        private float yaw = 0f;

        public BatchedData(VItemDisplay display) {
            this.display = display;
        }

        /**
         * Set the position
         *
         * @since    0.5.0
         */
        public void setPosition(Vec3d pos) {
            this.pos = pos;
        }

        /**
         * Set the pitch
         *
         * @since    0.5.0
         */
        public void setPitch(float pitch) {
            this.pitch = pitch;
        }

        /**
         * Set the yaw
         *
         * @since    0.5.0
         */
        public void setYaw(float yaw) {
            this.yaw = yaw;
        }

        /**
         * Create a data tracker update
         *
         * @since    0.5.0
         */
        public <T> void set(TrackedData<T> key, T value) {

            DataTracker.Entry<?> entry = new DataTracker.Entry<>(key, value);

            this.map.put(key, entry);
            this.is_dirty = true;
        }

        /**
         * Send a single width update
         *
         * @since    0.5.0
         */
        public void setWidth(float width) {
            this.set(WIDTH_KEY, width);
        }

        /**
         * Send a single height update
         *
         * @since    0.5.0
         */
        public void setHeight(float height) {
            this.set(HEIGHT_KEY, height);
        }

        /**
         * Send a single scale update
         *
         * @since    0.5.0
         */
        public void setScale(Vector3f scale) {
            this.set(SCALE_KEY, scale);
        }

        /**
         * Send a single brightness update
         *
         * @since    0.5.0
         */
        public void setBrightness(Integer value) {
            this.set(BRIGHTNESS_KEY, value);
        }

        /**
         * Send a single item update
         *
         * @since    0.5.0
         */
        public void setItem(ItemStack stack) {
            this.set(ITEM_KEY, stack);
        }

        /**
         * Send a single view-range update
         *
         * @since    0.5.0
         */
        public void setViewRange(Float range) {
            this.set(VIEW_RANGE_KEY, range);
        }

        /**
         * Get the entries
         *
         * @since    0.5.0
         */
        public List<DataTracker.Entry<?>> getEntries() {

            if (this.is_dirty) {
                this.entries.clear();

                this.map.forEach((trackedData, entry) -> {
                    this.entries.add(entry);
                });

                this.is_dirty = false;
            }

            return this.entries;
        }

        /**
         * Submit the spawn packet & the updates
         *
         * @since    0.5.0
         */
        public void spawnAndSubmit(PacketConsumer view) {
            this.display.spawn(view, this.pos, this.pitch, this.yaw);
            this.submit(view);
        }

        /**
         * Submit these updates
         *
         * @since    0.5.0
         */
        public void submit(PacketConsumer view) {
            view.sendPacket(EntityUtil.createDataTrackerUpdate(
                    this.display.id,
                    this.getEntries()
            ));
        }
    }
}
