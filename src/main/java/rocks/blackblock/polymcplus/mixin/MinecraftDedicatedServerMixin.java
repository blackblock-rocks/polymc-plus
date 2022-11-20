package rocks.blackblock.polymcplus.mixin;

import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rocks.blackblock.polymcplus.PolyMcPlus;

/**
 * Get the server instance
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.2.0
 */
@Mixin(MinecraftDedicatedServer.class)
public class MinecraftDedicatedServerMixin {
    @Inject(at = @At("TAIL"), method = "setupServer")
    private void setupServer(CallbackInfoReturnable<Boolean> cir) {
        PolyMcPlus.setServer((MinecraftDedicatedServer) (Object) this);
    }
}
