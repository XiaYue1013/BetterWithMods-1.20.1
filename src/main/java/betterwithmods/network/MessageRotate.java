package betterwithmods.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageRotate {
    private final int entityId;
    private final float yaw;
    private final float pitch;

    public MessageRotate(int entityId, float yaw, float pitch) {
        this.entityId = entityId;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public static MessageRotate decode(FriendlyByteBuf buffer) {
        return new MessageRotate(buffer.readVarInt(), buffer.readFloat(), buffer.readFloat());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(entityId);
        buffer.writeFloat(yaw);
        buffer.writeFloat(pitch);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleRotate(entityId, yaw, pitch));
        });
        ctx.get().setPacketHandled(true);
    }
}
