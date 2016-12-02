package tv.dotstart.mc.icbm.common.block.explosives;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import tv.dotstart.mc.icbm.ICBMModification;
import tv.dotstart.mc.icbm.common.block.AbstractBlockEntityBlock;
import tv.dotstart.mc.icbm.common.block.entity.explosives.ModularExplosiveBlockEntity;
import tv.dotstart.mc.icbm.common.creative.ICBMCreativeTab;
import tv.dotstart.mc.icbm.common.item.MetadataItem;
import tv.dotstart.mc.icbm.api.explosive.ModularExplosive;
import tv.dotstart.mc.icbm.api.explosive.ModularExplosiveCraftingComponent;

/**
 * Provides a block which provides a block representation of an explosive.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class ModularExplosiveBlock extends AbstractBlockEntityBlock<ModularExplosiveBlockEntity> {
    public static final ModularExplosiveBlock INSTANCE = new ModularExplosiveBlock();
    public static final Item ITEM = new Item(INSTANCE);

    private ModularExplosiveBlock() {
        super(ModularExplosiveBlockEntity.class, Material.TNT);

        this.setRegistryName(new ResourceLocation(ICBMModification.IDENTIFIER, "explosive"));
        this.setCreativeTab(ICBMCreativeTab.INSTANCE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        ItemStack stack = new ItemStack(ITEM, 1);
        ITEM.setExplosive(stack, this.getBlockEntity(worldIn, pos).map(ModularExplosiveBlockEntity::getExplosive).orElseGet(ModularExplosive::getDefault));
        spawnAsEntity(worldIn, pos, stack);

        super.breakBlock(worldIn, pos, state);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canDropFromExplosion(Explosion explosionIn) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<ItemStack> getDrops(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) {
        return Collections.emptyList();
    }

    /**
     * Retrieves the explosive of a certain block.
     */
    @Nonnull
    public ModularExplosive getExplosive(@Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        return this.getBlockEntity(world, pos).map(ModularExplosiveBlockEntity::getExplosive).orElseGet(ModularExplosive::getDefault);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
        ItemStack stack = new ItemStack(ITEM, 1);

        ModularExplosive explosive = this.getBlockEntity(world, pos).map(ModularExplosiveBlockEntity::getExplosive).orElseGet(ModularExplosive::getDefault);
        ITEM.setExplosive(stack, explosive);

        return stack;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nullable ItemStack heldItem, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
        if (heldItem != null && !worldIn.isRemote && (heldItem.getItem() == Items.FLINT_AND_STEEL || heldItem.getItem() == Items.FIRE_CHARGE)) {
            this.prime(worldIn, pos, state, playerIn);

            if (playerIn.capabilities.isCreativeMode) {
                return true;
            }

            if (heldItem.getItem() == Items.FLINT_AND_STEEL) {
                heldItem.damageItem(1, playerIn);
            } else {
                --heldItem.stackSize;
            }

            return true;
        }

        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBlockExploded(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Explosion explosion) {
        if (world.isRemote) {
            return;
        }

        this.prime(world, pos, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEntityCollidedWithBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entityIn) {
        super.onEntityCollidedWithBlock(worldIn, pos, state, entityIn);

        if (!entityIn.isBurning() || worldIn.isRemote) {
            return;
        }

        if (entityIn instanceof EntityArrow) {
            EntityArrow arrow = (EntityArrow) entityIn;
            this.prime(worldIn, pos, null, (arrow.shootingEntity instanceof EntityPlayer ? (EntityPlayer) arrow.shootingEntity : null));
        }
    }

    /**
     * Handles the priming of this explosive through redstone, flint and steel, fire charges or
     * flaming entities.
     */
    private void prime(@Nonnull World world, @Nonnull BlockPos pos, @Nullable IBlockState state, @Nullable EntityPlayer player) {
        this.getBlockEntity(world, pos).ifPresent((e) -> e.prime(player));
    }

    /**
     * Provides an item block which can be used as a crafting component.
     */
    public static final class Item extends ItemBlock implements MetadataItem, ModularExplosiveCraftingComponent {

        protected Item(@Nonnull ModularExplosiveBlock block) {
            super(block);

            this.setRegistryName(block.getRegistryName());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addInformation(@Nonnull ItemStack stack, @Nonnull EntityPlayer playerIn, @Nonnull List<String> tooltip, boolean advanced) {
            super.addInformation(stack, playerIn, tooltip, advanced);

            String key = this.getUnlocalizedName(stack) + ".description";
            String text = I18n.format(key);

            if (!key.equals(text)) {
                tooltip.add(text);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public ModularExplosive getExplosive(@Nonnull ItemStack itemStack) {
            return this.getCompound(itemStack).flatMap((c) -> {
                String explosiveName = c.getString("Explosive");

                if (explosiveName.isEmpty()) {
                    return Optional.empty();
                }

                return Optional.of(ModularExplosive.getRegistry().getValue(new ResourceLocation(explosiveName)));
            }).orElseGet(ModularExplosive::getDefault);
        }

        /**
         * Sets the explosive within the item stack.
         */
        public void setExplosive(@Nonnull ItemStack stack, @Nonnull ModularExplosive explosive) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setString("Explosive", explosive.getRegistryName().toString());

            stack.setTagCompound(compound);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void getSubItems(@Nonnull net.minecraft.item.Item itemIn, @Nullable CreativeTabs tab, @Nonnull List<ItemStack> subItems) {
            ModularExplosive.getRegistry().forEach((e) -> {
                NBTTagCompound compound = new NBTTagCompound();
                compound.setString("Explosive", e.getRegistryName().toString());

                ItemStack stack = new ItemStack(this, 1, 0);
                stack.setTagCompound(compound);

                subItems.add(stack);
            });
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public String getUnlocalizedName(@Nonnull ItemStack stack) {
            ModularExplosive explosive = this.getExplosive(stack);
            ResourceLocation location = explosive.getRegistryName();

            return "item." + location.getResourceDomain() + ".explosive." + location.getResourcePath();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean placeBlockAt(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, World world, @Nonnull BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
            if (super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)) {
                INSTANCE.getBlockEntity(world, pos).ifPresent((e) -> e.setExplosive(this.getExplosive(stack)));
                return true;
            }

            return false;
        }
    }
}
