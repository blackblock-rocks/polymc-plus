package rocks.blackblock.polymcplus.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    /**
     * This method handles opening of custom written books.
     * Actually just intercepts the server-side item check to make it more broad.
     *
     * @author   Jade Godwin   <icanhasabanana@gmail.com>
     * @since    0.5.2
     */
    @Redirect(method = "useBook",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    public boolean isOf(ItemStack itemStack, Item item) {
        return itemStack.getItem() instanceof WrittenBookItem;
    }

}
