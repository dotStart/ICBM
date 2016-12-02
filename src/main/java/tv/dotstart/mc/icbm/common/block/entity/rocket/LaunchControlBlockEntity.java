package tv.dotstart.mc.icbm.common.block.entity.rocket;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import tv.dotstart.mc.icbm.common.block.entity.AbstractBlockEntity;
import tv.dotstart.mc.icbm.common.block.rocket.LaunchControlBlock;
import tv.dotstart.mc.icbm.rocket.CoordinateSourceItem;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class LaunchControlBlockEntity extends AbstractBlockEntity implements ISidedInventory {
    private ItemStack coordinateSource;

    /**
     * Retrieves the connected launch ignition.
     */
    @Nonnull
    public Optional<LaunchIgnitionBlockEntity> getConnectedIngition() {
        return LaunchControlBlock.INSTANCE.getConnectedIgnition(this.worldObj, this.pos);
    }

    /**
     * Retrieves the coordinate source item stored within this launch control (or null if not
     * present).
     */
    @Nullable
    public ItemStack getCoordinateSource() {
        return this.coordinateSource;
    }

    /**
     * Retrieves the coordinates stored within this launch control block (or null if not present).
     */
    @Nullable
    public BlockPos getCoordinates() {
        if (this.coordinateSource == null) {
            return null;
        }

        return ((CoordinateSourceItem) this.coordinateSource.getItem()).extractCoordinate(this.coordinateSource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (compound.hasKey("CoordinateSource")) {
            this.coordinateSource = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("CoordinateSource"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        if (this.coordinateSource != null) {
            NBTTagCompound itemCompound = new NBTTagCompound();
            this.coordinateSource.writeToNBT(itemCompound);

            compound.setTag("CoordinateSource", itemCompound);
        }

        return super.writeToNBT(compound);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public int[] getSlotsForFace(@Nonnull EnumFacing side) {
        return new int[]{1};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canInsertItem(int index, @Nonnull ItemStack itemStackIn, @Nonnull EnumFacing direction) {
        return !(index != 0 || itemStackIn.stackSize != 1 || !(itemStackIn.getItem() instanceof CoordinateSourceItem));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canExtractItem(int index, @Nonnull ItemStack stack, @Nonnull EnumFacing direction) {
        return !(index != 0 || stack.stackSize != 1 || this.coordinateSource == null || stack.getItem() != this.coordinateSource.getItem());

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSizeInventory() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public ItemStack getStackInSlot(int index) {
        if (index != 0) {
            return null;
        }

        return this.coordinateSource;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (index != 0) {
            return null;
        }

        if (count != 0) {
            return null;
        }

        return this.coordinateSource;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (index != 0) {
            return null;
        }

        ItemStack stack = this.coordinateSource;
        this.coordinateSource = null;

        return stack;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
        if (index != 0) {
            return;
        }

        this.coordinateSource = stack;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUseableByPlayer(@Nonnull EntityPlayer player) {
        return player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void openInventory(@Nonnull EntityPlayer player) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeInventory(@Nonnull EntityPlayer player) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
        return index == 0 && stack.stackSize == 1 && stack.getItem() instanceof CoordinateSourceItem;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getField(int id) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setField(int id, int value) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFieldCount() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        this.coordinateSource = null;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String getName() {
        return "icbm.container.launcher";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasCustomName() {
        return false;
    }
}
