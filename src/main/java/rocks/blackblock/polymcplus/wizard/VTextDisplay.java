package rocks.blackblock.polymcplus.wizard;

import io.github.theepicblock.polymc.api.wizard.PacketConsumer;
import io.github.theepicblock.polymc.impl.poly.wizard.AbstractVirtualEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import rocks.blackblock.polymcplus.mixin.TextDisplayEntityAccessor;

/**
 * A Virtual Text Display entity
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.5.1
 */
public class VTextDisplay extends VDisplayEntity {

    public static final TrackedData<Text> TEXT_KEY = TextDisplayEntityAccessor.getTEXT();
    public static final TrackedData<Integer> LINE_WIDTH_KEY = TextDisplayEntityAccessor.getLINE_WIDTH();
    public static final TrackedData<Integer> BACKGROUND_KEY = TextDisplayEntityAccessor.getBACKGROUND();
    public static final TrackedData<Byte> TEXT_OPACITY_KEY = TextDisplayEntityAccessor.getTEXT_OPACITY();
    public static final TrackedData<Byte> TEXT_DISPLAY_FLAGS_KEY = TextDisplayEntityAccessor.getTEXT_DISPLAY_FLAGS();

    /**
     * The type of entity this uses
     *
     * @since    0.5.1
     */
    @Override
    public EntityType<?> getEntityType() {
        return EntityType.TEXT_DISPLAY;
    }

    /**
     * Send a single text update
     *
     * @since    0.5.1
     */
    public void setText(PacketConsumer view, Text text) {
        this.sendSingleTrackedData(view, TEXT_KEY, text);
    }

    /**
     * Send a single line-width update
     *
     * @since    0.5.1
     */
    public void setLineWidth(PacketConsumer view, Integer line_width) {
        this.sendSingleTrackedData(view, LINE_WIDTH_KEY, line_width);
    }

    /**
     * Send a single background update
     *
     * @since    0.5.1
     */
    public void setBackground(PacketConsumer view, Integer background) {
        this.sendSingleTrackedData(view, BACKGROUND_KEY, background);
    }

    /**
     * Send a single text-opacity update
     *
     * @since    0.5.1
     */
    public void setTextOpacity(PacketConsumer view, Byte opacity) {
        this.sendSingleTrackedData(view, TEXT_OPACITY_KEY, opacity);
    }

    /**
     * Send a single text-display-flags update
     *
     * @since    0.5.1
     */
    public void setTextDisplayFlags(PacketConsumer view, Byte display_flags) {
        this.sendSingleTrackedData(view, TEXT_DISPLAY_FLAGS_KEY, display_flags);
    }

}
