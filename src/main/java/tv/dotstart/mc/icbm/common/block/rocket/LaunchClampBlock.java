package tv.dotstart.mc.icbm.common.block.rocket;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import tv.dotstart.mc.icbm.ICBMModification;
import tv.dotstart.mc.icbm.common.block.AbstractBlockEntityBlock;
import tv.dotstart.mc.icbm.common.block.entity.rocket.LaunchClampBlockEntity;
import tv.dotstart.mc.icbm.common.creative.ICBMCreativeTab;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class LaunchClampBlock extends AbstractBlockEntityBlock<LaunchClampBlockEntity> {
    private static final PropertyEnum<Part> PART = PropertyEnum.create("part", Part.class);
    private static final PropertyInteger FACING = PropertyInteger.create("facing", 0, 3);

    public static final LaunchClampBlock INSTANCE = new LaunchClampBlock();
    public static final Item ITEM = new ItemBlock(INSTANCE).setRegistryName(INSTANCE.getRegistryName());

    private LaunchClampBlock() {
        super(LaunchClampBlockEntity.class, Material.ROCK);

        this.setRegistryName(new ResourceLocation(ICBMModification.IDENTIFIER, "launch_clamp"));
        this.setUnlocalizedName("doticbm.launch_clamp");

        this.setDefaultState(this.blockState.getBaseState().withProperty(PART, Part.BLUEPRINT));
        this.setCreativeTab(ICBMCreativeTab.INSTANCE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canPlaceBlockAt(@Nonnull World worldIn, @Nonnull BlockPos pos) {
        IBlockState state = worldIn.getBlockState(pos.offset(EnumFacing.DOWN));

        if (state.getBlock() == this) {
            return worldIn.getBlockState(pos.offset(EnumFacing.DOWN, 2)).getBlock() == LaunchPadBlock.INSTANCE;
        }

        return state.getBlock() == LaunchPadBlock.INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, PART, FACING);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getActualState(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        BlockPos origin = BlockPos.ORIGIN;

        switch (state.getValue(PART)) {
            case FORMED:
                if (world.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock() == this) {
                    origin = new BlockPos(0, -1, 0);
                }
                break;
            case BLUEPRINT:
            case TOP_TE:
            case BOTTOM_TE:
                return state;
        }

        int facing = 2;

        if (LaunchPadBlock.INSTANCE.isMultiblockCenteredAt(world, pos.add(origin).add(1, -1, -1))) {
            facing = 1;
        } else if (LaunchPadBlock.INSTANCE.isMultiblockCenteredAt(world, pos.add(origin).add(-1, -1, -1))) {
            facing = 0;
        } else if (LaunchPadBlock.INSTANCE.isMultiblockCenteredAt(world, pos.add(origin).add(-1, -1, 1))) {
            facing = 3;
        }

        return state.withProperty(FACING, facing);
    }

    /**
     * Retrieves the state used to render the bottom state from within a TE.
     */
    @Nonnull
    public IBlockState getBottomState(@Nonnull IBlockState base) {
        return base.withProperty(PART, Part.BOTTOM_TE);
    }

    /**
     * Retrieves the facing direction of a block.
     */
    @Nonnegative
    @SideOnly(Side.CLIENT)
    public int getFacingByState(@Nonnull IBlockState state) {
        return state.getValue(FACING);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMetaFromState(@Nonnull IBlockState state) {
        return state.getValue(PART).ordinal() ^ (state.getValue(FACING) << 2);
    }

    /**
     * Retrieves the origin offset based on its block state.
     */
    @Nonnull
    @SideOnly(Side.CLIENT)
    public BlockPos getOriginOffsetByState(@Nonnull IBlockState state) {
        BlockPos pos = new BlockPos(-1, -1, -1);

        switch (this.getFacingByState(state)) {
            case 1:
                pos = new BlockPos(1, -1, -1);
                break;
            case 2:
                pos = new BlockPos(1, -1, 1);
                break;
            case 3:
                pos = new BlockPos(-1, -1, 1);
                break;
        }

        if (this.getPart(state) == Part.TOP_TE) {
            return pos.offset(EnumFacing.DOWN);
        }

        return pos;
    }

    /**
     * Retrieves the part the state represents.
     */
    @Nonnull
    public Part getPart(@Nonnull IBlockState state) {
        return state.getValue(PART);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public EnumBlockRenderType getRenderType(@Nonnull IBlockState state) {
        Part part = state.getValue(PART);

        return (part == Part.BLUEPRINT || part == Part.FORMED ? EnumBlockRenderType.INVISIBLE : EnumBlockRenderType.MODEL);
    }

    /**
     * Retrieves the state used to render the top state from within a TE.
     */
    @Nonnull
    public IBlockState getTopState(@Nonnull IBlockState base) {
        return base.withProperty(PART, Part.TOP_TE);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        int part = meta & 0x3;

        Part[] parts = Part.values();

        if (part >= parts.length) {
            return this.getDefaultState();
        }

        return this.blockState.getBaseState().withProperty(PART, parts[part]);
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
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state) {
        return false;
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
    public void onBlockPlacedBy(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase placer, @Nonnull ItemStack stack) {
        if (worldIn.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock() == this) {
            worldIn.setBlockState(pos, this.blockState.getBaseState().withProperty(PART, Part.FORMED));
            worldIn.setBlockState(pos.offset(EnumFacing.DOWN), this.blockState.getBaseState().withProperty(PART, Part.FORMED));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
        super.neighborChanged(state, worldIn, pos, blockIn);

        if (!this.canPlaceBlockAt(worldIn, pos)) {
            this.dropBlockAsItem(worldIn, pos, worldIn.getBlockState(pos), 0);
            worldIn.setBlockToAir(pos);
        }
    }

    /**
     * Provides a list of possible parts a block instance can represent within the world.
     */
    public enum Part implements IStringSerializable {
        BLUEPRINT,
        FORMED,
        BOTTOM_TE,
        TOP_TE;


        /**
         * {@inheritDoc}
         */
        @Override
        public String getName() {
            return this.name().toLowerCase();
        }
    }
}
