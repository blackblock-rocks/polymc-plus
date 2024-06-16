package rocks.blackblock.polymcplus.tools;

import net.minecraft.block.*;
import net.minecraft.sound.BlockSoundGroup;
import rocks.blackblock.polymcplus.mixin.AbstractBlockAccessor;

/**
 * Class to get the original Material of a block
 */
public class MaterialLookup {

    /**
     * Get the material of a Block and its specific BlockState
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.5.0
     */
    public static Type getMaterial(Block block, BlockState state) {

        if (block instanceof StainedGlassBlock || block instanceof PaneBlock) {
            return Type.GLASS;
        }

        BlockSoundGroup sound_group = ((AbstractBlockAccessor) block).getSoundGroup();

        Type result = getMaterial(sound_group);

        // Make sure it's glass
        if (result == Type.GLASS) {
            // Check if it's ice
            if (block instanceof IceBlock || block == Blocks.PACKED_ICE) {
                result = Type.ICE;
            }

            // Default slipperiness is 0.6f
            if (block.getSlipperiness() > 0.6f) {
                result = Type.OTHER;
            }
        }

        return result;
    }

    /**
     * Get the material of a BlockState
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.5.0
     */
    public static Type getMaterial(BlockState blockState) {
        return getMaterial(blockState.getBlock(), blockState);
    }

    /**
     * Get the material of a Block (using its default state)
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.5.0
     */
    public static Type getMaterial(Block block) {
        return getMaterial(block, block.getDefaultState());
    }

    /**
     * Get the (most likely) material of a BlockSoundGroup.
     * This is not 100% accurate (like in the case of ice being glass)
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.5.0
     */
    public static Type getMaterial(BlockSoundGroup soundGroup) {

        if (soundGroup == BlockSoundGroup.METAL) {
            return Type.METAL;
        }

        if (soundGroup == BlockSoundGroup.WOOD) {
            return Type.WOOD;
        }

        if (soundGroup == BlockSoundGroup.STONE) {
            return Type.STONE;
        }

        if (soundGroup == BlockSoundGroup.GRAVEL ||
                soundGroup == BlockSoundGroup.SAND) {
            return Type.SAND;
        }

        if (soundGroup == BlockSoundGroup.WOOL) {
            return Type.WOOL;
        }

        if (soundGroup == BlockSoundGroup.ROOTED_DIRT || soundGroup == BlockSoundGroup.MUD) {
            return Type.DIRT;
        }

        if (soundGroup == BlockSoundGroup.GLASS) {
            // This includes ice and nether portals
            return Type.GLASS;
        }

        return null;
    }

    /**
     * Get the material of a DoorBlock (using its default state)
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.5.0
     */
    public static Type getMaterial(DoorBlock block) {
        Type result = getMaterial(block.getBlockSetType());

        if (result == null) {
            BlockSoundGroup sound_group = ((AbstractBlockAccessor) block).getSoundGroup();
            result = getMaterial(sound_group);
        }

        return result;
    }

    /**
     * Get the material of a BlockSetType
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.5.0
     */
    public static Type getMaterial(BlockSetType blockSetType) {

        Type result = null;

        if (blockSetType == BlockSetType.OAK ||
                blockSetType == BlockSetType.SPRUCE ||
                blockSetType == BlockSetType.BIRCH ||
                blockSetType == BlockSetType.ACACIA ||
                blockSetType == BlockSetType.CHERRY ||
                blockSetType == BlockSetType.JUNGLE ||
                blockSetType == BlockSetType.DARK_OAK ||
                blockSetType == BlockSetType.CRIMSON ||
                blockSetType == BlockSetType.WARPED ||
                blockSetType == BlockSetType.MANGROVE ||
                blockSetType == BlockSetType.BAMBOO) {
            result = Type.WOOD;
        } else if (blockSetType == BlockSetType.STONE ||
                blockSetType == BlockSetType.POLISHED_BLACKSTONE) {
            result = Type.STONE;
        } else if (blockSetType == BlockSetType.IRON ||
                blockSetType == BlockSetType.GOLD) {
            result = Type.METAL;
        }

        return result;
    }

    /**
     * All of our possible material types
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.5.0
     */
    public enum Type {
        METAL,
        WOOD,
        STONE,
        DIRT,
        SAND,
        GLASS,
        WOOL,
        LEAVES,
        PLANT,
        OTHER,
        ICE
    }
}
