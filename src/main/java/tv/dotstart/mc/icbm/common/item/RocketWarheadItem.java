package tv.dotstart.mc.icbm.common.item;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import tv.dotstart.mc.icbm.ICBMModification;
import tv.dotstart.mc.icbm.common.creative.ICBMCreativeTab;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class RocketWarheadItem extends Item {
    public static final RocketWarheadItem INSTANCE = new RocketWarheadItem();

    public RocketWarheadItem() {
        this.setRegistryName(new ResourceLocation(ICBMModification.IDENTIFIER, "rocket_warhead"));

        this.setUnlocalizedName(ICBMModification.IDENTIFIER + ".rocket_warhead");
        this.setCreativeTab(ICBMCreativeTab.INSTANCE);
    }
}
