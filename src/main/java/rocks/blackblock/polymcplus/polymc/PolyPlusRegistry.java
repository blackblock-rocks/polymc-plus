package rocks.blackblock.polymcplus.polymc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.theepicblock.polymc.api.PolyRegistry;
import io.github.theepicblock.polymc.api.block.BlockStateManager;
import io.github.theepicblock.polymc.api.block.BlockStateProfile;
import io.github.theepicblock.polymc.api.item.ItemTransformer;
import io.github.theepicblock.polymc.api.resource.ModdedResources;
import io.github.theepicblock.polymc.api.resource.PolyMcResourcePack;
import io.github.theepicblock.polymc.api.resource.json.JBlockState;
import io.github.theepicblock.polymc.api.resource.json.JBlockStateVariant;
import io.github.theepicblock.polymc.api.resource.json.JModel;
import io.github.theepicblock.polymc.impl.Util;
import io.github.theepicblock.polymc.impl.misc.logging.SimpleLogger;
import io.github.theepicblock.polymc.impl.resource.json.JModelImpl;
import net.minecraft.block.*;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import rocks.blackblock.polymcplus.block.ItemBlockPoly;
import rocks.blackblock.polymcplus.block.PolyPlusBlockStateProfile;
import rocks.blackblock.polymcplus.generator.BlockPolyPlusGenerator;

import java.util.*;
import java.util.stream.Stream;

/**
 * The PolyPlus registry
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.1.0
 */
public class PolyPlusRegistry extends PolyRegistry {

    /**
     * Generate a new PolyMap based on this registry
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     */
    @Override
    public PolyPlusMap build() {
        return new PolyPlusMap(
                this,
                ImmutableMap.copyOf(itemPolys),
                globalItemPolys.toArray(new ItemTransformer[0]),
                ImmutableMap.copyOf(blockPolys),
                ImmutableMap.copyOf(guiPolys),
                ImmutableMap.copyOf(entityPolys),
                ImmutableList.copyOf(sharedValues.entrySet().stream().map((entry) -> entry.getKey().createResources(entry.getValue())).filter(Objects::nonNull).iterator()));
    }

    /**
     * Register certain blocks PolyPlus wants to use
     * (mostly for ItemBlockPoly reasons)
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public void registerDefaultBlocks() {

        // Get the (probably empty) blockstate manager
        // So we can claim states first
        BlockStateManager manager = this.getSharedValues(BlockStateManager.KEY);

        // Have to revisit this
    }

    /**
     * Generate default resources for our "default" blocks
     * (Make them invisible)
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public void generateDefaultResources(ModdedResources moddedResources, PolyMcResourcePack pack, SimpleLogger logger) {

        JModel invisible_model = new JModelImpl();
        pack.setModel("polymcplus", "invisible", invisible_model);

        // Again, placeholder for later
        List<BlockState> states = new ArrayList<>();

        for (BlockState client_state : states) {
            Block client_block = client_state.getBlock();
            Identifier client_block_id = Registry.BLOCK.getId(client_block);

            JBlockState client_block_states = pack.getOrDefaultBlockState(client_block_id.getNamespace(), client_block_id.getPath());
            String client_state_string = Util.getPropertiesFromBlockState(client_state);

            JBlockStateVariant invisible_variant = new JBlockStateVariant("polymcplus:invisible", 0, 0, false);
            JBlockStateVariant[] invisible_variants = {invisible_variant};
            client_block_states.setVariant(client_state_string, invisible_variants);
        }
    }

    /**
     * Find invisible collision states to use
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public BlockState findInvisibleCollisionState(ItemBlockPoly.ItemBlockStateInfo info) {
        return Blocks.BARRIER.getDefaultState();
    }

    /**
     * Return a readable string of this instance
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     */
    public String toString() {
        return "PolyPlusRegistry{}";
    }
}
