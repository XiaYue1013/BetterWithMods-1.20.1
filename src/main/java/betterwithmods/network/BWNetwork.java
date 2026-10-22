package betterwithmods.network;

import betterwithmods.BWMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class BWNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(BWMod.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        CHANNEL.messageBuilder(MessageRotate.class, packetId++, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(MessageRotate::decode)
                .encoder(MessageRotate::encode)
                .consumerMainThread(MessageRotate::handle)
                .add();

        CHANNEL.messageBuilder(MessageGloom.class, packetId++, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(MessageGloom::decode)
                .encoder(MessageGloom::encode)
                .consumerMainThread(MessageGloom::handle)
                .add();

        CHANNEL.messageBuilder(MessageFXDynamite.class, packetId++, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(MessageFXDynamite::decode)
                .encoder(MessageFXDynamite::encode)
                .consumerMainThread(MessageFXDynamite::handle)
                .add();

        CHANNEL.messageBuilder(MessagePlaced.class, packetId++, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(MessagePlaced::decode)
                .encoder(MessagePlaced::encode)
                .consumerMainThread(MessagePlaced::handle)
                .add();
    }

    public static void sendToPlayer(Object message, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static void sendToAllAround(Object message, Level level, BlockPos pos, double radius) {
        CHANNEL.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, radius, level.dimension()
        )), message);
    }
}
