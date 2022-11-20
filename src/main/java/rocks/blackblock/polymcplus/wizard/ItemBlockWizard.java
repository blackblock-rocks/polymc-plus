package rocks.blackblock.polymcplus.wizard;

import io.github.theepicblock.polymc.api.wizard.PacketConsumer;
import io.github.theepicblock.polymc.api.wizard.Wizard;
import io.github.theepicblock.polymc.api.wizard.WizardInfo;
import net.minecraft.util.math.Direction;
import rocks.blackblock.polymcplus.block.ItemBlockPoly;

public class ItemBlockWizard extends Wizard {
    protected VItemFrame item_frame;
    protected ItemBlockPoly.ItemBlockStateInfo info;

    public ItemBlockWizard(ItemBlockPoly.ItemBlockStateInfo blockstate_info, WizardInfo wizard_info) {
        super(wizard_info);
        this.info = blockstate_info;
        this.item_frame = new VItemFrame();
    }

    @Override
    public void addPlayer(PacketConsumer player) {
        this.item_frame.spawn(player, this.getPosition(), Direction.SOUTH);
        this.item_frame.setNoGravity(player, true);
        this.item_frame.sendItemStack(player, this.info.getClientStack());
        this.item_frame.makeInvisible(player);
        this.item_frame.move(player, this.getPosition(), this.info.getYaw(), 0, false);
    }

    @Override
    public void removePlayer(PacketConsumer player) {
        this.item_frame.remove(player);
    }

}
