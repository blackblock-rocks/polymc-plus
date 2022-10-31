package rocks.blackblock.polymcplus.block;

import io.github.theepicblock.polymc.api.wizard.Wizard;
import io.github.theepicblock.polymc.api.wizard.WizardInfo;

/**
 * Simple functional interface for turning WizardInfo into a Wizard
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.2.0
 */
@FunctionalInterface
public interface WizardProvider {
    Wizard getWizard(WizardInfo info);
}