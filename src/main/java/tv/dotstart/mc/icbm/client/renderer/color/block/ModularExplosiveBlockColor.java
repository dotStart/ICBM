package tv.dotstart.mc.icbm.client.renderer.color.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import tv.dotstart.mc.icbm.common.block.explosives.ModularExplosiveBlock;

/**
 * Provides a recoloring value for instances of {@link ModularExplosiveBlock} in order to make them
 * distinguishable for players.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class ModularExplosiveBlockColor implements IBlockColor {
    public static final ModularExplosiveBlockColor INSTANCE = new ModularExplosiveBlockColor();
    private int colorOverride = 0xFFFFFF;
    private boolean overrideEnabled;

    private ModularExplosiveBlockColor() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int colorMultiplier(@Nonnull IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
        if (this.overrideEnabled) {
            return this.colorOverride;
        }

        if (worldIn == null || pos == null) {
            return 0xFFFFFF;
        }

        return ModularExplosiveBlock.INSTANCE.getExplosive(worldIn, pos).getColor();
    }

    public void resetColorOverride() {
        this.overrideEnabled = false;
    }

    public void setColorOverride(int colorOverride) {
        this.colorOverride = colorOverride;
        this.overrideEnabled = true;
    }
}
