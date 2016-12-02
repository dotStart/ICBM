package tv.dotstart.mc.icbm.common;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;

import tv.dotstart.mc.icbm.ICBMModification;
import tv.dotstart.mc.icbm.common.block.entity.explosives.ModularExplosiveBlockEntity;
import tv.dotstart.mc.icbm.common.block.entity.rocket.LaunchClampBlockEntity;
import tv.dotstart.mc.icbm.common.block.entity.rocket.LaunchControlBlockEntity;
import tv.dotstart.mc.icbm.common.block.entity.rocket.LaunchIgnitionBlockEntity;
import tv.dotstart.mc.icbm.common.block.explosives.ModularExplosiveBlock;
import tv.dotstart.mc.icbm.common.block.rocket.LaunchClampBlock;
import tv.dotstart.mc.icbm.common.block.rocket.LaunchControlBlock;
import tv.dotstart.mc.icbm.common.block.rocket.LaunchIgnitionBlock;
import tv.dotstart.mc.icbm.common.block.rocket.LaunchPadBlock;
import tv.dotstart.mc.icbm.common.crafting.recipe.RocketPartRecipe;
import tv.dotstart.mc.icbm.common.crafting.recipe.RocketWarheadRecipe;
import tv.dotstart.mc.icbm.common.entity.EntityIdentifier;
import tv.dotstart.mc.icbm.common.entity.explosives.ModularExplosiveEntity;
import tv.dotstart.mc.icbm.common.entity.explosives.gravity.AntiGravityBlockEntity;
import tv.dotstart.mc.icbm.common.entity.explosives.gravity.AntiGravitySourceEntity;
import tv.dotstart.mc.icbm.common.entity.rocket.RocketEntity;
import tv.dotstart.mc.icbm.common.explosive.AntiGravityExplosive;
import tv.dotstart.mc.icbm.common.explosive.ChemicalExplosive;
import tv.dotstart.mc.icbm.common.explosive.ConventionalExplosive;
import tv.dotstart.mc.icbm.common.explosive.DenseExplosive;
import tv.dotstart.mc.icbm.common.explosive.LiquidExplosive;
import tv.dotstart.mc.icbm.common.explosive.ThermalExplosive;
import tv.dotstart.mc.icbm.common.gui.ICBMGuiHandler;
import tv.dotstart.mc.icbm.common.item.LocationReaderItem;
import tv.dotstart.mc.icbm.common.item.RocketBellItem;
import tv.dotstart.mc.icbm.common.item.RocketItem;
import tv.dotstart.mc.icbm.common.item.RocketTankItem;
import tv.dotstart.mc.icbm.common.item.RocketWarheadItem;
import tv.dotstart.mc.icbm.common.network.handler.RocketLaunchHandler;
import tv.dotstart.mc.icbm.common.network.message.RocketLaunchMessage;
import tv.dotstart.mc.icbm.api.explosive.ModularExplosive;

/**
 * Provides a common base type used to synchronize functionality between clients and servers.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class CommonProxy {

    /**
     * Handles the modification pre initialization event.
     */
    public void onPreInitialization(@Nonnull FMLPreInitializationEvent event) {
        this.registerEntitySerializers();

        // forcefully initialize registry early on
        ModularExplosive.getRegistry();

        ForgeChunkManager.setForcedChunkLoadingCallback(ICBMModification.getInstance(), (tickets, world) -> {
        });
        NetworkRegistry.INSTANCE.registerGuiHandler(ICBMModification.getInstance(), new ICBMGuiHandler());

        this.registerNetworkMessages();
        this.registerModularExplosives();
    }

    /**
     * Handles the modification initialization event.
     */
    public void onInitialization(@Nonnull FMLInitializationEvent event) {
        this.registerBlocks();
        this.registerBlockEntities();
        this.registerItems();
        this.registerEntities();

        this.registerCraftingRecipes();
    }

    /**
     * Handles the modification post initialization event.
     */
    public void onPostInitialization(@Nonnull FMLPostInitializationEvent event) {
    }

    /**
     * Registers all block instances within this modification against the global game registry.
     */
    @SuppressWarnings("deprecation")
    private void registerBlocks() {
        GameRegistry.register(LaunchControlBlock.INSTANCE);
        GameRegistry.register(LaunchIgnitionBlock.INSTANCE);
        GameRegistry.register(LaunchClampBlock.INSTANCE);
        GameRegistry.register(LaunchPadBlock.INSTANCE);

        GameRegistry.register(ModularExplosiveBlock.INSTANCE);
    }

    /**
     * Registers all block entities within this modification against the global game registry.
     */
    private void registerBlockEntities() {
        GameRegistry.registerTileEntity(ModularExplosiveBlockEntity.class, "explosive");

        GameRegistry.registerTileEntity(LaunchClampBlockEntity.class, "launch_clamp");
        GameRegistry.registerTileEntity(LaunchIgnitionBlockEntity.class, "launch_ignition");
        GameRegistry.registerTileEntity(LaunchControlBlockEntity.class, "launch_control");
    }

    /**
     * Registers all modification crafting recipes.
     */
    private void registerCraftingRecipes() {
        // Rocket
        GameRegistry.addRecipe(RocketWarheadRecipe.INSTANCE);
        GameRegistry.addRecipe(RocketPartRecipe.INSTANCE);

        // Rocket Parts
        GameRegistry.addRecipe(
                new ItemStack(RocketWarheadItem.INSTANCE, 1),
                " X ",
                "XXX",
                "   ",
                'X', Items.IRON_INGOT
        );

        GameRegistry.addRecipe(
                new ItemStack(RocketTankItem.INSTANCE, 1),
                "XYX",
                "XYX",
                "XZX",
                'X', Items.IRON_INGOT,
                'Y', Items.CLAY_BALL,
                'Z', Items.GUNPOWDER
        );

        GameRegistry.addRecipe(
                new ItemStack(RocketBellItem.INSTANCE, 1),
                " X ",
                "XXX",
                "XXX",
                'X', Items.IRON_INGOT
        );

        // Launch Pads
        GameRegistry.addRecipe(
                new ItemStack(LaunchPadBlock.ITEM, 4, 0),
                "XX ",
                "XX ",
                "   ",
                'X', Blocks.STONE
        );

        GameRegistry.addRecipe(
                new ItemStack(LaunchPadBlock.ITEM, 4, 1),
                "XX ",
                "XX ",
                "   ",
                'X', Blocks.BRICK_BLOCK
        );

        GameRegistry.addRecipe(
                new ItemStack(LaunchPadBlock.ITEM, 4, 2),
                "XX ",
                "XX ",
                "   ",
                'X', Blocks.OBSIDIAN
        );

        // Launch Clamps
        GameRegistry.addRecipe(
                new ItemStack(LaunchClampBlock.ITEM, 4, 0),
                "XXX",
                " X ",
                " X",
                'X', Blocks.OBSIDIAN
        );

        // Launch Control
        GameRegistry.addRecipe(
                new ItemStack(LaunchControlBlock.ITEM, 1, 0),
                "   ",
                "XYX",
                "   ",
                'X', LaunchClampBlock.ITEM,
                'Y', Items.REDSTONE
        );

        // Launch Ignition
        GameRegistry.addShapelessRecipe(
                new ItemStack(LaunchIgnitionBlock.INSTANCE),
                Blocks.OBSIDIAN,
                Items.REDSTONE
        );

        // Location Reader
        GameRegistry.addRecipe(
                new ItemStack(LocationReaderItem.INSTANCE),
                "XYX",
                "XZX",
                "XXX",
                'X', Items.IRON_INGOT,
                'Y', Items.REDSTONE,
                'Z', Items.ENDER_PEARL
        );

        // Explosives
        {
            ItemStack stack = new ItemStack(ModularExplosiveBlock.ITEM, 1);
            ModularExplosiveBlock.ITEM.setExplosive(stack, AntiGravityExplosive.INSTANCE);

            GameRegistry.addRecipe(
                    stack,
                    "XYX",
                    "YZY",
                    "XYX",
                    'X', Items.GUNPOWDER,
                    'Y', Blocks.SAND,
                    'Z', Items.ENDER_EYE
            );
        }

        {
            ItemStack stack = new ItemStack(ModularExplosiveBlock.ITEM, 1);
            ModularExplosiveBlock.ITEM.setExplosive(stack, ChemicalExplosive.INSTANCE);

            GameRegistry.addRecipe(
                    stack,
                    "XYX",
                    "YZY",
                    "XYX",
                    'X', Items.GUNPOWDER,
                    'Y', Blocks.SAND,
                    'Z', Items.SPIDER_EYE
            );
        }

        {
            ItemStack stack = new ItemStack(ModularExplosiveBlock.ITEM, 1);
            ModularExplosiveBlock.ITEM.setExplosive(stack, ConventionalExplosive.INSTANCE);

            GameRegistry.addShapelessRecipe(
                    stack,
                    Blocks.TNT
            );
        }

        {
            ItemStack input = new ItemStack(ModularExplosiveBlock.ITEM, 1);
            ModularExplosiveBlock.ITEM.setExplosive(input, ConventionalExplosive.INSTANCE);

            ItemStack stack = new ItemStack(ModularExplosiveBlock.ITEM, 1);
            ModularExplosiveBlock.ITEM.setExplosive(stack, DenseExplosive.INSTANCE);

            GameRegistry.addRecipe(
                    stack,
                    "XYX",
                    "YZY",
                    "XYX",
                    'X', Items.GUNPOWDER,
                    'Y', Blocks.SAND,
                    'Z', input
            );
        }

        {
            ItemStack stack = new ItemStack(ModularExplosiveBlock.ITEM, 1);
            ModularExplosiveBlock.ITEM.setExplosive(stack, LiquidExplosive.INSTANCE);

            GameRegistry.addRecipe(
                    stack,
                    "XYX",
                    "YZY",
                    "XYX",
                    'X', Items.GUNPOWDER,
                    'Y', Blocks.SAND,
                    'Z', Items.WATER_BUCKET
            );
        }

        {
            ItemStack stack = new ItemStack(ModularExplosiveBlock.ITEM, 1);
            ModularExplosiveBlock.ITEM.setExplosive(stack, ThermalExplosive.INSTANCE);

            GameRegistry.addRecipe(
                    stack,
                    "XYX",
                    "YZY",
                    "XYX",
                    'X', Items.GUNPOWDER,
                    'Y', Blocks.SAND,
                    'Z', Items.LAVA_BUCKET
            );
        }
    }

    /**
     * Registers all modification entities against the global game registry.
     */
    private void registerEntities() {
        EntityRegistry.registerModEntity(ModularExplosiveEntity.class, "modular_explosive", EntityIdentifier.MODULAR_EXPLOSIVE.ordinal(), ICBMModification.getInstance(), 64, 10, true);
        EntityRegistry.registerModEntity(RocketEntity.class, "rocket", EntityIdentifier.ROCKET.ordinal(), ICBMModification.getInstance(), 64, 10, true);

        EntityRegistry.registerModEntity(AntiGravityBlockEntity.class, "anti_gravity_block", EntityIdentifier.ANTI_GRAVITY_BLOCK.ordinal(), ICBMModification.getInstance(), 128, 10, true);
        EntityRegistry.registerModEntity(AntiGravitySourceEntity.class, "anti_gravity_source", EntityIdentifier.ANTI_GRAVITY_SOURCE.ordinal(), ICBMModification.getInstance(), 64, 1000, true);
    }

    /**
     * Registers all modification specific data serializers.
     */
    private void registerEntitySerializers() {
        DataSerializers.registerSerializer(CustomDataSerializers.BLOCKPOS_ARRAY);
        DataSerializers.registerSerializer(CustomDataSerializers.INTEGER_ARRAY);
    }

    /**
     * Registers all modification items with the global game registry.
     */
    private void registerItems() {
        GameRegistry.register(ModularExplosiveBlock.ITEM);

        GameRegistry.register(RocketItem.INSTANCE);
        GameRegistry.register(LocationReaderItem.INSTANCE);
        GameRegistry.register(LaunchIgnitionBlock.ITEM);
        GameRegistry.register(LaunchClampBlock.ITEM);
        GameRegistry.register(LaunchControlBlock.ITEM);
        GameRegistry.register(LaunchPadBlock.ITEM);

        GameRegistry.register(RocketBellItem.INSTANCE);
        GameRegistry.register(RocketTankItem.INSTANCE);
        GameRegistry.register(RocketWarheadItem.INSTANCE);
    }

    /**
     * Registers all messages against the modification network channel.
     */
    private void registerNetworkMessages() {
        ICBMModification.getInstance().getChannel().registerMessage(RocketLaunchHandler.class, RocketLaunchMessage.class, 1, Side.SERVER);
    }

    /**
     * Registers all explosives with the respective registry.
     */
    private void registerModularExplosives() {
        GameRegistry.register(AntiGravityExplosive.INSTANCE);
        GameRegistry.register(ChemicalExplosive.INSTANCE);
        GameRegistry.register(ConventionalExplosive.INSTANCE);
        GameRegistry.register(DenseExplosive.INSTANCE);
        GameRegistry.register(LiquidExplosive.INSTANCE);
        GameRegistry.register(ThermalExplosive.INSTANCE);
    }
}
