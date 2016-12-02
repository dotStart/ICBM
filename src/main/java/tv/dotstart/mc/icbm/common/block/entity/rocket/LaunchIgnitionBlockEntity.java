package tv.dotstart.mc.icbm.common.block.entity.rocket;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import tv.dotstart.mc.icbm.common.block.entity.AbstractBlockEntity;
import tv.dotstart.mc.icbm.common.block.rocket.LaunchClampBlock;
import tv.dotstart.mc.icbm.common.block.rocket.LaunchPadBlock;
import tv.dotstart.mc.icbm.common.entity.rocket.RocketEntity;
import tv.dotstart.mc.icbm.explosive.ModularExplosive;

/**
 * Provides a block which is capable of containing and launching rockets (in form of {@link
 * tv.dotstart.mc.icbm.common.item.RocketItem} and {@link RocketEntity} respectively).
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class LaunchIgnitionBlockEntity extends AbstractBlockEntity implements ITickable {
    private ModularExplosive warhead;
    private int launchCounter = -1;
    private BlockPos target;
    private EntityLivingBase launchedBy;

    /**
     * Checks whether the launch sequence can still be aborted.
     */
    public boolean canAbortLaunchSequence() {
        return this.launchCounter != -1 && this.launchCounter > 100;
    }

    /**
     * Retrieves the material this guide is made up of.
     */
    @Nonnull
    public LaunchPadBlock.PadMaterial getMaterial() {
        return LaunchPadBlock.INSTANCE.getPadMaterial(this.worldObj.getBlockState(this.pos.offset(EnumFacing.DOWN)));
    }

    /**
     * Retrieves the loaded warhead.
     */
    @Nonnull
    public Optional<ModularExplosive> getWarhead() {
        return Optional.ofNullable(this.warhead);
    }

    /**
     * Checks whether the launch sequence is currently active.
     */
    public boolean isLaunchSequenceActive() {
        return this.launchCounter != -1;
    }

    /**
     * Launches a rocket from the block entity position with a specified target position.
     *
     * @param position   declares the target position the rocket will head to.
     * @param launchedBy declares the entity which requested the rocket launch (if any).
     */
    public void launch(@Nonnull BlockPos position, @Nullable EntityLivingBase launchedBy) {
        if (this.warhead == null) {
            return;
        }

        this.target = position;
        this.launchedBy = launchedBy;
        this.launchCounter = 140;

        this.synchronize();
    }

    /**
     * Sets the warhead the rocket within this block entity carries.
     */
    public void setWarhead(@Nullable ModularExplosive warhead) {
        this.warhead = warhead;
        this.synchronize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (compound.hasKey("Warhead")) {
            this.warhead = ModularExplosive.getRegistry().getValue(new ResourceLocation(compound.getString("Warhead")));
        }

        if (compound.hasKey("LaunchCounter")) {
            this.launchCounter = compound.getInteger("LaunchCounter");
        }

        if (compound.hasKey("Target")) {
            this.target = BlockPos.fromLong(compound.getLong("Target"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        if (this.warhead != null) {
            compound.setString("Warhead", this.warhead.getRegistryName().toString());
        }

        if (this.launchCounter != -1) {
            compound.setInteger("LaunchCounter", this.launchCounter);
            compound.setLong("Target", this.target.toLong());
        }

        return super.writeToNBT(compound);
    }

    /**
     * Releases all adjacent launch clamps.
     */
    private void releaseClamps() {
        for (EnumFacing f1 : EnumFacing.HORIZONTALS) {
            for (EnumFacing f2 : EnumFacing.HORIZONTALS) {
                if (f1 == f2) {
                    continue;
                }

                LaunchClampBlock.INSTANCE.getBlockEntity(this.worldObj, this.pos.offset(EnumFacing.UP).offset(f1).offset(f2)).ifPresent(LaunchClampBlockEntity::release);
                LaunchClampBlock.INSTANCE.getBlockEntity(this.worldObj, this.pos.offset(EnumFacing.UP).offset(f1, -1).offset(f2, -1)).ifPresent(LaunchClampBlockEntity::release);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        if (this.launchCounter == -1) {
            return;
        }

        if (--this.launchCounter == 0) {
            if (!this.worldObj.isRemote) {
                RocketEntity entity = new RocketEntity(this.worldObj, this.pos.getX() + 0.5, this.pos.getY() + 0.4, this.pos.getZ() + 0.5, this.launchedBy, this.warhead, this.target);
                this.worldObj.spawnEntityInWorld(entity);
            }

            this.setWarhead(null);
            this.launchCounter = -1;
        } else if (this.launchCounter > 100) {
            if (this.worldObj.rand.nextInt(6) != 0) {
                return;
            }

            this.worldObj.spawnParticle(EnumParticleTypes.FLAME, this.pos.getX() + 0.5, this.pos.getY() - 0.5, this.pos.getZ() + 0.5, 0.0d, 0.0d, 0.0d);
        } else {
            if (this.worldObj.rand.nextInt(1) != 0) {
                return;
            }

            if (this.launchCounter == 5) {
                this.releaseClamps();
            }

            this.worldObj.spawnParticle(EnumParticleTypes.CLOUD, this.pos.getX() + 0.5, this.pos.getY() - 0.85, this.pos.getZ() + 0.5, (this.worldObj.rand.nextFloat() * 0.25) - 0.125, 0, (this.worldObj.rand.nextFloat() * 0.25) - 0.125);
        }
    }
}
