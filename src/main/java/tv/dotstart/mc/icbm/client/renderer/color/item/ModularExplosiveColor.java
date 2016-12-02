package tv.dotstart.mc.icbm.client.renderer.color.item;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

import tv.dotstart.mc.icbm.common.block.explosives.ModularExplosiveBlock;

/**
 * Provides an item color provider for item versions of {@link ModularExplosiveBlock}.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class ModularExplosiveColor implements IItemColor {

    /**
     * {@inheritDoc}
     */
    @Override
    public int getColorFromItemstack(@Nonnull ItemStack stack, int tintIndex) {
        return ModularExplosiveBlock.ITEM.getExplosive(stack).getColor();
    }
}
