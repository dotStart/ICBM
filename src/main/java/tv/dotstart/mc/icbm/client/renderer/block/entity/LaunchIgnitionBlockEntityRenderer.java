package tv.dotstart.mc.icbm.client.renderer.block.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.TRSRTransformation;

import tv.dotstart.mc.icbm.ICBMModification;
import tv.dotstart.mc.icbm.common.block.entity.rocket.LaunchIgnitionBlockEntity;
import tv.dotstart.mc.icbm.explosive.ModularExplosive;

/**
 * Provides a tile entity renderer for handling the rendering of the launch ignition system.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class LaunchIgnitionBlockEntityRenderer extends TileEntitySpecialRenderer<LaunchIgnitionBlockEntity> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void renderTileEntityAt(LaunchIgnitionBlockEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
        if (te.getWarhead().isPresent()) {
            ModularExplosive explosive = te.getWarhead().get();
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();

            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(x - 1.5, y - .5, z - 1.5);
                GlStateManager.scale(4.0, 4.0, 4.0);

                IBakedModel bakedModel;
                try {
                    IModel model = ModelLoaderRegistry.getModel(new ResourceLocation(ICBMModification.IDENTIFIER, "entity/rocket"));
                    bakedModel = model.bake(TRSRTransformation.identity(), DefaultVertexFormats.ITEM, location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    bakedModel = ModelLoaderRegistry.getMissingModel().bake(TRSRTransformation.identity(), DefaultVertexFormats.ITEM, location -> Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite());
                }

                this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                int color = explosive.getColor();
                blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(bakedModel, 1.0f, ((color >> 16) & 0xFF) / 255f, ((color >> 8) & 0xFF) / 255f, (color & 0xFF) / 255f);
            }
            GlStateManager.popMatrix();
        }

        super.renderTileEntityAt(te, x, y, z, partialTicks, destroyStage);
    }
}
