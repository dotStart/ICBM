package tv.dotstart.mc.icbm.common.block.rocket;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import tv.dotstart.mc.icbm.ICBMModification;
import tv.dotstart.mc.icbm.common.block.AbstractBlockEntityBlock;
import tv.dotstart.mc.icbm.common.block.entity.rocket.LaunchIgnitionBlockEntity;
import tv.dotstart.mc.icbm.common.creative.ICBMCreativeTab;
import tv.dotstart.mc.icbm.common.item.RocketItem;
import tv.dotstart.mc.icbm.api.explosive.ModularExplosive;

/**
 * Provides a block used for "guiding" rockets off the launch pad.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class LaunchIgnitionBlock extends AbstractBlockEntityBlock<LaunchIgnitionBlockEntity> {
    public static final LaunchIgnitionBlock INSTANCE = new LaunchIgnitionBlock();
    public static final Item ITEM = new ItemBlock(INSTANCE).setRegistryName(INSTANCE.getRegistryName());

    private LaunchIgnitionBlock() {
        super(LaunchIgnitionBlockEntity.class, Material.IRON);

        this.setRegistryName(new ResourceLocation(ICBMModification.IDENTIFIER, "launch_ignition"));
        this.setCreativeTab(ICBMCreativeTab.INSTANCE);

        this.setUnlocalizedName("doticbm.launch_ignition");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canPlaceBlockAt(@Nonnull World worldIn, @Nonnull BlockPos pos) {
        return LaunchPadBlock.INSTANCE.isMultiblockCenteredAt(worldIn, pos.offset(EnumFacing.DOWN));
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
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (heldItem != null) {
            return false;
        }

        LaunchIgnitionBlockEntity e = this.getBlockEntity(worldIn, pos).orElseThrow(() -> new IllegalStateException("Could not locate corresponding block entity for launch ignition at x=" + pos.getX() + ",y=" + pos.getY() + ",z=" + pos.getZ()));
        ModularExplosive warhead = e.getWarhead().orElse(null);

        if (warhead == null) {
            return false;
        }

        ItemStack stack = new ItemStack(RocketItem.INSTANCE, 1);
        RocketItem.INSTANCE.setExplosive(stack, warhead);

        playerIn.setHeldItem(hand, stack);
        e.setWarhead(null);

        return true;
    }

    /**
     * Handles the destruction of the launch pad below a guidance block.
     */
    public void onLaunchPadDestroyed(@Nonnull World world, @Nonnull BlockPos pos) {
        this.dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
        world.setBlockToAir(pos);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn) {
        if (world.getBlockState(pos).getBlock() == this && !this.canPlaceBlockAt(world, pos)) {
            this.onLaunchPadDestroyed(world, pos);
        }
    }
}
