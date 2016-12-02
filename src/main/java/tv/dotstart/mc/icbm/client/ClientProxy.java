package tv.dotstart.mc.icbm.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nonnull;

import tv.dotstart.mc.icbm.ICBMModification;
import tv.dotstart.mc.icbm.client.block.LaunchPadBlockStateMapper;
import tv.dotstart.mc.icbm.client.renderer.block.entity.LaunchClampBlockEntityRenderer;
import tv.dotstart.mc.icbm.client.renderer.block.entity.LaunchIgnitionBlockEntityRenderer;
import tv.dotstart.mc.icbm.client.renderer.color.block.ModularExplosiveBlockColor;
import tv.dotstart.mc.icbm.client.renderer.color.item.ModularExplosiveColor;
import tv.dotstart.mc.icbm.client.renderer.color.item.RocketItemColor;
import tv.dotstart.mc.icbm.client.renderer.entity.explosive.AntiGravityBlockEntityRenderer;
import tv.dotstart.mc.icbm.client.renderer.entity.explosive.ModularExplosiveEntityRenderer;
import tv.dotstart.mc.icbm.client.renderer.entity.rocket.RocketEntityRenderer;
import tv.dotstart.mc.icbm.common.CommonProxy;
import tv.dotstart.mc.icbm.common.block.entity.rocket.LaunchClampBlockEntity;
import tv.dotstart.mc.icbm.common.block.entity.rocket.LaunchIgnitionBlockEntity;
import tv.dotstart.mc.icbm.common.block.explosives.ModularExplosiveBlock;
import tv.dotstart.mc.icbm.common.block.rocket.LaunchClampBlock;
import tv.dotstart.mc.icbm.common.block.rocket.LaunchControlBlock;
import tv.dotstart.mc.icbm.common.block.rocket.LaunchIgnitionBlock;
import tv.dotstart.mc.icbm.common.block.rocket.LaunchPadBlock;
import tv.dotstart.mc.icbm.common.entity.explosives.ModularExplosiveEntity;
import tv.dotstart.mc.icbm.common.entity.explosives.gravity.AntiGravityBlockEntity;
import tv.dotstart.mc.icbm.common.entity.rocket.RocketEntity;
import tv.dotstart.mc.icbm.common.item.LocationReaderItem;
import tv.dotstart.mc.icbm.common.item.RocketBellItem;
import tv.dotstart.mc.icbm.common.item.RocketItem;
import tv.dotstart.mc.icbm.common.item.RocketTankItem;
import tv.dotstart.mc.icbm.common.item.RocketWarheadItem;

/**
 * Handles registrations for the client side.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class ClientProxy extends CommonProxy {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPreInitialization(@Nonnull FMLPreInitializationEvent event) {
        super.onPreInitialization(event);

        this.registerBlockStateMappers();

        this.registerEntityRenderers();
        this.registerBlockEntityRenderers();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onInitialization(@Nonnull FMLInitializationEvent event) {
        super.onInitialization(event);

        this.registerItemRenderers();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPostInitialization(@Nonnull FMLPostInitializationEvent event) {
        super.onPostInitialization(event);

        this.registerBlockColors();
        this.registerItemColors();
    }

    /**
     * Registers handlers in order to customize the coloring of certain block instances when
     * rendered within the world.
     */
    private void registerBlockColors() {
        BlockColors colors = Minecraft.getMinecraft().getBlockColors();

        colors.registerBlockColorHandler(ModularExplosiveBlockColor.INSTANCE, ModularExplosiveBlock.INSTANCE);
    }

    /**
     * Registers renderers for specific implementations of
     * {@link net.minecraft.tileentity.TileEntity} in order to present their respective contents
     * or state accurately.
     */
    private void registerBlockEntityRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(LaunchClampBlockEntity.class, new LaunchClampBlockEntityRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(LaunchIgnitionBlockEntity.class, new LaunchIgnitionBlockEntityRenderer());
    }

    /**
     * Registers state mappers in order to improve integration with resource packs on some blocks.
     */
    private void registerBlockStateMappers() {
        ModelLoader.setCustomStateMapper(LaunchPadBlock.INSTANCE, new LaunchPadBlockStateMapper());
    }

    /**
     * Registers entity renderers for specific implementations of
     * {@link net.minecraft.entity.Entity}.
     */
    private void registerEntityRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(ModularExplosiveEntity.class, ModularExplosiveEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(RocketEntity.class, manager -> new RocketEntityRenderer(manager, Minecraft.getMinecraft().getRenderItem()));

        RenderingRegistry.registerEntityRenderingHandler(AntiGravityBlockEntity.class, AntiGravityBlockEntityRenderer::new);
    }

    /**
     * Registers handlers in order to customize the coloring of certain item instances when rendered
     * in an inventory, GUI or within the world in form of
     * {@link net.minecraft.entity.item.EntityItem}.
     */
    private void registerItemColors() {
        ItemColors colors = Minecraft.getMinecraft().getItemColors();

        colors.registerItemColorHandler(new RocketItemColor(), RocketItem.INSTANCE);

        colors.registerItemColorHandler(new ModularExplosiveColor(), ModularExplosiveBlock.INSTANCE);
    }

    /**
     * Registers renderers and model locations for instances of {@link net.minecraft.item.Item} or
     * {@link net.minecraft.item.ItemBlock}.
     */
    private void registerItemRenderers() {
        ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();

        mesher.register(RocketItem.INSTANCE, 0, new ModelResourceLocation(new ResourceLocation(ICBMModification.IDENTIFIER, "rocket"), "inventory"));
        mesher.register(LaunchPadBlock.ITEM, 0, new ModelResourceLocation(new ResourceLocation(ICBMModification.IDENTIFIER, "launch_pad_stone"), "damage=0"));
        mesher.register(LaunchPadBlock.ITEM, 1, new ModelResourceLocation(new ResourceLocation(ICBMModification.IDENTIFIER, "launch_pad_brick"), "damage=0"));
        mesher.register(LaunchPadBlock.ITEM, 2, new ModelResourceLocation(new ResourceLocation(ICBMModification.IDENTIFIER, "launch_pad_obsidian"), "damage=0"));
        mesher.register(LaunchIgnitionBlock.ITEM, 0, new ModelResourceLocation(new ResourceLocation(ICBMModification.IDENTIFIER, "launch_ignition"), "inventory"));
        mesher.register(LaunchClampBlock.ITEM, 0, new ModelResourceLocation(new ResourceLocation(ICBMModification.IDENTIFIER, "launch_clamp"), "inventory"));
        mesher.register(LaunchControlBlock.ITEM, 0, new ModelResourceLocation(new ResourceLocation(ICBMModification.IDENTIFIER, "launch_control"), "inventory"));
        mesher.register(LocationReaderItem.INSTANCE, 0, new ModelResourceLocation(new ResourceLocation(ICBMModification.IDENTIFIER, "location_reader"), "inventory"));

        mesher.register(ModularExplosiveBlock.ITEM, 0, new ModelResourceLocation(new ResourceLocation(ICBMModification.IDENTIFIER, "explosive"), "normal"));

        mesher.register(RocketBellItem.INSTANCE, 0, new ModelResourceLocation(new ResourceLocation(ICBMModification.IDENTIFIER, "rocket_bell"), "inventory"));
        mesher.register(RocketTankItem.INSTANCE, 0, new ModelResourceLocation(new ResourceLocation(ICBMModification.IDENTIFIER, "rocket_tank"), "inventory"));
        mesher.register(RocketWarheadItem.INSTANCE, 0, new ModelResourceLocation(new ResourceLocation(ICBMModification.IDENTIFIER, "rocket_warhead"), "inventory"));
    }
}
