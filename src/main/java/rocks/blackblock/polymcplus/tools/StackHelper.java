package rocks.blackblock.polymcplus.tools;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

/**
 * Utilities to work with ItemStacks
 *
 * @since    0.5.2
 */
public class StackHelper {

    /**
     * Return a string representation of the custom data of a stack
     *
     * @since    0.5.2
     */
    public static String getCustomDataString(ItemStack stack) {
        // @TODO
        return stack.toString();
    }

    /**
     * Get the old-style NBT compound
     *
     * @since    0.5.2
     */
    public static NbtCompound getCustomNbt(ItemStack stack) {
        NbtComponent data = stack.get(DataComponentTypes.CUSTOM_DATA);

        if (data == null) {
            data = NbtComponent.of(new NbtCompound());
            stack.set(DataComponentTypes.CUSTOM_DATA, data);
        }

        return data.getNbt();
    }

    /**
     * Create a stack of the given item and
     * set the CustomModelData value of it
     *
     * @since    0.5.2
     */
    public static ItemStack createCustomModelDataStack(Item item, int value) {
        return setCustomModelData(new ItemStack(item), value);
    }

    /**
     * Set the CustomModelData value of a stack
     *
     * @since    0.5.2
     */
    public static ItemStack setCustomModelData(ItemStack stack, int value) {

        var cmd_component = new CustomModelDataComponent(value);
        stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, cmd_component);

        return stack;
    }
}
