package tv.dotstart.mc.icbm.common.item;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import tv.dotstart.mc.icbm.ICBMModification;
import tv.dotstart.mc.icbm.common.creative.ICBMCreativeTab;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class RocketBellItem extends Item {
    public static final RocketBellItem INSTANCE = new RocketBellItem();

    public RocketBellItem() {
        this.setRegistryName(new ResourceLocation(ICBMModification.IDENTIFIER, "rocket_bell"));

        this.setUnlocalizedName(ICBMModification.IDENTIFIER + ".rocket_bell");
        this.setCreativeTab(ICBMCreativeTab.INSTANCE);
    }
}
