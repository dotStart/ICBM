package tv.dotstart.mc.icbm.common.block.rocket;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import tv.dotstart.mc.icbm.ICBMModification;
import tv.dotstart.mc.icbm.common.creative.ICBMCreativeTab;

/**
 * Provides a block which acts as a base for launching rockets.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class LaunchPadBlock extends Block {
    public static final PropertyEnum<PadMaterial> MATERIAL = PropertyEnum.create("material", PadMaterial.class);
    public static final PropertyInteger DAMAGE = PropertyInteger.create("damage", 0, 3);

    private static final AxisAlignedBB BLOCK_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);

    public static final LaunchPadBlock INSTANCE = new LaunchPadBlock();
    public static final Item ITEM = new Item(INSTANCE);

    private LaunchPadBlock() {
        super(Material.GROUND);

        this.setRegistryName(new ResourceLocation(ICBMModification.IDENTIFIER, "launch_pad"));
        this.setDefaultState(this.blockState.getBaseState().withProperty(MATERIAL, PadMaterial.STONE).withProperty(DAMAGE, 0));
        this.setUnlocalizedName("launch_pad");

        this.setCreativeTab(ICBMCreativeTab.INSTANCE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canPlaceBlockAt(@Nonnull World worldIn, @Nonnull BlockPos pos) {
        return worldIn.isSideSolid(pos.offset(EnumFacing.DOWN), EnumFacing.UP);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, MATERIAL, DAMAGE);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        Block block = source.getBlockState(pos.offset(EnumFacing.UP)).getBlock();

        if (block == LaunchIgnitionBlock.INSTANCE || block == LaunchClampBlock.INSTANCE) {
            return FULL_BLOCK_AABB;
        }

        return BLOCK_AABB;
    }

    /**
     * Retrieves the current block damage.
     */
    @Nonnegative
    public int getDamage(@Nonnull IBlockState state) {
        return state.getValue(DAMAGE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int damageDropped(@Nonnull IBlockState state) {
        return this.getMetaFromState(state);
    }

    /**
     * Retrieves the pad material.
     */
    @Nonnull
    public PadMaterial getPadMaterial(@Nonnull IBlockState state) {
        return state.getValue(MATERIAL);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        int ordinal = meta & 3;
        int damage = (meta >>> 2);

        if (ordinal == 3) {
            // FIXME: This needs to be updated whenever a new material is added to the mix
            // There is currently space for exactly one more material
            return this.getDefaultState();
        }

        return this.blockState.getBaseState()
                .withProperty(MATERIAL, PadMaterial.values()[ordinal])
                .withProperty(DAMAGE, damage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getSubBlocks(@Nonnull net.minecraft.item.Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        for (PadMaterial material : PadMaterial.values()) {
            list.add(new ItemStack(itemIn, 1, material.ordinal()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMetaFromState(@Nonnull IBlockState state) {
        return (state.getValue(MATERIAL).ordinal() & 3) ^ (state.getValue(DAMAGE) << 1);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
        return new ItemStack(ITEM, 1, this.getMetaFromState(state));
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
     * Checks whether the multiblock is centered at the specified position.
     */
    public boolean isMultiblockCenteredAt(@Nonnull IBlockAccess world, @Nonnull BlockPos position) {
        IBlockState originState = world.getBlockState(position);

        if (originState.getBlock() != this) {
            return false;
        }

        for (int x = -1; x <= 1; ++x) {
            for (int z = -1; z <= 1; ++z) {
                if (x == z && x == 0) {
                    continue;
                }

                IBlockState state = world.getBlockState(position.add(x, 0, z));

                if (state.getBlock() != this || state.getValue(MATERIAL) != originState.getValue(MATERIAL)) {
                    return false;
                }
            }
        }

        return true;
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
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
        boolean broken = false;

        if (state.getBlock() == this && !this.canPlaceBlockAt(worldIn, pos)) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);

            broken = true;
        }

        if (broken || !this.isMultiblockCenteredAt(worldIn, pos)) {
            BlockPos abovePosition = pos.offset(EnumFacing.UP);
            IBlockState above = worldIn.getBlockState(abovePosition);

            if (above.getBlock() == LaunchIgnitionBlock.INSTANCE) {
                LaunchIgnitionBlock.INSTANCE.onLaunchPadDestroyed(worldIn, abovePosition);
            }
        }
    }

    /**
     * Provides an item representation for launch pads.
     */
    public static class Item extends ItemBlock {

        private Item(@Nonnull Block block) {
            super(block);

            this.setRegistryName(block.getRegistryName());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getMetadata(int damage) {
            return damage & 3;
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public String getUnlocalizedName(@Nonnull ItemStack stack) {
            return "item.doticbm.launch_pad." + PadMaterial.values()[this.getMetadata(stack.getItemDamage())].getName();
        }
    }

    /**
     * Provides a list of valid launch pad variables.
     */
    public enum PadMaterial implements IStringSerializable {
        STONE(8),
        BRICK(16),
        OBSIDIAN(32);

        private int durability;

        PadMaterial(@Nonnegative int durability) {
            this.durability = durability;
        }

        /**
         * Retrieves the durability of the material.
         *
         * The durability is calculated by chance of applying damage to the pad while every pad can
         * endure 4 instances of damage at a time regardless of its durability.
         *
         * The damage is applied whenever a random value between 0 and the durability value is equal
         * to 0. The higher the durability, the less likely it is to take damage.
         */
        @Nonnegative
        public int getDurability() {
            return this.durability;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getName() {
            return this.name().toLowerCase();
        }
    }
}
