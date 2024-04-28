package rocks.blackblock.polymcplus.polymc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.github.theepicblock.polymc.api.DebugInfoProvider;
import io.github.theepicblock.polymc.api.PolyMap;
import io.github.theepicblock.polymc.api.PolyMcEntrypoint;
import io.github.theepicblock.polymc.api.SharedValuesKey;
import io.github.theepicblock.polymc.api.block.BlockPoly;
import io.github.theepicblock.polymc.api.entity.EntityPoly;
import io.github.theepicblock.polymc.api.gui.GuiPoly;
import io.github.theepicblock.polymc.api.item.ItemLocation;
import io.github.theepicblock.polymc.api.item.ItemPoly;
import io.github.theepicblock.polymc.api.item.ItemTransformer;
import io.github.theepicblock.polymc.api.resource.PolyMcResourcePack;
import io.github.theepicblock.polymc.impl.Util;
import io.github.theepicblock.polymc.impl.misc.logging.SimpleLogger;
import io.github.theepicblock.polymc.impl.resource.ModdedResourceContainerImpl;
import io.github.theepicblock.polymc.impl.resource.ResourcePackImplementation;
import net.fabricmc.fabric.api.util.NbtType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.resource.InputSupplier;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import rocks.blackblock.polymcplus.PolyMcPlus;
import rocks.blackblock.polymcplus.block.MushroomFilters;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * The PolyMcPlus map
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.1.0
 */
public class PolyPlusMap implements PolyMap {

    /**
     * The nbt tag name that stores the original item nbt so it can be restored
     * @see PolyMap#getClientItem(ItemStack, ServerPlayerEntity, ItemLocation)
     * @see #recoverOriginalItem(ItemStack)
     */
    private static final String ORIGINAL_ITEM_NBT = "PolyPlusOriginal";

    private final PolyPlusRegistry registry;
    private final ImmutableMap<Item,ItemPoly> itemPolys;
    private final ItemTransformer[] globalItemPolys;
    private final ImmutableMap<Block,BlockPoly> blockPolys;
    private final ImmutableMap<ScreenHandlerType<?>,GuiPoly> guiPolys;
    private final ImmutableMap<EntityType<?>,EntityPoly<?>> entityPolys;
    private final ImmutableList<SharedValuesKey.ResourceContainer> sharedValueResources;
    private final boolean hasBlockWizards;

    public PolyPlusMap(PolyPlusRegistry registry,
                       ImmutableMap<Item,ItemPoly> itemPolys,
                       ItemTransformer[] globalItemPolys,
                       ImmutableMap<Block,BlockPoly> blockPolys,
                       ImmutableMap<ScreenHandlerType<?>,GuiPoly> guiPolys,
                       ImmutableMap<EntityType<?>,EntityPoly<?>> entityPolys,
                       ImmutableList<SharedValuesKey.ResourceContainer> sharedValueResources) {
        this.registry = registry;
        this.itemPolys = itemPolys;
        this.globalItemPolys = globalItemPolys;
        this.blockPolys = blockPolys;
        this.guiPolys = guiPolys;
        this.entityPolys = entityPolys;
        this.sharedValueResources = sharedValueResources;

        this.hasBlockWizards = blockPolys.values().stream().anyMatch(BlockPoly::hasWizard);
    }

    /**
     * Convert the given server-side ItemStack into something the client understands
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     *
     * @param    serverItem   The itemstack as it exists on the server
     * @param    player       The player this is for
     * @param    location     The location of the item
     */
    @Override
    public ItemStack getClientItem(ItemStack serverItem, @Nullable ServerPlayerEntity player, @Nullable ItemLocation location) {
        ItemStack ret = serverItem;
        NbtCompound originalNbt = serverItem.writeNbt(new NbtCompound());

        ItemPoly poly = itemPolys.get(serverItem.getItem());

        if (poly != null) {
            ret = poly.getClientItem(serverItem, player, location);
        }

        for (ItemTransformer globalPoly : globalItemPolys) {
            ret = globalPoly.transform(serverItem, ret, player, location);
        }

        if ((player == null || player.isCreative()) && !ItemStack.canCombine(serverItem, ret) && !serverItem.isEmpty()) {
            // Preserves the nbt of the original item so it can be reverted
            ret = ret.copy();
            originalNbt.remove("Count");
            ret.setSubNbt(ORIGINAL_ITEM_NBT, originalNbt);
        }

        return ret;
    }

    /**
     * Get the ItemPoly that is used for the given item
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     *
     * @param    serverItem   The item on the server
     */
    @Override
    public ItemPoly getItemPoly(Item serverItem) {
        return itemPolys.get(serverItem);
    }

    /**
     * Get the BlockPoly that is used for the given block
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     *
     * @param    serverBlock   The block on the server
     */
    @Override
    public BlockPoly getBlockPoly(Block serverBlock) {
        return blockPolys.get(serverBlock);
    }

    /**
     * Get the GuiPoly that is used for the given ScreenHandlerType
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     *
     * @param    serverGuiType   The server-side screenhandler
     */
    @Override
    public GuiPoly getGuiPoly(ScreenHandlerType<?> serverGuiType) {
        return guiPolys.get(serverGuiType);
    }

    /**
     * Get the EntityPoly that is used for the given EntityType
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     *
     * @param    serverEntityType   The server-side entity type
     */
    @Override
    public <T extends Entity> EntityPoly<T> getEntityPoly(EntityType<T> serverEntityType) {
        return (EntityPoly<T>)entityPolys.get(serverEntityType);
    }

    /**
     * Turn a client-side ItemStack back into a server-side ItemStack
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     *
     * @param    clientItemStack   The client-side item stack
     */
    @Override
    public ItemStack reverseClientItem(ItemStack clientItemStack) {
        return recoverOriginalItem(clientItemStack);
    }

    /**
     * Turn a client-side ItemStack back into a server-side ItemStack
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     *
     * @param    clientItemStack   The client-side item stack
     */
    public static ItemStack recoverOriginalItem(ItemStack clientItemStack) {

        if (clientItemStack.getNbt() == null || !clientItemStack.getNbt().contains(ORIGINAL_ITEM_NBT, NbtType.COMPOUND)) {
            return clientItemStack;
        }

        NbtCompound tag = clientItemStack.getNbt().getCompound(ORIGINAL_ITEM_NBT);
        ItemStack stack = ItemStack.fromNbt(tag);
        stack.setCount(clientItemStack.getCount()); // The clientside count is leading, to support middle mouse button duplication and stack splitting and such

        if (stack.isEmpty() && !clientItemStack.isEmpty()) {
            stack = new ItemStack(Items.CLAY_BALL);
            stack.setCustomName(Text.literal("Invalid Item").formatted(Formatting.ITALIC));
        }
        return stack;
    }

    @Override
    public boolean isVanillaLikeMap() {
        return true;
    }

    @Override
    public boolean hasBlockWizards() {
        return this.hasBlockWizards;
    }

    @Override
    public boolean shouldForceBlockStateSync(BlockState sourceState, BlockState clientState, Direction direction) {
        Block block = clientState.getBlock();

        // Mushroom blocks only trigger updates when a non-sheared face touches another mushroom block of the same type
        if (block == Blocks.BROWN_MUSHROOM_BLOCK || block == Blocks.RED_MUSHROOM_BLOCK || block == Blocks.MUSHROOM_STEM) {
            return MushroomFilters.mushroomNeedsResync(this, sourceState, clientState, direction);
        }

        // Wall blocks update whenever a block updates around them
        if (block instanceof WallBlock) {
            return true;
        }

        if (block == Blocks.NOTE_BLOCK) {
            return direction == Direction.UP;
        } else if (block == Blocks.MYCELIUM || block == Blocks.PODZOL) {
            return direction == Direction.DOWN;
        } else if (block == Blocks.TRIPWIRE) {
            if (sourceState == null) return direction.getAxis().isHorizontal();

            //Checks if the connected property for the block isn't what it should be
            //If the source block in that direction is string, it should be true. Otherwise false
            return direction.getAxis().isHorizontal() &&
                    clientState.get(ConnectingBlock.FACING_PROPERTIES.get(direction.getOpposite())) != (sourceState.getBlock() instanceof TripwireBlock);
        }
        return false;
    }

    /**
     * Generate the resource pack to use for this map
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     *
     * @param    logger   The logger instance
     */
    @Override
    public @Nullable PolyMcResourcePack generateResourcePack(SimpleLogger logger) {
        var moddedResources = PolyMcPlus.getModdedResources();
        var pack = new ResourcePackImplementation();

        this.registry.generateDefaultResources(moddedResources, pack, logger);

        //Let mods register resources via the api
        List<PolyMcEntrypoint> entrypoints = FabricLoader.getInstance().getEntrypoints("polymc", PolyMcEntrypoint.class);
        for (PolyMcEntrypoint entrypointEntry : entrypoints) {
            entrypointEntry.registerModSpecificResources(moddedResources, pack, logger);
        }

        // Hooks for all itempolys
        this.itemPolys.forEach((item, itemPoly) -> {
            try {
                itemPoly.addToResourcePack(item, moddedResources, pack, logger);
            } catch (Exception e) {
                logger.warn("Exception whilst generating resources for " + item.getTranslationKey());
                e.printStackTrace();
            }
        });

        // Hooks for all blockpolys
        this.blockPolys.forEach((block, blockPoly) -> {
            try {
                blockPoly.addToResourcePack(block, moddedResources, pack, logger);
            } catch (Exception e) {
                logger.warn("Exception whilst generating resources for " + block.getTranslationKey());
                e.printStackTrace();
            }
        });

        // Write the resources generated from shared values
        sharedValueResources.forEach((sharedValueResourceContainer) -> {
            try {
                sharedValueResourceContainer.addToResourcePack(moddedResources, pack, logger);
            } catch (Exception e) {
                logger.warn("Exception whilst generating resources for shared values: " + sharedValueResourceContainer);
                e.printStackTrace();
            }
        });

        // Import the language files for all mods
        var languageKeys = new TreeMap<String, Map<String, String>>(); // The first hashmap is per-language. Then it's translationkey->translation
        for (var lang : moddedResources.locateLanguageFiles()) {
            Identifier lang_identifier = lang.getLeft();
            InputSupplier<InputStream> supplier = lang.getRight();

            // Ignore fapi
            if (lang_identifier.getNamespace().equals("fabric")) continue;
            for (var stream : moddedResources.getInputStreams(lang_identifier.getNamespace(), lang_identifier.getPath())) {
                try (var streamReader = new InputStreamReader(stream.get(), StandardCharsets.UTF_8)){
                    // Copy all of the language keys into the main map
                    var languageObject = pack.getGson().fromJson(streamReader, JsonObject.class);
                    var mainLangMap = languageKeys.computeIfAbsent(lang_identifier.getPath(), (key) -> new TreeMap<>());
                    languageObject.entrySet().forEach(entry -> mainLangMap.put(entry.getKey(), JsonHelper.asString(entry.getValue(), entry.getKey())));
                } catch (JsonParseException | IOException e) {
                    logger.error("Couldn't parse lang file "+lang_identifier);
                    e.printStackTrace();
                }
            }
        }
        // It doesn't actually matter which namespace the language files are under. We're just going to put them all under 'polymc-lang'
        languageKeys.forEach((path, translations) -> {
            pack.setAsset("polymc-lang", path, (stream, gson) -> {
                try (var writer = new OutputStreamWriter(stream)) {
                    gson.toJson(translations, writer);
                }
            });
        });

        // Import sounds
        for (var namespace : moddedResources.getAllNamespaces()) {
            var soundsRegistry = moddedResources.getSoundRegistry(namespace, "sounds.json");
            if (soundsRegistry == null) continue;
            pack.setSoundRegistry(namespace, "sounds.json", soundsRegistry);
            pack.importRequirements(moddedResources, soundsRegistry, logger);
        }

        try {
            moddedResources.close();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Failed to close modded resources");
        }
        return pack;
    }

    @Override
    public String dumpDebugInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append("###########\n## ITEMS ##\n###########\n");
        this.itemPolys
                .entrySet()
                .stream()
                .sorted(Comparator.comparing(item -> item.getKey().getTranslationKey()))
                .forEach(entry -> {
                    var item = entry.getKey();
                    var poly = entry.getValue();
                    addDebugProviderToDump(builder, item, item.getTranslationKey(), poly);
                });
        builder.append("############\n## BLOCKS ##\n############\n");
        this.blockPolys
                .entrySet()
                .stream()
                .sorted(Comparator.comparing(block -> block.getKey().getTranslationKey()))
                .forEach(entry -> {
                    var block = entry.getKey();
                    var poly = entry.getValue();
                    addDebugProviderToDump(builder, block, block.getTranslationKey(), poly);
                });
        return builder.toString();
    }

    private static <T> void addDebugProviderToDump(StringBuilder b, T object, String key, DebugInfoProvider<T> poly) {
        b.append(Util.expandTo(key, 45));
        b.append(" --> ");
        b.append(Util.expandTo(poly.getClass().getName(), 60));
        try {
            String info = poly.getDebugInfo(object);
            if (info != null) {
                b.append("|");
                b.append(info);
            }
        } catch (Exception e) {
            PolyMcPlus.LOGGER.info(String.format("Error whilst getting debug info from '%s' which is registered to '%s'", poly.getClass().getName(), key));
            e.printStackTrace();
        }
        b.append("\n");
    }
}
