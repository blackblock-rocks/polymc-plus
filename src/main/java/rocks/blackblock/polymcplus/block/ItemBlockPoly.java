package rocks.blackblock.polymcplus.block;

import io.github.theepicblock.polymc.api.PolyRegistry;
import io.github.theepicblock.polymc.api.block.BlockPoly;
import io.github.theepicblock.polymc.api.resource.ModdedResources;
import io.github.theepicblock.polymc.api.resource.PolyMcResourcePack;
import io.github.theepicblock.polymc.api.resource.json.*;
import io.github.theepicblock.polymc.api.wizard.Wizard;
import io.github.theepicblock.polymc.api.wizard.WizardInfo;
import io.github.theepicblock.polymc.impl.misc.logging.SimpleLogger;
import io.github.theepicblock.polymc.impl.resource.ModdedResourceContainerImpl;
import io.github.theepicblock.polymc.impl.resource.json.JModelImpl;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.shape.VoxelShape;
import rocks.blackblock.polymcplus.polymc.PolyPlusRegistry;
import rocks.blackblock.polymcplus.wizard.ItemBlockWizard;

import java.util.*;

/**
 * This BlockPoly uses items in item frames to display blocks
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.2.0
 */
public class ItemBlockPoly implements BlockPoly {

    // All the collected info of the modded blockstates
    protected final Map<BlockState, ItemBlockStateInfo> states = new HashMap<>();

    // All the ItemCMDs in use
    protected final Map<String, ItemCMD> model_to_cmd = new HashMap<>();

    protected final Map<BlockState, Integer> states_cmd_values;
    protected final Map<BlockState, Item> states_client_items;
    protected final Map<BlockState, ThreadLocal<ItemStack>> states_cached_items;
    protected final Block modded_block;
    protected final Identifier block_id;
    protected PolyPlusRegistry poly_plus_registry = null;

    protected final BlockState barrier_state = Blocks.BARRIER.getDefaultState();
    protected final String preferred_collision_type;

    /**
     * Initialize the ItemBlockPoly for all the states of the given block
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public ItemBlockPoly(Block modded_block, PolyRegistry registry, String collision_type) {
        this(modded_block.getStateManager().getStates(), registry, collision_type);
    }

    /**
     * Initialize the ItemBlockPoly for all the states of the given block
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public ItemBlockPoly(Block modded_block, PolyRegistry registry) {
        this(modded_block, registry, null);
    }

    /**
     * Initialize the ItemBlockPoly for all the given states
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public ItemBlockPoly(List<BlockState> modded_states, PolyRegistry registry) {
        this(modded_states, registry, null);
    }

    /**
     * Initialize the ItemBlockPoly for all the given states
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public ItemBlockPoly(List<BlockState> modded_states, PolyRegistry registry, String collision_type) {

        if (registry instanceof PolyPlusRegistry poly_plus_registry) {
            this.poly_plus_registry = poly_plus_registry;
        }

        this.preferred_collision_type = collision_type;

        this.states_cmd_values = new HashMap<>();
        this.states_client_items = new HashMap<>();
        this.states_cached_items = new HashMap<>();

        this.modded_block = modded_states.get(0).getBlock();
        this.block_id = Registries.BLOCK.getId(this.modded_block);

        this.processModdedStates(modded_states, registry);
    }

    /**
     * Process all the modded states
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    private void processModdedStates(List<BlockState> modded_states, PolyRegistry registry) {

        var modded_resources = new ModdedResourceContainerImpl();

        // We need the blockstate json info, because we can re-use models that only need rotating
        JBlockState modded_block_state = modded_resources.getBlockState(this.block_id.getNamespace(), this.block_id.getPath());

        for (BlockState modded_state : modded_states) {

            ItemBlockStateInfo info = new ItemBlockStateInfo(modded_state, this);

            JBlockStateVariant[] modded_variants = modded_block_state.getVariantsBestMatching(modded_state);
            JBlockStateVariant best_variant = modded_variants[0];

            Integer x = best_variant.x();
            Integer y = best_variant.y();
            String model = best_variant.model();
            ItemCMD item_cmd = null;

            if (this.model_to_cmd.containsKey(model)) {
                item_cmd = this.model_to_cmd.get(model);
            } else {
                Pair<Item,Integer> pair  = registry.getCMDManager().requestCMD();
                Item client_item = pair.getLeft();
                Integer cmd_value = pair.getRight();
                item_cmd = new ItemCMD(client_item, cmd_value);
                this.model_to_cmd.put(model, item_cmd);
            }

            info.setClientItem(item_cmd);
            info.setX(x);
            info.setY(y);

            this.states.put(modded_state, info);
        }
    }

    /**
     * Get the client block to show to the user
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    @Override
    public BlockState getClientBlock(BlockState modded_state) {

        ItemBlockStateInfo info = this.states.get(modded_state);

        if (info != null) {
            return info.getClientCollisionState();
        }

        return barrier_state;
    }

    /**
     * All instances of this BlockPoly have wizards
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    @Override
    public boolean hasWizard() {
        return true;
    }

    /**
     * Use the provider to create the wizard
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    @Override
    public Wizard createWizard(WizardInfo wizard_info) {
        ItemBlockStateInfo info = this.states.get(wizard_info.getBlockState());
        return new ItemBlockWizard(info, wizard_info);
    }

    /**
     * Add new CMD item overrides for all the states of the given block
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    @Override
    public void addToResourcePack(Block modded_block, ModdedResources modded_resources, PolyMcResourcePack pack, SimpleLogger logger) {
        this.addToResourcePack(this.states.keySet().stream().toList(), modded_resources, pack, logger);
    }

    /**
     * Add new CMD item overrides for all the given states
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public void addToResourcePack(List<BlockState> modded_states, ModdedResources modded_resources, PolyMcResourcePack pack, SimpleLogger logger) {

        Block modded_block = modded_states.get(0).getBlock();
        Identifier modded_block_id = Registries.BLOCK.getId(modded_block);
        JBlockState modded_block_state = modded_resources.getBlockState(modded_block_id.getNamespace(), modded_block_id.getPath());

        // First we have to import all the models of the modded states
        for (BlockState modded_state : modded_states) {
            // Get the model that the modded block state uses
            JBlockStateVariant[] modded_variants = modded_block_state.getVariantsBestMatching(modded_state);

            if (modded_variants == null) {
                continue;
            }

            // Make sure these block models are imported: the item will use these models
            pack.importRequirements(modded_resources, modded_variants, logger);
        }

        int counter = -1;

        for (String model_location : this.model_to_cmd.keySet()) {
            counter++;
            ItemCMD item_cmd = this.model_to_cmd.get(model_location);

            Identifier item_identifier = item_cmd.getIdentifier();


            // Copy and retrieve the vanilla item's model
            JModel client_item_model = pack.getOrDefaultVanillaItemModel(modded_resources, item_identifier.getNamespace(), item_identifier.getPath(), logger);

            JModel frame_model = new JModelImpl();
            frame_model.setParent(model_location);

            double[] rotation = {0, 0, 0};
            double[] translation = {0, 0, -16};
            double[] scale = {2, 2, 2};

            JModelDisplay display = new JModelDisplay(rotation, translation, scale);
            frame_model.setDisplay(JModelDisplayType.FIXED, display);

            String item_model_name = this.block_id.getNamespace() + "/" + this.block_id.getPath() + "_" + counter;
            String polyplus_item_location = "polymcplus:item/" + item_model_name;

            pack.setItemModel("polymcplus", item_model_name, frame_model);

            // Add an override into the vanilla item's model that references the modded one
            client_item_model.getOverrides().add(JModelOverride.ofCMD(item_cmd.cmd_value, polyplus_item_location));
        }
    }

    /**
     * Add new CMD item overrides for all the given states
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public void old_addToResourcePack(List<BlockState> modded_states, ModdedResources modded_resources, PolyMcResourcePack pack, SimpleLogger logger) {

        Block modded_block = modded_states.get(0).getBlock();
        Identifier modded_block_id = Registries.BLOCK.getId(modded_block);
        JBlockState modded_block_state = modded_resources.getBlockState(modded_block_id.getNamespace(), modded_block_id.getPath());

        if (modded_block_state == null) {
            logger.error("Can't find blockstate definition for "+modded_block_id+", can't generate resources for it");
            return;
        }

        HashSet<BlockState> client_states_done = new HashSet<>();
        int state_nr = -1;

        for (BlockState modded_state : modded_states) {
            state_nr++;

            // Get the model that the modded block state uses
            JBlockStateVariant[] modded_variants = modded_block_state.getVariantsBestMatching(modded_state);

            if (modded_variants == null) {
                continue;
            }

            System.out.println("Fount variants for " + modded_state);

            // Make sure these block models are imported: the item will use these models
            pack.importRequirements(modded_resources, modded_variants, logger);

            Item client_item = this.states_client_items.get(modded_state);
            Identifier client_item_id = Registries.ITEM.getId(client_item);

            // Copy and retrieve the vanilla item's model
            JModel client_item_model = pack.getOrDefaultVanillaItemModel(modded_resources, client_item_id.getNamespace(), client_item_id.getPath(), logger);

            String item_model_name = modded_block_id.getPath() + "_" + state_nr;
            String polyplus_item_location = "polymcplus:item/" + item_model_name;

            JBlockStateVariant best_variant = modded_variants[0];

            if (best_variant == null) {
                continue;
            }

            String model_location = best_variant.model();

            int x = best_variant.x();
            int y = best_variant.y();

            JModel frame_model = new JModelImpl();
            frame_model.setParent(model_location);

            double[] rotation = {x,y,0};
            double[] translation = {0, 0, -16};
            double[] scale = {2, 2, 2};

            JModelDisplay display = new JModelDisplay(rotation, translation, scale);
            frame_model.setDisplay(JModelDisplayType.FIXED, display);


            pack.setItemModel("polymcplus", item_model_name, frame_model);

            // Add an override into the vanilla item's model that references the modded one
            client_item_model.getOverrides().add(JModelOverride.ofCMD(this.states_cmd_values.get(modded_state), polyplus_item_location));
        }

    }

    /**
     * Info per blockstate
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public static class ItemBlockStateInfo {

        // The actual modded state this represents
        protected BlockState modded_state;

        // The ItemBlockPoly
        protected ItemBlockPoly poly;

        // The ItemCMD
        protected ItemCMD item_cmd;

        // The cached itemstack to send to the client
        protected ThreadLocal<ItemStack> cached_client_stack = null;

        // The X value
        protected Integer x = null;

        // The Y value
        protected Integer y = null;

        // The calculated yaw
        protected int yaw;

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
        public void setClientItem(Item client_item, int cmd_value) {
            this.setClientItem(new ItemCMD(client_item, cmd_value));
        }

        /**
         * Set the item & CMD value to use
         *
         * @author   Jelle De Loecker   <jelle@elevenways.be>
         * @since    0.2.0
         */
        public void setClientItem(ItemCMD item_cmd) {
            this.item_cmd = item_cmd;
            this.generateClientStack();
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
         * Set the X value
         *
         * @author   Jelle De Loecker   <jelle@elevenways.be>
         * @since    0.2.0
         */
        public void setX(Integer x) {
            this.x = x;
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
         * Generate the threadsafe client stack
         *
         * @author   Jelle De Loecker   <jelle@elevenways.be>
         * @since    0.2.0
         */
        private void generateClientStack() {
            this.cached_client_stack = ThreadLocal.withInitial(() -> {
                var stack = new ItemStack(this.item_cmd.client_item);
                NbtCompound tag = stack.getOrCreateNbt();
                tag.putInt("CustomModelData", this.item_cmd.cmd_value);
                stack.setNbt(tag);
                return stack;
            });
        }

        /**
         * Get the cached stack
         *
         * @author   Jelle De Loecker   <jelle@elevenways.be>
         * @since    0.2.0
         */
        public ThreadLocal<ItemStack> getCachedStack() {
            return this.cached_client_stack;
        }

        /**
         * Return the client itemstack to use
         *
         * @author   Jelle De Loecker   <jelle@elevenways.be>
         * @since    0.2.0
         */
        public ItemStack getClientStack() {
            return this.cached_client_stack.get();
        }
    }

    /**
     * Item & CMD info
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public static class ItemCMD {

        // The client-side replacement item
        protected Item client_item;

        // The CMD value for the item
        protected int cmd_value;

        /**
         * Set the item & CMD value to use
         *
         * @author   Jelle De Loecker   <jelle@elevenways.be>
         * @since    0.2.0
         */
        public ItemCMD(Item client_item, int cmd_value) {
            this.setClientItem(client_item, cmd_value);
        }

        /**
         * Set the item & CMD value to use
         *
         * @author   Jelle De Loecker   <jelle@elevenways.be>
         * @since    0.2.0
         */
        public void setClientItem(Item client_item, int cmd_value) {
            this.client_item = client_item;
            this.cmd_value = cmd_value;
        }

        /**
         * Get the client item's identifier
         *
         * @author   Jelle De Loecker   <jelle@elevenways.be>
         * @since    0.2.0
         */
        public Identifier getIdentifier() {
            return Registries.ITEM.getId(this.client_item);
        }

        /**
         * String representation of this instance
         *
         * @author   Jelle De Loecker   <jelle@elevenways.be>
         * @since    0.2.0
         */
        public String toString() {
            return "ItemCMD{" + this.cmd_value + "," + this.client_item + "}";
        }
    }

    /**
     * Combined property keys
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public static class CombinedPropertyKey {

        private Map<String, Property<?>> property_map = new HashMap<>();
        private TreeMap<String, Comparable<?>> properties = new TreeMap<>();

        public void setProperty(Property<?> property, Comparable<?> value) {
            String property_name = property.getName();
            this.properties.put(property_name, value);
            this.property_map.put(property_name, property);

            System.out.println("Setting property " + property_name + " to " + value + " - Key is now: " + this);
        }

        @Override
        public boolean equals(Object o) {
            return ((o != null) && (this.hashCode() == o.hashCode()));
        }

        public int hashCode() {

            int code = this.properties.hashCode();

            System.out.println("    [" + this + "=" + code + "]");

            return code;
        }

        public String toString() {
            StringBuilder result = new StringBuilder("CombinedPropertyKey{");


            int counter = -1;

            for (String key : this.properties.keySet()) {
                counter++;

                if (counter > 0) {
                    result.append(",");
                }

                result.append(key).append("=").append(this.properties.get(key));
            }

            result.append("}");

            return result.toString();
        }
    }
}
