package tv.dotstart.mc.icbm.common.crafting.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import tv.dotstart.mc.icbm.common.item.RocketBellItem;
import tv.dotstart.mc.icbm.common.item.RocketItem;
import tv.dotstart.mc.icbm.common.item.RocketTankItem;
import tv.dotstart.mc.icbm.common.item.RocketWarheadItem;
import tv.dotstart.mc.icbm.explosive.ModularExplosive;
import tv.dotstart.mc.icbm.explosive.ModularExplosiveCraftingComponent;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class RocketPartRecipe implements IRecipe {
    public static final RocketPartRecipe INSTANCE = new RocketPartRecipe();

    /**
     * Checks whether a certain slot contains at least one of a certain item.
     */
    private boolean contains(@Nonnull InventoryCrafting inventory, @Nonnegative int slotId, @Nonnull Item item) {
        ItemStack stack = inventory.getStackInSlot(slotId);
        return stack != null && stack.getItem() == item;
    }

    /**
     * Checks whether a certain slot is empty.
     */
    private boolean isEmpty(@Nonnull InventoryCrafting inventory, @Nonnegative int slotId) {
        return inventory.getStackInSlot(slotId) == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {
        if (inv.getSizeInventory() < 9) {
            return false;
        }

        if (!this.isEmpty(inv, 2) || !this.isEmpty(inv, 3) || !this.isEmpty(inv, 5) || !this.isEmpty(inv, 6) || !this.isEmpty(inv, 8)) {
            return false;
        }

        {
            ItemStack stack = inv.getStackInSlot(0);

            if (stack != null && !(stack.getItem() instanceof ModularExplosiveCraftingComponent)) {
                return false;
            }
        }

        return this.contains(inv, 1, RocketWarheadItem.INSTANCE) && this.contains(inv, 4, RocketTankItem.INSTANCE) && this.contains(inv, 7, RocketBellItem.INSTANCE);
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
        ItemStack stack = inv.getStackInSlot(0);
        ItemStack out = new ItemStack(RocketItem.INSTANCE, 1, 0);

        if (stack != null) {
            if (!(stack.getItem() instanceof ModularExplosiveCraftingComponent)) {
                return null;
            }

            ModularExplosive explosive = ((ModularExplosiveCraftingComponent) stack.getItem()).getExplosive(stack);

            if (explosive != null) {
                RocketItem.INSTANCE.setExplosive(out, explosive);
            }
        }

        return out;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRecipeSize() {
        return 4;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(RocketItem.INSTANCE, 1, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public ItemStack[] getRemainingItems(@Nonnull InventoryCrafting inv) {
        ItemStack[] aitemstack = new ItemStack[inv.getSizeInventory()];

        for (int i = 0; i < aitemstack.length; ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            aitemstack[i] = net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack);
        }

        return aitemstack;
    }
}
