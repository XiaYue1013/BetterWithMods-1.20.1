package betterwithmods.common.event;

import betterwithmods.BWMod;
import betterwithmods.common.config.BWMConfig;
import betterwithmods.common.registry.BWMItems;
import betterwithmods.common.registry.BWMSounds;
import betterwithmods.network.BWNetwork;
import betterwithmods.network.MessageGloom;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BWMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HardcoreEvents {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) return;
        ServerPlayer player = (ServerPlayer) event.player;
        Level level = player.level();

        // 1. Hardcore Gloom (terror in total pitch black darkness)
        if (BWMConfig.COMMON.hcGloom.get()) {
            BlockPos pos = player.blockPosition().above();
            int brightness = level.getRawBrightness(pos, 0);

            CompoundTag data = player.getPersistentData();
            int gloomTicks = data.getInt("BWM_Gloom");

            if (brightness == 0 && !player.isCreative() && !player.isSpectator()) {
                gloomTicks++;
                if (gloomTicks == 100) {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            BWMSounds.AMBIENT_GLOOM.get(), SoundSource.AMBIENT, 0.7f, 1.0f);
                    BWNetwork.sendToPlayer(new MessageGloom(1), player);
                } else if (gloomTicks == 200) {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            BWMSounds.AMBIENT_SPOOK.get(), SoundSource.AMBIENT, 1.0f, 1.0f);
                    BWNetwork.sendToPlayer(new MessageGloom(2), player);
                    player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0, false, false));
                } else if (gloomTicks >= 300) {
                    player.hurt(level.damageSources().starve(), 2.0f);
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            BWMSounds.PLAYER_OOF.get(), SoundSource.PLAYERS, 0.8f, 1.0f);
                    BWNetwork.sendToPlayer(new MessageGloom(3), player);
                    gloomTicks = 240; // Repeat attacks every 3 seconds in total darkness
                }
            } else {
                if (gloomTicks > 0) {
                    BWNetwork.sendToPlayer(new MessageGloom(0), player);
                }
                gloomTicks = 0;
            }
            data.putInt("BWM_Gloom", gloomTicks);
        }

        // 2. Hardcore Injury (penalties when low on health)
        if (BWMConfig.COMMON.hcInjury.get() && !player.isCreative()) {
            if (player.getHealth() <= 6.0f && !player.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0, false, false));
            }
        }

        // 3. Hardcore Hunger
        if (BWMConfig.COMMON.hcHunger.get() && !player.isCreative()) {
            if (player.getFoodData().getFoodLevel() <= 6 && !player.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0, false, false));
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player.isCreative()) return;

        // Hardcore Lumber: Axes are required to harvest wood logs cleanly
        if (BWMConfig.COMMON.hcLumber.get()) {
            if (event.getState().is(BlockTags.LOGS)) {
                ItemStack tool = player.getMainHandItem();
                if (!tool.is(ItemTags.AXES)) {
                    // Chopping with bare hands or non-axe yields sawdust and bark, not whole logs
                    event.setCanceled(true);
                    Level level = (Level) event.getLevel();
                    BlockPos pos = event.getPos();
                    level.destroyBlock(pos, false);
                    Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(BWMItems.SAWDUST.get(), 1));
                    Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(BWMItems.BARK_OAK.get(), 1));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!BWMConfig.COMMON.hcSpawn.get()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ServerLevel level = player.serverLevel();

        // If player does not have a bed spawn position, scatter respawn around world spawn
        BlockPos spawnPos = level.getSharedSpawnPos();
        double angle = level.random.nextDouble() * 2 * Math.PI;
        double dist = 200 + level.random.nextDouble() * 800; // 200 to 1000 blocks away
        int x = (int) (spawnPos.getX() + Math.cos(angle) * dist);
        int z = (int) (spawnPos.getZ() + Math.sin(angle) * dist);
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);

        player.teleportTo(level, x + 0.5, y + 1.0, z + 0.5, player.getYRot(), player.getXRot());
    }
}
