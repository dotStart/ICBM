package tv.dotstart.mc.icbm.common.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import javax.annotation.Nonnull;

import tv.dotstart.mc.icbm.ICBMModification;
import tv.dotstart.mc.icbm.common.block.rocket.LaunchIgnitionBlock;
import tv.dotstart.mc.icbm.common.creative.ICBMCreativeTab;
import tv.dotstart.mc.icbm.explosive.ModularExplosive;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class RocketItem extends Item {
    public static final RocketItem INSTANCE = new RocketItem();

    private RocketItem() {
        super();

        this.setRegistryName(new ResourceLocation(ICBMModification.IDENTIFIER, "rocket"));

        this.setUnlocalizedName("rocket");
        this.setCreativeTab(ICBMCreativeTab.INSTANCE);
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
     * Retrieves the explosive a rocket item is equipped with (falls back to the system default
     * explosive if the explosive becomes invalid).
     */
    @Nonnull
    public ModularExplosive getExplosive(@Nonnull ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();

        if (compound == null || !compound.hasKey("Warhead")) {
            return ModularExplosive.getDefault();
        }

        return ModularExplosive.getRegistry().getValue(new ResourceLocation(stack.getTagCompound().getString("Warhead")));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getHasSubtypes() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        ModularExplosive.getRegistry().forEach((e) -> {
            ItemStack stack = new ItemStack(this, 1);
            this.setExplosive(stack, e);
            subItems.add(stack);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String getUnlocalizedName(@Nonnull ItemStack stack) {
        ResourceLocation location = this.getExplosive(stack).getRegistryName();
        return "item." + location.getResourceDomain() + ".explosive.rocket." + location.getResourcePath();
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.getBlockState(pos).getBlock() != LaunchIgnitionBlock.INSTANCE) {
            return EnumActionResult.PASS;
        }

        if (!worldIn.isRemote) {
            LaunchIgnitionBlock.INSTANCE.getBlockEntity(worldIn, pos).ifPresent((i) -> {
                if (!playerIn.capabilities.isCreativeMode) {
                    stack.stackSize--;
                }

                ModularExplosive warhead = i.getWarhead().orElse(null);
                i.setWarhead(this.getExplosive(stack));

                if (warhead != null && !playerIn.capabilities.isCreativeMode) {
                    if (stack.stackSize == 0) {
                        stack.stackSize = 1;
                        RocketItem.INSTANCE.setExplosive(stack, warhead);
                    } else {
                        ItemStack rocketStack = new ItemStack(RocketItem.INSTANCE, 1);
                        RocketItem.INSTANCE.setExplosive(rocketStack, warhead);

                        playerIn.inventory.addItemStackToInventory(rocketStack);
                    }
                }
            });
        }

        return EnumActionResult.SUCCESS;
    }

    /**
     * Sets the explosive a rocket item is equipped with.
     */
    public void setExplosive(@Nonnull ItemStack stack, @Nonnull ModularExplosive explosive) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("Warhead", explosive.getRegistryName().toString());
        stack.setTagCompound(compound);
    }
}
