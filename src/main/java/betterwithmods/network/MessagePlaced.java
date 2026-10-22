package betterwithmods.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessagePlaced {
    private final BlockPos pos;

    public MessagePlaced(BlockPos pos) {
        this.pos = pos;
    }

    public static MessagePlaced decode(FriendlyByteBuf buffer) {
        return new MessagePlaced(buffer.readBlockPos());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handlePlaced(pos));
        });
        ctx.get().setPacketHandled(true);
    }
}
