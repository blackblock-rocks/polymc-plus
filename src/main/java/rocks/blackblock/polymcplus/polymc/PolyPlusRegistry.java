package rocks.blackblock.polymcplus.polymc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.theepicblock.polymc.api.PolyRegistry;
import io.github.theepicblock.polymc.api.block.BlockStateManager;
import io.github.theepicblock.polymc.api.block.BlockStateProfile;
import io.github.theepicblock.polymc.api.item.ItemTransformer;
import io.github.theepicblock.polymc.api.resource.ModdedResources;
import io.github.theepicblock.polymc.api.resource.PolyMcResourcePack;
import io.github.theepicblock.polymc.api.resource.TextureAsset;
import io.github.theepicblock.polymc.api.resource.json.JBlockState;
import io.github.theepicblock.polymc.api.resource.json.JBlockStateVariant;
import io.github.theepicblock.polymc.api.resource.json.JModel;
import io.github.theepicblock.polymc.impl.Util;
import io.github.theepicblock.polymc.impl.misc.logging.SimpleLogger;
import io.github.theepicblock.polymc.impl.resource.json.JModelImpl;
import net.minecraft.block.*;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
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
    public BlockState INVISIBLE_FULL_BLOCK = null;
    public BlockState INVISIBLE_TRANSPARENT_FULL_BLOCK = null;
    public BlockState INVISIBLE_CACTUS = null;
    public BlockState INVISIBLE_CAMPFIRE = null;
    public BlockState INVISIBLE_WATERLOGGED_CAMPFIRE = null;
    public BlockState INVISIBILE_THICK_TRIPWIRE = null;
    public BlockState INVISIBLE_TRIPWIRE_HOOK_NORTH = null;
    public BlockState INVISIBLE_TRIPWIRE_HOOK_EAST = null;
    public BlockState INVISIBLE_TRIPWIRE_HOOK_SOUTH = null;
    public BlockState INVISIBLE_TRIPWIRE_HOOK_WEST = null;
    public BlockState INVISIBLE_TURTLE_EGG = null;
    public BlockState INVISIBLE_DOUBLE_TURTLE_EGG = null;
    public BlockState INVISIBLE_POTTED_PLANT = null;
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

    /**
     * Request a blockstate from the manager
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public BlockState requestBlockState(BlockStateProfile profile) {

        BlockStateManager manager = this.getSharedValues(BlockStateManager.KEY);

        try {
            return manager.requestBlockState(profile);
        } catch (BlockStateManager.StateLimitReachedException ignored) {}

        return null;
    }

    /**
     * Invisible campfire states
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public BlockState getInvisibleCampfireState() {

        if (this.INVISIBLE_CAMPFIRE == null) {
            this.INVISIBLE_CAMPFIRE = this.requestBlockState(PolyPlusBlockStateProfile.SOUL_CAMPFIRE_PROFILE.and(blockState -> !blockState.get(Properties.WATERLOGGED)));
        }

        return this.INVISIBLE_CAMPFIRE;
    }

    /**
     * Invisible waterlogged campfire states
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public BlockState getInvisibleWaterloggedCampfireState() {

        if (this.INVISIBLE_WATERLOGGED_CAMPFIRE == null) {
            this.INVISIBLE_WATERLOGGED_CAMPFIRE = this.requestBlockState(PolyPlusBlockStateProfile.SOUL_CAMPFIRE_PROFILE.and(blockState -> blockState.get(Properties.WATERLOGGED)));
        }

        return this.INVISIBLE_WATERLOGGED_CAMPFIRE;
    }

    /**
     * Invisible waterlogged campfire states
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public BlockState getThickTripwireState() {

        if (this.INVISIBILE_THICK_TRIPWIRE == null) {
            this.INVISIBILE_THICK_TRIPWIRE = this.requestBlockState(PolyPlusBlockStateProfile.TRIPWIRE_THICK_PROFILE);
        }

        return this.INVISIBILE_THICK_TRIPWIRE;
    }

    /**
     * Invisible northern tripwire hook state
     *
     * @author   Jade Godwin    <icanhasabanana@gmail.com>
     * @since    0.5.2
     */
    public BlockState getInvisibleTripwireHookNorthState() {

        if (this.INVISIBLE_TRIPWIRE_HOOK_NORTH == null) {
            this.INVISIBLE_TRIPWIRE_HOOK_NORTH = this.requestBlockState(PolyPlusBlockStateProfile.TRIPWIRE_HOOK_NORTH_PRFILE);
        }

        return this.INVISIBLE_TRIPWIRE_HOOK_NORTH;
    }

    /**
     * Invisible eastern tripwire hook state
     *
     * @author   Jade Godwin    <icanhasabanana@gmail.com>
     * @since    0.5.2
     */
    public BlockState getInvisibleTripwireHookEastState() {

        if (this.INVISIBLE_TRIPWIRE_HOOK_EAST == null) {
            this.INVISIBLE_TRIPWIRE_HOOK_EAST = this.requestBlockState(PolyPlusBlockStateProfile.TRIPWIRE_HOOK_EAST_PRFILE);
        }

        return this.INVISIBLE_TRIPWIRE_HOOK_EAST;
    }

    /**
     * Invisible southern tripwire hook state
     *
     * @author   Jade Godwin    <icanhasabanana@gmail.com>
     * @since    0.5.2
     */
    public BlockState getInvisibleTripwireHookSouthState() {

        if (this.INVISIBLE_TRIPWIRE_HOOK_SOUTH == null) {
            this.INVISIBLE_TRIPWIRE_HOOK_SOUTH = this.requestBlockState(PolyPlusBlockStateProfile.TRIPWIRE_HOOK_SOUTH_PRFILE);
        }

        return this.INVISIBLE_TRIPWIRE_HOOK_SOUTH;
    }

    /**
     * Invisible western tripwire hook state
     *
     * @author   Jade Godwin    <icanhasabanana@gmail.com>
     * @since    0.5.2
     */
    public BlockState getInvisibleTripwireHookWestState() {

        if (this.INVISIBLE_TRIPWIRE_HOOK_WEST == null) {
            this.INVISIBLE_TRIPWIRE_HOOK_WEST = this.requestBlockState(PolyPlusBlockStateProfile.TRIPWIRE_HOOK_WEST_PRFILE);
        }

        return this.INVISIBLE_TRIPWIRE_HOOK_WEST;
    }

    /**
     * Invisible cactus states
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public BlockState getInvisibleCactusState() {

        if (this.INVISIBLE_CACTUS == null) {
            this.INVISIBLE_CACTUS = this.requestBlockState(BlockStateProfile.CACTUS_PROFILE);
        }

        return this.INVISIBLE_CACTUS;
    }

    /**
     * Invisible turtle egg state
     *
     * @since    0.5.0
     */
    public BlockState getInvisibleTurtleEggState() {

        if (this.INVISIBLE_TURTLE_EGG == null) {
            this.INVISIBLE_TURTLE_EGG = this.requestBlockState(PolyPlusBlockStateProfile.ONE_TURTLE_EGG_PROFILE);
        }

        return this.INVISIBLE_TURTLE_EGG;
    }

    /**
     * Invisible turtle egg state
     *
     * @since    0.5.0
     */
    public BlockState getInvisibleDoubleTurtleEggState() {

        if (this.INVISIBLE_DOUBLE_TURTLE_EGG == null) {
            this.INVISIBLE_DOUBLE_TURTLE_EGG = this.requestBlockState(PolyPlusBlockStateProfile.TWO_TURTLE_EGG_PROFILE);
        }

        return this.INVISIBLE_DOUBLE_TURTLE_EGG;
    }

    /**
     * Invisible potted plant state
     *
     * @since    0.5.0
     */
    public BlockState getInvisiblePottedPlantState() {

        if (this.INVISIBLE_POTTED_PLANT == null) {
            this.INVISIBLE_POTTED_PLANT = this.requestBlockState(PolyPlusBlockStateProfile.POTTED_PLANT_PROFILE);
        }

        return this.INVISIBLE_POTTED_PLANT;
    }

    /**
     * Invisible full (non light-transparent) block.
     * These blocks make the surrounding blocks appear darker.
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public BlockState getInvisibleFullBlock() {

        if (this.INVISIBLE_FULL_BLOCK == null) {
            this.INVISIBLE_FULL_BLOCK = this.requestBlockState(BlockStateProfile.CHORUS_FLOWER_BLOCK_PROFILE);

            if (this.INVISIBLE_FULL_BLOCK == null) {
                this.INVISIBLE_FULL_BLOCK = this.getInvisibleFullTransparentBlock();
            }
        }

        return this.INVISIBLE_FULL_BLOCK;
    }

    /**
     * Invisible full transparent block.
     * These blocks don't make the surrounding blocks appear darker.
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public BlockState getInvisibleFullTransparentBlock() {

        if (this.INVISIBLE_TRANSPARENT_FULL_BLOCK == null) {
            this.INVISIBLE_TRANSPARENT_FULL_BLOCK = Blocks.BARRIER.getDefaultState();
        }

        return this.INVISIBLE_TRANSPARENT_FULL_BLOCK;
    }

    /**
     * Invisible slab states
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public Map<ItemBlockPoly.CombinedPropertyKey, BlockState> getInvisibleSlabStates() {

        if (this.INVISIBLE_SLABS == null) {
            this.INVISIBLE_SLABS = new HashMap<>();

            for (int waterlogged_counter = 0; waterlogged_counter <= 1; waterlogged_counter++) {
                boolean is_waterlogged = waterlogged_counter == 1;

                for (int slab_type_counter = 0; slab_type_counter <= 1; slab_type_counter++) {
                    boolean is_bottom = slab_type_counter == 0;
                    boolean is_top = slab_type_counter == 1;

                    SlabType wanted_type = null;

                    if (is_bottom) {
                        wanted_type = SlabType.BOTTOM;
                    } else if (is_top) {
                        wanted_type = SlabType.TOP;
                    }

                    SlabType finalWanted_type = wanted_type;
                    BlockState state = this.requestBlockState(BlockStateProfile.SLAB_PROFILE.and(
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

                    if (state != null) {
                        ItemBlockPoly.CombinedPropertyKey key = new ItemBlockPoly.CombinedPropertyKey();
                        key.setProperty(SlabBlock.WATERLOGGED, is_waterlogged);
                        key.setProperty(SlabBlock.TYPE, wanted_type);

                        this.INVISIBLE_SLABS.put(key, state);
                    }
                }
            }
        }

        return this.INVISIBLE_SLABS;
    }

    /**
     * Invisible stair states
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public Map<ItemBlockPoly.CombinedPropertyKey, BlockState> getInvisibleStairStates() {

        if (this.INVISIBLE_STAIRS == null) {
            this.INVISIBLE_STAIRS = new HashMap<>();

            for (int waterlogged_counter = 0; waterlogged_counter <= 1; waterlogged_counter++) {
                boolean is_waterlogged = waterlogged_counter == 1;

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

                    Direction wanted_facing = facing;
                    BlockState state = this.requestBlockState(BlockStateProfile.WAXED_COPPER_STAIR_PROFILE.and(
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

                    if (state != null) {
                        ItemBlockPoly.CombinedPropertyKey key = new ItemBlockPoly.CombinedPropertyKey();
                        key.setProperty(Properties.WATERLOGGED, is_waterlogged);
                        key.setProperty(HorizontalFacingBlock.FACING, wanted_facing);

                        this.INVISIBLE_STAIRS.put(key, state);
                    }
                }
            }
        }

        return this.INVISIBLE_STAIRS;
    }

    /**
     * Generate all the default (invisible) resources
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public void generateDefaultResources(ModdedResources moddedResources, PolyMcResourcePack pack, SimpleLogger logger) {

        JModel invisible_model = new JModelImpl();
        Map<String, String> textures = invisible_model.getTextures();

        // Make the invisible model use invisible particles
        textures.put("particle", "polymcplus:invisible");

        // Get the "invisible" (transparent png) texture
        TextureAsset invisible_texture = moddedResources.getTexture("polymcplus", "invisible");

        // Add the invisible texture to the generated resource map
        pack.setTexture("polymcplus", "invisible", invisible_texture);
        pack.setModel("polymcplus", "invisible", invisible_model);

        Map<ItemBlockPoly.CombinedPropertyKey, BlockState> stairs_map = this.INVISIBLE_STAIRS;
        Map<ItemBlockPoly.CombinedPropertyKey, BlockState> slabs_map = this.INVISIBLE_SLABS;

        if (stairs_map == null) {
            stairs_map = new HashMap<>();
        }

        if (slabs_map == null) {
            slabs_map = new HashMap<>();
        }

        List<BlockState> states = new ArrayList<>(Stream.of(stairs_map, slabs_map).flatMap(map -> map.values().stream()).toList());

        if (this.INVISIBLE_FULL_BLOCK != null) {
            states.add(this.INVISIBLE_FULL_BLOCK);
        }

        if (this.INVISIBLE_TRANSPARENT_FULL_BLOCK != null) {
            states.add(this.INVISIBLE_TRANSPARENT_FULL_BLOCK);
        }

        if (this.INVISIBLE_CACTUS != null) {
            states.add(this.INVISIBLE_CACTUS);
        }

        if (this.INVISIBLE_CAMPFIRE != null) {
            states.add(this.INVISIBLE_CAMPFIRE);
        }

        if (this.INVISIBLE_WATERLOGGED_CAMPFIRE != null) {
            states.add(this.INVISIBLE_WATERLOGGED_CAMPFIRE);
        }

        if (this.INVISIBLE_TURTLE_EGG != null) {
            states.add(this.INVISIBLE_TURTLE_EGG);
        }

        if (this.INVISIBLE_DOUBLE_TURTLE_EGG != null) {
            states.add(this.INVISIBLE_DOUBLE_TURTLE_EGG);
        }

        if (this.INVISIBLE_POTTED_PLANT != null) {
            states.add(this.INVISIBLE_POTTED_PLANT);
        }

        if (this.INVISIBILE_THICK_TRIPWIRE != null) {
            states.add(this.INVISIBILE_THICK_TRIPWIRE);
        }

        if (this.INVISIBLE_TRIPWIRE_HOOK_NORTH != null) {
            states.add(this.INVISIBLE_TRIPWIRE_HOOK_NORTH);
        }

        if (this.INVISIBLE_TRIPWIRE_HOOK_EAST != null) {
            states.add(this.INVISIBLE_TRIPWIRE_HOOK_EAST);
        }

        if (this.INVISIBLE_TRIPWIRE_HOOK_SOUTH != null) {
            states.add(this.INVISIBLE_TRIPWIRE_HOOK_SOUTH);
        }

        if (this.INVISIBLE_TRIPWIRE_HOOK_WEST != null) {
            states.add(this.INVISIBLE_TRIPWIRE_HOOK_WEST);
        }

        for (BlockState client_state : states) {
            Block client_block = client_state.getBlock();
            Identifier client_block_id = Registries.BLOCK.getId(client_block);

            JBlockState client_block_states = pack.getOrDefaultBlockState(client_block_id.getNamespace(), client_block_id.getPath());
            String client_state_string = Util.getPropertiesFromBlockState(client_state);

            JBlockStateVariant invisible_variant = new JBlockStateVariant("polymcplus:invisible", 0, 0, false);
            JBlockStateVariant[] invisible_variants = {invisible_variant};
            client_block_states.setVariant(client_state_string, invisible_variants);
        }
    }

    public BlockState findInvisibleCollisionState(ItemBlockPoly.ItemBlockStateInfo info) {

        String preferred_collision_type = info.getPreferredCollisionType();
        BlockState modded_state = info.getModdedState();
        BlockState state = null;
        VoxelShape shape = BlockPolyPlusGenerator.getCollisionShape(modded_state);

        double min_y = shape.getMin(Direction.Axis.Y);
        double max_y = shape.getMax(Direction.Axis.Y);
        boolean do_egg = "egg".equals(preferred_collision_type);
        boolean do_double_egg = "double_egg".equals(preferred_collision_type);
        boolean do_thick_tripwire = "thick_tripwire".equals(preferred_collision_type);
        boolean do_stairs = "stairs".equals(preferred_collision_type);
        boolean do_bed = "bed".equals(preferred_collision_type);
        boolean do_cactus = "cactus".equals(preferred_collision_type);
        boolean do_campfire = "campfire".equals(preferred_collision_type);
        boolean do_full_transparent = "full_transparent".equals(preferred_collision_type);
        boolean do_tripwire = "tripwire_hook".equals(preferred_collision_type);
        boolean is_waterlogged = false;
        boolean blocks_light = modded_state.isOpaque();

        if (modded_state.contains(Properties.WATERLOGGED)) {
            is_waterlogged = modded_state.get(Properties.WATERLOGGED);
        }

        if (preferred_collision_type == null) {
            if (Block.isShapeFullCube(shape)) {
                if (blocks_light) {
                    state = this.getInvisibleFullBlock();
                } else {
                    state = this.getInvisibleFullTransparentBlock();
                }
            } else if (Block.isFaceFullSquare(shape, Direction.UP) && min_y <= 0) {
                if (blocks_light) {
                    state = this.getInvisibleFullBlock();
                } else {
                    state = this.getInvisibleFullTransparentBlock();
                }
            } else if (max_y < 0.5) {
                // Get a campfire
                state = this.getInvisibleCampfireState();
            }

            if (state == null && max_y <= 0.5) {
                // Get a bottom slab!

                ItemBlockPoly.CombinedPropertyKey key = new ItemBlockPoly.CombinedPropertyKey();
                key.setProperty(SlabBlock.WATERLOGGED, is_waterlogged);
                key.setProperty(SlabBlock.TYPE, SlabType.BOTTOM);

                state = this.getInvisibleSlabStates().get(key);
            }
        }

        if (do_full_transparent) {
            state = this.getInvisibleFullTransparentBlock();
        } else if (do_egg) {
            state = this.getInvisibleTurtleEggState();
        } else if (do_double_egg) {
            state = this.getInvisibleDoubleTurtleEggState();
        } else if (do_campfire) {
            if (is_waterlogged) {
                state = this.getInvisibleWaterloggedCampfireState();
            }

            if (state == null) {
                state = this.getInvisibleCampfireState();
            }
        } else if (do_cactus) {
            state = this.getInvisibleCactusState();
        } else if (do_thick_tripwire) {
            state = this.getThickTripwireState();
        } else if (do_stairs) {

            ItemBlockPoly.CombinedPropertyKey key = new ItemBlockPoly.CombinedPropertyKey();
            key.setProperty(Properties.WATERLOGGED, is_waterlogged);

            if (modded_state.contains(HorizontalFacingBlock.FACING)) {
                Direction facing = modded_state.get(HorizontalFacingBlock.FACING);
                key.setProperty(HorizontalFacingBlock.FACING, facing);
            }

            state = this.getInvisibleStairStates().get(key);

            for (var ikey : this.getInvisibleStairStates().keySet()) {
                var val = this.getInvisibleStairStates().get(ikey);
            }
        } else if (do_tripwire) {
            if (modded_state.contains(Properties.HORIZONTAL_FACING)) {
                state = switch(modded_state.get(Properties.HORIZONTAL_FACING)) {
                    case NORTH -> this.getInvisibleTripwireHookNorthState();
                    case EAST -> this.getInvisibleTripwireHookEastState();
                    case SOUTH -> this.getInvisibleTripwireHookSouthState();
                    case WEST -> this.getInvisibleTripwireHookWestState();
                    default -> null;
                };
            }
        }

        if (state == null) {
            state = this.getInvisibleFullBlock();
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
