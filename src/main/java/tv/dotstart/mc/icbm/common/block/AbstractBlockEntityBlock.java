package tv.dotstart.mc.icbm.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Optional;

import javax.annotation.Nonnull;

/**
 * Provides a base for blocks which provide their own implementation of {@link TileEntity} in order
 * to simplify maintenance of commonly needed code snippets.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public abstract class AbstractBlockEntityBlock<B extends TileEntity> extends Block implements ITileEntityProvider {
    private final Class<B> entityType;

    public AbstractBlockEntityBlock(@Nonnull Class<B> entityType, @Nonnull Material blockMaterialIn, @Nonnull MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
        this.entityType = entityType;
    }

    public AbstractBlockEntityBlock(@Nonnull Class<B> entityType, @Nonnull Material materialIn) {
        super(materialIn);
        this.entityType = entityType;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        try {
            return this.entityType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Could not create BlockEntity through standard constructor: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves the corresponding block entity to this block.
     *
     * @param world a world.
     * @param pos   a block position.
     * @return a corresponding entity or an empty optional.
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    public Optional<B> getBlockEntity(@Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        TileEntity entity = world.getTileEntity(pos);

        if (!this.entityType.isInstance(entity)) {
            return Optional.empty();
        }

        return Optional.of((B) entity);
    }
}
