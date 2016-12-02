package tv.dotstart.mc.icbm.common.network.message;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class RocketLaunchMessage implements IMessage {

    /**
     * {@inheritDoc}
     */
    @Override
    public void fromBytes(@Nonnull ByteBuf buf) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void toBytes(@Nonnull ByteBuf buf) {
    }
}
