package betterwithmods.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageGloom {
    private final int gloomLevel;

    public MessageGloom(int gloomLevel) {
        this.gloomLevel = gloomLevel;
    }

    public static MessageGloom decode(FriendlyByteBuf buffer) {
        return new MessageGloom(buffer.readVarInt());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(gloomLevel);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleGloom(gloomLevel));
        });
        ctx.get().setPacketHandled(true);
    }
}
