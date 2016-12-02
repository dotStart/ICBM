package tv.dotstart.mc.icbm.common.explosive;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import tv.dotstart.mc.icbm.ICBMModification;
import tv.dotstart.mc.icbm.explosive.ModularExplosive;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class LiquidExplosive extends ModularExplosive {
    public static final LiquidExplosive INSTANCE = new LiquidExplosive();

    private LiquidExplosive() {
        this.setRegistryName(new ResourceLocation(ICBMModification.IDENTIFIER, "liquid"));
        this.setColor(0x445099);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void explode(@Nonnull World world, double posX, double posY, double posZ, @Nullable EntityLivingBase launchedBy) {
        for (int x = -2; x <= 2; ++x) {
            for (int z = -2; z <= 2; z++) {
                world.setBlockState(new BlockPos(posX + x, posY, posZ + z), Blocks.WATER.getDefaultState(), 7);
            }
        }
    }
}
