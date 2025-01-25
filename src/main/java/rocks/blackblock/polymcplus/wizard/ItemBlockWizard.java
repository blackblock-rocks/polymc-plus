package rocks.blackblock.polymcplus.wizard;

import io.github.theepicblock.polymc.api.wizard.PacketConsumer;
import io.github.theepicblock.polymc.api.wizard.Wizard;
import io.github.theepicblock.polymc.api.wizard.WizardInfo;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import rocks.blackblock.polymcplus.PolyMcPlus;
import rocks.blackblock.polymcplus.block.ItemBlockStateInfo;

import java.util.ArrayList;
import java.util.List;

public class ItemBlockWizard extends Wizard {

    protected ItemBlockStateInfo info;
    protected List<VItemDisplay.BatchedData> batched_data;

    public ItemBlockWizard(ItemBlockStateInfo blockstate_info, WizardInfo wizard_info) {
        super(wizard_info);
        this.info = blockstate_info;
        int item_count = 0;

        if (this.info == null) {
            PolyMcPlus.LOGGER.info("ItemBlockWizard: ItemBlockPoly.ItemBlockStateInfo is null for state " + wizard_info.getBlockState());
            PolyMcPlus.LOGGER.info(" -- WizardInfo is: " + wizard_info);
            Thread.dumpStack();
            this.batched_data = new ArrayList<>(1);

            VItemDisplay.BatchedData data = new VItemDisplay.BatchedData(new VItemDisplay());
            data.setPosition(this.getPosition());
            data.setYaw(0);
            data.setPitch(0);
            data.setItem(Items.BARRIER.getDefaultStack());
            data.setWidth(16);
            data.setHeight(16);
            this.batched_data.add(data);
        } else {
            List<ItemBlockStateInfo.RotatedItemCMD> rotated_item_cmds = blockstate_info.getRotatedItemCMDs();
            item_count = rotated_item_cmds.size();
            this.batched_data = new ArrayList<>(item_count);

            for (ItemBlockStateInfo.RotatedItemCMD rotated_item_cmd : rotated_item_cmds) {
                VItemDisplay.BatchedData data = new VItemDisplay.BatchedData(new VItemDisplay());
                data.setPosition(this.getPosition());
                data.setYaw(rotated_item_cmd.getYaw());
                data.setPitch(rotated_item_cmd.getPitch());
                data.setItem(rotated_item_cmd.getClientStack());
                data.setWidth(16);
                data.setHeight(16);
                this.batched_data.add(data);
            }
        }
    }

    @Override
    @NotNull
    public Vec3d getPosition() {
        Vec3d pos = super.getPosition();
        return pos.add(0, 0.5, 0);
    }

    @Override
    public void addPlayer(PacketConsumer player) {
        for (VItemDisplay.BatchedData data : this.batched_data) {
            data.spawnAndSubmit(player);
        }
    }

    @Override
    public void removePlayer(PacketConsumer player) {
        for (VItemDisplay.BatchedData data : this.batched_data) {
            data.removeConsumer(player);
        }
    }
}
