package tv.dotstart.mc.icbm.common.entity.explosives.gravity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import javax.annotation.Signed;

import tv.dotstart.mc.icbm.common.CustomDataSerializers;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class AntiGravityBlockEntity extends Entity {
    private static final DataParameter<BlockPos[]> RELATIVE_POSITIONS = EntityDataManager.createKey(AntiGravityBlockEntity.class, CustomDataSerializers.BLOCKPOS_ARRAY);
    private static final DataParameter<Integer[]> STATES = EntityDataManager.createKey(AntiGravityBlockEntity.class, CustomDataSerializers.INTEGER_ARRAY);

    private BlockPos[] relativePositions;
    private IBlockState[] states;
    private int lifetime;
    private boolean maySettle = true;

    public AntiGravityBlockEntity(@Nonnull World world, @Nonnull BlockPos[] relativePositions, @Nonnull IBlockState[] state) {
        super(world);

        this.relativePositions = relativePositions;
        this.states = state;

        this.dataManager.register(RELATIVE_POSITIONS, this.relativePositions);
        this.dataManager.register(STATES, this.serializeBlockStates());
    }

    public AntiGravityBlockEntity(@Nonnull World world) {
        this(world, new BlockPos[]{new BlockPos(0, 0, 0)}, new IBlockState[]{Blocks.STONE.getDefaultState()});
    }

    public AntiGravityBlockEntity(@Nonnull World world, @Signed double x, @Signed double y, @Signed double z, @Nonnull BlockPos[] relativePositions, @Nonnull IBlockState[] state) {
        this(world, relativePositions, state);
        this.setPosition(x, y, z);
    }

    @Nonnull
    private Integer[] serializeBlockStates() {
        Integer[] states = new Integer[this.states.length];

        for (int i = 0; i < this.states.length; ++i) {
            states[i] = Block.getStateId(this.states[i]);
        }

        return states;
    }

    private void deserializeBlockStates(@Nonnull Integer[] states) {
        IBlockState[] state = new IBlockState[states.length];

        for (int i = 0; i < state.length; ++i) {
            state[i] = Block.getStateById(states[i]);
        }

        this.states = state;
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
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (!this.func_189652_ae()) {
            this.motionY -= 0.08D;
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);

        if (this.maySettle && ++this.lifetime > 60) {
            if (this.onGround) {
                this.setDead();
            } else if (this.motionY <= 0.1) {
                this.setDead();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("deprecation")
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (RELATIVE_POSITIONS.equals(key)) {
            this.relativePositions = this.dataManager.get(RELATIVE_POSITIONS);
        } else if (STATES.equals(key)) {
            this.deserializeBlockStates(this.dataManager.get(STATES));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("deprecation")
    protected void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
        this.maySettle = compound.getInteger("MaySettle") == 1;

        NBTTagList relativePositions = compound.getTagList("RelativePositions", Constants.NBT.TAG_LONG);
        this.relativePositions = new BlockPos[relativePositions.tagCount()];

        for (int i = 0; i < relativePositions.tagCount(); ++i) {
            this.relativePositions[i] = BlockPos.fromLong(((NBTTagLong) relativePositions.get(i)).getLong());
        }

        this.deserializeBlockStates(ArrayUtils.toObject(compound.getIntArray("States")));
    }

    /**
     * Sets whether this entity instance is allowed to settle (e.g. turn back into a full block).
     */
    public void setMaySettle(boolean maySettle) {
        this.maySettle = maySettle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeEntityToNBT(@Nonnull NBTTagCompound compound) {
        NBTTagList relativePositions = new NBTTagList();

        for (BlockPos pos : this.relativePositions) {
            relativePositions.appendTag(new NBTTagLong(pos.toLong()));
        }

        compound.setTag("RelativePositions", relativePositions);
        compound.setInteger("MaySettle", this.maySettle ? 1 : 0);
        compound.setIntArray("States", ArrayUtils.toPrimitive(this.serializeBlockStates()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDead() {
        super.setDead();

        if (!this.maySettle) {
            return;
        }

        for (int i = 0; i < this.relativePositions.length; ++i) {
            BlockPos relativePosition = this.relativePositions[i];
            IBlockState state = this.states[i];

            this.worldObj.setBlockState(this.getPosition().add(relativePosition), state);
        }
    }

    @Nonnull
    public BlockPos[] getRelativePositions() {
        return this.relativePositions;
    }

    @Nonnull
    public IBlockState[] getStates() {
        return this.states;
    }
}
