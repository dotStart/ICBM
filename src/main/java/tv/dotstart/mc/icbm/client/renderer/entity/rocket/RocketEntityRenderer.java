package tv.dotstart.mc.icbm.client.renderer.entity.rocket;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

import tv.dotstart.mc.icbm.ICBMModification;
import tv.dotstart.mc.icbm.common.entity.rocket.RocketEntity;
import tv.dotstart.mc.icbm.common.item.RocketItem;

/**
 * Renders instances of {@link RocketEntity} within the world.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class RocketEntityRenderer extends Render<RocketEntity> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(ICBMModification.IDENTIFIER, "textures/entities/rocket.png");
    private final RenderItem itemRenderer;

    public RocketEntityRenderer(@Nonnull RenderManager renderManager, @Nonnull RenderItem itemRenderer) {
        super(renderManager);

        this.itemRenderer = itemRenderer;

        this.shadowSize = 0.5f;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRender(@Nonnull RocketEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        {
            GlStateManager.disableLighting();
            {
                ItemStack stack = new ItemStack(RocketItem.INSTANCE, 1);
                RocketItem.INSTANCE.setExplosive(stack, entity.getWarhead());

                GlStateManager.pushAttrib();
                {
                    RenderHelper.enableStandardItemLighting();
                    {
                        GlStateManager.translate(x, y + 1.0, z);
                        GlStateManager.scale(1.4, 1.4, 1.4);

                        if (entity.getPhase() == RocketEntity.Phase.DESTROY) {
                            GlStateManager.rotate(180.0f, 1.0f, 0.0f, 0.0f);
                        }

                        this.itemRenderer.renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
                    }
                    RenderHelper.disableStandardItemLighting();
                }
                GlStateManager.popAttrib();
            }
            GlStateManager.enableLighting();
        }
        GlStateManager.popMatrix();

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    protected ResourceLocation getEntityTexture(@Nonnull RocketEntity entity) {
        return TEXTURE_LOCATION;
    }
}
