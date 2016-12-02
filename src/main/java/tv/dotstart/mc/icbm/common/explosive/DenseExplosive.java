package tv.dotstart.mc.icbm.common.explosive;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import tv.dotstart.mc.icbm.ICBMModification;
import tv.dotstart.mc.icbm.api.explosive.ModularExplosive;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class DenseExplosive extends ModularExplosive {
    public static final DenseExplosive INSTANCE = new DenseExplosive();

    private DenseExplosive() {
        this.setRegistryName(new ResourceLocation(ICBMModification.IDENTIFIER, "dense"));
        this.setColor(0x540C1D);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void explode(@Nonnull World world, double posX, double posY, double posZ, @Nullable EntityLivingBase launchedBy) {
        world.createExplosion(launchedBy, posX, posY, posZ, 12, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void spawnEffects(@Nonnull World world, double posX, double posY, double posZ, @Nullable EntityLivingBase launchedBy) {
    }
}
