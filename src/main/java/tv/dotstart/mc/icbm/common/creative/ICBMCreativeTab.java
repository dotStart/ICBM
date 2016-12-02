package tv.dotstart.mc.icbm.common.creative;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import tv.dotstart.mc.icbm.common.item.RocketItem;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class ICBMCreativeTab extends CreativeTabs {
    public static final ICBMCreativeTab INSTANCE = new ICBMCreativeTab();

    ICBMCreativeTab() {
        super("doticbm");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Item getTabIconItem() {
        return RocketItem.INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasSearchBar() {
        // TODO: Searching might become relevant if we keep extending this further
        // return true;
        return false;
    }
}
