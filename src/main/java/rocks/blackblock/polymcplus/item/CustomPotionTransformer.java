package rocks.blackblock.polymcplus.item;

import io.github.theepicblock.polymc.api.PolyMap;
import io.github.theepicblock.polymc.api.item.ItemLocation;
import io.github.theepicblock.polymc.api.item.ItemTransformer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CustomPotionTransformer implements ItemTransformer {

    protected static Map<Identifier, Integer> CUSTOM_POTION_COLORS = new HashMap<>();
    protected static Map<Identifier, String> CUSTOM_POTION_LANG_POINTERS = new HashMap<>();

    public ItemStack transform(ItemStack original, ItemStack input, PolyMap polyMap, @Nullable ServerPlayerEntity player, @Nullable ItemLocation location) {
        // Determine if we need to transform this based off what the item is.
        if (shouldPort(input, original)) {

            // Get the potion effects and key and put them in the potion's lore and name.
            PotionContentsComponent old_component = original.get(DataComponentTypes.POTION_CONTENTS);
            ItemStack output = original == input ? input.copy() : input;

            // Take the potion and, instead of depending on the client to do it right, set it to have custom effects and color instead.
            try {
                // See if we have this potion color registered.
                RegistryEntry<Potion> potion = old_component.potion().get();
                Identifier potion_id = potion.getKey().get().getValue();
                Integer color = CUSTOM_POTION_COLORS.get(potion_id);
                String potion_lang_pointer = CUSTOM_POTION_LANG_POINTERS.get(potion_id);

                // Set the new potion contents.
                if (color != null) output.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Optional.empty(), Optional.of(color), potion.value().getEffects(), Optional.empty()));
                else output.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Optional.empty(), Optional.empty(), potion.value().getEffects(), Optional.empty()));

                // Set the potion name as well.
                if (potion_lang_pointer != null && !potion_lang_pointer.isEmpty()) {
                    String output_translation_key = output.getItem().getTranslationKey();
                    if (output_translation_key.endsWith(".effect.empty")) output_translation_key = output_translation_key.substring(0, output_translation_key.length() - 13);
                    output.set(DataComponentTypes.CUSTOM_NAME, Text.translatable( output_translation_key + ".effect." + potion_lang_pointer).setStyle(Style.EMPTY.withItalic(false)));
                }
            } catch (Throwable var13) {
                return input;
            }

            // Return.
            return output;

        // Return.
        } else {
            return input;
        }
    }

    private static boolean shouldPort(ItemStack stack, ItemStack original) {
        // If potion component doesn't exist, return.
        PotionContentsComponent potionContentsComponent = original.get(DataComponentTypes.POTION_CONTENTS);
        if (potionContentsComponent == null) return false;

        // If potion doesn't exist, return.
        Optional<RegistryEntry<Potion>> potion = potionContentsComponent.potion();
        if (potion.isEmpty()) return false;

        // If key doesn't exist, return.
        Optional<RegistryKey<Potion>> key = potion.get().getKey();
        if (key.isEmpty()) return false;

        // Return whether the key is in the Minecraft namespace.
        return !"minecraft".equals(key.get().getValue().getNamespace());
    }

    public static void registerModdedPotionInfo(Identifier potion_key, Integer color, String lang_pointer) {
        CUSTOM_POTION_COLORS.put(potion_key, color);
        CUSTOM_POTION_LANG_POINTERS.put(potion_key, lang_pointer);
    }
}
