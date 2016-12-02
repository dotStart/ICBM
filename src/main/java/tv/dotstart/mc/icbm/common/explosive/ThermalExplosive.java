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
public class ThermalExplosive extends ModularExplosive {
    public static final ThermalExplosive INSTANCE = new ThermalExplosive();
    private static final int RANGE = 16;

    private ThermalExplosive() {
        this.setRegistryName(new ResourceLocation(ICBMModification.IDENTIFIER, "thermal"));
        this.setColor(0xD68733);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void explode(@Nonnull World world, double posX, double posY, double posZ, @Nullable EntityLivingBase launchedBy) {
        if (!world.isAirBlock(new BlockPos(posX, posY, posZ))) {
            ++posY;
        }

        for (int x = -RANGE; x <= RANGE; ++x) {
            for (int z = -RANGE; z <= RANGE; z++) {
                BlockPos position = new BlockPos(posX + x, posY, posZ + z);

                if (!world.isAirBlock(position)) {
                    continue;
                }

                if (world.rand.nextInt(32) == 0) {
                    world.setBlockState(position, Blocks.LAVA.getDefaultState(), 7);
                } else if (world.rand.nextInt(16) == 0) {
                    world.setBlockState(position, Blocks.FIRE.getDefaultState(), 7);
                }
            }
        }
    }
}
