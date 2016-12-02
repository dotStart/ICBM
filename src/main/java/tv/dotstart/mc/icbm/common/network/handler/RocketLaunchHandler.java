package tv.dotstart.mc.icbm.common.network.handler;

import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;

import tv.dotstart.mc.icbm.ICBMModification;
import tv.dotstart.mc.icbm.common.block.rocket.LaunchControlBlock;
import tv.dotstart.mc.icbm.common.inventory.LaunchControlContainer;
import tv.dotstart.mc.icbm.common.network.message.RocketLaunchMessage;

/**
 * Provides a network handler for instances of {@link RocketLaunchHandler}.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class RocketLaunchHandler implements IMessageHandler<RocketLaunchMessage, IMessage> {

    /**
     * {@inheritDoc}
     */
    @Override
    public IMessage onMessage(@Nonnull RocketLaunchMessage message, @Nonnull MessageContext ctx) {
        Container container = ctx.getServerHandler().playerEntity.openContainer;

        if (container == null || !(container instanceof LaunchControlContainer)) {
            ICBMModification.getInstance().getLogger().warn("Player " + ctx.getServerHandler().playerEntity.getName() + " (UUID " + ctx.getServerHandler().playerEntity.getPersistentID() + ") tried to launch a rocket without its GUI open (either lagging or hacking).");
            return null;
        }

        LaunchControlContainer controlContainer = (LaunchControlContainer) container;
        LaunchControlBlock.INSTANCE.getConnectedIgnition(controlContainer.getLaunchControlBlockEntity().getWorld(), controlContainer.getLaunchControlBlockEntity().getPos()).ifPresent((i) -> {
            BlockPos pos = controlContainer.getLaunchControlBlockEntity().getCoordinates();

            if (pos != null) {
                i.launch(pos, ctx.getServerHandler().playerEntity);
            }
        });

        return null;
    }
}
