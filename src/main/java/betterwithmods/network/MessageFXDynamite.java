package betterwithmods.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageFXDynamite {
    private final double x;
    private final double y;
    private final double z;

    public MessageFXDynamite(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static MessageFXDynamite decode(FriendlyByteBuf buffer) {
        return new MessageFXDynamite(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeDouble(x);
        buffer.writeDouble(y);
        buffer.writeDouble(z);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleFXDynamite(x, y, z));
        });
        ctx.get().setPacketHandled(true);
    }
}
