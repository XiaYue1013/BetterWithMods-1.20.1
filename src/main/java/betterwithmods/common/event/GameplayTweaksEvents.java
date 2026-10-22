package betterwithmods.common.event;

import betterwithmods.BWMod;
import betterwithmods.common.config.BWMConfig;
import betterwithmods.common.registry.BWMItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BWMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GameplayTweaksEvents {

    @SubscribeEvent
    public static void onCreeperInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof Creeper creeper) {
            Player player = event.getEntity();
            ItemStack held = player.getItemInHand(event.getHand());

            if (held.is(Items.SHEARS) && !creeper.isIgnited()) {
                Level level = event.getLevel();
                if (!level.isClientSide) {
                    level.playSound(null, creeper.getX(), creeper.getY(), creeper.getZ(),
                            SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1.0f, 1.0f);
                    Containers.dropItemStack(level, creeper.getX(), creeper.getY(), creeper.getZ(),
                            new ItemStack(Items.GUNPOWDER, 1 + level.random.nextInt(2)));
                    held.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(event.getHand()));
                }
                event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onGrassBreak(BlockEvent.BreakEvent event) {
        if (!BWMConfig.COMMON.dropHempSeeds.get()) return;

        if (event.getState().is(Blocks.GRASS) || event.getState().is(Blocks.TALL_GRASS) || event.getState().is(Blocks.FERN)) {
            Level level = (Level) event.getLevel();
            if (level.random.nextInt(20) == 0) { // 5% chance
                BlockPos pos = event.getPos();
                Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        new ItemStack(BWMItems.HEMP_SEEDS.get(), 1));
            }
        }
    }
}
