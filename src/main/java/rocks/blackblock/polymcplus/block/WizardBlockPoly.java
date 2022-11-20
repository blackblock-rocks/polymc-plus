package rocks.blackblock.polymcplus.block;

import io.github.theepicblock.polymc.api.PolyRegistry;
import io.github.theepicblock.polymc.api.wizard.Wizard;
import io.github.theepicblock.polymc.api.wizard.WizardInfo;
import io.github.theepicblock.polymc.impl.misc.BooleanContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import rocks.blackblock.polymcplus.generator.BlockPolyPlusGenerator;

import java.util.function.BiFunction;

/**
 * The BlockPoly that can be used for blocks with Wizards,
 * without having to manually choose which client-side blockstate to use
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.2.0
 */
public class WizardBlockPoly extends PlusFunctionBlockStatePoly {

    // The function that will actually create the wizard
    private final WizardProvider wizard_provider;

    public WizardBlockPoly(Block moddedBlock, WizardProvider wizard_provider, BiFunction<BlockState, BooleanContainer, BlockState> registrationProvider) {
        super(moddedBlock, registrationProvider);
        this.wizard_provider = wizard_provider;
    }

    /**
     * Use the provider to create the wizard
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    @Override
    public Wizard createWizard(WizardInfo info) {
        return this.wizard_provider.getWizard(info);
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
     * Convenient static method to actually register a block as having a Wizard
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public static void registerBlockWizard(Block modded_block, PolyRegistry registry, WizardProvider wizard_provider) {

        BiFunction<BlockState, BooleanContainer, BlockState> registrationProvider = BlockPolyPlusGenerator.getBlockStateRegistrationProvider(registry);

        WizardBlockPoly wizardBlockPoly = new WizardBlockPoly(modded_block, wizard_provider, registrationProvider);

        registry.registerBlockPoly(modded_block, wizardBlockPoly);
    }

    /**
     * Convenient static method to actually register a block as having a Wizard
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public static void registerBlockWizard(Block modded_block, BlockState simple_replacement, PolyRegistry registry, WizardProvider wizard_provider) {

        WizardSimpleBlockPoly wizardSimpleBlockPoly = new WizardSimpleBlockPoly(simple_replacement, wizard_provider);

        registry.registerBlockPoly(modded_block, wizardSimpleBlockPoly);
    }

    /**
     * Convenient static method to actually register a block as having a Wizard
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public static void registerBlockWizard(Block modded_block, Block retaining_replacement, PolyRegistry registry, WizardProvider wizard_provider) {

        WizardRetainingBlockPoly wizard_poly = new WizardRetainingBlockPoly(retaining_replacement, wizard_provider);

        registry.registerBlockPoly(modded_block, wizard_poly);
    }

    /**
     * Register as an item block
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public static void registerItemBlock(Block modded_block, PolyRegistry registry) {
        registerItemBlock(modded_block, registry, null);
    }

    /**
     * Register as an item block
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    public static void registerItemBlock(Block modded_block, PolyRegistry registry, String collision_type) {
        ItemBlockPoly item_block_poly = new ItemBlockPoly(modded_block, registry, collision_type);
        registry.registerBlockPoly(modded_block, item_block_poly);
    }


}
