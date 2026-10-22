package betterwithmods.common.blocks;

import betterwithmods.common.registry.BWMBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class HibachiBlock extends Block {
    public HibachiBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide) {
            boolean hasSignal = level.hasNeighborSignal(pos);
            BlockPos above = pos.above();
            BlockState aboveState = level.getBlockState(above);

            if (hasSignal) {
                if (aboveState.isAir()) {
                    level.setBlock(above, Blocks.FIRE.defaultBlockState(), 3);
                    level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
            } else {
                if (aboveState.is(Blocks.FIRE) || aboveState.is(BWMBlocks.STOKED_FLAME.get())) {
                    level.setBlock(above, Blocks.AIR.defaultBlockState(), 3);
                    level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.0f);
                }
            }
        }
    }
}
