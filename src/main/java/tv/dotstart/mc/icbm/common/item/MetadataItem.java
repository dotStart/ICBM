package tv.dotstart.mc.icbm.common.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Optional;

import javax.annotation.Nonnull;

/**
 * Provides a base to interfaces which store additional data in form of NBT.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface MetadataItem {

    /**
     * Retrieves the tag compound of an item stack or an empty optional if none is set.
     */
    @Nonnull
    default Optional<NBTTagCompound> getCompound(@Nonnull ItemStack stack) {
        return Optional.ofNullable(stack.getTagCompound());
    }
}
