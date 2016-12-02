package tv.dotstart.mc.icbm.rocket;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Provides an interface for items which provide coordinates to rocket target systems in order to
 * navigate their respective rockets to a specific target destination.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface CoordinateSourceItem {

    /**
     * Extracts the respective target coordinates from an item stack based on its metadata, specific
     * item instance or NBT data.
     *
     * <strong>Note:</strong> This method may also return null if the stack does not contain
     * target information or is not qualified to return this information.
     */
    @Nullable
    BlockPos extractCoordinate(@Nonnull ItemStack stack);
}
