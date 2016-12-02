package tv.dotstart.mc.icbm.common.crafting.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import tv.dotstart.mc.icbm.common.item.RocketItem;
import tv.dotstart.mc.icbm.api.explosive.ModularExplosive;
import tv.dotstart.mc.icbm.api.explosive.ModularExplosiveCraftingComponent;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class RocketWarheadRecipe implements IRecipe {
    public static final RocketWarheadRecipe INSTANCE = new RocketWarheadRecipe();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {
        boolean hasRocket = false;
        boolean hasComponent = false;

        for (int i = 0; i < inv.getHeight(); ++i) {
            for (int j = 0; j < inv.getWidth(); ++j) {
                ItemStack itemstack = inv.getStackInRowAndColumn(j, i);

                if (itemstack != null) {
                    if (itemstack.getItem() instanceof ModularExplosiveCraftingComponent) {
                        if (hasComponent) {
                            return false;
                        }

                        hasComponent = true;
                    }

                    if (itemstack.getItem() == RocketItem.INSTANCE) {
                        if (hasRocket) {
                            return false;
                        }

                        hasRocket = true;
                    }
                }
            }
        }

        return hasComponent && hasRocket;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
        ModularExplosive explosive = null;

        for (int i = 0; i < inv.getHeight(); ++i) {
            for (int j = 0; j < inv.getWidth(); ++j) {
                ItemStack itemstack = inv.getStackInRowAndColumn(j, i);

                if (itemstack != null) {
                    if (itemstack.getItem() instanceof ModularExplosiveCraftingComponent) {
                        explosive = ((ModularExplosiveCraftingComponent) itemstack.getItem()).getExplosive(itemstack);
                        break;
                    }
                }
            }

            if (explosive != null) {
                break;
            }
        }

        ItemStack stack = new ItemStack(RocketItem.INSTANCE, 1);
        RocketItem.INSTANCE.setExplosive(stack, explosive);

        return stack;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRecipeSize() {
        return 2;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(RocketItem.INSTANCE, 1);
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
