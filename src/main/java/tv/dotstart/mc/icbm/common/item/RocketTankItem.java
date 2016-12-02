package tv.dotstart.mc.icbm.common.item;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import tv.dotstart.mc.icbm.ICBMModification;
import tv.dotstart.mc.icbm.common.creative.ICBMCreativeTab;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class RocketTankItem extends Item {
    public static final RocketTankItem INSTANCE = new RocketTankItem();

    public RocketTankItem() {
        this.setRegistryName(new ResourceLocation(ICBMModification.IDENTIFIER, "rocket_tank"));

        this.setUnlocalizedName(ICBMModification.IDENTIFIER + ".rocket_tank");
        this.setCreativeTab(ICBMCreativeTab.INSTANCE);
    }
}
