package tv.dotstart.mc.icbm.common.entity.explosives;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Signed;

import tv.dotstart.mc.icbm.api.explosive.ModularExplosive;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class ModularExplosiveEntity extends Entity {
    private static final DataParameter<Integer> FUSE = EntityDataManager.createKey(ModularExplosiveEntity.class, DataSerializers.VARINT);
    private static final DataParameter<String> BASE_BLOCK = EntityDataManager.createKey(ModularExplosiveEntity.class, DataSerializers.STRING);
    private static final DataParameter<String> EXPLOSIVE = EntityDataManager.createKey(ModularExplosiveEntity.class, DataSerializers.STRING);

    private EntityLivingBase placedBy;
    private ModularExplosive explosive;
    private Block baseBlock;
    private short fuse;

    public ModularExplosiveEntity(@Nonnull World world, @Nonnegative short fuse, @Nonnull Block baseBlock, @Nonnull ModularExplosive explosive) {
        super(world);

        this.fuse = fuse;
        this.preventEntitySpawning = true;
        this.baseBlock = baseBlock;
        this.explosive = explosive;
        this.setSize(0.98F, 0.98F);

        this.dataManager.register(FUSE, (int) this.fuse);
        this.dataManager.register(BASE_BLOCK, this.baseBlock.getRegistryName().toString());
        this.dataManager.register(EXPLOSIVE, this.explosive.getRegistryName().toString());
    }

    public ModularExplosiveEntity(@Nonnull World world) {
        this(world, (short) 80, Blocks.TNT, ModularExplosive.getDefault());
    }

    public ModularExplosiveEntity(@Nonnull World world, @Signed double x, @Signed double y, @Signed double z, @Nullable EntityLivingBase placedBy, @Nonnegative short fuse, @Nonnull Block block, @Nonnull ModularExplosive explosive) {
        this(world, fuse, block, explosive);

        this.setPosition(x, y, z);
        this.placedBy = placedBy;
    }

    /**
     * Handles the explosion at the end of the fuse.
     */
    public void explode() {
        this.explosive.spawnEffects(this.worldObj, this.posX, this.posY, this.posZ, this.placedBy);
        this.explosive.explode(this.worldObj, this.posX, this.posY, this.posZ, this.placedBy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void entityInit() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdate() {
        super.onUpdate();

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (!this.func_189652_ae()) {
            this.motionY -= 0.03999999910593033D;
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;

        if (this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
            this.motionY *= -0.5D;
        }

        if (this.fuse == 0 || --this.fuse <= 0) {
            this.setDead();
            this.explode();
        } else {
            this.handleWaterMovement();
            this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (FUSE.equals(key)) {
            this.fuse = (short) (int) this.dataManager.get(FUSE);
        } else if (BASE_BLOCK.equals(key)) {
            this.baseBlock = Block.getBlockFromName(this.dataManager.get(BASE_BLOCK));
        } else if (EXPLOSIVE.equals(key)) {
            this.explosive = ModularExplosive.getRegistry().getValue(new ResourceLocation(this.dataManager.get(EXPLOSIVE)));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
        this.fuse = compound.getShort("fuse");
        this.baseBlock = Block.getBlockFromName(compound.getString("blockType"));
        this.explosive = ModularExplosive.getRegistry().getValue(new ResourceLocation(compound.getString("explosive")));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeEntityToNBT(@Nonnull NBTTagCompound compound) {
        compound.setShort("fuse", this.fuse);
        compound.setString("blockType", this.baseBlock.getRegistryName().toString());
        compound.setString("explosive", this.explosive.getRegistryName().toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Nonnull
    public ModularExplosive getExplosive() {
        return this.explosive;
    }

    @Nonnegative
    public short getFuse() {
        return this.fuse;
    }

    @Nonnull
    public Block getBaseBlock() {
        return this.baseBlock;
    }

    public void setBaseBlock(@Nonnull Block baseBlock) {
        this.dataManager.set(BASE_BLOCK, baseBlock.getRegistryName().toString());
        this.baseBlock = baseBlock;
    }

    public void setFuse(@Nonnegative short fuse) {
        this.dataManager.set(FUSE, (int) this.fuse);
        this.fuse = fuse;
    }
}
