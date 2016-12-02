package tv.dotstart.mc.icbm.common.block.entity.explosives;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import tv.dotstart.mc.icbm.common.block.explosives.ModularExplosiveBlock;
import tv.dotstart.mc.icbm.common.entity.explosives.ModularExplosiveEntity;
import tv.dotstart.mc.icbm.api.explosive.ModularExplosive;

/**
 * Stores information regarding the explosive which is contained in blocks of type {@link
 * ModularExplosiveBlock}.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class ModularExplosiveBlockEntity extends TileEntity {
    private ModularExplosive explosive = ModularExplosive.getDefault();

    @Nonnull
    public ModularExplosive getExplosive() {
        return this.explosive;
    }

    public void setExplosive(@Nonnull ModularExplosive explosive) {
        this.explosive = explosive;

        this.markDirty();
        this.worldObj.notifyBlockUpdate(this.pos, this.worldObj.getBlockState(this.pos), this.worldObj.getBlockState(this.pos), 3);
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.getPos(), 0, this.getUpdateTag());
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    /**
     * Primes the block (e.g. spawns a primed entity and deletes this block).
     */
    public void prime(@Nonnull EntityLivingBase placedBy) {
        if (!this.worldObj.isRemote) {
            double motionX = this.worldObj.rand.nextFloat() * 0.125;
            double motionY = this.worldObj.rand.nextFloat() * 0.3;
            double motionZ = this.worldObj.rand.nextFloat() * 0.125;

            ModularExplosiveEntity entity = new ModularExplosiveEntity(this.worldObj, this.pos.getX() + 0.5, this.pos.getY(), this.pos.getZ() + 0.5, placedBy, (short) 80, this.blockType, this.explosive);
            entity.motionX = motionX;
            entity.motionY = motionY;
            entity.motionZ = motionZ;
            this.worldObj.spawnEntityInWorld(entity);

            this.worldObj.setBlockToAir(this.pos);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        super.readFromNBT(compound);

        this.explosive = ModularExplosive.getRegistry().getValue(new ResourceLocation(compound.getString("Explosive")));
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        compound.setString("Explosive", this.explosive.getRegistryName().toString());

        return super.writeToNBT(compound);
    }
}
