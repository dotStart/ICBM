package tv.dotstart.mc.icbm.common.entity.explosives.gravity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Signed;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class AntiGravitySourceEntity extends Entity {
    private static final DataParameter<Integer> LIFETIME = EntityDataManager.createKey(AntiGravitySourceEntity.class, DataSerializers.VARINT);
    private static final EnumFacing[] BLOCK_FACES = new EnumFacing[]{
            EnumFacing.UP,
            EnumFacing.NORTH,
            EnumFacing.EAST,
            EnumFacing.SOUTH,
            EnumFacing.WEST,
            EnumFacing.DOWN
    };
    private static final double RADIUS = 25;

    private int lifetime;

    public AntiGravitySourceEntity(World worldIn) {
        super(worldIn);

        this.setSize(0, 0);

        this.lifetime = 6000;
        this.dataManager.register(LIFETIME, 6000);
    }

    public AntiGravitySourceEntity(@Nonnull World world, @Signed double x, @Signed double y, @Signed double z) {
        this(world);
        this.setPosition(x, y, z);
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
        if (this.lifetime != 0 && --this.lifetime > 0) {
            AxisAlignedBB boundingBox = new AxisAlignedBB(this.posX - RADIUS, this.posY - RADIUS, this.posZ - RADIUS, this.posX + RADIUS, this.posY + RADIUS, this.posZ + RADIUS);
            this.worldObj.getEntitiesWithinAABB(Entity.class, boundingBox, (e) -> {
                if (e instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) e;

                    if (player.capabilities.isFlying) {
                        return false;
                    }
                }

                return !(e instanceof AntiGravitySourceEntity);
            }).forEach((e) -> e.motionY += .1);

            if (this.rand.nextInt(8) == 0) {
                for (int x = (int) -RADIUS; x <= RADIUS; x += 5) {
                    for (int z = (int) -RADIUS; z <= RADIUS; z += 5) {
                        if (this.rand.nextInt(4) != 0) {
                            continue;
                        }

                        this.worldObj.spawnParticle(EnumParticleTypes.DRAGON_BREATH, this.posX + x + this.rand.nextFloat() * 0.5f, this.posY + 0.8D, this.posZ + z + this.rand.nextFloat() * 0.5f, 0.0D, 0.2D, 0.0D);
                    }
                }
            }

            if (this.rand.nextInt(24) == 0 && !this.worldObj.isRemote) {
                int x = (int) this.posX + this.rand.nextInt((int) RADIUS * 2) - (int) RADIUS;
                int z = (int) this.posZ + this.rand.nextInt((int) RADIUS * 2) - (int) RADIUS;
                BlockPos pos = null;

                for (int y = (int) RADIUS; y >= (int) -RADIUS; --y) {
                    pos = new BlockPos(x, this.posY + y, z);

                    if (!this.worldObj.isAirBlock(pos)) {
                        break;
                    }
                }

                if (pos == null) {
                    return;
                }

                Set<BlockPos> relativePositions = new HashSet<>();
                IBlockState[] states = new IBlockState[24 + this.rand.nextInt(80)];
                this.worldObj.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);

                relativePositions.add(new BlockPos(0, 0, 0));
                states[0] = this.worldObj.getBlockState(pos);
                this.fillConnections(this.worldObj, pos, pos, relativePositions, states);

                if (relativePositions.size() < states.length) {
                    IBlockState[] tmp = new IBlockState[relativePositions.size()];
                    System.arraycopy(states, 0, tmp, 0, relativePositions.size());

                    states = tmp;
                }

                BlockPos[] relativePositionsArray = new BlockPos[states.length];
                relativePositions.toArray(relativePositionsArray);

                AntiGravityBlockEntity entity = new AntiGravityBlockEntity(this.worldObj, pos.getX(), pos.getY(), pos.getZ(), relativePositionsArray, states);
                this.worldObj.spawnEntityInWorld(entity);
            }
        } else {
            this.setDead();
        }
    }

    private void fillConnections(@Nonnull World world, @Nonnull BlockPos originalOrigin, @Nonnull BlockPos origin, @Nonnull Set<BlockPos> relativePositions, @Nonnull IBlockState[] states) {
        BlockPos[] neighbors = this.getNeighbors(world, origin);

        for (BlockPos pos : neighbors) {
            if (pos == null) {
                break;
            }

            BlockPos relativePosition = originalOrigin.subtract(pos);

            if (states.length == relativePositions.size()) {
                return;
            }

            if (relativePositions.add(relativePosition)) {
                states[relativePositions.size() - 1] = world.getBlockState(pos);
                this.worldObj.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
            }
        }

        for (BlockPos pos : neighbors) {
            if (pos == null) {
                break;
            }

            if (this.rand.nextInt(4) != 0) {
                continue;
            }

            if (states.length == relativePositions.size()) {
                return;
            }

            this.fillConnections(world, originalOrigin, pos, relativePositions, states);
        }
    }

    @Nonnull
    private BlockPos[] getNeighbors(@Nonnull World world, @Nonnull BlockPos origin) {
        BlockPos[] positions = new BlockPos[6];
        int i = 0;

        for (EnumFacing facing : BLOCK_FACES) {
            BlockPos pos = origin.offset(facing);

            if (!world.isAirBlock(pos) && world.getBlockState(pos).getBlock() != Blocks.BEDROCK) {
                positions[i++] = pos;
            }
        }

        return positions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
        this.setLifetime(compound.getInteger("lifetime"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeEntityToNBT(@Nonnull NBTTagCompound compound) {
        compound.setInteger("lifetime", this.lifetime);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SideOnly(Side.CLIENT)
    public boolean canRenderOnFire() {
        return false;
    }

    @Nonnegative
    public int getLifetime() {
        return this.lifetime;
    }

    public void setLifetime(@Nonnegative int lifetime) {
        this.dataManager.set(LIFETIME, lifetime);
        this.lifetime = lifetime;
    }
}
