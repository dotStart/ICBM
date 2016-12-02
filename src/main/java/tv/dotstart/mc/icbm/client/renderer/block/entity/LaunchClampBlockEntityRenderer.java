package tv.dotstart.mc.icbm.client.renderer.block.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

import tv.dotstart.mc.icbm.common.block.entity.rocket.LaunchClampBlockEntity;
import tv.dotstart.mc.icbm.common.block.rocket.LaunchClampBlock;

/**
 * Provides a special renderer for instances of {@link LaunchClampBlockEntity} in order to render
 * the respective clamps and their opening rotation.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class LaunchClampBlockEntityRenderer extends TileEntitySpecialRenderer<LaunchClampBlockEntity> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void renderTileEntityAt(@Nonnull LaunchClampBlockEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        IBlockState state = LaunchClampBlock.INSTANCE.getActualState(te.getWorld().getBlockState(te.getPos()), te.getWorld(), te.getPos());

        if (te.getWorld().getBlockState(te.getPos().offset(EnumFacing.DOWN)).getBlock() == LaunchClampBlock.INSTANCE) {
            state = LaunchClampBlock.INSTANCE.getTopState(state);
        } else {
            state = LaunchClampBlock.INSTANCE.getBottomState(state);
        }

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(x, y, z);

            if (LaunchClampBlock.INSTANCE.getPart(state) == LaunchClampBlock.Part.TOP_TE) {
                BlockPos centerOffset = LaunchClampBlock.INSTANCE.getOriginOffsetByState(state);
                GlStateManager.translate(centerOffset.getX() + 0.5, -.25, centerOffset.getZ() + 0.5);

                float angle = 15.0f * te.getReleaseProgress();
                switch (LaunchClampBlock.INSTANCE.getFacingByState(state)) {
                    case 0:
                        GlStateManager.rotate(angle, 1, 0, -1);
                        break;
                    case 1:
                        GlStateManager.rotate(angle, 1, 0, 1);
                        break;
                    case 2:
                        GlStateManager.rotate(angle, -1, 0, 1);
                        break;
                    case 3:
                        GlStateManager.rotate(angle, -1, 0, -1);
                        break;
                }

                GlStateManager.translate(-(centerOffset.getX() + 0.5), .25, -(centerOffset.getZ() + 0.5));
            }

            GlStateManager.pushMatrix();
            {
                GlStateManager.scale(1.5, 1.5, 1.5);
                GlStateManager.translate(-.18, 0, .85);

                if (LaunchClampBlock.INSTANCE.getPart(state) == LaunchClampBlock.Part.BOTTOM_TE) {
                    GlStateManager.translate(0, -0.6, 0);
                } else {
                    GlStateManager.translate(0, -0.27, 0);
                }

                float brightness = te.getWorld().getLightBrightness(te.getPos());
                GlStateManager.color(brightness, brightness, brightness, 1.0F);

                this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                blockrendererdispatcher.renderBlockBrightness(state, 1.0F);
            }
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();

        super.renderTileEntityAt(te, x, y, z, partialTicks, destroyStage);
    }
}
