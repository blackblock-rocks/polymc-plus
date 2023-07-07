package rocks.blackblock.polymcplus.wizard;

import io.github.theepicblock.polymc.api.wizard.PacketConsumer;
import io.github.theepicblock.polymc.api.wizard.Wizard;
import io.github.theepicblock.polymc.api.wizard.WizardInfo;
import net.minecraft.item.Items;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import rocks.blackblock.polymcplus.PolyMcPlus;
import rocks.blackblock.polymcplus.block.ItemBlockPoly;

public class ItemBlockWizard extends Wizard {

    protected ItemBlockPoly.ItemBlockStateInfo info;
    protected VItemDisplay display;
    protected VItemDisplay.BatchedData batched_data;

    public ItemBlockWizard(ItemBlockPoly.ItemBlockStateInfo blockstate_info, WizardInfo wizard_info) {
        super(wizard_info);
        this.info = blockstate_info;

        if (this.info == null) {
            PolyMcPlus.LOGGER.info("ItemBlockWizard: ItemBlockPoly.ItemBlockStateInfo is null for state " + wizard_info.getBlockState());
            PolyMcPlus.LOGGER.info(" -- WizardInfo is: " + wizard_info);
            Thread.dumpStack();
        }

        this.display = new VItemDisplay();
        this.batched_data = new VItemDisplay.BatchedData(display);
        this.batched_data.setPosition(this.getPosition());

        if (this.info == null) {
            this.batched_data.setItem(Items.JUKEBOX.getDefaultStack());
        } else {
            this.batched_data.setYaw(this.info.getYaw());
            this.batched_data.setPitch(this.info.getPitch());
            this.batched_data.setItem(this.info.getClientStack());
        }

        this.batched_data.setWidth(16);
        this.batched_data.setHeight(16);
    }

    @Override
    @NotNull
    public Vec3d getPosition() {
        Vec3d pos = super.getPosition();
        return pos.add(0, 0.5, 0);
    }

    @Override
    public void addPlayer(PacketConsumer player) {
        this.batched_data.spawnAndSubmit(player);
    }

    @Override
    public void removePlayer(PacketConsumer player) {
        this.display.remove(player);
    }

}
