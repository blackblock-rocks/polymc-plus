package rocks.blackblock.polymcplus.block;

import io.github.theepicblock.polymc.api.wizard.Wizard;
import io.github.theepicblock.polymc.api.wizard.WizardInfo;
import io.github.theepicblock.polymc.impl.poly.block.PropertyRetainingReplacementPoly;
import net.minecraft.block.Block;

public class WizardRetainingBlockPoly extends PropertyRetainingReplacementPoly {

    // The function that will actually create the wizard
    private final WizardProvider wizard_provider;

    public WizardRetainingBlockPoly(Block clientBlock, WizardProvider wizard_provider) {
        super(clientBlock);
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
}
