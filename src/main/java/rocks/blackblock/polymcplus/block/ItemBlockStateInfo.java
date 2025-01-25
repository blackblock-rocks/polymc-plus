package rocks.blackblock.polymcplus.block;

import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.shape.VoxelShape;
import rocks.blackblock.bib.util.BibItem;
import rocks.blackblock.bib.util.BibLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Info per blockstate
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.2.0
 */
public class ItemBlockStateInfo {

    // The actual modded state this represents
    protected BlockState modded_state;

    // The ItemBlockPoly
    protected ItemBlockPoly poly;

    // The ItemCMD
    protected List<RotatedItemCMD> rotated_item_cmds = new ArrayList<>();

    // The cached itemstack to send to the client
    protected ThreadLocal<List<RotatedItemCMD>> threaded_rotated_item_cmds = null;

    // The client collision blockstate
    protected BlockState client_collision_state = null;

    // The preferred collision shape to use
    protected VoxelShape preferred_collision_shape = null;

    /**
     * Initialize this instance
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public ItemBlockStateInfo(BlockState modded_state, ItemBlockPoly poly) {
        this.modded_state = modded_state;
        this.poly = poly;
        this.generateClientCollisionState();
    }

    /**
     * Set the item & CMD value to use
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public RotatedItemCMD addClientItem(ItemCMD item_cmd) {

        this.threaded_rotated_item_cmds = null;

        RotatedItemCMD rotated_item_cmd = new RotatedItemCMD(item_cmd);
        this.rotated_item_cmds.add(rotated_item_cmd);
        return rotated_item_cmd;
    }

    /**
     * Get the preferred collision type
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public String getPreferredCollisionType() {
        return this.poly.preferred_collision_type;
    }

    /**
     * Generate the client-side blockstate to use as a basis
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    private void generateClientCollisionState() {
        if (this.poly.poly_plus_registry != null) {
            this.client_collision_state = this.poly.poly_plus_registry.findInvisibleCollisionState(this);
        } else {
            this.client_collision_state = this.poly.barrier_state;
        }
    }

    /**
     * Get the modded BlockState
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public BlockState getModdedState() {
        return this.modded_state;
    }

    /**
     * Get the BlockState to use for the collision.
     * This should ideally be an invisible state.
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public BlockState getClientCollisionState() {
        return this.client_collision_state;
    }

    /**
     * Generate the threadsafe client stack
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public void generateThreadsafeRotatedItemCmds() {
        this.threaded_rotated_item_cmds = ThreadLocal.withInitial(() -> {

            List<RotatedItemCMD> result = new ArrayList<>(this.rotated_item_cmds.size());

            for (RotatedItemCMD rotated_item_cmd : this.rotated_item_cmds) {
                RotatedItemCMD copy = rotated_item_cmd.copy();
                copy.generateClientStack();
                result.add(copy);
            }

            return result;
        });
    }

    /**
     * Get the RotatedItemCMDs
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.5.1
     */
    public List<RotatedItemCMD> getRotatedItemCMDs() {

        if (this.threaded_rotated_item_cmds == null) {
            this.generateThreadsafeRotatedItemCmds();
        }

        return this.threaded_rotated_item_cmds.get();
    }

    /**
     * Return the client itemstack to use
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public List<ItemStack> getClientStacks() {

        List<ItemStack> result = new ArrayList<>(this.rotated_item_cmds.size());

        for (RotatedItemCMD rotated_item_cmd : this.getRotatedItemCMDs()) {
            result.add(rotated_item_cmd.getClientStack());
        }

        return result;
    }

    /**
     * String representation of this instance
     *
     * @since    0.5.0
     */
    @Override
    public String toString() {

        StringBuilder stack_info = new StringBuilder();
        List<ItemStack> stacks = this.getClientStacks();

        for (ItemStack stack : stacks) {
            if (!stack_info.isEmpty()) {
                stack_info.append(", ");
            }

            stack_info.append(BibItem.toDebugString(stack));
        }

        return "ItemBlockStateInfo{modded_state=" + this.modded_state + ",client_items=" + stack_info + ",client_collision_state=" + this.client_collision_state + "}";
    }

    /**
     * A class to group an ItemCMD and its wanted rotations
     *
     * @since    0.5.1
     */
    public static class RotatedItemCMD implements BibLog.Argable {

        protected ItemCMD item_cmd;

        // The X value
        protected Integer x = null;

        // The Y value
        protected Integer y = null;

        // The calculated yaw
        protected int yaw;

        // The calculated pitch
        protected int pitch;

        // The cached item stack
        protected ItemStack cached_client_stack = null;

        /**
         * Initialize the class
         *
         * @author   Jelle De Loecker   <jelle@elevenways.be>
         * @since    0.5.1
         */
        public RotatedItemCMD(ItemCMD item_cmd) {
            this.item_cmd = item_cmd;
        }

        /**
         * Set the X value
         *
         * @author   Jelle De Loecker   <jelle@elevenways.be>
         * @since    0.2.0
         */
        public void setX(Integer x) {
            this.x = x;

            if (x == null || x == 0) {
                this.pitch = 0;
            } else {
                this.pitch = (x + 180) % 360;
            }
        }

        /**
         * Get the X value
         *
         * @author   Jelle De Loecker   <jelle@elevenways.be>
         * @since    0.2.0
         */
        public int getX() {
            return this.x;
        }

        /**
         * Set the Y rotation value
         *
         * @author   Jelle De Loecker   <jelle@elevenways.be>
         * @since    0.2.0
         */
        public void setY(Integer y) {
            this.y = y;

            if (y == null) {
                this.yaw = 0;
            } else {
                this.yaw = (y + 180) % 360;
            }
        }

        /**
         * Get the Y value
         *
         * @author   Jelle De Loecker   <jelle@elevenways.be>
         * @author   Jelle De Loecker   <jelle@elevenways.be>
         * @since    0.2.0
         */
        public int getY() {
            return this.y;
        }

        /**
         * Get the yaw rotation
         *
         * @author   Jelle De Loecker   <jelle@elevenways.be>
         * @since    0.2.0
         */
        public int getYaw() {
            return this.yaw;
        }

        /**
         * Get the pitch rotation
         *
         * @author   Jelle De Loecker   <jelle@elevenways.be>
         * @since    0.4.0
         */
        public int getPitch() {
            return this.pitch;
        }

        /**
         * Generate the client stack
         *
         * @author   Jelle De Loecker   <jelle@elevenways.be>
         * @since    0.5.1
         */
        public ItemStack generateClientStack() {

            // We actually don't need CMDs anymore, but we'll clean this up another time
            //this.cached_client_stack = BibItem.createStackWithCustomModelData(this.item_cmd.client_item, this.item_cmd.cmd_value);

            this.cached_client_stack = new ItemStack(this.item_cmd.client_item);

            // Remove all the existing components (like name, lore, glint, ...)
            // we don't need them
            var components = this.cached_client_stack.getComponents();
            List<ComponentType<?>> to_remove = new ArrayList<>(components.getTypes());
            for (ComponentType<?> component : to_remove) {
                this.cached_client_stack.remove(component);
            }

            this.cached_client_stack.set(DataComponentTypes.ITEM_MODEL, this.item_cmd.getItemModelIdentifier());

            return this.cached_client_stack;
        }

        /**
         * Get the ItemStack
         *
         * @author   Jelle De Loecker   <jelle@elevenways.be>
         * @since    0.5.1
         */
        public ItemStack getClientStack() {
            return this.cached_client_stack;
        }

        /**
         * Create a copy
         *
         * @author   Jelle De Loecker   <jelle@elevenways.be>
         * @since    0.5.1
         */
        public RotatedItemCMD copy() {
            RotatedItemCMD result = new RotatedItemCMD(this.item_cmd);
            result.setX(this.x);
            result.setY(this.y);
            return result;
        }

        /**
         * BibLog.Arg representation of this instance
         *
         * @since    0.7.0
         */
        @Override
        public BibLog.Arg toBBLogArg() {
            var result = BibLog.createArg(this);

            result.add("item_cmd", this.item_cmd);
            result.add("x", this.x);
            result.add("y", this.y);
            result.add("yaw", this.yaw);
            result.add("pitch", this.pitch);

            return result;
        }

        /**
         * String representation of this instance
         *
         * @since    0.7.0
         */
        @Override
        public String toString() {
            return this.toBBLogArg().toString();
        }
    }
}
