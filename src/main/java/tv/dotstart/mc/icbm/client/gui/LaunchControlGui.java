package tv.dotstart.mc.icbm.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.TRSRTransformation;

import java.io.IOException;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import tv.dotstart.mc.icbm.ICBMModification;
import tv.dotstart.mc.icbm.common.block.entity.rocket.LaunchIgnitionBlockEntity;
import tv.dotstart.mc.icbm.common.inventory.LaunchControlContainer;
import tv.dotstart.mc.icbm.common.item.RocketItem;
import tv.dotstart.mc.icbm.common.network.message.RocketLaunchMessage;
import tv.dotstart.mc.icbm.api.explosive.ModularExplosive;

/**
 * Provides a client side GUI screen which is displayed to users which interact with any instance of
 * {@link tv.dotstart.mc.icbm.common.block.entity.rocket.LaunchControlBlockEntity}.
 *
 * This GUI provides the direct ability to gain coordinate information from instances of
 * {@link tv.dotstart.mc.icbm.api.rocket.CoordinateSourceItem} through adding them to the respective
 * item slot as well as inspecting and launching the respective carrier rocket from a nearby
 * instance of {@link LaunchIgnitionBlockEntity} within the world.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class LaunchControlGui extends GuiContainer {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(ICBMModification.IDENTIFIER, "textures/gui/launch_control_background.png");
    private static final int xSize = 256;
    private static final int ySize = 250;

    private final LaunchControlContainer container;
    private GuiButton launchButton;

    public LaunchControlGui(@Nonnull LaunchControlContainer container) {
        super(container);
        this.container = container;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initGui() {
        super.initGui();

        int x = (this.width - xSize) / 2;
        int y = (this.height - ySize) / 2;

        this.launchButton = new GuiButton(0, x + 118, y + 120, 100, 20, I18n.format("container.doticbm.launcher.launch"));

        this.buttonList.clear();
        this.buttonList.add(this.launchButton);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateScreen() {
        super.updateScreen();

        this.launchButton.enabled = this.container.getLaunchControlBlockEntity().getConnectedIngition().flatMap(LaunchIgnitionBlockEntity::getWarhead).isPresent() && this.container.getLaunchControlBlockEntity().getCoordinateSource() != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void actionPerformed(@Nonnull GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                ICBMModification.getInstance().getChannel().sendToServer(new RocketLaunchMessage());
                this.mc.thePlayer.closeScreen();
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultBackground();
        this.drawWindowBackground();

        this.drawRocket();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        this.drawLocation();
    }

    /**
     * Draws the location information stored within the respective coordinate source on-screen.
     */
    private void drawLocation() {
        BlockPos location = this.container.getLaunchControlBlockEntity().getCoordinates();

        String xCoordinateField;
        String zCoordinateField;

        if (location != null) {
            xCoordinateField = Integer.toString(location.getX());
            zCoordinateField = Integer.toString(location.getZ());
        } else {
            xCoordinateField = zCoordinateField = I18n.format("container.doticbm.launcher.coordinate.unspecified");
        }

        this.fontRendererObj.drawString(I18n.format("container.doticbm.launcher.coordinate.x", xCoordinateField), 58, 15, 0xA0A0A0, true);
        this.fontRendererObj.drawString(I18n.format("container.doticbm.launcher.coordinate.z", zCoordinateField), 58, 50, 0xA0A0A0, true);
    }

    /**
     * Draws a small in-GUI representation of the loaded rocket or text which notifies the
     * respective user of the current loading state (e.g. being empty or lacking a nearby launch
     * platform).
     */
    private void drawRocket() {
        LaunchIgnitionBlockEntity entity = this.container.getLaunchControlBlockEntity().getConnectedIngition().orElse(null);

        int x = (this.width - xSize) / 2;
        int y = (this.height - ySize) / 2;

        if (entity == null) {
            String text = I18n.format("container.doticbm.launcher.disconnected");
            this.fontRendererObj.drawString(text, x + 48 - (this.fontRendererObj.getStringWidth(text) / 2), (this.height / 2 - 4), 0xFF0000);
        } else if (!entity.getWarhead().isPresent()) {
            String text = I18n.format("container.doticbm.launcher.empty");
            this.fontRendererObj.drawString(text, x + 48 - (this.fontRendererObj.getStringWidth(text) / 2), (this.height / 2 - 4), 0xFF0000);
        } else {
            final IBakedModel bakedModel;
            try {
                IModel model = ModelLoaderRegistry.getModel(new ResourceLocation(ICBMModification.IDENTIFIER, "entity/rocket"));
                bakedModel = model.bake(TRSRTransformation.identity(), DefaultVertexFormats.ITEM, location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
            } catch (Exception ex) {
                throw new RuntimeException("Could not load rocket model: " + ex.getMessage(), ex);
            }

            GlStateManager.pushMatrix();
            {
                float percentage = (this.mc.theWorld.getTotalWorldTime() % 200) / 200.0f;

                this.drawModel(bakedModel, x + 40, y + 70, percentage * 360.0f, entity.getWarhead().get());
            }
            GlStateManager.popMatrix();
        }
    }

    /**
     * Draws a basic image background in the very center of the screen in order to provide
     * visual bounds to the UI.
     */
    private void drawWindowBackground() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        this.mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        int x = (this.width - xSize) / 2;
        int y = (this.height - ySize) / 2;

        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }

    /**
     * Draws a specified model in GUI mode at a certain position using a specific Y-rotation.
     */
    private void drawModel(@Nonnull IBakedModel model, @Nonnegative int x, @Nonnegative int y, float rotation, @Nonnull ModularExplosive warhead) {
        GlStateManager.pushMatrix();
        {
            this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);

            GlStateManager.enableRescaleNormal();
            GlStateManager.enableColorMaterial();

            {
                GlStateManager.translate((float) x, (float) y, 100.0F + this.zLevel);
                GlStateManager.translate(8.0F, 8.0F, 0.0F);
                GlStateManager.scale(1.0F, -1.0F, 1.0F);
                GlStateManager.scale(100.0F, 100.0F, 100.0F);
                GlStateManager.rotate(rotation, 0.0f, 1.0f, 0.0f);
            }

            ItemStack stack = new ItemStack(RocketItem.INSTANCE, 1);
            RocketItem.INSTANCE.setExplosive(stack, warhead);

            this.mc.getRenderItem().renderItem(stack, model);

            GlStateManager.disableColorMaterial();
            GlStateManager.disableRescaleNormal();
        }
        GlStateManager.popMatrix();
        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
