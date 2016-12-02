package tv.dotstart.mc.icbm.client.renderer.color.item;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

import tv.dotstart.mc.icbm.common.item.RocketItem;

/**
 * Provides the color tinting for instances of {@link RocketItem} and their respective entity
 * representations of type {@link tv.dotstart.mc.icbm.common.entity.rocket.RocketEntity} within UIs
 * and the world.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class RocketItemColor implements IItemColor {

    /**
     * {@inheritDoc}
     */
    @Override
    public int getColorFromItemstack(@Nonnull ItemStack stack, int tintIndex) {
        return RocketItem.INSTANCE.getExplosive(stack).getColor();
    }
}
