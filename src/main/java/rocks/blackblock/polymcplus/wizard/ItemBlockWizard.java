package rocks.blackblock.polymcplus.wizard;

import io.github.theepicblock.polymc.api.wizard.PacketConsumer;
import io.github.theepicblock.polymc.api.wizard.Wizard;
import io.github.theepicblock.polymc.api.wizard.WizardInfo;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
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



        //vItemFrame.move(consumer, player.getPos().add(0.1f * i, 0.1f * i, 0.1f*i), 0, 0, false);

        /*
        this.armor_stand.spawn(player, this.getStandPosition());
        this.armor_stand.makeInvisible(player);
        this.armor_stand.setNoGravity(player, true);
        this.armor_stand.sendArmorStandFlags(player, false, false, false, true);

        //BlockState state = this.getBlockState();
        //var rotation = state.get(Properties.ROTATION);
        var rotation = 0;

        this.armor_stand.sendHeadRotation(player, 0, (float) ((rotation+8) * 22.5), 0);
        this.armor_stand.sendSingleSlot(player, EquipmentSlot.HEAD, this.client_item.get());

         */
    }

    public Vec3d getItemPosition() {
        return this.getPosition().add(0, -1.425, 0);
    }

    @Override
    public void removePlayer(PacketConsumer player) {
        this.item_frame.remove(player);
    }

}
