package rocks.blackblock.polymcplus.block;

import io.github.theepicblock.polymc.api.PolyRegistry;
import io.github.theepicblock.polymc.api.block.BlockPoly;
import io.github.theepicblock.polymc.api.resource.ModdedResources;
import io.github.theepicblock.polymc.api.resource.PolyMcResourcePack;
import io.github.theepicblock.polymc.api.resource.json.*;
import io.github.theepicblock.polymc.api.wizard.Wizard;
import io.github.theepicblock.polymc.api.wizard.WizardInfo;
import io.github.theepicblock.polymc.impl.misc.logging.SimpleLogger;
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
import rocks.blackblock.polymcplus.PolyMcPlus;
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

        // We need the modded block's blockstate json info, because we can re-use models that only need rotating
        JBlockState modded_block_state = PolyMcPlus.getModdedResources().getBlockState(this.block_id.getNamespace(), this.block_id.getPath());

        if (modded_block_state == null) {
            PolyMcPlus.LOGGER.error("Modded block " + this.block_id + " doesn't seem to have a blockstate json");
            return;
        }

        for (BlockState modded_state : modded_states) {

            ItemBlockStateInfo info = new ItemBlockStateInfo(modded_state, this);

            // @TODO: If we actually get multiple "variants" (not "multiparts"),
            // only 1 of those variants should be rendered (at random)
            JBlockStateVariant[] modded_variants = modded_block_state.getVariantsBestMatching(modded_state);

            if (modded_variants == null || modded_variants.length == 0) {
                modded_variants = modded_block_state.getMultipartVariantsBestMatching(modded_state);
            }

            if (modded_variants == null || modded_variants.length == 0) {
                PolyMcPlus.LOGGER.error("No model variants found for modded BlockState " + modded_state);
                continue;
            }

            // Add all the variants
            for (JBlockStateVariant variant : modded_variants) {

                Integer x = variant.x();
                Integer y = variant.y();
                String model = variant.model();
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

                ItemBlockStateInfo.RotatedItemCMD rotated = info.addClientItem(item_cmd);
                rotated.setX(x);
                rotated.setY(y);
            }

            info.generateThreadsafeRotatedItemCmds();

            this.setBlockStateInfo(modded_state, info);
        }
    }

    /**
     * Add info on a modded state
     *
     * @since    0.5.0
     */
    public void setBlockStateInfo(BlockState modded_state, ItemBlockStateInfo info) {
        this.states.put(modded_state, info);
    }

    /**
     * Get ItemBlockStateInfo for the given modded state
     *
     * @since    0.5.0
     */
    public ItemBlockStateInfo getBlockStateInfo(BlockState modded_state) {
        return this.states.get(modded_state);
    }

    /**
     * Get the client block to show to the user
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    @Override
    public BlockState getClientBlock(BlockState modded_state) {

        ItemBlockStateInfo info = this.getBlockStateInfo(modded_state);

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
        ItemBlockStateInfo info = this.getBlockStateInfo(wizard_info.getBlockState());
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

            if (modded_variants != null) {
                // Make sure these block models are imported: the item will use these models
                pack.importRequirements(modded_resources, modded_variants, logger);
            }

            JBlockStateVariant[] modded_multipart_variants = modded_block_state.getMultipartVariantsBestMatching(modded_state);

            if (modded_multipart_variants != null) {
                pack.importRequirements(modded_resources, modded_multipart_variants, logger);
            }
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
     * String representation of this instance
     *
     * @since    0.5.0
     */
    @Override
    public String toString() {
        return "ItemBlockPoly{" + this.block_id + ",states=" + this.states + "}";
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
