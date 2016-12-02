package tv.dotstart.mc.icbm.common.block.entity.rocket;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import tv.dotstart.mc.icbm.common.block.entity.AbstractBlockEntity;

/**
 * Provides a block entity which is capable of animating its respective rendering on the client
 * side.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class LaunchClampBlockEntity extends AbstractBlockEntity implements ITickable {
    public static final float RELEASE_SPEED = (1f / 40); // 40 ticks

    private boolean released;
    private float releaseProgress;

    @Nonnegative
    public float getReleaseProgress() {
        return this.releaseProgress;
    }

    public boolean isReleased() {
        return this.released;
    }

    /**
     * Releases the clamps of this block.
     */
    public void release() {
        this.released = true;
        this.synchronize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        if (this.released) {
            this.releaseProgress = (float) Math.min(1.0, this.releaseProgress + RELEASE_SPEED);

            if (this.releaseProgress > 0.999) {
                this.released = false;
                this.releaseProgress = 1.0f;

                this.synchronize();
            }
        } else if (this.releaseProgress != 0) {
            this.releaseProgress = (float) Math.max(0.0, this.releaseProgress - RELEASE_SPEED);

            if (this.releaseProgress < 0.001) {
                this.releaseProgress = 0.0f;

                this.synchronize();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        compound.setByte("Released", (byte) (this.released ? 1 : 0));
        compound.setFloat("ReleaseProgress", this.releaseProgress);

        return super.writeToNBT(compound);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        super.readFromNBT(compound);

        this.released = compound.getByte("Released") == 1;
        this.releaseProgress = compound.getFloat("ReleaseProgress");
    }
}
