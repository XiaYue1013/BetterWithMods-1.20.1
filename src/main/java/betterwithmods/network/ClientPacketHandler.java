package betterwithmods.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;

public class ClientPacketHandler {

    public static void handleRotate(int entityId, float yaw, float pitch) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            Entity entity = mc.level.getEntity(entityId);
            if (entity != null) {
                entity.setYRot(yaw);
                entity.setXRot(pitch);
            }
        }
    }

    public static void handleGloom(int gloomLevel) {
        // Stored on client for vignette / gloom rendering
    }

    public static void handleFXDynamite(double x, double y, double z) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            for (int i = 0; i < 5; i++) {
                mc.level.addParticle(ParticleTypes.SMOKE,
                        x + mc.level.random.nextGaussian() * 0.1,
                        y + mc.level.random.nextGaussian() * 0.1,
                        z + mc.level.random.nextGaussian() * 0.1,
                        0.0, 0.05, 0.0);
                mc.level.addParticle(ParticleTypes.FLAME,
                        x + mc.level.random.nextGaussian() * 0.05,
                        y + mc.level.random.nextGaussian() * 0.05,
                        z + mc.level.random.nextGaussian() * 0.05,
                        0.0, 0.02, 0.0);
            }
        }
    }

    public static void handlePlaced(BlockPos pos) {
        // Client placement feedback
    }
}
