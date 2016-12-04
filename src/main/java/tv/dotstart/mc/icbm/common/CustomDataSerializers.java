package tv.dotstart.mc.icbm.common;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.util.math.BlockPos;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
public class CustomDataSerializers {
    public static final DataSerializer<BlockPos[]> BLOCKPOS_ARRAY = new DataSerializer<BlockPos[]>() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void write(@Nonnull PacketBuffer buf, @Nonnull BlockPos[] value) {
            buf.writeVarIntToBuffer(value.length);

            for (BlockPos pos : value) {
                buf.writeBlockPos(pos);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public BlockPos[] read(@Nonnull PacketBuffer buf) throws IOException {
            BlockPos[] value = new BlockPos[buf.readVarIntFromBuffer()];

            for (int i = 0; i < value.length; ++i) {
                value[i] = buf.readBlockPos();
            }

            return value;
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public DataParameter<BlockPos[]> createKey(int id) {
            return new DataParameter<>(id, this);
        }
    };

    public static final DataSerializer<Integer[]> INTEGER_ARRAY = new DataSerializer<Integer[]>() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void write(@Nonnull PacketBuffer buf, @Nonnull Integer[] value) {
            buf.writeVarIntArray(ArrayUtils.toPrimitive(value));
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public Integer[] read(@Nonnull PacketBuffer buf) throws IOException {
            return ArrayUtils.toObject(buf.readVarIntArray());
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public DataParameter<Integer[]> createKey(int id) {
            return new DataParameter<>(id, this);
        }
    };
}
