package tv.dotstart.mc.icbm.common.entity.rocket;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Signed;

import tv.dotstart.mc.icbm.ICBMModification;
import tv.dotstart.mc.icbm.explosive.ModularExplosive;

/**
 * Provides an entity which handles the behavior of a ballistic missile which carries a certain type
 * of warhead.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class RocketEntity extends Entity {
    private static final int SWITCH_ALTITUDE = 512;

    private static final DataParameter<Integer> PHASE = EntityDataManager.createKey(RocketEntity.class, DataSerializers.VARINT);
    private static final DataParameter<String> WARHEAD = EntityDataManager.createKey(RocketEntity.class, DataSerializers.STRING);

    private EntityLivingBase launchedBy;
    private ModularExplosive warhead;
    private BlockPos target;
    private Phase phase = Phase.LAUNCH;

    private ForgeChunkManager.Ticket ticket;

    public RocketEntity(@Nonnull World world) {
        this(world, null, ModularExplosive.getDefault(), BlockPos.ORIGIN);
    }

    public RocketEntity(@Nonnull World world, @Nullable EntityLivingBase launchedBy, @Nonnull ModularExplosive warhead, @Nonnull BlockPos target) {
        super(world);

        this.setSize(0.98F, 0.98F);
        this.launchedBy = launchedBy;
        this.warhead = warhead;
        this.target = target;

        this.dataManager.register(PHASE, this.phase.ordinal());
        this.dataManager.register(WARHEAD, this.warhead.getRegistryName().toString());
    }

    public RocketEntity(@Nonnull World world, @Signed double x, @Signed double y, @Signed double z, @Nullable EntityLivingBase launchedBy, @Nonnull ModularExplosive warhead, @Nonnull BlockPos target) {
        this(world, launchedBy, warhead, target);

        this.setPosition(x, y, z);
    }

    /**
     * Triggers the rocket to explode at its current position.
     */
    public void explode() {
        this.warhead.spawnEffects(this.worldObj, this.posX, this.posY, this.posZ, this.launchedBy);
        this.warhead.explode(this.worldObj, this.posX, this.posY, this.posZ, this.launchedBy);

        // don't forget to kill the entity ... otherwise you might end up crashing your world
        // through anti-gravity by ripping it to shreds ...
        this.setDead();
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

        if (!this.worldObj.isRemote && this.ticket == null) {
            this.forceLoadChunks(this.getPosition());
        }

        switch (this.phase) {
            case LAUNCH:
                if (this.posY >= SWITCH_ALTITUDE) {
                    this.phase = Phase.DESTROY;

                    if (!this.worldObj.isRemote) {
                        ForgeChunkManager.releaseTicket(this.ticket);

                        this.dataManager.set(PHASE, this.phase.ordinal());
                        this.setPositionAndUpdate(this.target.getX(), SWITCH_ALTITUDE, this.target.getZ());

                        this.forceLoadChunks(new BlockPos(this.target.getX(), SWITCH_ALTITUDE, this.target.getZ()));
                    }
                } else {
                    this.motionY = Math.min(1.0, this.ticksExisted / 120.0f) * 2f;
                    this.moveEntity(this.motionX, this.motionY, this.motionZ);
                }
                break;
            case DESTROY:
                this.motionY = -2;
                this.moveEntity(this.motionX, this.motionY, this.motionZ);

                if (this.onGround && !this.worldObj.isRemote) {
                    this.explode();
                }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDead() {
        super.setDead();

        if (this.ticket != null) {
            ForgeChunkManager.releaseTicket(this.ticket);
        }
    }

    /**
     * Forcefully loads chunks in proximity to the rocket to assure its effects are immediate.
     */
    private void forceLoadChunks(@Nonnull BlockPos position) {
        this.ticket = ForgeChunkManager.requestTicket(ICBMModification.getInstance(), this.worldObj, ForgeChunkManager.Type.ENTITY);
        this.ticket.bindEntity(this);

        ChunkPos pos = new ChunkPos(this.getPosition());
        ForgeChunkManager.forceChunk(this.ticket, pos);

        for (int x = -2; x <= 2; ++x) {
            for (int z = -2; z <= 2; ++z) {
                ForgeChunkManager.forceChunk(this.ticket, new ChunkPos(pos.chunkXPos + x, pos.chunkZPos + z));
            }
        }
    }

    /**
     * Retrieves the launch phase this rocket is currently in.
     */
    @Nonnull
    public Phase getPhase() {
        return this.phase;
    }

    /**
     * Retrieves the warhead this rocket is currently equipped with.
     */
    @Nonnull
    public ModularExplosive getWarhead() {
        return this.warhead;
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
    protected void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
        this.warhead = ModularExplosive.getRegistry().getValue(new ResourceLocation(compound.getString("Warhead")));
        this.target = BlockPos.fromLong(compound.getLong("Target"));
        this.phase = Phase.valueOf(compound.getString("Phase"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeEntityToNBT(@Nonnull NBTTagCompound compound) {
        compound.setString("Warhead", this.warhead.getRegistryName().toString());
        compound.setLong("Target", this.target.toLong());
        compound.setString("Phase", this.phase.name());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyDataManagerChange(@Nonnull DataParameter<?> key) {
        if (PHASE.equals(key)) {
            this.phase = Phase.values()[(this.dataManager.get(PHASE))];
        } else if (WARHEAD.equals(key)) {
            this.warhead = ModularExplosive.getRegistry().getValue(new ResourceLocation(this.dataManager.get(WARHEAD)));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    /**
     * Provides a list of valid rocket phases.
     */
    public enum Phase {
        LAUNCH,

        /**
         * Hasta La Vista Baby
         */
        DESTROY
    }
}
