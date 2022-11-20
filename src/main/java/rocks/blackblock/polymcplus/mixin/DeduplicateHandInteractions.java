package rocks.blackblock.polymcplus.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rocks.blackblock.polymcplus.tools.WeakTickCache;

/**
 * Client-side interactions with blocks that normally *DON'T* have client-side interactions
 * causes the client to send an interaction packet for BOTH the main hand & the off-hand.
 * This causes the server to do the interaction twice, which is a problem.
 *
 * This mixin will ignore any second interaction a player sends *in the same tick*
 * if the first interaction was successful
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.2.0
 */
@Mixin(ServerPlayerInteractionManager.class)
public class DeduplicateHandInteractions {

    private static final WeakTickCache<PlayerEntity, Boolean> INTERACTED = new WeakTickCache<>(1);

    /**
     * Cancel the interaction if the player already had a successful interaction this tick
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    @Inject(method="interactBlock", at = @At("HEAD"), cancellable = true)
    private void cancelDuplicateInteractions(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {

        Boolean result = INTERACTED.get(player);

        if (result != null && result) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }

    /**
     * Remember any interaction that was accepted
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.0
     */
    @Inject(method = "interactBlock", at = @At("RETURN"))
    private void rememberSuccesfulBlockInteraction(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {

        ActionResult result = cir.getReturnValue();

        if (result.isAccepted()) {
            INTERACTED.put(player, true);
        }
    }

}
