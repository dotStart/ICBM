package tv.dotstart.mc.icbm.common.explosive;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import tv.dotstart.mc.icbm.ICBMModification;
import tv.dotstart.mc.icbm.explosive.ModularExplosive;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class ConventionalExplosive extends ModularExplosive {
    public static final ConventionalExplosive INSTANCE = new ConventionalExplosive();

    private ConventionalExplosive() {
        this.setRegistryName(new ResourceLocation(ICBMModification.IDENTIFIER, "conventional"));
        this.setColor(0xC11B42);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void explode(@Nonnull World world, double posX, double posY, double posZ, @Nullable EntityLivingBase launchedBy) {
        world.createExplosion(launchedBy, posX, posY, posZ, 6, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void spawnEffects(@Nonnull World world, double posX, double posY, double posZ, @Nullable EntityLivingBase launchedBy) {
    }
}
