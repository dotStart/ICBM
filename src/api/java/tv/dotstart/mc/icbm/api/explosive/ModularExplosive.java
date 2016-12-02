package tv.dotstart.mc.icbm.api.explosive;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.fml.common.registry.RegistryBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Provides a base for explosive components which may be embedded into rockets and other explosive
 * types.
 *
 * <h2>Implementation Details</h2>
 * Explosives are registered against an instance of {@link IForgeRegistry} and can be accessed
 * directly by invoking {@link #getRegistry()} in order to retrieve the respective registry.
 *
 * In addition {@link net.minecraftforge.fml.common.registry.GameRegistry#register(IForgeRegistryEntry)}
 * will automatically push instances into the respective registry.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public abstract class ModularExplosive extends IForgeRegistryEntry.Impl<ModularExplosive> {
    private static final IForgeRegistry<ModularExplosive> registry;
    private static ModularExplosive defaultExplosive;
    private int color = 0xFFFFFF;

    static {
        registry = new RegistryBuilder<ModularExplosive>()
                .setName(new ResourceLocation(".icbm", "explosives"))
                .setType(ModularExplosive.class)
                .setIDRange(0, Short.MAX_VALUE)
                .create();

        registry.register(getDefault());
    }

    protected ModularExplosive() {
    }

    /**
     * Provides the effects which are applied to the world itself or entities within range of the
     * respective explosive container, in event of its priming.
     */
    public abstract void explode(@Nonnull World world, double posX, double posY, double posZ, @Nullable EntityLivingBase launchedBy);

    /**
     * Spawns an explosion effect on the client and/or server side.
     */
    public void spawnEffects(@Nonnull World world, double posX, double posY, double posZ, @Nullable EntityLivingBase launchedBy) {

        if (!world.isRemote) {
            world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
        } else {
            world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, posX, posY, posZ, 1.0D, 0.0D, 0.0D);
        }
    }

    /**
     * Retrieves a default explosive which has no specific effect, color or particle which may be
     * used as a replacement in cases where decoding from the respective data storage is required
     * first but null is undesirable as a replacement.
     */
    @Nonnull
    public static ModularExplosive getDefault() {
        if (defaultExplosive == null) {
            defaultExplosive = new UselessExplosive();
        }

        return defaultExplosive;
    }

    /**
     * Retrieves the registry which handles registration and ID allocation for these explosives.
     */
    @Nonnull
    public static IForgeRegistry<ModularExplosive> getRegistry() {
        return registry;
    }

    /**
     * Retrieves a hex encoded version of the color which visually represents the effects of this
     * explosive when the respective container (rockets or TNT) is displayed on the client side.
     */
    public int getColor() {
        return this.color;
    }

    /**
     * Sets the visual representation color of this explosive.
     *
     * @see #getColor() for a more descriptive explanation on explosive colors.
     */
    public void setColor(int color) {
        this.color = color;
    }

    /**
     * Provides a basic "useless" explosive which acts as a default in cases where a useless
     * implementation is favorable over null values or empty optionals.
     */
    private static final class UselessExplosive extends ModularExplosive {

        private UselessExplosive() {
            this.setRegistryName(new ResourceLocation("doticbm", "useless"));

            this.setColor(0xFF0000);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void explode(@Nonnull World world, double posX, double posY, double posZ, @Nullable EntityLivingBase launchedBy) {
        }
    }
}
