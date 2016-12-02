package tv.dotstart.mc.icbm.common.block.rocket;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import tv.dotstart.mc.icbm.ICBMModification;
import tv.dotstart.mc.icbm.common.block.entity.rocket.LaunchControlBlockEntity;
import tv.dotstart.mc.icbm.common.block.entity.rocket.LaunchIgnitionBlockEntity;
import tv.dotstart.mc.icbm.common.creative.ICBMCreativeTab;
import tv.dotstart.mc.icbm.common.gui.GUIIdentifier;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class LaunchControlBlock extends Block implements ITileEntityProvider {
    private static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class, EnumFacing.HORIZONTALS);
    private static final AxisAlignedBB BLOCK_BB = new AxisAlignedBB(0.0, 0.3, 0.0, 1.0, 0.7, 1.0);

    public static final LaunchControlBlock INSTANCE = new LaunchControlBlock();
    public static final Item ITEM = new ItemBlock(INSTANCE).setRegistryName(INSTANCE.getRegistryName());

    protected LaunchControlBlock() {
        super(Material.IRON);

        this.setRegistryName(new ResourceLocation(ICBMModification.IDENTIFIER, "launch_control"));

        this.setUnlocalizedName("doticbm.launch_control");
        this.setCreativeTab(ICBMCreativeTab.INSTANCE);

        this.translucent = true;
        this.fullBlock = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canPlaceBlockAt(@Nonnull World worldIn, @Nonnull BlockPos pos) {
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            if (worldIn.getBlockState(pos.offset(facing)).getBlock() == LaunchClampBlock.INSTANCE) {
                if (worldIn.getBlockState(pos.offset(facing.getOpposite())).getBlock() != LaunchClampBlock.INSTANCE) {
                    return false;
                }

                return worldIn.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock() == LaunchPadBlock.INSTANCE;
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
        return new LaunchControlBlockEntity();
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BLOCK_BB;
    }

    /**
     * Retrieves the ignition system this controller is directly connected to.
     */
    @Nonnull
    public Optional<LaunchIgnitionBlockEntity> getConnectedIgnition(@Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        IBlockState state = world.getBlockState(pos);

        if (state.getBlock() != this) {
            return Optional.empty();
        }

        return Optional.ofNullable(world.getTileEntity(pos.offset(state.getValue(FACING).getOpposite())))
                .filter((e) -> e instanceof LaunchIgnitionBlockEntity)
                .map((e) -> (LaunchIgnitionBlockEntity) e);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        if (meta >= EnumFacing.HORIZONTALS.length) {
            return this.blockState.getBaseState();
        }

        return this.blockState.getBaseState().withProperty(FACING, EnumFacing.HORIZONTALS[meta]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMetaFromState(@Nonnull IBlockState state) {
        return state.getValue(FACING).ordinal();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBlockSolid(IBlockAccess worldIn, @Nonnull BlockPos pos, EnumFacing side) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        playerIn.openGui(ICBMModification.getInstance(), GUIIdentifier.LAUNCH_CONTROL.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
        if (state.getBlock() != this) {
            return;
        }

        if (!this.canPlaceBlockAt(worldIn, pos)) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
            return;
        }

        this.updateFacingDirection(worldIn, pos);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

        worldIn.setBlockState(pos, this.updateFacingDirection(worldIn, pos), 7);
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.updateFacingDirection(worldIn, pos);
    }

    /**
     * Processes an update to the facing direction of a block.
     */
    private IBlockState updateFacingDirection(@Nonnull World world, @Nonnull BlockPos pos) {
        EnumFacing facing = EnumFacing.EAST;

        for (EnumFacing f : EnumFacing.HORIZONTALS) {
            if (world.getBlockState(pos.offset(f)).getBlock() == LaunchIgnitionBlock.INSTANCE) {
                facing = f;
                break;
            }
        }

        return this.blockState.getBaseState().withProperty(FACING, facing.getOpposite());
    }
}
