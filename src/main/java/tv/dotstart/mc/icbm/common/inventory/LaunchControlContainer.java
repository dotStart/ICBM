package tv.dotstart.mc.icbm.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import tv.dotstart.mc.icbm.common.block.entity.rocket.LaunchControlBlockEntity;
import tv.dotstart.mc.icbm.api.rocket.CoordinateSourceItem;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class LaunchControlContainer extends Container {
    private final IInventory playerInventory;
    private final LaunchControlBlockEntity launchControlBlockEntity;

    public LaunchControlContainer(@Nonnull IInventory playerInventory, @Nonnull LaunchControlBlockEntity launchControlBlockEntity) {
        this.playerInventory = playerInventory;
        this.launchControlBlockEntity = launchControlBlockEntity;

        this.addSlotToContainer(new CoordinateSourceSlot(launchControlBlockEntity, 0, 120, -23));

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 9 + j * 18, 126 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlotToContainer(new Slot(playerInventory, k, 9 + k * 18, 184));
        }
    }

    @Nonnull
    public IInventory getPlayerInventory() {
        return this.playerInventory;
    }

    @Nonnull
    public LaunchControlBlockEntity getLaunchControlBlockEntity() {
        return this.launchControlBlockEntity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
        return this.launchControlBlockEntity.isUseableByPlayer(playerIn);
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = null;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 2) {
                if (!this.mergeItemStack(itemstack1, 1, 37, true)) {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (index != 0) {
                if (this.launchControlBlockEntity.canInsertItem(0, itemstack1, EnumFacing.DOWN)) {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                        return null;
                    }
                }
            } else if (!this.mergeItemStack(itemstack1, 2, 37, false)) {
                return null;
            }

            if (itemstack1.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(playerIn, itemstack1);
        }

        return itemstack;
    }

    /**
     * Provides a slot which limits its contents to coordinate sources.
     */
    public static class CoordinateSourceSlot extends Slot {

        public CoordinateSourceSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isItemValid(@Nullable ItemStack stack) {
            return stack != null && stack.getItem() instanceof CoordinateSourceItem;
        }
    }
}
