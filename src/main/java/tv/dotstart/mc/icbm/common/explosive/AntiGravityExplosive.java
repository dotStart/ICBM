package tv.dotstart.mc.icbm.common.explosive;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import tv.dotstart.mc.icbm.ICBMModification;
import tv.dotstart.mc.icbm.common.entity.explosives.gravity.AntiGravitySourceEntity;
import tv.dotstart.mc.icbm.api.explosive.ModularExplosive;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class AntiGravityExplosive extends ModularExplosive {
    public static final AntiGravityExplosive INSTANCE = new AntiGravityExplosive();

    private AntiGravityExplosive() {
        this.setRegistryName(new ResourceLocation(ICBMModification.IDENTIFIER, "anti_gravity"));
        this.setColor(0xf07b1a);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void explode(@Nonnull World world, double posX, double posY, double posZ, @Nullable EntityLivingBase launchedBy) {
        if (!world.isRemote) {
            AntiGravitySourceEntity entity = new AntiGravitySourceEntity(world, posX, posY, posZ);
            world.spawnEntityInWorld(entity);
        }
    }
}
