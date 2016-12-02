package tv.dotstart.mc.icbm;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import org.apache.logging.log4j.Logger;

import java.io.File;

import javax.annotation.Nonnull;

import tv.dotstart.mc.icbm.common.CommonProxy;

/**
 * Provides an entry point to Forge.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@Mod(modid = ICBMModification.IDENTIFIER)
public class ICBMModification {
    public static final String IDENTIFIER = "doticbm";

    @Mod.Instance
    private static ICBMModification instance;
    @SidedProxy(clientSide = "tv.dotstart.mc.icbm.client.ClientProxy", serverSide = "tv.dotstart.mc.icbm.common.CommonProxy")
    private static CommonProxy proxy;

    private Logger logger;
    private SimpleNetworkWrapper channel;

    /**
     * Loads the modification configuration file/creates a fresh file.
     */
    private void loadConfiguration(@Nonnull File file) {
        Configuration configuration = new Configuration(file, "0.1.0");

        // TODO: Save configuration here
    }

    @Nonnull
    public static ICBMModification getInstance() {
        return instance;
    }

    @Nonnull
    public Logger getLogger() {
        return this.logger;
    }

    @Nonnull
    public SimpleNetworkWrapper getChannel() {
        return this.channel;
    }

    /**
     * Handles the pre-initialization phase of this modification.
     */
    @Mod.EventHandler
    public void onPreInitialization(@Nonnull FMLPreInitializationEvent event) {
        this.logger = event.getModLog();
        this.loadConfiguration(event.getSuggestedConfigurationFile());

        this.profile("pre-initialization", () -> {
            this.channel = NetworkRegistry.INSTANCE.newSimpleChannel(".icbm");

            proxy.onPreInitialization(event);
        });
    }

    /**
     * Handles the initialization phase of this modification.
     */
    @Mod.EventHandler
    public void onInitialization(@Nonnull FMLInitializationEvent event) {
        this.profile("initialization", () -> proxy.onInitialization(event));
    }

    /**
     * Handles the post initialization phase of this modification.
     */
    @Mod.EventHandler
    public void onPostInitialization(@Nonnull FMLPostInitializationEvent event) {
        this.profile("post-initialization", () -> proxy.onPostInitialization(event));
    }

    private void profile(@Nonnull String phase, @Nonnull Runnable runnable) {
        this.logger.info("Entering " + phase + " phase");
        long timestamp = System.currentTimeMillis();
        runnable.run();
        this.logger.info("Leaving " + phase + " phase (took " + (System.currentTimeMillis() - timestamp) + "ms)");
    }
}
