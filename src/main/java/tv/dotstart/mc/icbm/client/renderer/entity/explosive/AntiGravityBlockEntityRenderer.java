package tv.dotstart.mc.icbm.client.renderer.entity.explosive;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Signed;

import tv.dotstart.mc.icbm.common.entity.explosives.gravity.AntiGravityBlockEntity;

/**
 * Provides a specialized render for instances of {@link AntiGravityBlockEntity} in order to
 * replicate the look of flying blocks in-game.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class AntiGravityBlockEntityRenderer extends Render<AntiGravityBlockEntity> {

    public AntiGravityBlockEntityRenderer(@Nonnull RenderManager renderManager) {
        super(renderManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRender(@Nonnull AntiGravityBlockEntity entity, @Signed double x, @Signed double y, @Signed double z, float entityYaw, float partialTicks) {
        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z + 1);
        {
            this.bindEntityTexture(entity);

            BlockPos[] relativePositions = entity.getRelativePositions();
            IBlockState[] states = entity.getStates();

            for (int i = 0; i < relativePositions.length; ++i) {
                GlStateManager.pushMatrix();
                {
                    BlockPos relativeLocation = relativePositions[i];
                    GlStateManager.translate(relativeLocation.getX(), relativeLocation.getY(), relativeLocation.getZ());

                    blockrendererdispatcher.renderBlockBrightness(states[i], entity.getBrightness(partialTicks));
                }
                GlStateManager.popMatrix();
            }
        }
        GlStateManager.popMatrix();

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    protected ResourceLocation getEntityTexture(@Nonnull AntiGravityBlockEntity entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
