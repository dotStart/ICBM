package tv.dotstart.mc.icbm.common.explosive;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import tv.dotstart.mc.icbm.ICBMModification;
import tv.dotstart.mc.icbm.api.explosive.ModularExplosive;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class ChemicalExplosive extends ModularExplosive {
    public static final ChemicalExplosive INSTANCE = new ChemicalExplosive();
    private static final int RANGE = 16;
    private static final int DURATION = 800;

    private ChemicalExplosive() {
        this.setRegistryName(new ResourceLocation(ICBMModification.IDENTIFIER, "chemical"));
        this.setColor(0x208C3D);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void explode(@Nonnull World world, double posX, double posY, double posZ, @Nullable EntityLivingBase launchedBy) {
        AxisAlignedBB bb = new AxisAlignedBB(posX - RANGE, posY - RANGE, posZ - RANGE, posX + RANGE, posY + RANGE, posZ + RANGE);

        world.getEntitiesWithinAABB(EntityLivingBase.class, bb).forEach((e) -> {
            double distanceAmplifier = ((RANGE - e.getDistance(posX, posY, posZ)) / RANGE);

            e.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, (int) (distanceAmplifier * DURATION), 0));
            e.addPotionEffect(new PotionEffect(MobEffects.WITHER, (int) (distanceAmplifier * DURATION), 0));
        });
    }
}
