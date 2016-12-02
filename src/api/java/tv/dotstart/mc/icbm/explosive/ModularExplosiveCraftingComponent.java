package tv.dotstart.mc.icbm.explosive;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Provides a base interface to mark instances of {@link net.minecraft.item.Item} or similar
 * in-game elements, which may be combined with other items in order to create explosive containers.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public interface ModularExplosiveCraftingComponent {

    /**
     * Retrieves an instance of the modular explosive which this item creates a container for when
     * combined or used.
     *
     * <strong>Note:</strong> This method may also return null if its respective item stack is not
     * qualified to be used as a component for any reason.
     */
    @Nullable
    ModularExplosive getExplosive(@Nonnull ItemStack itemStack);
}
