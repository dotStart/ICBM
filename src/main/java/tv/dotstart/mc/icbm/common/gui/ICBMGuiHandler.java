package tv.dotstart.mc.icbm.common.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import tv.dotstart.mc.icbm.client.gui.LaunchControlGui;
import tv.dotstart.mc.icbm.common.block.entity.rocket.LaunchControlBlockEntity;
import tv.dotstart.mc.icbm.common.inventory.LaunchControlContainer;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class ICBMGuiHandler implements IGuiHandler {

    /**
     * Provides a utility method in order to simplify handling of GUIs within the modification.
     */
    @Nullable
    private Object handleGUI(int guiID, @Nonnull Function<GUIIdentifier, Object> identifierConsumer) {
        GUIIdentifier[] identifiers = GUIIdentifier.values();

        if (guiID >= identifiers.length) {
            return null;
        }

        GUIIdentifier identifier = identifiers[guiID];
        Object handler = identifierConsumer.apply(identifier);

        if (handler == null) {
            throw new RuntimeException("Unhandled GUI type: " + identifier);
        }

        return handler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public Object getServerGuiElement(int ID, EntityPlayer player, @Nonnull World world, int x, int y, int z) {
        return this.handleGUI(ID, (i) -> {
            switch (i) {
                case LAUNCH_CONTROL:
                    return new LaunchControlContainer(player.inventory, (LaunchControlBlockEntity) world.getTileEntity(new BlockPos(x, y, z)));
            }

            return null;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public Object getClientGuiElement(int ID, EntityPlayer player, @Nonnull World world, int x, int y, int z) {
        return this.handleGUI(ID, (i) -> {
            switch (i) {
                case LAUNCH_CONTROL:
                    return new LaunchControlGui(new LaunchControlContainer(player.inventory, (LaunchControlBlockEntity) world.getTileEntity(new BlockPos(x, y, z))));
            }

            return null;
        });
    }
}
