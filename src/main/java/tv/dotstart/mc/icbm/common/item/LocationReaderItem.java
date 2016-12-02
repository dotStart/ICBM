package tv.dotstart.mc.icbm.common.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import tv.dotstart.mc.icbm.ICBMModification;
import tv.dotstart.mc.icbm.common.creative.ICBMCreativeTab;
import tv.dotstart.mc.icbm.rocket.CoordinateSourceItem;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class LocationReaderItem extends Item implements CoordinateSourceItem {
    public static final LocationReaderItem INSTANCE = new LocationReaderItem();

    public LocationReaderItem() {
        super();

        this.setRegistryName(new ResourceLocation(ICBMModification.IDENTIFIER, "location_reader"));

        this.setUnlocalizedName(ICBMModification.IDENTIFIER + ".location_reader");
        this.setCreativeTab(ICBMCreativeTab.INSTANCE);

        this.setMaxStackSize(1);
        this.setMaxDamage(16);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);

        BlockPos location = this.extractCoordinate(stack);

        if (location != null) {
            tooltip.add(String.format("%d,%d", location.getX(), location.getZ()));
            tooltip.add(I18n.format("item." + ICBMModification.IDENTIFIER + ".location_reader.distance", location.getDistance((int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ)));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public BlockPos extractCoordinate(@Nonnull ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();

        if (compound == null || !compound.hasKey("Location")) {
            return null;
        }

        return BlockPos.fromLong(compound.getLong("Location"));
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!playerIn.capabilities.isCreativeMode) {
            stack.damageItem(1, playerIn);
        }

        if (!worldIn.isRemote) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setLong("Location", new BlockPos(pos.getX(), 0, pos.getZ()).toLong());
            stack.setTagCompound(compound);
        } else {
            playerIn.addChatMessage(new TextComponentTranslation("item." + ICBMModification.IDENTIFIER + ".location_reader.stored", pos.getX(), pos.getZ()));
        }

        return EnumActionResult.SUCCESS;
    }
}
