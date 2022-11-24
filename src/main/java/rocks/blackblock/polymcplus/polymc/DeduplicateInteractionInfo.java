package rocks.blackblock.polymcplus.polymc;

import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

public record DeduplicateInteractionInfo(BlockHitResult blockHitResult, Hand hand) {

    public boolean isSameHand(Hand test_hand) {
        return this.hand == test_hand;
    }

}
