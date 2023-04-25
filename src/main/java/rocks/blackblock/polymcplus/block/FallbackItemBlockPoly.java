package rocks.blackblock.polymcplus.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import io.github.theepicblock.polymc.api.PolyRegistry;
import io.github.theepicblock.polymc.api.block.BlockPoly;
import io.github.theepicblock.polymc.api.block.BlockStateMerger;
import io.github.theepicblock.polymc.api.resource.ModdedResources;
import io.github.theepicblock.polymc.api.resource.PolyMcResourcePack;
import io.github.theepicblock.polymc.api.wizard.Wizard;
import io.github.theepicblock.polymc.api.wizard.WizardInfo;
import io.github.theepicblock.polymc.impl.Util;
import io.github.theepicblock.polymc.impl.misc.BooleanContainer;
import io.github.theepicblock.polymc.impl.misc.logging.SimpleLogger;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.BiFunction;

/**
 * Use a function to determine the best client-side blockstate to use.
 * If none is found, use ItemBlocks
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.2.0
 */
public class FallbackItemBlockPoly implements BlockPoly {

    protected final Map<BlockState,BlockState> states;
    protected final ArrayList<BlockState> unique_client_blocks = new ArrayList<>();
    protected final Map<BlockState, ItemBlockPoly> fallback_states = new HashMap<>();
    protected ItemBlockPoly fallback_poly = null;
    protected boolean has_fallbacks = false;
    protected final Block modded_block;

    /**
     * Initialize the FallbackItemBlockPoly for all the states of the given block
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public FallbackItemBlockPoly(Block moddedBlock, BiFunction<BlockState, BooleanContainer, BlockState> registrationProvider, PolyRegistry registry) {
        this(moddedBlock, registrationProvider, registry, BlockStateMerger.DEFAULT);
    }

    /**
     * Initialize the FallbackItemBlockPoly for all the states of the given block
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public FallbackItemBlockPoly(Block moddedBlock, BiFunction<BlockState, BooleanContainer, BlockState> registrationProvider, PolyRegistry registry, BlockStateMerger merger) {
        this.modded_block = moddedBlock;

        HashMap<BlockState, BlockState> server_to_client_states = new HashMap<>();

        // Sort all the modded states into groups:
        // Each group has one block state which is the canonical block state of that group,
        // this canonical version is defined by a BlockStateMerger
        // The BlockStateMerger's purpose is to remove all properties and stuff that don't change the model, for example
        // the distance and persistence fields on leaves: the merger will normalize the distance property by setting it to 0.
        // this way, all the different leaves will be grouped together under the [distance=0,persistent=false]
        // Ofc, you might not want leaves to be grouped this way if you have different models for them,
        // that's why the BlockStateMerger can be swapped out with a different one
        Multimap<BlockState, BlockState> modded_state_groups = LinkedHashMultimap.create();
        for (BlockState modded_state : moddedBlock.getStateManager().getStates()) {
            modded_state_groups.put(merger.normalize(modded_state), modded_state);
        }

        // A list of modded states that need a ItemBlock poly
        List<BlockState> leftover_states = new ArrayList<>();

        BooleanContainer is_unique_callback = new BooleanContainer();
        modded_state_groups.asMap().forEach((normalizedState, group) -> {
            // Only call registrationProvider once for each normalized state,
            // then we apply the result to all blocks in the group
            is_unique_callback.set(false);
            BlockState client_state = registrationProvider.apply(normalizedState, is_unique_callback);

            if (client_state == null) {
                leftover_states.add(normalizedState);
                return;
            }

            for (BlockState modded_state : group) {
                server_to_client_states.put(modded_state, client_state);
            }
            if (is_unique_callback.get()) {
                this.unique_client_blocks.add(client_state);
            }
        });

        this.parseLeftoverStates(leftover_states, registry);

        this.states = ImmutableMap.copyOf(server_to_client_states);
    }

    /**
     * Parse all the leftover states
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    private void parseLeftoverStates(List<BlockState> modded_states, PolyRegistry registry) {

        if (modded_states.isEmpty()) {
            return;
        }

        this.has_fallbacks = true;

        ItemBlockPoly item_block_poly = new ItemBlockPoly(modded_states, registry);
        this.fallback_poly = item_block_poly;

        for (BlockState modded_state : modded_states) {
            fallback_states.put(modded_state, item_block_poly);
        }

    }

    @Override
    public BlockState getClientBlock(BlockState input) {

        if (this.states.containsKey(input)) {
            return this.states.get(input);
        }

        if (!this.has_fallbacks) {
            return null;
        }

        return this.fallback_poly.getClientBlock(input);
    }

    /**
     * All instances of this BlockPoly have wizards
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    @Override
    public boolean hasWizard() {
        return this.has_fallbacks;
    }

    /**
     * Use the provider to create the wizard
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    @Override
    public Wizard createWizard(WizardInfo info) {

        if (!this.has_fallbacks) {
            return null;
        }

        BlockState modded_state = info.getBlockState();

        if (!this.fallback_states.containsKey(modded_state)) {
            return null;
        }

        return this.fallback_poly.createWizard(info);
    }

    /**
     * Add all the required states to the resourcepack
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    @Override
    public void addToResourcePack(Block block, ModdedResources moddedResources, PolyMcResourcePack pack, SimpleLogger logger) {

        Identifier moddedBlockId = Registries.BLOCK.getId(block);
        // Read the modded block state file. This tells us which model is used for which block state
        var moddedBlockState = moddedResources.getBlockState(moddedBlockId.getNamespace(), moddedBlockId.getPath());
        if (moddedBlockState == null) {
            logger.error("Can't find blockstate definition for "+moddedBlockId+", can't generate resources for it");
            return;
        }

        HashSet<BlockState> clientStatesDone = new HashSet<>();
        // Iterate modded block states
        this.states.forEach((moddedState, clientState) -> {
            if (clientStatesDone.contains(clientState)) return;
            if (!unique_client_blocks.contains(clientState)) return;

            var clientBlockId = Registries.BLOCK.getId(clientState.getBlock());
            var clientBlockStates = pack.getOrDefaultBlockState(clientBlockId.getNamespace(), clientBlockId.getPath());
            var clientStateString = Util.getPropertiesFromBlockState(clientState);

            // Get the model that the modded block state uses and assign it to the client block state
            var moddedVariants = moddedBlockState.getVariantsBestMatching(moddedState);

            try {
                clientBlockStates.setVariant(clientStateString, moddedVariants);
            } catch (Exception e) {
                logger.error("Error while setting variant for "+clientBlockId+" "+clientStateString+" to "+moddedVariants + " of modded state " + moddedState);
                e.printStackTrace();

                logger.error("Modded blockstate: "+moddedState);
            }

            pack.importRequirements(moddedResources, moddedVariants, logger);

            clientStatesDone.add(clientState);
        });

        if (this.has_fallbacks) {
            this.fallback_poly.addToResourcePack(this.fallback_states.keySet().stream().toList(), moddedResources, pack, logger);
        }
    }

    @Override
    public String toString() {
        return "FallbackItemBlockPoly{" + this.modded_block + ",replacedstates=" + this.states.size() + ",itemstates=" + this.fallback_states.size() + "}";
    }

}
