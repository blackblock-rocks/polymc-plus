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
import net.minecraft.state.property.DirectionProperty;
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

    public Map<ItemBlockPoly.CombinedPropertyKey, BlockState> INVISIBLE_SLABS;
    public Map<ItemBlockPoly.CombinedPropertyKey, BlockState> INVISIBLE_STAIRS;
    public BlockState INVISIBLE_BED;
    private boolean registered = false;

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

    public void registerDefaultBlocks() {

        this.registered = true;
        this.INVISIBLE_SLABS = new HashMap<>();
        this.INVISIBLE_STAIRS = new HashMap<>();

        // Get the (probably empty) blockstate manager
        // So we can claim states first
        BlockStateManager manager = this.getSharedValues(BlockStateManager.KEY);

        boolean register_slabs = false;
        boolean register_stairs = false;

        for (int waterlogged_counter = 0; waterlogged_counter <= 1; waterlogged_counter++) {
            boolean is_waterlogged = waterlogged_counter == 1;

            if (register_slabs) {
                // Register slabs
                for (int slab_type_counter = 0; slab_type_counter <= 1; slab_type_counter++) {
                    boolean is_bottom = slab_type_counter == 0;
                    boolean is_top = slab_type_counter == 1;

                    SlabType wanted_type = null;

                    if (is_bottom) {
                        wanted_type = SlabType.BOTTOM;
                    } else if (is_top) {
                        wanted_type = SlabType.TOP;
                    }

                    try {
                        SlabType finalWanted_type = wanted_type;
                        BlockState state = manager.requestBlockState(BlockStateProfile.SLAB_PROFILE.and(
                                offered_state -> {

                                    Boolean offered_is_waterlogged = offered_state.get(SlabBlock.WATERLOGGED);

                                    if (offered_is_waterlogged != is_waterlogged) {
                                        return false;
                                    }

                                    SlabType slab_type = offered_state.get(SlabBlock.TYPE);

                                    if (slab_type != finalWanted_type) {
                                        return true;
                                    }

                                    return false;
                                }
                        ));

                        ItemBlockPoly.CombinedPropertyKey key = new ItemBlockPoly.CombinedPropertyKey();
                        key.setProperty(SlabBlock.WATERLOGGED, is_waterlogged);
                        key.setProperty(SlabBlock.TYPE, wanted_type);

                        this.INVISIBLE_SLABS.put(key, state);

                    } catch (BlockStateManager.StateLimitReachedException ignored) {
                    }
                }
            }

            if (register_stairs) {
                // Register stairs
                for (int stair_direction = 0; stair_direction <= 4; stair_direction++) {
                    Direction facing = null;

                    if (stair_direction == 0) {
                        facing = Direction.NORTH;
                    } else if (stair_direction == 1) {
                        facing = Direction.EAST;
                    } else if (stair_direction == 2) {
                        facing = Direction.SOUTH;
                    } else if (stair_direction == 3) {
                        facing = Direction.WEST;
                    } else {
                        continue;
                    }

                    try {
                        Direction wanted_facing = facing;
                        BlockState state = manager.requestBlockState(BlockStateProfile.WAXED_COPPER_STAIR_PROFILE.and(
                                offered_state -> {

                                    BlockHalf half = offered_state.get(Properties.BLOCK_HALF);

                                    // We only want bottom stairs, otherwise we can just use a full block
                                    if (half != BlockHalf.BOTTOM) {
                                        return false;
                                    }

                                    Boolean offered_is_waterlogged = offered_state.get(Properties.WATERLOGGED);

                                    if (offered_is_waterlogged != is_waterlogged) {
                                        return false;
                                    }

                                    Direction offered_facing = offered_state.get(HorizontalFacingBlock.FACING);

                                    if (offered_facing == wanted_facing) {
                                        return true;
                                    }

                                    return false;
                                }
                        ));

                        ItemBlockPoly.CombinedPropertyKey key = new ItemBlockPoly.CombinedPropertyKey();
                        key.setProperty(Properties.WATERLOGGED, is_waterlogged);
                        key.setProperty(HorizontalFacingBlock.FACING, wanted_facing);

                        this.INVISIBLE_STAIRS.put(key, state);

                    } catch (BlockStateManager.StateLimitReachedException ignored) {}
                }
            }

            try {
                BlockState state = manager.requestBlockState(PolyPlusBlockStateProfile.BED_PROFILE.and(offered_state -> {
                    BedPart part = offered_state.get(BedBlock.PART);

                    if (part == BedPart.FOOT) {
                        return false;
                    }

                    return true;
                }));
                this.INVISIBLE_BED = state;
            } catch (BlockStateManager.StateLimitReachedException ignored) {}
        }

    }

    public void generateDefaultResources(ModdedResources moddedResources, PolyMcResourcePack pack, SimpleLogger logger) {

        JModel invisible_model = new JModelImpl();
        pack.setModel("polymcplus", "invisible", invisible_model);

        List<BlockState> states = Stream.of(this.INVISIBLE_STAIRS, this.INVISIBLE_SLABS).flatMap(map -> map.values().stream()).toList();

        if (this.INVISIBLE_BED != null) {
            states = new ArrayList<>(states);
            states.add(this.INVISIBLE_BED);
        }

        for (BlockState client_state : states) {
            Block client_block = client_state.getBlock();
            Identifier client_block_id = Registry.BLOCK.getId(client_block);

            JBlockState client_block_states = pack.getOrDefaultBlockState(client_block_id.getNamespace(), client_block_id.getPath());
            String client_state_string = Util.getPropertiesFromBlockState(client_state);

            JBlockStateVariant invisible_variant = new JBlockStateVariant("polymcplus:invisible", 0, 0, false);
            JBlockStateVariant[] invisible_variants = {invisible_variant};
            client_block_states.setVariant(client_state_string, invisible_variants);

            System.out.println(" -- Registered default resources for ...");
            System.out.println("    »» " + client_state + " - "+ client_block);
        }
    }

    public BlockState findInvisibleCollisionState(ItemBlockPoly.ItemBlockStateInfo info) {

        String preferred_collision_type = info.getPreferredCollisionType();
        BlockState modded_state = info.getModdedState();
        BlockState state = null;
        VoxelShape shape = BlockPolyPlusGenerator.getCollisionShape(modded_state);

        double min_y = shape.getMin(Direction.Axis.Y);
        double max_y = shape.getMax(Direction.Axis.Y);
        boolean do_stairs = "stairs".equals(preferred_collision_type);
        boolean do_bed = "bed".equals(preferred_collision_type);
        boolean is_waterlogged = false;

        if (modded_state.contains(Properties.WATERLOGGED)) {
            is_waterlogged = modded_state.get(Properties.WATERLOGGED);
        }

        System.out.println("Min Y of " + modded_state + " == " + min_y + ", Max Y is == " + max_y);

        if (preferred_collision_type == null) {
            if (Block.isShapeFullCube(shape)) {
                state = Blocks.BARRIER.getDefaultState();
            } else if (Block.isFaceFullSquare(shape, Direction.UP) && min_y <= 0) {
                state = Blocks.BARRIER.getDefaultState();
            } else if (max_y <= 0.5) {
                // Get a bottom slab!

                ItemBlockPoly.CombinedPropertyKey key = new ItemBlockPoly.CombinedPropertyKey();
                key.setProperty(SlabBlock.WATERLOGGED, is_waterlogged);
                key.setProperty(SlabBlock.TYPE, SlabType.BOTTOM);

                state = this.INVISIBLE_SLABS.get(key);

                System.out.println("Found invisible slab for " + key + " -- " + state);
            }
        }

        if (do_bed) {
            state = this.INVISIBLE_BED;
        } else if (do_stairs) {

            ItemBlockPoly.CombinedPropertyKey key = new ItemBlockPoly.CombinedPropertyKey();
            key.setProperty(Properties.WATERLOGGED, is_waterlogged);

            if (modded_state.contains(HorizontalFacingBlock.FACING)) {
                Direction facing = modded_state.get(HorizontalFacingBlock.FACING);
                key.setProperty(HorizontalFacingBlock.FACING, facing);
            }

            System.out.println("Getting stair state for key " + key + "");
            System.out.println(" -- State: " + modded_state);

            state = this.INVISIBLE_STAIRS.get(key);

            System.out.println("   -- Found client state: " + state + " - Statecount: " + this.INVISIBLE_STAIRS.size());

            for (var ikey : this.INVISIBLE_STAIRS.keySet()) {
                var val = this.INVISIBLE_STAIRS.get(ikey);
                System.out.println(" -- AWEL?" +  ikey + "--" + val);
            }
        }

        if (state == null) {
            state = Blocks.BARRIER.getDefaultState();
        }

        return state;
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
