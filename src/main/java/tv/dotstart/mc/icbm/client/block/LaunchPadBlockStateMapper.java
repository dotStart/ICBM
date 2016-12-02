package tv.dotstart.mc.icbm.client.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;

import javax.annotation.Nonnull;

import tv.dotstart.mc.icbm.ICBMModification;
import tv.dotstart.mc.icbm.common.block.rocket.LaunchPadBlock;

/**
 * Provides a state mapper for instances of {@link LaunchPadBlock} in order to properly display its
 * respective material and damage state.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class LaunchPadBlockStateMapper extends StateMapperBase {

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
        return new ModelResourceLocation(new ResourceLocation(ICBMModification.IDENTIFIER, "launch_pad_" + state.getValue(LaunchPadBlock.MATERIAL).getName()), this.getPropertyString(Collections.singletonMap(LaunchPadBlock.DAMAGE, state.getValue(LaunchPadBlock.DAMAGE))));
    }
}
